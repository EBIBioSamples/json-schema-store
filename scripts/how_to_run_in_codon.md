# Codon run instructions

- Login to codon cluster and go to the project directory
```shell
ssh codon-login.ebi.ac.uk
cd /nfs/production/tburdett/workstreams/fairification/checklists/checklist-converter
```
- Setup environment
```shell
export PYTHONHOME=/hps/software/jupyterhub
export PATH=$PATH:$PYTHONHOME/bin
python --version

./.venv/bin/activate

export RUN_DIR=run010
mkdir /nfs/production/tburdett/workstreams/fairification/checklists/data/$RUN_DIR
```

- Get and validate JSONs (or copy from previous run)
```shell
cut -f2 -d, data/accessions.csv | tail -n +2 | xargs -n1 -t -I{} python src/bsd_sample_retriever.py --out_dir /nfs/production/tburdett/workstreams/fairification/checklists/data/$RUN_DIR/jsons --accessions {}

# run biovalidator in your local machine, get IP address
export BIOVALIDATOR_SERVER=http://10.52.16.78:3020

tail -n +2 data/accessions.csv | awk -F, '{print "--schema schema/"$1"-BSD.json" " --data /nfs/production/tburdett/workstreams/fairification/checklists/data/run009/jsons/"$2".json"}' | xargs -L 1 -t python src/validate_json_using_biovalidator.py --output_dir /nfs/production/tburdett/workstreams/fairification/checklists/data/$RUN_DIR/json_validation
```

- Get and validate XMLs (or copy from previous run)
```shell
export ENA_USER=<WEBIN_USER>
export ENA_PASSWORD=<WEBIN_PASS>

mkdir /nfs/production/tburdett/workstreams/fairification/checklists/data/$RUN_DIR/xml_validation/

python src/validate_xml_against_ena_dev.py --input /nfs/production/tburdett/workstreams/fairification/checklists/data/$RUN_DIR/xmls/ --out_dir /nfs/production/tburdett/workstreams/fairification/checklists/data/$RUN_DIR/xml_validation/ --user $ENA_USER --password $ENA_PASSWORD
```

- Compare results
```shell
python src/compare_results.py --json_dir /nfs/production/tburdett/workstreams/fairification/checklists/data/$RUN_DIR/json_validation --xml_dir /nfs/production/tburdett/workstreams/fairification/checklists/data/$RUN_DIR/xml_validation --output_dir /nfs/production/tburdett/workstreams/fairification/checklists/data/$RUN_DIR
```


# Notes

## Running biovalidator on local machine
```shell
# cd to the project directory
docker-compose up
```

## If Python virtual environment is not setup
```shell
# in project root directory in codon
python -mvenv .venv
chmod +x .venv/bin/activate
```
