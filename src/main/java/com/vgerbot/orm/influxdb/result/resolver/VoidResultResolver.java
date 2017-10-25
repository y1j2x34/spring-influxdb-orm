package com.vgerbot.orm.influxdb.result.resolver;

import com.vgerbot.orm.influxdb.binding.MethodSignature;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.ResultContext;
import com.vgerbot.orm.influxdb.result.ResultResolver;

public class VoidResultResolver implements ResultResolver<Void> {

	@Override
	public Void resolve(ResultContext context, MethodSignature methodSignature, InfluxDBRepository repository) {
		return null;
	}

	@Override
	public boolean support(ResultContext context, MethodSignature methodSignature) {
		return context == ResultContext.VOID && methodSignature.returnsVoid();
	}
}
