package com.vgerbot.test.orm.influxdb.intergration.testcase;

import java.util.Collection;
import java.util.List;

import com.vgerbot.test.orm.influxdb.intergration.dao.EnumTypeMeasurementDao;
import com.vgerbot.test.orm.influxdb.intergration.entity.CensusMeasurementUsingEnum;
import static org.hamcrest.CoreMatchers.*;

import org.hamcrest.CustomMatcher;
import org.hamcrest.MatcherAssert;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import com.vgerbot.test.orm.influxdb.intergration.dao.CensusDao;
import com.vgerbot.test.orm.influxdb.intergration.entity.CensusMeasurement;
import com.vgerbot.test.orm.influxdb.common.JSON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:ApplicationContext.xml")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InfluxDBORMTest {
	@Autowired
	private CensusDao dao;
	@Autowired
	private EnumTypeMeasurementDao etmDao;

	@Test
	public void test01AssertDaoValue() {
		assertNotNull(dao);
	}

	@Test
	public void test02SelectAll() {
		List<CensusMeasurement> results = dao.selectAll();
		assertFalse(CollectionUtils.isEmpty(results));
		System.out.println(JSON.stringify(results));
		/*
		 * [
		 * {"time":123,"location":"10","scientist":"lanstroth","butterflies":11,
		 * "honeybees":12},
		 * {"time":1500738046311,"location":"10","scientist":"lanstroth",
		 * "butterflies":12,"honeybees":12},
		 * {"time":1500740417231,"location":"10","scientist":"lanstroth",
		 * "butterflies":11,"honeybees":123},
		 * {"time":1500740425269,"location":"10","scientist":"\"lanstroth\"",
		 * "butterflies":11,"honeybees":123},
		 * {"time":1500740510595,"location":"10","scientist":"'lanstroth'",
		 * "butterflies":11,"honeybees":12} ]
		 */
	}

	@Test
	public void test03InfluxDBSelect_annotation_should_working_correctly() {
		List<CensusMeasurement> results = dao.selectByScientist("lanstroth");
		assertFalse(CollectionUtils.isEmpty(results));
		System.out.println(JSON.stringify(results));
	}
	@Test
	public void test04EnumType_measurement_field_should_work() {
		List<CensusMeasurementUsingEnum> lanstrothsData = etmDao.selectAll_lanstroths_data();
		assertThat(lanstrothsData, is(notNullValue()));
		assertThat(lanstrothsData.isEmpty(), is(false));
		assertThat(lanstrothsData.get(0).getScientist(),is(equalTo(CensusMeasurementUsingEnum.ScientistName.LANSTROTH)));
	}
}
