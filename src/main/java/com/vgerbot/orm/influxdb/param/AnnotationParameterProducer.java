package com.vgerbot.orm.influxdb.param;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vgerbot.orm.influxdb.annotations.InfluxDBParam;

public class AnnotationParameterProducer implements ParameterProducer {
	private final List<ParameterSignature> signatures;

	public AnnotationParameterProducer(Method method) {
		signatures = new ArrayList<>();

		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			InfluxDBParam param = parameter.getAnnotation(InfluxDBParam.class);
			if (param != null) {
				signatures.add(ParameterSignature.signature(i, parameter, param.value()));
			} else {
				signatures.add(ParameterSignature.signature(i, parameter, "p" + i));
			}
		}
	}

	@Override
	public Map<String, ParameterValue> produce(Object... arguments) {
		Map<String, ParameterValue> params = new HashMap<>(signatures.size());
		for (ParameterSignature signature : signatures) {
			params.put(signature.getName(), signature.value(arguments[signature.getIndex()]));
		}
		return params;
	}

}
