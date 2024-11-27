# VM to K8s Migration Guide: Validation Infrastructure

## Migration
1. Take DB backup
2. Change BSD config
3. Clear DB
4. Import documents
   a. ENA documents
   b. BioSamples documents
5. Restart biosamples-core services

## Rollback
1. Clear DB
2. Restore DB
3. Change BSD config
4. Restart services

## Scripts and Config
### DB Backup
```shell
ssh bsd_prod@wp-p1m2-3d
cd ~/schemastore
# copy scripts to the directory
# edit env.sh to have db config
./mongo_backup.sh
```

### DB Restore
```shell
ssh bsd_prod@wp-p1m2-3d
cd ~/schemastore
# copy scripts to the directory
# edit env.sh to have db config
./mongo_restore.sh
```

### BioSamples config
```properties
# application.properties
biosamples.schemaValidator=<validator-url>
biosamples.schemaStore=<schema-store-url>
```

### Import Documents
```shell
curl https://wwwint.ebi.ac.uk/biosamples/schema-store/checklist/converter/convert/all
# import biosamples manually. check `biosamples_schema.json`
```