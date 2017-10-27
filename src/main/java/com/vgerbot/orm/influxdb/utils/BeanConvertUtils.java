package com.vgerbot.orm.influxdb.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.TypeConverter;

import com.vgerbot.orm.influxdb.InfluxDBException;
import com.vgerbot.orm.influxdb.metadata.MeasurementClassMetadata;

public class BeanConvertUtils {

	private static final ConcurrentMap<Class<?>, Constructor<?>> constructorCache = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public static <T> T convert(Map<String, Object> source, MeasurementClassMetadata metadata, TypeConverter convert) {
		Class<?> measurementClass = metadata.getMeasurementClass();

		T target = null;

		Constructor<T> constructor = (Constructor<T>) constructorOf(measurementClass);
		try {
			target = constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new InfluxDBException(e);
		}

		PropertyDescriptor timeFieldDescriptor = metadata.getTimeFieldDescriptor();
		copyTimeValue(source, convert, target, timeFieldDescriptor);

		Map<String, PropertyDescriptor> tagColumnDescriptors = metadata.getTagColumnDescriptors();
		Map<String, PropertyDescriptor> fieldColumnDescriptors = metadata.getFieldColumnDescriptors();
		copyDescriptors(source, convert, target, tagColumnDescriptors);
		copyDescriptors(source, convert, target, fieldColumnDescriptors);

		return target;
	}

	private static <T> void copyTimeValue(Map<String, Object> source, TypeConverter convert, T target,
			PropertyDescriptor timeFieldDescriptor) {
		Class<?> propertyType = timeFieldDescriptor.getPropertyType();
		Object timeValue = source.get(timeFieldDescriptor.getName());
		timeValue = convert.convertIfNecessary(timeValue, propertyType);
		writeValue(target, timeFieldDescriptor, timeFieldDescriptor.getWriteMethod(), timeValue);
	}

	private static <T> void copyDescriptors(Map<String, Object> source, TypeConverter convert, T target,
			Map<String, PropertyDescriptor> descriptorsMap) {
		for (Map.Entry<String, PropertyDescriptor> entry : descriptorsMap.entrySet()) {
			String dbFieldName = entry.getKey();
			PropertyDescriptor descriptor = entry.getValue();
			Class<?> propertyType = descriptor.getPropertyType();

			if (source.containsKey(dbFieldName)) {
				Object value = source.get(dbFieldName);
				if (value != null) {
					value = convert.convertIfNecessary(value, propertyType);
				} else if (propertyType.isPrimitive()) {
					continue;
				}
				writeValue(target, descriptor, descriptor.getWriteMethod(), value);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T convert(Map<String, Object> source, Class<T> targetType, TypeConverter converter) {
		if (Map.class.isAssignableFrom(targetType)) {
			Constructor<T> constructor = constructorOf(targetType);
			try {
				T instance = constructor.newInstance();
				((Map) instance).putAll(source);
				return instance;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new InfluxDBException(e);
			}
		}
		PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(targetType);
		T target = null;

		Constructor<T> constructor = constructorOf(targetType);
		try {
			target = constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new InfluxDBException(e);
		}

		for (PropertyDescriptor targetPd : targetPds) {
			String name = targetPd.getName();
			if ("class".equals(name)) {
				continue;
			}
			Method writeMethod = targetPd.getWriteMethod();
			Class<?> propertyType = targetPd.getPropertyType();
			if (writeMethod != null) {
				if (source.containsKey(name)) {
					Object value = source.get(name);
					if (value != null) {
						value = converter.convertIfNecessary(value, propertyType);
					} else if (propertyType.isPrimitive()) {
						continue;
					}
					writeValue(target, targetPd, writeMethod, value);
				}
			}
		}
		return target;
	}

	private static <T> void writeValue(T target, PropertyDescriptor targetPd, Method writeMethod, Object value) {
		try {
			if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
				writeMethod.setAccessible(true);
			}
			writeMethod.invoke(target, value);
		} catch (Throwable ex) {
			throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> Constructor<T> constructorOf(Class<T> targetType) {
		Constructor<T> constructor = (Constructor<T>) constructorCache.get(targetType);
		if (constructor != null) {
			return constructor;
		}
		try {
			constructor = targetType.getDeclaredConstructor();
			Constructor<?> previous = constructorCache.putIfAbsent(targetType, constructor);
			if (previous != null) {
				return (Constructor<T>) previous;
			}
			if (!Modifier.isPrivate(constructor.getModifiers())) {
				constructor.setAccessible(true);
			}
		} catch (NoSuchMethodException | SecurityException e) {
			throw new InfluxDBException(e);
		}
		return constructor;
	}
}
