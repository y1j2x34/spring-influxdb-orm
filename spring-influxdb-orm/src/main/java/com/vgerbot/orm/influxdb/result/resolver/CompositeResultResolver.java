package com.vgerbot.orm.influxdb.result.resolver;

import java.util.ArrayList;
import java.util.List;

import com.vgerbot.orm.influxdb.InfluxDBException;
import com.vgerbot.orm.influxdb.binding.MethodSignature;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.ResultContext;
import com.vgerbot.orm.influxdb.result.ResultResolver;

public class CompositeResultResolver implements ResultResolver<Object> {

	private List<ResultResolver<?>> resolvers;

	public CompositeResultResolver() {
		resolvers = new ArrayList<>();
	}

	@Override
	public boolean support(ResultContext context, MethodSignature methodSignature) {
		for (ResultResolver<?> resultResolver : resolvers) {
			if (resultResolver.support(context, methodSignature)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object resolve(ResultContext context, MethodSignature methodSignature, InfluxDBRepository repository) {
		for (ResultResolver<?> resultResolver : resolvers) {
			if (resultResolver.support(context, methodSignature)) {
				return resultResolver.resolve(context, methodSignature, repository);
			}
		}
		throw new InfluxDBException("Couldn't found resolver for context: " + context);
	}

	public List<ResultResolver<?>> getResolvers() {
		return resolvers;
	}

	public void setResolvers(List<ResultResolver<?>> resolvers) {
		this.resolvers = resolvers;
	}

}
