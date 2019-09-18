package com.vgerbot.test.orm.influxdb.common;

import com.vgerbot.orm.influxdb.param.ParameterSignature;
import com.vgerbot.orm.influxdb.param.ParameterValue;

import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockParameterMap extends HashMap<String, ParameterValue> {
    public <T> ParameterValue putParameter(int index, String name, Class<T> type, T value) {
        ParameterSignature signature = createMockParameterSignature(index, name, type);
        ParameterValue parameterValue = new ParameterValue(value, signature);
        return this.put(name, parameterValue);
    }
    private static ParameterSignature createMockParameterSignature(int index, String name, Class<?> type) {
        ParameterSignature signature = mock(ParameterSignature.class);
        when(signature.getIndex()).thenReturn(index);
        when(signature.getName()).thenReturn(name);
        when((Object)signature.getType()).thenReturn(type);
        return signature;
    }
}
