package com.vgerbot.orm.influxdb.param;

public class ParameterValue {
	private final Object value;
	private final ParameterSignature signature;

	public ParameterValue(Object value, ParameterSignature signature) {
		super();
		this.value = value;
		this.signature = signature;
	}

	public Object getValue() {
		return value;
	}

	public ParameterSignature getSignature() {
		return signature;
	}

}
