package com.vgerbot.orm.influxdb.result.resolver;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vgerbot.orm.influxdb.InfluxDBException;
import com.vgerbot.orm.influxdb.annotations.SelectKey;
import com.vgerbot.orm.influxdb.binding.MethodSignature;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.NativeResultContext;
import com.vgerbot.orm.influxdb.result.ResultContext;
import com.vgerbot.orm.influxdb.result.ResultResolver;

public class NativeResultResolver implements ResultResolver<Object> {

	@Override
	public boolean support(ResultContext context, MethodSignature methodSignature) {
		return context instanceof NativeResultContext;
	}

	@Override
	public Object resolve(ResultContext context, MethodSignature methodSignature, InfluxDBRepository repository) {
		@SuppressWarnings("unchecked")
		Map<String, List<Map<String, Object>>> value = (Map<String, List<Map<String, Object>>>) context.getValue();

		Class<?> returnType = methodSignature.getReturnType();
		Class<?> impCls = getImplClass(returnType);
		SelectKey selectKeyAnnotation = methodSignature.getAnnotation(SelectKey.class);
		String selectKey = selectKeyAnnotation == null ? null : selectKeyAnnotation.value();
		Collection<List<Map<String, Object>>> values = value.values();

		// XXX 现在使用正则查询只支持返回第一个measurement数据
		List<Map<String, Object>> firstMeasurementList;
		if (values.isEmpty()) {
			firstMeasurementList = new ArrayList<>(0);
		} else {
			firstMeasurementList = value.values().iterator().next();
		}
		int size = firstMeasurementList.size();

		if (impCls.isArray()) {
			return transformToArray(repository, impCls, firstMeasurementList, size, selectKey);
		} else if (Collection.class.isAssignableFrom(impCls)) {
			return transformToCollection(methodSignature, repository, impCls, firstMeasurementList, size, selectKey);
		} else if (Map.class.isAssignableFrom(impCls)) {
			if (size == 1) {
				return firstMeasurementList.get(0);
			} else if (size > 1) {
				throw new InfluxDBException("Too many results! expected 1, actual:" + size);
			}
			return null;
		} else if (selectKey != null) {
			Map<String, Object> firstResult;
			if (size == 1) {
				firstResult = firstMeasurementList.get(0);
			} else if (size > 1) {
				throw new InfluxDBException("Too many results! expected 1, actual:" + size);
			} else {
				return null;
			}
			Object valueOfKey = firstResult.get(selectKey);
			return repository.convert(valueOfKey, returnType);
		}
		return null;
	}

	private Collection<Object> transformToCollection(MethodSignature methodSignature, InfluxDBRepository repository, Class<?> impCls,
			List<Map<String, Object>> firstMeasurementList, int size, String selectKey) {
		Type genericReturnType = methodSignature.getMethod().getGenericReturnType();
		ParameterizedType retType = (ParameterizedType) genericReturnType;
		Type arg0Type = retType.getActualTypeArguments()[0];

		Class<?> componentType;
		if (arg0Type instanceof Class) {
			componentType = getImplClass((Class<?>) arg0Type);
		} else {
			ParameterizedType argType = (ParameterizedType) arg0Type;
			componentType = getImplClass((Class<?>) argType.getRawType());
		}

		@SuppressWarnings("unchecked")
		Collection<Object> collection = (Collection<Object>) instantiate(impCls);
		if (size > 0) {
			if (selectKey != null) {
				for (int i = 0; i < size; i++) {
					Map<String, Object> map = firstMeasurementList.get(i);
					collection.add(repository.convert(map.get(selectKey), componentType));
				}
			} else {
				for (int i = 0; i < size; i++) {
					Map<String, Object> map = firstMeasurementList.get(i);
					collection.add(repository.convert(map, componentType));
				}
			}
		}
		return collection;
	}

	private Object transformToArray(InfluxDBRepository repository, Class<?> impCls, List<Map<String, Object>> firstMeasurementList,
			int size, String selectKey) {
		Class<?> componentType = getImplClass(impCls.getComponentType());
		Object arr = Array.newInstance(componentType, size);
		if (size > 0) {
			if (selectKey != null) {
				for (int i = 0; i < size; i++) {
					Map<String, Object> map = firstMeasurementList.get(i);
					Object bean = repository.convert(map.get(selectKey), componentType);
					Array.set(arr, i, bean);
				}
			} else {
				for (int i = 0; i < size; i++) {
					Map<String, Object> map = firstMeasurementList.get(i);
					Object bean = repository.convert(map, componentType);
					Array.set(arr, i, bean);
				}
			}
		}
		return arr;
	}

	private <T> T instantiate(Class<T> type) {
		T bean;
		try {
			Constructor<T> constructor = type.getDeclaredConstructor();
			bean = constructor.newInstance();
		} catch (NoSuchMethodException | SecurityException e) {
			throw new InfluxDBException("No default constructor found for instantiate type: " + type);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new InfluxDBException("An error occurred while instantiating type: " + type);
		}
		return bean;
	}

	private Class<?> getImplClass(Class<?> type) {
		if (!Modifier.isAbstract(type.getModifiers())) {
			return type;
		}

		if (Collection.class == type || List.class == type || AbstractList.class == type) {
			return ArrayList.class;
		}
		if (Map.class == type || AbstractMap.class == type) {
			return HashMap.class;
		}
		throw new IllegalStateException("cannot resolve result type: " + type);
	}

	public static class A {
		private int num;

		public int getNum() {
			return num;
		}

		public void setNum(int num) {
			this.num = num;
		}

	}
}
