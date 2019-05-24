package com.vgerbot.orm.influxdb.annotations;

import com.vgerbot.orm.influxdb.configure.MeasurementScannerAutoConfigure;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MeasurementScannerAutoConfigure.class)
public @interface InfluxDBORM {
    String FIELD_NAME_ENTITY_BASE_PACKAGE = "entityBasePackage";
    String FIELD_NAME_DAO_BASE_PACKAGE = "daoBasePackage";
    String[] entityBasePackage() default {};
    String[] daoBasePackage() default {};
//    String includeDaoClassNameRegex() default "";
//    String excludeDaoClassNameRegex() default "";
}
