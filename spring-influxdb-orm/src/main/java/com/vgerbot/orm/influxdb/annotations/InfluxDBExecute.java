package com.vgerbot.orm.influxdb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vgerbot.orm.influxdb.exec.ExecuteExecutor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@AnnotateExecutor(ExecuteExecutor.class)
public @interface InfluxDBExecute {
	String value();
}
