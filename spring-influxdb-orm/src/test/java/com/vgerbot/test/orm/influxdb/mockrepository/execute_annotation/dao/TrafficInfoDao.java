package com.vgerbot.test.orm.influxdb.mockrepository.execute_annotation.dao;

import com.vgerbot.orm.influxdb.InfluxDBDao;
import com.vgerbot.orm.influxdb.annotations.InfluxDBExecute;
import com.vgerbot.orm.influxdb.annotations.InfluxDBParam;
import com.vgerbot.test.orm.influxdb.mockrepository.execute_annotation.entity.TrafficInfo;

public interface TrafficInfoDao extends InfluxDBDao<TrafficInfo> {

    String EXECUTE_RECORD_TRAFIC_QL = "insert traffic_info pageUrl=${page} count=${count}";

    @InfluxDBExecute(EXECUTE_RECORD_TRAFIC_QL)
    void recordTraffic(@InfluxDBParam("page")  String page, @InfluxDBParam("count") int count);
}
