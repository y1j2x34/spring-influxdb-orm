package com.vgerbot.orm.influxdb.configure;

import com.vgerbot.orm.influxdb.factory.ClassPathMeasurementScanner;
import com.vgerbot.orm.influxdb.factory.InfluxDBRepositoryFactoryBean;
import com.vgerbot.orm.influxdb.props.DataSourceProperties;
import com.vgerbot.orm.influxdb.props.OptionsProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({
        DataSourceProperties.class,
        OptionsProperties.class
})
@AutoConfigureAfter(MeasurementScannerAutoConfigure.class)
public class RepositoryAutoConfigure {
    static final String REPOSITORY_BEAN_NAME = "com.vgerbot.orm.influxdb.repo.InfluxDBRepository$bean";
    private final DataSourceProperties properties;
    private final OptionsProperties optionsProperties;
    private ClassPathMeasurementScanner measurementScanner;

    public RepositoryAutoConfigure(
            DataSourceProperties dataSourceProperties,
            OptionsProperties optionsProperties,
            ClassPathMeasurementScanner measurementScanner
    ) {
        this.properties = dataSourceProperties;
        this.optionsProperties = optionsProperties;
        this.measurementScanner = measurementScanner;
    }

    @Bean(REPOSITORY_BEAN_NAME)
    @ConditionalOnMissingBean
    private InfluxDBRepositoryFactoryBean influxDBRepository(){
        InfluxDBRepositoryFactoryBean bean = new InfluxDBRepositoryFactoryBean();

        bean.setMeasurementScanner(this.measurementScanner);

        bean.setHost(properties.getHost());
        bean.setDatabaseName(properties.getDatabaseName());
        bean.setUsername(properties.getUsername());
        bean.setPassword(properties.getPassword());
        bean.setPort(properties.getPort());

        bean.setBatchActions(optionsProperties.getBatchActions());
        bean.setBatchFlushDuration(optionsProperties.getBatchFlushDuration());
        bean.setConnectTimeoutSeconds(optionsProperties.getHttpConnectTimeout());
        bean.setEnableBatch(optionsProperties.getEnableBatch());
        bean.setEnableGzip(optionsProperties.getEnableGzip());
        bean.setReadTimeoutSeconds(optionsProperties.getHttpReadTimeout());
        bean.setWriteTimeoutSeconds(optionsProperties.getHttpWriteTimeout());
        return bean;
    }

}
