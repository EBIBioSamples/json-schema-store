import json
import requests
import os
import sys
import argparse

def read_file(file_path):
    try:
        with open(file_path, 'r') as file:
            return json.load(file)
    except Exception as e:
        print(f"Error reading file {file_path}: {e}")
        sys.exit(1)

def write_output(output_dir, input_file_name, content):
    os.makedirs(output_dir, exist_ok=True)
    output_path = os.path.join(output_dir, input_file_name)
    try:
        with open(output_path, 'w') as file:
            file.write(content)
    except Exception as e:
        print(f"Error writing to output file {output_path}: {e}")
        sys.exit(1)

def main(data, schema, output_dir):
    # Read the server address from the environment variable
    server_url = os.getenv('BIOVALIDATOR_SERVER')
    if not server_url:
        print("Environment variable BIOVALIDATOR_SERVER is not set")
        sys.exit(1)

    # Read the content of files data and schema
    content_data = read_file(data)
    content_schema = read_file(schema)

    # Create the JSON document
    json_document = {
        'data': content_data,
        'schema': content_schema
    }

    # Send POST request to the server
    try:
        headers = {'Content-Type': 'application/json'}
        response = requests.post(f'{server_url}/validate', json=json_document, headers=headers)
    except requests.RequestException as e:
        print(f"Error sending POST request: {e}")
        sys.exit(1)

    # Determine the output file name
    input_file_name = os.path.basename(data)
    write_output(output_dir+'/'+str(response.status_code), input_file_name, response.text)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Send file contents in a POST request to a server and save the response.')
    parser.add_argument('--data', type=str, help='Path to the data file')
    parser.add_argument('--schema', type=str, help='Path to the schema file')
    parser.add_argument('--output_dir', type=str, help='Directory where the output file will be saved')

    args = parser.parse_args()

    main(args.data, args.schema, args.output_dir)
