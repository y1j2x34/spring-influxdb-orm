package com.vgerbot.orm.influxdb.param;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class J8ParameterProducer implements ParameterProducer {
	List<ParameterSignature> signatures;

	public J8ParameterProducer(Method method) {
		signatures = ParameterSignature.signatures(method);
	}

	@Override
	public Map<String, ParameterValue> produce(Object... arguments) {
		Map<String, ParameterValue> params = new HashMap<>(signatures.size());
		for (ParameterSignature signature : signatures) {
			int index = signature.getIndex();
			params.put(signature.getName(), signature.value(arguments[index]));
		}
		return params;
	}

}
