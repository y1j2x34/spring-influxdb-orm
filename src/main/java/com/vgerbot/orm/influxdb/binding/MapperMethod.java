package com.vgerbot.orm.influxdb.binding;

import java.util.Collection;
import java.util.LinkedList;

import com.vgerbot.orm.influxdb.exec.Executor;
import com.vgerbot.orm.influxdb.exec.ExecutorFactory;
import com.vgerbot.orm.influxdb.param.AnnotationParameterProducer;
import com.vgerbot.orm.influxdb.param.BatchParameterProducer;
import com.vgerbot.orm.influxdb.param.J8ParameterProducer;
import com.vgerbot.orm.influxdb.param.ParameterProducer;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;

public class MapperMethod {
	private final Executor executor;
	private final ParameterProducer producer;
	private final MethodSignature methodSignature;
	private final MapperInterface mapperInterface;
	private final InfluxDBRepository repository;

	public MapperMethod(Class<?> mapperInterface, InfluxDBRepository repository, MethodSignature signature) {
		this.mapperInterface = new MapperInterface(mapperInterface);
		this.repository = repository;
		this.methodSignature = signature;
		this.executor = ExecutorFactory.getInstance().executorOf(signature, repository);

		final Collection<ParameterProducer> parameterProducers = new LinkedList<>();
		parameterProducers.add(new AnnotationParameterProducer(signature.getMethod()));
		parameterProducers.add(new J8ParameterProducer(signature.getMethod()));
		this.producer = new BatchParameterProducer(parameterProducers);
	}

	public Executor getExecutor() {
		return executor;
	}

	public ParameterProducer getProducer() {
		return producer;
	}

	public MethodSignature getMethodSignature() {
		return methodSignature;
	}

	public MapperInterface getMapperInterface() {
		return mapperInterface;
	}

	public InfluxDBRepository getRepository() {
		return repository;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((methodSignature == null) ? 0 : methodSignature.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapperMethod other = (MapperMethod) obj;
		if (methodSignature == null) {
			if (other.methodSignature != null)
				return false;
		} else if (!methodSignature.equals(other.methodSignature))
			return false;
		return true;
	}

}
