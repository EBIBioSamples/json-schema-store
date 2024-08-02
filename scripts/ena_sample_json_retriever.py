import requests
import json
import argparse

# to run: python ena_sample_json_retriever.py --accession accession --user <> --password <> --out_file accession.json

WEBIN_REST_API = "https://www.ebi.ac.uk/ena/submit/webin-v2/sample/"
SUMMARY_API = "https://www.ebi.ac.uk/ena/browser/api/summary/"


def get_ena_accession(accession):
    headers = {"accept": "application/json"}

    # get ena_sample_accession from browser api
    summary_api = SUMMARY_API + accession
    response = requests.get(summary_api, headers=headers)
    response.raise_for_status()
    ena_accession = None
    if response.status_code == 200:
        data = response.json()
        ena_accession = data['summaries'][0]["secondaryAccession"][0]

    return ena_accession


def retrieve_ena_sample():
    parser = argparse.ArgumentParser(description='Retrieve and Write ENA SAMPLE JSON into a file.')

    parser.add_argument('--accession', type=str, required=True, help='ENA sample accession')
    parser.add_argument('--user', type=str, required=True, help='username for ENA REST service')
    parser.add_argument('--password', type=str, required=True, help='password for ENA REST service')
    parser.add_argument('--out_file', type=str, required=True, help='output file path with name')
    args = parser.parse_args()
    headers = {"accept": "application/json"}

    ena_accession = get_ena_accession(args.accession)
    if ena_accession is None:
        raise ValueError("ena_accession not fetched from summary API")

    # get sample json from WEBIN-REST
    webin_api_url = WEBIN_REST_API + ena_accession

    response = requests.get(webin_api_url, auth=(args.user, args.password), headers=headers)
    response.raise_for_status()

    with open(args.out_file, 'w') as file:
        json.dump(response.json(), file, indent=4)


if __name__ == '__main__':
    retrieve_ena_sample()
