import requests
import csv
import xml.etree.ElementTree as ET
import json
import random
import os
import argparse


biosample_field_vals = {
    "collection date":"collection_date"
}

mandatory_error_file_extension = '_mandatory.json'
enum_error_file_extension = '_enum.json'
def parse_csv_to_dict(file_path):
    result_dict = {}
    with open(file_path, mode='r') as file:
        csv_reader = csv.DictReader(file)
        for row in csv_reader:
            key = row['accession']
            value = row['checklist']
            result_dict[key] = value
    return result_dict

def get_biovalidator_url():
    value = os.getenv('BIOVALIDATOR_SERVICE')
    print('BIOVALIDATOR_SERVICE:'+value)
    if value is None:
        raise ValueError(f"Environment variable BIOVALIDATOR_SERVICE not found.")
    return value

def download_ena_checklists():
    with open('./data/checklists.txt', 'r') as file:
        lines = file.readlines()

    for checklist_id in lines:
        checklist_id=checklist_id.strip()
        print(checklist_id.strip())
        url = 'https://www.ebi.ac.uk/ena/browser/api/xml/'+checklist_id

        headers = {
            'Accept': 'application/xml'
        }

        response = requests.get(url, headers=headers)
        response.raise_for_status()
        with open('./data/ena_checklists/'+checklist_id+'.xml', "w") as file:
            file.write(response.text)


def get_ena_checklist(checklist_id):
    tree = ET.parse('./data/ena_checklists/'+checklist_id+'.xml')
    return tree

def get_biosample_json_from_file(file_name):
    with open('./data/invalid/'+file_name, "r") as file:
        content = json.load(file)
    return content
def get_checklist_json_schema(file_path) :
    with open(file_path, "r") as file:
        content = json.load(file)
    return content

def get_biosample(biosample_id):
    url = 'https://www.ebi.ac.uk/biosamples/samples/'+biosample_id

    headers = {
        'Accept': 'application/json'
    }

    response = requests.get(url, headers=headers)
    response.raise_for_status()
    return response.json()
def add_enum_value_error( biosample_json_obj, biosample_id, output_directory):
    location_list = biosample_json_obj.get("characteristics", {}).get("geographic location (country and/or sea)", [])
    print('location_list:',location_list)
    if location_list:
        for location in location_list:
            location['text'] = 'interstellar or beyond'
            write_invalid_biosample(output_directory , biosample_id + enum_error_file_extension, biosample_json_obj)



def add_mandatory_element_error(checklist_xml_str, biosample_json, biosample_id, output_directory):
    root = checklist_xml_str.getroot()
    #print("biosample-json:",biosample_json)
    #find mandatory fields
    mandatory_fields = []
    for field in root.findall('.//FIELD'):
        mandatory = field.find('MANDATORY')
        name = field.find('NAME')

        if mandatory is not None and name is not None and mandatory.text == 'mandatory':
            field_text = name.text
            if field_text in biosample_field_vals:
                field_text= biosample_field_vals.get(field_text)
            mandatory_fields.append(field_text)
    print('Mandatory fields:',mandatory_fields)

    #now remove mandatory any of the mandatory field from json randomly and write to file
    if mandatory_fields :
        element_to_remove = mandatory_fields[random.randint(0, len(mandatory_fields)-1)]
        if element_to_remove in biosample_json['characteristics']:
            del biosample_json['characteristics'][element_to_remove]
            write_invalid_biosample(output_directory , biosample_id + mandatory_error_file_extension, biosample_json)

def write_invalid_biosample(output_directory, file_name, data):
    os.makedirs(output_directory, exist_ok=True)
    with open(output_directory+file_name , 'w') as file:
        json.dump(data, file, indent=4)
def validate_and_write_result_to_file(biovalidator_service, biosample_schema, biosample_json_data, file_path):

    headers = {
        'Accept': '*/*',
        'Content-Type': 'application/json'
    }

    body ={
        "schema":biosample_schema,
        "data":biosample_json_data
    }

    response = requests.post(biovalidator_service , headers=headers, json=body)
    response.raise_for_status()
    with open(file_path, "w") as file:
        print(response.text,'writing to file',file_path)
        file.write(response.text)

# prerequisite: run bio validator docker compose up
# to run the script: python invalid_sample_generator.py
# input ./data/accessions.csv add or edit this file to change input
# this script will
# 1) pull the biosample json and
#       i)remove a mandatory field randomly
#       ii)add invalid value for enum constant
#      and write the invalid  file into ./data/invalid directory with filename in the format accession_error_type.json
# 2) validate invalid documents using biovalidator and write output to directory ./data/invalid/validation_result/
if __name__ == '__main__':
    biovalidator_url = get_biovalidator_url()
    accession_checklist_id_dict = parse_csv_to_dict('./data/accessions.csv')

    parser = argparse.ArgumentParser(description='make invalid biosample json and validate using biovalidator')

    parser.add_argument('--action', type=str, required=True, help='generate : generate invalid documents or validate: validate invalid documents using biovalidator')
    args = parser.parse_args()
    if args.action == 'generate':
        for accession, checklist in accession_checklist_id_dict.items():
            print(accession + ' <-acc,checklist-> ' + checklist)
            ena_checklist_xml = get_ena_checklist(checklist)
            add_mandatory_element_error(ena_checklist_xml, get_biosample(accession), accession, './data/invalid/')
            add_enum_value_error( get_biosample(accession), accession, './data/invalid/')
    else:
        result_dir='./data/invalid/validation_result/'
        os.makedirs(result_dir, exist_ok=True)
        for accession, checklist in accession_checklist_id_dict.items():
            try:
                invalid_biosample_json = get_biosample_json_from_file(accession + mandatory_error_file_extension)
                biosample_json_schema = get_checklist_json_schema('./schema/'+checklist+'-BSD.json')
                validate_and_write_result_to_file(biovalidator_url, biosample_json_schema, invalid_biosample_json, result_dir+accession + mandatory_error_file_extension)

                invalid_biosample_json = get_biosample_json_from_file(accession + enum_error_file_extension)
                validate_and_write_result_to_file(biovalidator_url, biosample_json_schema, invalid_biosample_json, result_dir+accession + enum_error_file_extension)
            except Exception as e:
                #let it silently fail, FileNotFound expected, best is to just iterate files in invalid directory instead of accession.csv
                print(accession+'<-->'+checklist+'failed to validate, some fails may not be available')
