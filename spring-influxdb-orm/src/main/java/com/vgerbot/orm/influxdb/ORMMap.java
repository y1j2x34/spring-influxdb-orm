package com.vgerbot.orm.influxdb;

import java.util.HashMap;
import java.util.Map;

public class ORMMap extends HashMap<String, Object> implements Map<String, Object> {
	private static final long serialVersionUID = 7363945321487011747L;

	public static ORMMap single(String key, Object value) {
		return empty().append(key, value);
	}

	public static ORMMap empty() {
		return new ORMMap();
	}

	public ORMMap append(String key, Object value) {
		this.put(key, value);
		return this;
	}
}
