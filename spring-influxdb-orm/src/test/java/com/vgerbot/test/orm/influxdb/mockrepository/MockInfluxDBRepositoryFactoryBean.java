package com.vgerbot.test.orm.influxdb.mockrepository;

import com.vgerbot.orm.influxdb.factory.InfluxDBRepositoryFactoryBean;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;

public class MockInfluxDBRepositoryFactoryBean extends InfluxDBRepositoryFactoryBean {

    public interface RepositoryGenerator {
        default void setup(){}
        public InfluxDBRepository generate(InfluxDBRepositoryFactoryBean factoryBean);
    }

    private static RepositoryGenerator generator;
    private InfluxDBRepository repository;
    @Override
    public void afterPropertiesSet() {
        if (generator == null) {
            throw new RuntimeException("Generator has not been assigned");
        } else {
            repository = generator.generate(this);
        }
    }

    @Override
    public InfluxDBRepository getObject() throws Exception {
        return repository;
    }
    public static void setGenerator(RepositoryGenerator generator) {
        MockInfluxDBRepositoryFactoryBean.generator = generator;
    }
}
