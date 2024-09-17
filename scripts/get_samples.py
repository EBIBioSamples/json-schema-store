import os

import pandas as pd
import requests
from tqdm import tqdm


CHECKLISTS_FILE_PATH = os.getenv('CHECKLISTS_FILE_PATH', '../data/checklists.txt')
ACCESSIONS_FILE_PATH = os.getenv('ACCESSIONS_FILE_PATH', '../data/accessions.csv')


def main():
    checklists = read_checklists_from_file(CHECKLISTS_FILE_PATH)
    retrieve_and_save_samples(checklists)


def write_to_file(filename, content):
    f = open(filename, "w")
    f.write(content)
    f.close()


def read_checklists_from_file(filename):
    f = open(filename, "r")
    checklists = f.readlines()
    print('Found ' + str(len(checklists)) + ' checklists')
    return checklists


def retrieve_and_save_samples(checklists):
    accession_list = []
    for checklist in tqdm(checklists):
        r = requests.get(
            'https://www.ebi.ac.uk/biosamples/samples?filter=attr:ENA-CHECKLIST:' + checklist + '&page=1&size=100')

        if '_embedded' in r.json():
            for sample in r.json()['_embedded']['samples']:
                accession = sample['accession']
                sra_accession = sample['characteristics']['SRA accession'][0]['text']
                accession_list.append(
                    {"checklist": checklist.strip(), "accession": accession, "sra_accession": sra_accession})

    df = pd.DataFrame(accession_list)
    df.to_csv(ACCESSIONS_FILE_PATH, index=False)


if __name__ == '__main__':
    main()
