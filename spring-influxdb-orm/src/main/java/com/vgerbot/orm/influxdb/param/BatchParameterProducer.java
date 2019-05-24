package com.vgerbot.orm.influxdb.param;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BatchParameterProducer implements ParameterProducer {

	private Collection<ParameterProducer> producers;

	public BatchParameterProducer() {
		producers = new LinkedList<>();
	}

	public BatchParameterProducer(ParameterProducer... producers) {
		if (producers != null) {
			this.producers = Arrays.asList(producers);
		} else {
			this.producers = new LinkedList<>();
		}
	}

	public BatchParameterProducer(Collection<ParameterProducer> producers) {
		if (producers != null) {
			this.producers = producers;
		} else {
			this.producers = new LinkedList<>();
		}
	}

	public void addProducer(ParameterProducer producer) {
		producers.add(producer);
	}

	@Override
	public Map<String, ParameterValue> produce(Object... arguments) {
		Map<String, ParameterValue> allParams = new HashMap<>();
		for (ParameterProducer producer : producers) {
			Map<String, ParameterValue> params = producer.produce(arguments);
			if (params != null) {
				allParams.putAll(params);
			}
		}
		return allParams;
	}

}
