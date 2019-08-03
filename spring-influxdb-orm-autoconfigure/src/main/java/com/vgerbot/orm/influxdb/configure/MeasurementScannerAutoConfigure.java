package com.vgerbot.orm.influxdb.configure;

import com.vgerbot.orm.influxdb.annotations.InfluxDBORM;
import com.vgerbot.orm.influxdb.factory.ClassPathMeasurementScannerFactoryBean;
import com.vgerbot.orm.influxdb.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.List;

public class MeasurementScannerAutoConfigure implements ImportBeanDefinitionRegistrar, BeanFactoryAware {
    private BeanFactory beanFactory;
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(InfluxDBORM.class.getName(), false)
        );
        String[] measurementPackages = null;
        if (attributes != null) {
            measurementPackages = attributes.getStringArray(InfluxDBORM.FIELD_NAME_ENTITY_BASE_PACKAGE);
        }
        if (ArrayUtils.isEmpty(measurementPackages)) {
            if(AutoConfigurationPackages.has(beanFactory)) {
                List<String> packages = AutoConfigurationPackages.get(beanFactory);
                measurementPackages = packages.toArray(new String[packages.size()]);
            }
        }
        Assert.notEmpty(measurementPackages, "At least one base measurement package must be specified");
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ClassPathMeasurementScannerFactoryBean.class);
        builder.addPropertyValue(ClassPathMeasurementScannerFactoryBean.MEASUREMENT_PACKAGE, StringUtils.join(measurementPackages, ","));
        BeanDefinition definition = builder.getBeanDefinition();
        String beanName = BeanDefinitionReaderUtils.generateBeanName(definition, registry);
        registry.registerBeanDefinition(beanName, definition);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
