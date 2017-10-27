package com.vgerbot.orm.influxdb.exec;

import java.util.Map;

import com.vgerbot.orm.influxdb.binding.MapperMethod;
import com.vgerbot.orm.influxdb.param.ParameterValue;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.ResultContext;

public abstract class Executor {
	protected final InfluxDBRepository repository;

	public Executor(InfluxDBRepository repository) {
		this.repository = repository;
	}

	public abstract ResultContext execute(MapperMethod method, Map<String, ParameterValue> parameters);
}