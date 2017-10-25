package com.vgerbot.orm.influxdb.result.resolver;

import java.util.List;
import java.util.Map;

import com.vgerbot.orm.influxdb.binding.MethodSignature;
import com.vgerbot.orm.influxdb.exception.InfluxDBException;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.NativeResultContext;
import com.vgerbot.orm.influxdb.result.ResultContext;
import com.vgerbot.orm.influxdb.result.ResultResolver;

public class SingleResultResolver implements ResultResolver<Object> {

	@Override
	public boolean support(ResultContext context, MethodSignature methodSignature) {
		return context instanceof NativeResultContext && !methodSignature.returnsMany();
	}

	@Override
	public Object resolve(ResultContext context, MethodSignature methodSignature, InfluxDBRepository repository) {

		@SuppressWarnings("unchecked")
		Map<String, List<Map<String, Object>>> value = (Map<String, List<Map<String, Object>>>) context.getValue();

		if (value == null || value.isEmpty()) {
			return null;
		}

		List<Map<String, Object>> firstMeasurementResult = value.values().iterator().next();
		if (value.size() > 1 || firstMeasurementResult.size() > 1) {
			throw new InfluxDBException("Too many results! expected 1");
		}

		Map<String, Object> firstMap = firstMeasurementResult.get(0);
		if (methodSignature.returnsMap()) {
			return firstMap;
		} else {
			return repository.convert(firstMap, methodSignature.getReturnType());
		}
	}
}
