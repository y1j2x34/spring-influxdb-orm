package com.vgerbot.orm.influxdb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vgerbot.orm.influxdb.exec.AnnotationExecutor;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotateExecutor {
	Class<? extends AnnotationExecutor<?>> value();
}
