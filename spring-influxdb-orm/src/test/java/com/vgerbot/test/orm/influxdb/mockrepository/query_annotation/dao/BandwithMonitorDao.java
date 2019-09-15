package com.vgerbot.test.orm.influxdb.mockrepository.query_annotation.dao;

import com.vgerbot.orm.influxdb.InfluxDBDao;
import com.vgerbot.orm.influxdb.annotations.InfluxDBParam;
import com.vgerbot.orm.influxdb.annotations.InfluxDBSelect;
import com.vgerbot.test.orm.influxdb.mockrepository.query_annotation.entity.BandwithMonitorEntity;

public interface BandwithMonitorDao extends InfluxDBDao<BandwithMonitorEntity> {
    String SELECT_BANDWIDTH_BY_IP_ADDRESS = "select from bandwidth_monitor where ipAddress=#{ipAddress}";
    @InfluxDBSelect(SELECT_BANDWIDTH_BY_IP_ADDRESS)
    public BandwithMonitorEntity selectBandwidthByIpAddress(@InfluxDBParam("ipAddress") String ipAddress);
}
