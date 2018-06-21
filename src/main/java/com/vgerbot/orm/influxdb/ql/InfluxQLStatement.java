package com.vgerbot.orm.influxdb.ql;

public class InfluxQLStatement {
	public static enum ACTION {
		SELECT, EXECUTE
	}

	private final ACTION action;
	private final String template;

	public InfluxQLStatement(String template, ACTION action) {
		this.template = template;
		this.action = action;
	}

	public ACTION getAction() {
		return action;
	}

	public String getTemplate() {
		return template;
	}
}
