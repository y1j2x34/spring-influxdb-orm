package com.vgerbot.orm.influxdb.test.testcase;

import com.vgerbot.orm.influxdb.test.dao.CensusDao;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import com.vgerbot.orm.influxdb.test.entity.CensusMeasurement;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(value= MethodSorters.NAME_ASCENDING)
public class TestCase {
    @Resource
    private CensusDao dao;

    @Test
    public void test1DaoNotNull() {
        assertThat(dao, notNullValue());
    }

    @Test
    public void test2Query() {
        List<CensusMeasurement> scientists = dao.selectByScientist("lanstroth");
        assertThat(scientists, is(not(empty())));
        System.out.println(scientists);
    }
}
