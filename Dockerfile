FROM ubuntu
LABEL MAINTAINER=y1j2x34@qq.com
RUN apt-get update;\
    apt-get install wget -y; \
    wget https://dl.influxdata.com/influxdb/releases/influxdb_1.2.2_amd64.deb -O influxdb.deb  --show-progress; \
    dpkg -i ./influxdb.deb;\
    rm ./influxdb.deb; \
    mkdir -p /var/lib/influxdb/meta /var/lib/influxdb/data /backups /scripts

COPY ./src/resources/backups/ /backups/
COPY ./src/scripts/ /scripts/

RUN chmod +x /scripts/start.sh /scripts/restore.sh

VOLUME ["/var/lib/influxdb/meta", "/var/lib/influxdb/data"]
EXPOSE 8086 8083

CMD ["/scripts/start.sh"]