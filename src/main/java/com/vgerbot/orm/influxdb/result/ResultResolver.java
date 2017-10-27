package com.vgerbot.orm.influxdb.result;

import com.vgerbot.orm.influxdb.binding.MethodSignature;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;

/**
 * 
 * 
 * @param <T>
 */
public interface ResultResolver<T> {

	boolean support(ResultContext context, MethodSignature methodSignature);

	T resolve(ResultContext context, MethodSignature methodSignature, InfluxDBRepository repository);
}
