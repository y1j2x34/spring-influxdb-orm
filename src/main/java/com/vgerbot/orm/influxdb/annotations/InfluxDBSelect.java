package com.vgerbot.orm.influxdb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vgerbot.orm.influxdb.exec.SelectExecutor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@AnnotateExecutor(SelectExecutor.class)
public @interface InfluxDBSelect {
	/**
	 * 直接提供查询语句
	 * 
	 * @return
	 */
	String value();
}
