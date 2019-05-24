sudo service influxdb stop # Service should not be running
influxd restore -metadir /var/lib/influxdb/meta ./backups
influxd restore -database test -datadir /var/lib/influxdb/data ./backups
sudo service influxdb start
