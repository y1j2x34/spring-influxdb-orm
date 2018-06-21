package com.vgerbot.test.orm.influxdb.testcase;

import java.util.List;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.vgerbot.test.orm.influxdb.dao.CensusDao;
import com.vgerbot.test.orm.influxdb.entity.CensusMeasurement;

public class Main {
	public static void main(String[] args) {
		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml")) {
			context.refresh();
			CensusDao dao = context.getBean(CensusDao.class);
			List<CensusMeasurement> measurements = dao.selectByScientist("lanstroth");
			System.out.println(measurements);

			dao.deleteSeries("lastroth", 12);

			List<Map<String, Integer>> result = dao.selectButterflies("lanstroth", 10);
			System.out.println(result);

			dao.hello();

			List<Map<String, Object>> data = dao.findByScientist("lanstroth");
			System.out.println(data);
		}

	}
}
