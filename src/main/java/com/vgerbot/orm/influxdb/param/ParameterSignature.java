package com.vgerbot.orm.influxdb.param;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class ParameterSignature {
	private final int index;
	private final String name;
	private final Class<?> type;

	private ParameterSignature(int index, String name, Class<?> type) {
		super();
		this.index = index;
		this.name = name;
		this.type = type;
	}

	public static ParameterSignature signature(int index, Parameter parameter, String aliasName) {
		Class<?> type = parameter.getType();
		return new ParameterSignature(index, aliasName, type);
	}

	public static ParameterSignature signature(int index, Parameter parameter) {
		String name = parameter.getName();
		Class<?> type = parameter.getType();
		return new ParameterSignature(index, name, type);
	}

	public static List<ParameterSignature> signatures(Method method) {
		Parameter[] parameters = method.getParameters();
		List<ParameterSignature> signatures = new ArrayList<>(parameters.length);

		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			signatures.add(signature(i, parameter));
		}

		return signatures;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public ParameterValue value(Object value) {
		return new ParameterValue(value, this);
	}
}
