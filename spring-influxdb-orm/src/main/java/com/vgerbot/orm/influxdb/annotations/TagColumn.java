package com.vgerbot.orm.influxdb.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface TagColumn {
	/**
	 * 对应数据库字段名称
	 * 
	 * @return
	 */
	String value();
}
