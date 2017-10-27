package com.vgerbot.orm.influxdb.utils;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.vgerbot.orm.influxdb.param.ParameterValue;
import com.vgerbot.orm.influxdb.utils.StringUtils.ReplaceCallback;

public class CommandUtils {
	public static String parseCommand(String command, final Map<String, ParameterValue> parameters) {
		command = StringUtils.replace(command, "\\$\\{([^\\}]+)\\}", 1, new ReplaceCallback() {
			@Override
			public String replace(String word, int index, String source) {
				ParameterValue parameterValue = parameters.get(word);
				if (parameterValue == null || parameterValue.getValue() == null) {
					return "";
				}
				Object value = parameterValue.getValue();
				return value.toString();
			}
		});
		command = StringUtils.replace(command, "\\#\\{([^\\}]+)\\}", 1, new ReplaceCallback() {
			@Override
			public String replace(String word, int index, String source) {
				ParameterValue parameterValue = parameters.get(word);
				if (parameterValue == null) {
					return "";
				}
				Object value = parameterValue.getValue();
				if (value == null) {
					return "null";
				}
				Class<?> type = parameterValue.getSignature().getType();
				if (CharSequence.class.isAssignableFrom(type)) {
					return "'" + value + "'";
				} else if (Date.class.isInstance(value)) {
					return Long.toString(TimeUnit.MILLISECONDS.toNanos(((Date) value).getTime()), 10);
				}
				return value.toString();
			}
		});
		return command;
	}
}
