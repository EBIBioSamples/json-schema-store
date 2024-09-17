import argparse

import re
import requests

from os.path import join, isfile, isdir
from os import mkdir
from typing import List
from argparse import ArgumentParser
from xml.etree import ElementTree
from uuid import uuid4


def str_or_file_path(value: str) -> List:
    if isfile(value):
        with open(value, 'r') as f:
            values = f.read().splitlines()
    else:
        values = value.split(',')
    return values


def directory_exists(value):
    if isdir(value):
        return value
    raise FileNotFoundError(f"folder '{value}' does not exist. Please create and re-try.")


def parse_arguments() -> argparse.Namespace:
    parser = ArgumentParser()
    parser.add_argument('-a', '--accessions', type=str_or_file_path, required=True, help='ENA sample accession. A file '
                                                                                         'path or a commma-separated '
                                                                                         'list of values can be '
                                                                                         'provided')
    parser.add_argument('-o', '--out_dir', type=directory_exists, required=True, help='Output directory. Please note, '
                                                                                      'documents will be saved as '
                                                                                      '{accession}.xml')
    arguments = parser.parse_args()
    return arguments


def get_document(accession):
    url = f"https://www.ebi.ac.uk/ena/browser/api/xml/{accession}"
    response = requests.get(url)
    response.raise_for_status()
    return response.text


def write_document(output_path, content):
    with open(output_path, 'w') as f:
        f.write(content)


def main(accessions, output_path):
    for accession in accessions:
        document = get_document(accession)
        checklist = re.search("ERC\\d{6}", document).group()
        checklist_path = join(output_path, checklist)
        if not isdir(checklist_path):
            mkdir(checklist_path)

        output_file_path = join(checklist_path, f"{accession}.xml")
        write_document(output_file_path, document)


if __name__ == '__main__':
    args = parse_arguments()
    main(args.accessions, args.out_dir)
