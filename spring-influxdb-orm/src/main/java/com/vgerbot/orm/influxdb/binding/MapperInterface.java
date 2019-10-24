package com.vgerbot.orm.influxdb.binding;

import com.vgerbot.orm.influxdb.utils.ReflectionUtils;

public class MapperInterface {
	private final Class<?> interfaceClass;
	private final Class<?> measurementClass;

	public MapperInterface(Class<?> interfaceClass) {
		super();
		this.interfaceClass = interfaceClass;
		this.measurementClass = (Class<?>) ReflectionUtils.getInterfaceGenericType(interfaceClass, 0);
	}

	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}

	public Class<?> getMeasurementClass() {
		return measurementClass;
	}

}
