package com.vgerbot.orm.influxdb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vgerbot.orm.influxdb.exec.Executor;
import com.vgerbot.orm.influxdb.exec.SpecificExecutor;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@AnnotateExecutor(SpecificExecutor.class)
public @interface SpecifiedExecutor {
	Class<? extends Executor> value();
}
