package com.vgerbot.orm.influxdb.result;

public interface ResultContext {
	public static final ResultContext VOID = VoidResultContext.INSTANCE;

	public boolean hasResult();

	public boolean hasError();

	public String getErrorMessage();

	public Object getValue();
}