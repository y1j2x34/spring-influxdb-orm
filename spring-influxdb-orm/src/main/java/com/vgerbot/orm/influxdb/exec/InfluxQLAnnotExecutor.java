package com.vgerbot.orm.influxdb.exec;

import java.util.Map;

import org.springframework.util.StringUtils;

import com.vgerbot.orm.influxdb.InfluxDBException;
import com.vgerbot.orm.influxdb.annotations.InfluxQL;
import com.vgerbot.orm.influxdb.binding.MapperMethod;
import com.vgerbot.orm.influxdb.param.ParameterValue;
import com.vgerbot.orm.influxdb.ql.InfluxQLStatement;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.ResultContext;
import com.vgerbot.orm.influxdb.utils.CommandUtils;

public class InfluxQLAnnotExecutor extends AnnotationExecutor<InfluxQL> {

	public InfluxQLAnnotExecutor(InfluxDBRepository repository, InfluxQL annotation) {
		super(repository, annotation);
	}

	@Override
	public ResultContext execute(MapperMethod method, Map<String, ParameterValue> parameters) {
		String key = super.annotation.value();
		if (StringUtils.isEmpty(key)) {
			key = method.getMethodSignature().getMethod().getName();
		}
		InfluxQLStatement statement = this.repository.getStatement(key);
		if (statement == null) {
			throw new InfluxDBException("Statement not found: " + key);
		}
		String influxQL = CommandUtils.parseCommand(statement.getTemplate(), parameters);

		switch (statement.getAction()) {
		case SELECT:
			return this.repository.query(influxQL);
		case EXECUTE:
			this.repository.execute(influxQL);
		}
		return ResultContext.VOID;
	}

}
