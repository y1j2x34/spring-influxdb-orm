#!/bin/bash

if [[ $INFLUXDB_RESTORED=1 ]]; then  
    influxd restore -metadir /var/lib/influxdb/meta /backups
    influxd restore -database test -datadir /var/lib/influxdb/data /backups
fi

export  INFLUXDB_RESTORED=1;