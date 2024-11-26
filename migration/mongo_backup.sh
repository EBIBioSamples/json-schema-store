#!/bin/bash
set -e
#backup schemastore database

source env.sh
mkdir -p /homes/bsd_prod/schemastore/mongobackup

TIMESTAMP=`date +%Y%m%d%H%M%S`
ARCHIVE="/homes/bsd_prod/schemastore/mongobackup/schemastore-$TIMESTAMP.gzip"
echo "Archiving to $ARCHIVE"

time ~/mongodb-tools/bin/mongodump --host $MONGO_HOST --db schemastore --gzip --archive=$ARCHIVE --username $MONGO_USER --password $MONGO_PASS --authenticationDatabase admin

echo "Created archive of `gzip -dc $ARCHIVE | wc -c` bytes"