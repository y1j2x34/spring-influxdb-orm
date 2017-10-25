package com.vgerbot.orm.influxdb.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(ElementType.PARAMETER)
public @interface InfluxDBParam {
	/**
	 * 参数名称
	 * 
	 * @return
	 */
	String value() default "";
}