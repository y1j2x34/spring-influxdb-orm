package com.vgerbot.test.orm.influxdb.intergration.entity;

import com.vgerbot.orm.influxdb.EnumType;
import com.vgerbot.orm.influxdb.annotations.FieldColumn;
import com.vgerbot.orm.influxdb.annotations.InfluxDBMeasurement;
import com.vgerbot.orm.influxdb.annotations.TagColumn;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@InfluxDBMeasurement("census")
public class CensusMeasurementUsingEnum implements Serializable {
    public static enum ScientistName implements EnumType {
        LANSTROTH("lanstroth");

        private String name;
        ScientistName(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return this.name();
        }

        @Override
        public Object getValue() {
            return name;
        }
    }
    private Date time;

    @TagColumn("location")
    private String location;
    @TagColumn("scientist")
    private ScientistName scientist;

    @FieldColumn("butterflies")
    private Integer butterflies;
    @FieldColumn("honeybees")
    private Integer honeybees;


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ScientistName getScientist() {
        return scientist;
    }

    public void setScientist(ScientistName scientist) {
        this.scientist = scientist;
    }

    public Integer getButterflies() {
        return butterflies;
    }

    public void setButterflies(Integer butterflies) {
        this.butterflies = butterflies;
    }

    public Integer getHoneybees() {
        return honeybees;
    }

    public void setHoneybees(Integer honeybees) {
        this.honeybees = honeybees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CensusMeasurementUsingEnum that = (CensusMeasurementUsingEnum) o;
        return time.equals(that.time) &&
                Objects.equals(location, that.location) &&
                scientist == that.scientist &&
                Objects.equals(butterflies, that.butterflies) &&
                Objects.equals(honeybees, that.honeybees);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, location, scientist, butterflies, honeybees);
    }
}
