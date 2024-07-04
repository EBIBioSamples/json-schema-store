import os

import requests

from argparse import ArgumentParser, Namespace
from io import StringIO
from uuid import uuid4
from os.path import join, isfile, isdir
from xml.etree import ElementTree
from time import sleep
import re
import glob
import tqdm
from typing import List

def str_or_file_path(value: str) -> List:
    if isfile(value):
        return [value]
    elif isdir(value):
        return glob.glob(join(value, "**/*.xml"))
    raise ValueError("Input provided is not a file or an existing directory. Please provide proper input.")


def directory_exists(value) -> str:
    if isdir(value):
        return value
    raise FileNotFoundError(f"folder '{value}' does not exist. Please create and re-try.")


def parse_arguments() -> Namespace:
    parser = ArgumentParser()
    parser.add_argument('-i', '--input', type=str_or_file_path, required=True, help='input sample to be sent to ENA '
                                                                                    'for validation. A path to a file '
                                                                                    'or a directory can be used')
    parser.add_argument('-o', '--out_dir', type=directory_exists, required=True, help='Output directory. Please note, '
                                                                                      'documents will be saved as '
                                                                                      '{accession}.xml')
    parser.add_argument('-u', '--user', type=str, required=True, help='username for ENA REST service')
    parser.add_argument('-p', '--password', type=str, required=True, help='password for ENA REST service')
    arguments = parser.parse_args()
    return arguments


def create_submission_xml():
    submission_xml = """<?xml version="1.0" encoding="UTF-8"?>
<SUBMISSION>
   <ACTIONS>
      <ACTION>
         <VALIDATE/>
      </ACTION>
   </ACTIONS>
</SUBMISSION>
    """
    return submission_xml


def clean_document(xml_document: str) -> str:
    """
    Remove accession identifiers from ENA XML document. They are not allowed for ENA validation.
    :param xml_document:
    :return:
    """
    root = ElementTree.XML(xml_document)
    for parent in root.iter():
        for child in list(parent):
            if child.tag == 'SAMPLE':
                del child.attrib['accession']
                child.attrib['alias'] = str(uuid4())
            if child.tag == 'IDENTIFIERS':
                parent.remove(child)
    return ElementTree.tostring(root).decode()


def submit(sub_xml_streamable, sample_xml_streamable, url, username, password):
    r = requests.post(url, files={'SUBMISSION': sub_xml_streamable, 'SAMPLE': sample_xml_streamable},
                      auth=(username, password))
    sleep(0.02)  # hard-capping rate of requests to 50 per second (https://ena-docs.readthedocs.io/en/latest/retrieval/programmatic-access.html#rate-limits)
    return r


def retrieve_receipt_result(xml_receipt: str):
    root = ElementTree.XML(xml_receipt)
    return 'valid' if root.attrib['success'] == 'true' else 'invalid'


def main(username, password, input_paths, output_path):
    submission_xml = create_submission_xml()
    submission_xml_streamable = StringIO(submission_xml)
    submission_url = "https://wwwdev.ebi.ac.uk/ena/submit/drop-box/submit/"

    for sample_path in tqdm.tqdm(input_paths):
        submission_xml_streamable.seek(0)
        output_name = sample_path.split('/')[-1]
        with open(sample_path, 'r') as f:
            sample = clean_document(f.read())
        sample_streamable = StringIO(sample)
        response = submit(submission_xml_streamable, sample_streamable, submission_url, username, password)
        receipt = response.text
        receipt_tag = retrieve_receipt_result(receipt)
        checklist_id = re.search("ERC\\d{6}", sample_path).group()

        sample_out_path = join(output_path, f"{checklist_id}/{receipt_tag}")
        if not isdir(sample_out_path):
            os.makedirs(sample_out_path)
        with open(join(sample_out_path, output_name), 'w') as f:
            f.write(response.text)


if __name__ == '__main__':
    args = parse_arguments()
    main(args.user, args.password, args.input, args.out_dir)

