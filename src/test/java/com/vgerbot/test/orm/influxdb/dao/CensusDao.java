package com.vgerbot.test.orm.influxdb.dao;

import java.util.List;

import com.vgerbot.orm.influxdb.InfluxDBDao;
import com.vgerbot.orm.influxdb.annotations.InfluxDBParam;
import com.vgerbot.orm.influxdb.annotations.InfluxDBSelect;
import com.vgerbot.test.orm.influxdb.entity.CensusMeasurement;

public interface CensusDao extends InfluxDBDao<CensusMeasurement> {

	@InfluxDBSelect("select * from census where scientist = ${scientist}")
	public List<CensusMeasurement> selectByScientist(@InfluxDBParam("scientist") String scientist);

}
