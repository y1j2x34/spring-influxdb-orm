package com.vgerbot.orm.influxdb.result;

class VoidResultContext implements ResultContext {

	static final VoidResultContext INSTANCE = new VoidResultContext();

	private VoidResultContext() {
	}

	@Override
	public boolean hasResult() {
		return false;
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
		return null;
	}
}