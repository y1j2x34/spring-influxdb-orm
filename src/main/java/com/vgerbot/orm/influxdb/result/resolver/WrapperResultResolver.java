package com.vgerbot.orm.influxdb.result.resolver;

import com.vgerbot.orm.influxdb.binding.MethodSignature;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.ResultContext;
import com.vgerbot.orm.influxdb.result.ResultResolver;
import com.vgerbot.orm.influxdb.result.WrapperResultContext;

public class WrapperResultResolver implements ResultResolver<Object> {

	@Override
	public boolean support(ResultContext context, MethodSignature methodSignature) {
		return context instanceof WrapperResultContext;
	}

	@Override
	public Object resolve(ResultContext context, MethodSignature methodSignature, InfluxDBRepository repository) {
		return context.getValue();
	}

}
