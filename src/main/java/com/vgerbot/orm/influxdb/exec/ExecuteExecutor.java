package com.vgerbot.orm.influxdb.exec;

import java.util.Map;

import com.vgerbot.orm.influxdb.annotations.InfluxDBExecute;
import com.vgerbot.orm.influxdb.binding.MapperMethod;
import com.vgerbot.orm.influxdb.param.ParameterValue;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.ResultContext;
import com.vgerbot.orm.influxdb.utils.CommandUtils;

public class ExecuteExecutor extends AnnotationExecutor<InfluxDBExecute> {

	public ExecuteExecutor(InfluxDBRepository repository, InfluxDBExecute annotation) {
		super(repository, annotation);
	}

	@Override
	public ResultContext execute(MapperMethod method, Map<String, ParameterValue> parameters) {
		String command = super.annotation.value();
		String parsedCommand = CommandUtils.parseCommand(command, parameters);
		repository.execute(parsedCommand);
		return ResultContext.VOID;
	}

}
