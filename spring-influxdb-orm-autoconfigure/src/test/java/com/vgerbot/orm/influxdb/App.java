package com.vgerbot.orm.influxdb;

import com.vgerbot.orm.influxdb.annotations.InfluxDBORM;
import com.vgerbot.orm.influxdb.test.dao.CensusDao;
import com.vgerbot.orm.influxdb.test.entity.CensusMeasurement;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.util.List;

@SpringBootApplication
@InfluxDBORM(
        daoBasePackage = "com.vgerbot.orm.influxdb.test.dao",
        entityBasePackage = "com.vgerbot.orm.influxdb.test.entity"
)
public class App implements CommandLineRunner {
    @Resource
    private CensusDao dao;
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<CensusMeasurement> measurements = dao.selectByScientist("lanstroth");
        System.out.println(measurements);
    }
}
