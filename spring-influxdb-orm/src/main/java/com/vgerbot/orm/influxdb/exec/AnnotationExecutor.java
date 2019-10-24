package com.vgerbot.orm.influxdb.exec;

import java.lang.annotation.Annotation;

import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;

public abstract class AnnotationExecutor<Anno extends Annotation> extends Executor {
	protected final Anno annotation;

	public AnnotationExecutor(InfluxDBRepository repository, Anno annotation) {
		super(repository);
		this.annotation = annotation;
	}

	public Anno getAnnotation() {
		return annotation;
	}
}