package com.vgerbot.test.orm.influxdb.testcase;

import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import com.vgerbot.test.orm.influxdb.dao.CensusDao;
import com.vgerbot.test.orm.influxdb.entity.CensusMeasurement;
import com.vgerbot.test.orm.influxdb.utils.JSON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:ApplicationContext.xml")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InfluxDBORMTest {
	@Autowired
	private CensusDao dao;

	@Test
	public void test01AssertDaoValue() {
		Assert.assertNotNull(dao);
	}

	@Test
	public void test02Query() {
		List<CensusMeasurement> results = dao.selectAll();
		Assert.assertFalse(CollectionUtils.isEmpty(results));
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
	public void test03Query() {
		List<CensusMeasurement> results = dao.selectByScientist("lanstroth");
		Assert.assertFalse(CollectionUtils.isEmpty(results));
		System.out.println(JSON.stringify(results));
	}
}
