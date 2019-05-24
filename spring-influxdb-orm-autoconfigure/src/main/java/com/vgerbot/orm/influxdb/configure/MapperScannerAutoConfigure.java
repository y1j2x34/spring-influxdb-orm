package com.vgerbot.orm.influxdb.configure;

import com.vgerbot.orm.influxdb.annotations.InfluxDBORM;
import com.vgerbot.orm.influxdb.mapper.MapperScannerConfigurer;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.List;

@AutoConfigureAfter({ RepositoryAutoConfigure.class})
public class MapperScannerAutoConfigure implements ImportBeanDefinitionRegistrar, BeanFactoryAware {
    private BeanFactory beanFactory;
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(InfluxDBORM.class.getName(), false)
        );
        String[] daoBasePackages = null;
        if (attributes != null) {
            daoBasePackages = attributes.getStringArray(InfluxDBORM.FIELD_NAME_DAO_BASE_PACKAGE);
        }
        if (ArrayUtils.isEmpty(daoBasePackages)) {
            if(AutoConfigurationPackages.has(beanFactory)) {
                List<String> packages = AutoConfigurationPackages.get(beanFactory);
                daoBasePackages = packages.toArray(new String[packages.size()]);
            }
        }
        Assert.notEmpty(daoBasePackages, "At least one base dao package must be specified");

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
        builder.addPropertyValue(MapperScannerConfigurer.BASE_DAO_PACKAGE_FIELD, daoBasePackages);
        builder.addPropertyValue(MapperScannerConfigurer.REPOSITORY_NAME_FIELD, RepositoryAutoConfigure.REPOSITORY_BEAN_NAME);
        BeanDefinition definition = builder.getBeanDefinition();
        String beanName = BeanDefinitionReaderUtils.generateBeanName(definition, registry);
        registry.registerBeanDefinition(beanName, definition);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
