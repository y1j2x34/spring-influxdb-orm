package com.vgerbot.orm.influxdb.param;

import java.util.Map;

public interface ParameterProducer {
	Map<String, ParameterValue> produce(Object... arguments);
}
