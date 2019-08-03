package com.vgerbot.orm.influxdb.test.entity;

import com.vgerbot.orm.influxdb.annotations.FieldColumn;
import com.vgerbot.orm.influxdb.annotations.InfluxDBMeasurement;
import com.vgerbot.orm.influxdb.annotations.TagColumn;

import java.io.Serializable;
import java.util.Date;

@InfluxDBMeasurement("census")
public class CensusMeasurement implements Serializable {
	private static final long serialVersionUID = 8260424450884444916L;

	private Date time;

	@TagColumn("location")
	private String location;
	@TagColumn("scientist")
	private String scientist;

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

	public String getScientist() {
		return scientist;
	}

	public void setScientist(String scientist) {
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((scientist == null) ? 0 : scientist.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CensusMeasurement other = (CensusMeasurement) obj;
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (scientist == null) {
			if (other.scientist != null) {
				return false;
			}
		} else if (!scientist.equals(other.scientist)) {
			return false;
		}
		if (time == null) {
			if (other.time != null) {
				return false;
			}
		} else if (!time.equals(other.time)) {
			return false;
		}
		return true;
	}

	public String toString() {
		return "[time: " + time.getTime() + ", location: " + location + ", scientist: " + scientist + ", butterflies: " + butterflies
				+ ", honeybees: " + honeybees + "]";
	}
}
