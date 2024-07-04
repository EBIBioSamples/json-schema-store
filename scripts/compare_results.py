import os
import re
from pathlib import Path
from tqdm import tqdm
import pandas as pd
import requests
import argparse
import sys
import json
import glob
import pandas as pd


def main(json_validation_dir, xml_validation_dir, output_dir):
    results = []
    checklist_dirs = glob.glob(xml_validation_dir + '/*/')
    # checklist_dirs = [x[0] for x in os.walk(xml_validation_dir)]
    for checklist_dir in checklist_dirs:
        valid_dir = os.path.join(checklist_dir, 'valid')
        if os.path.isdir(valid_dir):
            files = glob.glob(valid_dir + '/*')
            results.extend([{"CHECKLIST": re.search('(ERC[0-9]*)', f).group(0), "ACCESSION": re.search('(SAMEA[0-9]*)', f).group(0), "XML_VALID": True} for f in files])

        invalid_dir = os.path.join(checklist_dir, 'invalid')
        if os.path.isdir(invalid_dir):
            files = glob.glob(invalid_dir + '/*')
            results.extend([{"CHECKLIST": re.search('(ERC[0-9]*)', f).group(0), "ACCESSION": re.search('(SAMEA[0-9]*)', f).group(0), "XML_VALID": False} for f in files])

    for r in results:
        r['JSON_VALID'] = get_json_validation_result(os.path.join(json_validation_dir, '200', str(r['ACCESSION']) + '.json'))
        r['MATCH'] = r['JSON_VALID'] == r['XML_VALID']
        if not r['MATCH']:
            print('Mismatching validation results for checklist: ' + r['CHECKLIST'] + ', accession: ' + r['ACCESSION'])
            r['SAMPLE_LINK'] = 'https://www.ebi.ac.uk/biosamples/samples/' + r['ACCESSION']
            r['ENA_VALIDATION_RESULT'] = read_file_content_to_string(os.path.join(xml_validation_dir, r['CHECKLIST'], 'valid' if r['XML_VALID'] else 'invalid', r['ACCESSION'] + '.xml'))

    write_to_file(output_dir, results)


def get_json_validation_result(file_path):
    try:
        with open(file_path, 'r') as file:
            content = json.load(file)
            return content == list()
    except Exception as e:
        print(f"Error reading file {file_path}: {e}")
        sys.exit(1)


def read_file_content_to_string(file_path):
    try:
        with open(file_path, 'r') as file:
            return str(file.readlines())
    except Exception as e:
        print(f"Error reading file {file_path}: {e}")
        sys.exit(1)

def write_to_file(output_dir, content):
    dataframe = pd.DataFrame(content)
    dataframe.to_csv(os.path.join(output_dir, 'results.csv'), index=False)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Compare results of xml_validation and json_validation')
    parser.add_argument('--json_dir', type=str, help='Path to json_validation directory')
    parser.add_argument('--xml_dir', type=str, help='Path to xml_validation directory')
    parser.add_argument('--output_dir', type=str, help='Directory where the output file will be saved')

    args = parser.parse_args()

    main(args.json_dir, args.xml_dir, args.output_dir)


