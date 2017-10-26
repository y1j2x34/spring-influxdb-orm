package com.vgerbot.test.orm.influxdb.utils;

import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vgerbot.test.orm.influxdb.entity.CensusMeasurement;

public class JSON {
	private static ObjectMapper mapper = new ObjectMapper();

	public static String stringify(Object value) {
		if (value == null) {
			return "null";
		} else {
			StringWriter sw = new StringWriter();
			try {
				mapper.writerFor(value.getClass()).writeValue(sw, value);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return sw.toString();
		}
	}

	public static void main(String[] args) throws Exception {
		// mapper.configure(Feature.IGNORE_UNKNOWN, true);

		CensusMeasurement m = new CensusMeasurement();
		m.setButterflies(100);
		m.setLocation("Shanghai,China");
		mapper.writerFor(CensusMeasurement.class).writeValue(System.out, m);
	}
}
