package com.vgerbot.orm.influxdb.exec;

import java.util.Map;

import com.vgerbot.orm.influxdb.annotations.InfluxDBSelect;
import com.vgerbot.orm.influxdb.binding.MapperMethod;
import com.vgerbot.orm.influxdb.param.ParameterValue;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.ResultContext;
import com.vgerbot.orm.influxdb.utils.CommandUtils;

public class SelectExecutor extends AnnotationExecutor<InfluxDBSelect> {

	public SelectExecutor(InfluxDBRepository repository, InfluxDBSelect selectAnnotation) {
		super(repository, selectAnnotation);
	}

	@Override
	public ResultContext execute(MapperMethod method, Map<String, ParameterValue> parameters) {
		String command = super.annotation.value();
		String parsedCommand = CommandUtils.parseCommand(command, parameters);
		return repository.query(parsedCommand);
	}

}
