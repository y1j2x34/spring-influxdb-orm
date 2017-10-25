package com.vgerbot.orm.influxdb.exception;

public class InfluxDBException extends RuntimeException {

	private static final long serialVersionUID = -4410268653794610252L;

	public InfluxDBException() {
		super();
	}

	public InfluxDBException(String message, Throwable cause) {
		super(message, cause);
	}

	public InfluxDBException(String message) {
		super(message);
	}

	public InfluxDBException(Throwable cause) {
		super(cause);
	}
}
