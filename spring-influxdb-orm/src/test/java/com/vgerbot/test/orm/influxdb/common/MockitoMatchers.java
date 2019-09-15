package com.vgerbot.test.orm.influxdb.common;

import com.vgerbot.orm.influxdb.param.ParameterSignature;
import com.vgerbot.orm.influxdb.param.ParameterValue;
import org.apache.commons.lang3.StringUtils;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.mockito.internal.util.Primitives;

import java.util.Map;
import java.util.Objects;

public class MockitoMatchers {
    public static Map<String, ParameterValue> parameters(Map<String, ParameterValue> value) {
        return customize(
                new ParametersArgumentMatcher(value),
                value
        );
    }
    public static <T> T customize(ArgumentMatcher<T> matcher, T value) {
        ThreadSafeMockingProgress.mockingProgress().getArgumentMatcherStorage().reportMatcher(matcher);
        return value == null ? null : (T)Primitives.defaultValue(value.getClass());
    }
    private static class ParametersArgumentMatcher implements ArgumentMatcher<Map<String, ParameterValue>> {
        private Map<String, ParameterValue> excepted;

        public ParametersArgumentMatcher(Map<String, ParameterValue> excepted) {
            this.excepted = excepted;
        }

        @Override
        public boolean matches(Map<String, ParameterValue> other) {
            if (this.excepted.size() != other.size()) {
                return false;
            }
            for (Map.Entry<String, ParameterValue> entry : this.excepted.entrySet()) {
                ParameterValue otherValue = other.get(entry.getKey());
                if (!compare(entry.getValue(), otherValue)) {
                    return false;
                }
            }
            return true;
        }

        private boolean compare(ParameterValue a, ParameterValue b) {
            if (a == b) {
                return true;
            }
            if (a == null || b == null) {
                return false;
            }
            if (Objects.equals(a.getValue(), b.getValue())) {
                return true;
            }
            ParameterSignature signatureA = a.getSignature();
            ParameterSignature signatureB = b.getSignature();
            if (signatureA == signatureB) {
                return true;
            }
            if (signatureA == null || signatureB == null) {
                return false;
            }
            if (signatureA.getIndex() != signatureB.getIndex()) {
                return false;
            }
            if (!StringUtils.equals(signatureA.getName(), signatureB.getName())) {
                return false;
            }
            if (signatureA.getType() != signatureB.getType()) {
                return false;
            }
            return true;
        }
    }
}
