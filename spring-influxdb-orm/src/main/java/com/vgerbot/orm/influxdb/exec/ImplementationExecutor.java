package com.vgerbot.orm.influxdb.exec;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.springframework.util.CollectionUtils;

import com.vgerbot.orm.influxdb.InfluxDBDao;
import com.vgerbot.orm.influxdb.InfluxDBException;
import com.vgerbot.orm.influxdb.binding.MapperMethod;
import com.vgerbot.orm.influxdb.binding.MethodSignature;
import com.vgerbot.orm.influxdb.metadata.MeasurementClassMetadata;
import com.vgerbot.orm.influxdb.param.ParameterValue;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.ResultContext;
import com.vgerbot.orm.influxdb.result.WrapperResultContext;

public class ImplementationExecutor extends Executor {
	private ConcurrentMap<MeasurementClassMetadata, InfluxDBDao<Serializable>> implementationCache = new ConcurrentHashMap<>();

	public ImplementationExecutor(InfluxDBRepository repository) {
		super(repository);
	}

	@Override
	public ResultContext execute(MapperMethod method, Map<String, ParameterValue> parameters) {
		MethodSignature methodSignature = method.getMethodSignature();
		Class<?> declaringClass = methodSignature.getMethod().getDeclaringClass();
		if (declaringClass == InfluxDBDao.class) {
			MeasurementClassMetadata classMetadata = repository.getClassMetadata(method.getMapperInterface().getMeasurementClass());
			InfluxDBDao<Serializable> influxDBDao = getInfluxDBDaoImpl(classMetadata);
			Object[] arguments = exportArguments(methodSignature, parameters);
			return executeImpl(methodSignature, influxDBDao, arguments);
		}
		throw new IllegalStateException("No instantiation found for method: " + methodSignature.getMethod());
	}

	private InfluxDBDao<Serializable> getInfluxDBDaoImpl(MeasurementClassMetadata classMetadata) {
		InfluxDBDao<Serializable> influxDBDao = implementationCache.get(classMetadata);
		if (influxDBDao == null) {
			influxDBDao = new InfluxDBDaoImpl(repository, classMetadata);
			InfluxDBDao<Serializable> previous = implementationCache.putIfAbsent(classMetadata, influxDBDao);
			if (previous != null) {
				influxDBDao = previous;
			}
		}
		return influxDBDao;
	}

	private ResultContext executeImpl(MethodSignature methodSignature, InfluxDBDao<Serializable> influxDBDao, Object[] arguments) {
		try {
			Object result = methodSignature.getMethod().invoke(influxDBDao, arguments);
			if (methodSignature.returnsVoid()) {
				return ResultContext.VOID;
			} else {
				return new WrapperResultContext(result);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new InfluxDBException(e);
		}
	}

	private static Object[] exportArguments(MethodSignature signature, Map<String, ParameterValue> parametersMap) {
		Parameter[] parametersArray = signature.getParameters();
		Object[] args = new Object[parametersArray.length];
		if (args.length > 0) {
			for (int i = 0; i < parametersArray.length; i++) {
				Parameter parameter = parametersArray[i];
				ParameterValue parameterValue = parametersMap.get(parameter.getName());
				args[i] = parameterValue.getValue();
			}
		}
		return args;
	}

	private static final class InfluxDBDaoImpl implements InfluxDBDao<Serializable> {
		private final InfluxDBRepository repository;
		private final MeasurementClassMetadata metadata;

		public InfluxDBDaoImpl(InfluxDBRepository repository, MeasurementClassMetadata metadata) {
			super();
			this.repository = repository;
			this.metadata = metadata;
		}

		@Override
		public void insert(Serializable measurement) {
			repository.insert(measurement);
		}

		@Override
		public void batchInsert(Collection<Serializable> measurements) {
			if (CollectionUtils.isEmpty(measurements)) {
				return;
			}
			final Collection<Object> converted = new ArrayList<>(measurements);
			repository.batchInsert(converted);
		}

		@Override
		public List<Serializable> selectAll() {
			String measurementName = metadata.getMeasurementName();
			String policy = metadata.getRetentionPolicy();
			ResultContext result = repository.query(String.format("select * from \"%s\".\"%s\"", policy, measurementName), Collections.emptyMap());
			return convertResult(result);
		}

		@Override
		public List<Serializable> select(Map<String, Object> tagCondition) {
			if (CollectionUtils.isEmpty(tagCondition)) {
				throw new IllegalArgumentException();
			}
			StringBuilder clause = buildTagsWhereClause(tagCondition);
			String whereClause = "where " + clause;

			String measurementName = metadata.getMeasurementName();
			String policy = metadata.getRetentionPolicy();
			ResultContext result = repository.query(String.format("select * from \"%s\".\"%s\" %s", policy, measurementName, whereClause), Collections.emptyMap());

			return convertResult(result);
		}

		@Override
		public List<Serializable> selectBetween(Date timeStart, Date timeEnd, Map<String, Object> tagCondition) {
			String whereClause = "";

			if (timeStart != null) {
				whereClause += String.format("time >= %d", TimeUnit.MILLISECONDS.toNanos(timeStart.getTime()));
			}
			if (timeEnd != null) {
				if (whereClause.length() > 0) {
					whereClause += " and ";
				}
				whereClause += String.format("time <= %d", TimeUnit.MILLISECONDS.toNanos(timeEnd.getTime()));
			}

			if (tagCondition != null && tagCondition.size() > 0) {
				StringBuilder clause = buildTagsWhereClause(tagCondition);
				if (clause.length() > 0) {
					if (!whereClause.isEmpty()) {
						whereClause += " and ";
					}
					whereClause += clause;
				}
			}

			if (!whereClause.isEmpty()) {
				whereClause = "where " + whereClause;
			}

			String measurementName = metadata.getMeasurementName();
			String policy = metadata.getRetentionPolicy();
			ResultContext result = repository.query(String.format("select * from \"%s\".\"%s\" %s", policy, measurementName, whereClause), Collections.emptyMap());

			return convertResult(result);
		}

		private StringBuilder buildTagsWhereClause(Map<String, Object> tagCondition) {
			StringBuilder clause = new StringBuilder();
			for (Map.Entry<String, Object> entry : tagCondition.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				String converted = repository.convert(value, String.class);
				if (value != null && converted == null) {
					throw new InfluxDBException("cannot convert type \"" + value.getClass() + "\" to String");
				}
				clause.append(key) //
						.append(" = '") //
						.append(value) //
						.append("' and ");
			}
			if (clause.length() > 0) {
				clause.delete(clause.length() - 5, clause.length());
			}
			return clause;
		}

		@Override
		public List<Serializable> selectAfter(Date time, Map<String, Object> tagCondition) {
			return selectBetween(time, null, tagCondition);
		}

		@Override
		public List<Serializable> selectBefore(Date time, Map<String, Object> tagCondition) {
			return selectBetween(null, time, tagCondition);
		}

		private List<Serializable> convertResult(ResultContext result) {
			// TODO 转换结果
			Object value = result.getValue();
			@SuppressWarnings("unchecked")
			Map<String, List<Map<String, Object>>> mapValue = (Map<String, List<Map<String, Object>>>) value;
			if (mapValue.isEmpty()) {
				return new ArrayList<>(0);
			}
			List<Map<String, Object>> valueList = mapValue.values().iterator().next();

			List<Serializable> resultList = new ArrayList<>(valueList.size());

			for (Map<String, Object> map : valueList) {
				Serializable val = repository.convert(map, metadata.getMeasurementClass());
				resultList.add(val);
			}

			return resultList;
		}
	}
}
