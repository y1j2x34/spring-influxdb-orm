package com.vgerbot.orm.influxdb.exec;

import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;

public abstract class XmlExecutor extends Executor {

	public XmlExecutor(InfluxDBRepository repository) {
		super(repository);
	}

}
