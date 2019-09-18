package com.vgerbot.test.orm.influxdb.mockrepository.execute_annotation.entity;

import com.vgerbot.orm.influxdb.annotations.FieldColumn;
import com.vgerbot.orm.influxdb.annotations.InfluxDBMeasurement;
import com.vgerbot.orm.influxdb.annotations.TagColumn;

import java.io.Serializable;

@InfluxDBMeasurement("traffic_info")
public class TrafficInfo implements Serializable {
    @TagColumn("page")
    private String page;
    @FieldColumn("count")
    private Integer count;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
