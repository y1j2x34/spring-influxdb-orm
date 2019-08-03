package com.vgerbot.orm.influxdb.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.util.Assert;

public class ReflectionUtils extends org.springframework.util.ReflectionUtils {

	public static Class<?> getInterfaceGenericType(Class<?> clazz, int typeIndex) {
		Assert.notNull(clazz, "class is required");
		Assert.isTrue(typeIndex >= 0, "Negative index");

		Type[] genericInterfaces = clazz.getGenericInterfaces();
		if (genericInterfaces == null || genericInterfaces.length < 1) {
			return null;
		}

		ParameterizedType pType = (ParameterizedType) genericInterfaces[0];
		Type[] actualTypeArguments = pType.getActualTypeArguments();
		if (actualTypeArguments == null || actualTypeArguments.length < 1) {
			return null;
		}

		return (Class<?>) actualTypeArguments[typeIndex];
	}
}
