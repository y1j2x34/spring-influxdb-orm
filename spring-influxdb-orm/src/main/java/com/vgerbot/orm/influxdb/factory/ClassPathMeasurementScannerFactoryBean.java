package com.vgerbot.orm.influxdb.factory;

import com.vgerbot.orm.influxdb.utils.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class ClassPathMeasurementScannerFactoryBean implements FactoryBean<ClassPathMeasurementScanner>, InitializingBean, EnvironmentAware {

    public static final String MEASUREMENT_PACKAGE = "entityPackage";

    private Environment environment;
    private String entityPackage;
    @Override
    public ClassPathMeasurementScanner getObject() {
        ClassPathMeasurementScanner scanner = new ClassPathMeasurementScanner(this.environment);
        if(!StringUtils.isBlank(entityPackage)) {
            scanner.setEntityPackages(entityPackage.trim().split("[,;\\s\\r\\n\\t]+"));
        }
        return scanner;
    }

    @Override
    public Class<?> getObjectType() {
        return ClassPathMeasurementScanner.class;
    }

    public void setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
