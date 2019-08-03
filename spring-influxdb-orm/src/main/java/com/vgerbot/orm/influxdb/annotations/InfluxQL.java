package com.vgerbot.orm.influxdb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vgerbot.orm.influxdb.exec.InfluxQLAnnotExecutor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@AnnotateExecutor(InfluxQLAnnotExecutor.class)
public @interface InfluxQL {

	/**
	 * 语句的key, 默认为被标记的方法名称
	 * 
	 * @return
	 */
	String value() default "";
}
