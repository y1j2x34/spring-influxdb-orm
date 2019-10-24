package com.vgerbot.test.orm.influxdb.intergration.dao;

import com.vgerbot.orm.influxdb.InfluxDBDao;
import com.vgerbot.orm.influxdb.annotations.InfluxDBSelect;
import com.vgerbot.test.orm.influxdb.intergration.entity.CensusMeasurementUsingEnum;

import java.util.List;

public interface EnumTypeMeasurementDao extends InfluxDBDao<CensusMeasurementUsingEnum> {
    @InfluxDBSelect("select * from census where scientist='lanstroth'")
    List<CensusMeasurementUsingEnum> selectAll_lanstroths_data();
}
