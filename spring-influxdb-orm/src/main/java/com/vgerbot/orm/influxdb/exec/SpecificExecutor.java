package com.vgerbot.orm.influxdb.exec;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.vgerbot.orm.influxdb.InfluxDBException;
import com.vgerbot.orm.influxdb.annotations.SpecifiedExecutor;
import com.vgerbot.orm.influxdb.binding.MapperMethod;
import com.vgerbot.orm.influxdb.param.ParameterValue;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.ResultContext;

public class SpecificExecutor extends AnnotationExecutor<SpecifiedExecutor> {
	private final Executor realExecutor;

	public SpecificExecutor(InfluxDBRepository repository, SpecifiedExecutor annotation) {
		super(repository, annotation);
		realExecutor = instantiateRealExecutor(repository, annotation);
	}

	private Executor instantiateRealExecutor(InfluxDBRepository repository, SpecifiedExecutor annotation) {
		Class<? extends Executor> executorClass = annotation.value();
		try {
			Constructor<? extends Executor> constructor = executorClass.getDeclaredConstructor(InfluxDBRepository.class);
			if (!constructor.isAccessible()) {
				constructor.setAccessible(true);
			}
			return constructor.newInstance(repository);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new InfluxDBException("invalid executor constructor", e);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new InfluxDBException("an error occured while instantiating executor", e);
		}
	}

	@Override
	public ResultContext execute(MapperMethod method, Map<String, ParameterValue> parameters) {
		return realExecutor.execute(method, parameters);
	}
}
