package com.vgerbot.orm.influxdb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InfluxDBMeasurement {
	String value();

	String retentionPolicy() default "";

	String shardingField() default "";

	String[] datetimePatterns() default { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ssZZ", "yyyy-MM-dd",
			"yyyy-MM-ddZZ", "'T'HH:mm:ss", "'T'HH:mm:ssZZ", "HH:mm:ss", "HH:mm:ssZZ", "EEE, dd MMM yyyy HH:mm:ss Z" };
}
