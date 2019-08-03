package com.vgerbot.orm.influxdb.result;

public class WrapperResultContext implements ResultContext {

	private final Object value;

	private final boolean hasResult;

	public WrapperResultContext() {
		value = null;
		hasResult = false;
	}

	public WrapperResultContext(Object value) {
		this.value = value;
		hasResult = true;
	}

	@Override
	public boolean hasResult() {
		return hasResult;
	}

	@Override
	public boolean hasError() {
		return false;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public Object getValue() {
		return value;
	}

}
