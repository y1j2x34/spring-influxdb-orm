package com.vgerbot.test.orm.influxdb.dao;

import java.util.List;
import java.util.Map;

import com.vgerbot.orm.influxdb.InfluxDBDao;
import com.vgerbot.orm.influxdb.annotations.InfluxDBExecute;
import com.vgerbot.orm.influxdb.annotations.InfluxDBParam;
import com.vgerbot.orm.influxdb.annotations.InfluxDBSelect;
import com.vgerbot.orm.influxdb.annotations.SpecifiedExecutor;
import com.vgerbot.orm.influxdb.binding.MapperMethod;
import com.vgerbot.orm.influxdb.exec.Executor;
import com.vgerbot.orm.influxdb.param.ParameterValue;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.ResultContext;
import com.vgerbot.test.orm.influxdb.entity.CensusMeasurement;

public interface CensusDao extends InfluxDBDao<CensusMeasurement> {

	@InfluxDBSelect("select * from census where scientist = #{scientist}")
	public List<CensusMeasurement> selectByScientist(@InfluxDBParam("scientist") String scientist);

	@InfluxDBExecute("DROP SERIES FROM census where scientist=#{scientist} and location=#{location}")
	public void deleteSeries( //
			@InfluxDBParam("scientist") String scientist, //
			@InfluxDBParam("location") Integer location //
	);

	@InfluxDBSelect("select scientist,location,butterflies from census where scientist = #{scientist}")
	public List<Map<String, Integer>> selectButterflies( //
			@InfluxDBParam("scientist") String scientist, //
			@InfluxDBParam("location") Integer location //
	);

	@SpecifiedExecutor(HelloWorldExecutor.class)
	public void hello();

	class HelloWorldExecutor extends Executor {

		public HelloWorldExecutor(InfluxDBRepository repository) {
			super(repository);
		}

		@Override
		public ResultContext execute(MapperMethod method, Map<String, ParameterValue> parameters) {
			System.out.println("hello world");
			return ResultContext.VOID;
		}
	}
}
