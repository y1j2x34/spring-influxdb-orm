package com.vgerbot.test.orm.influxdb.mockrepository.query_annotation.entity;

import com.vgerbot.orm.influxdb.annotations.FieldColumn;
import com.vgerbot.orm.influxdb.annotations.TagColumn;
import org.influxdb.annotation.Measurement;

import java.io.Serializable;

@Measurement(name = "bandwidth_monitor")
public class BandwithMonitorEntity implements Serializable {

    @TagColumn("ip")
    private String ipAddress;
    @FieldColumn("bandwidth")
    private Integer bandwidth;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }
}
