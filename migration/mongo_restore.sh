#!/bin/bash
set -e
#Restore schemastore backup
source env.sh

ARCHIVE=`ls -rt /homes/bsd_prod/schemastore/mongobackup/*.gzip | tail -n 1`
echo "Restoring file $ARCHIVE"

time ~/mongodb-tools/bin/mongorestore --host $MONGO_HOST --username $MONGO_USER --password $MONGO_PASS --authenticationDatabase admin --gzip --drop --numInsertionWorkersPerCollection=8 --verbose --archive=$ARCHIVE
