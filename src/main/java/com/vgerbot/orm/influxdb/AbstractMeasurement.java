package com.vgerbot.orm.influxdb;

import java.io.Serializable;
import java.util.Date;

public abstract class AbstractMeasurement implements Serializable {
	private static final long serialVersionUID = -6963550494966495987L;

	private Date time;

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}