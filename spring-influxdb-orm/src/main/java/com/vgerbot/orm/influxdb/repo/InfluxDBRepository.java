package com.vgerbot.orm.influxdb.repo;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vgerbot.orm.influxdb.param.ParameterValue;
import com.vgerbot.orm.influxdb.utils.CommandUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverterSupport;
import org.springframework.util.CollectionUtils;

import com.vgerbot.orm.influxdb.EnumType;
import com.vgerbot.orm.influxdb.InfluxDBException;
import com.vgerbot.orm.influxdb.metadata.MeasurementClassMetadata;
import com.vgerbot.orm.influxdb.ql.InfluxQLMapper;
import com.vgerbot.orm.influxdb.ql.InfluxQLStatement;
import com.vgerbot.orm.influxdb.result.NativeResultContext;
import com.vgerbot.orm.influxdb.result.ResultContext;
import com.vgerbot.orm.influxdb.supports.DatePropertyEditor;
import com.vgerbot.orm.influxdb.supports.EnumsValuePropertyEditor;
import com.vgerbot.orm.influxdb.utils.BeanConvertUtils;

public class InfluxDBRepository {

	private static final Logger logger = Logger.getLogger(InfluxDBRepository.class.getCanonicalName());

	public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;

	private final InfluxDB influxDB;
	private final String databaseName;

	/**
	 * map<classname, metadata>
	 */
	private final Map<String, MeasurementClassMetadata> classmetadata;
	/**
	 * map<key{measurementName, retentionPolicy}, metadata>
	 */
	private final Map<MeasurementKey, MeasurementClassMetadata> classmetadataMapByKey;

	private final TypeConverterSupport typeConverter;

	private final InfluxQLMapper qlMapper;

	public InfluxDBRepository(final InfluxDB influxDB, final String databaseName, final Map<String, MeasurementClassMetadata> classmetadata,
			InfluxQLMapper qlMapper) {
		super();
		this.influxDB = influxDB;
		this.databaseName = databaseName;
		this.classmetadata = classmetadata;
		this.qlMapper = qlMapper;
		this.classmetadataMapByKey = new HashMap<>();
		this.typeConverter = new SimpleTypeConverter();

		this.typeConverter.registerCustomEditor(Date.class, new DatePropertyEditor());

		transform(classmetadata, classmetadataMapByKey);
	}

	public void execute(String command, final Map<String, ParameterValue> parameters) {
		command = CommandUtils.parseCommand(command, parameters);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("execute → " + command);
		}
		final Query query = new Query(command, databaseName);
		influxDB.query(query, DEFAULT_TIME_UNIT);
	}

	public ResultContext query(String command, final Map<String, ParameterValue> parameters) {
		command = CommandUtils.parseCommand(command, parameters);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("query → " + command);
		}
		final Query query = new Query(command, databaseName);
		final QueryResult queryResult = influxDB.query(query, DEFAULT_TIME_UNIT);
		return new NativeResultContext(queryResult);
	}

	public void insert(final Object entity) {
		if (entity == null) {
			return;
		}
		final String entityClassName = entity.getClass().getName();
		final MeasurementClassMetadata classMetadata = getClassMetadata(entityClassName);
		if (classMetadata == null) {
			throw new InfluxDBException("Class not mapped: " + entityClassName);
		}
		final Point point = buildPoint(entity, classMetadata);
		influxDB.write(databaseName, classMetadata.getRetentionPolicy(), point);
	}

	public void batchInsert(final Collection<Object> entities) {
		if (CollectionUtils.isEmpty(entities)) {
			return;
		}
		final Map<MeasurementKey, Collection<Object>> entitiesGrouped = new HashMap<>();
		for (final Object entity : entities) {
			if (entity == null) {
				return;
			}
			final String entityClassName = entity.getClass().getName();
			final MeasurementClassMetadata classMetadata = getClassMetadata(entityClassName);
			if (classMetadata == null) {
				throw new InfluxDBException("Class not mapped: " + entityClassName);
			}
			final MeasurementKey key = new MeasurementKey(classMetadata.getMeasurementName(), classMetadata.getRetentionPolicy());

			Collection<Object> gEntities = entitiesGrouped.get(key);
			if (gEntities == null) {
				gEntities = new LinkedList<>();
				entitiesGrouped.put(key, gEntities);
			}
			gEntities.add(entity);
		}
		for (final Map.Entry<MeasurementKey, Collection<Object>> entry : entitiesGrouped.entrySet()) {
			batchInsert(entry.getKey(), entry.getValue());
		}
	}

	private static void transform(final Map<String, MeasurementClassMetadata> classmetadata,
			final Map<MeasurementKey, MeasurementClassMetadata> classmetadataMapByKey) {
		for (final MeasurementClassMetadata metadata : classmetadata.values()) {
			classmetadataMapByKey.put(new MeasurementKey(metadata.getMeasurementName(), metadata.getRetentionPolicy()), metadata);
		}
	}

	private void batchInsert(final MeasurementKey key, final Collection<Object> entities) {
		if (CollectionUtils.isEmpty(entities)) {
			return;
		}
		final MeasurementClassMetadata classMetadata = getClassMetadata(key);

		final BatchPoints.Builder batchPointsBuilder = BatchPoints //
				.database(databaseName) //
				.retentionPolicy(key.retentionPolicy);
		for (final Object entity : entities) {
			if (entity == null) {
				return;
			}
			final Point point = buildPoint(entity, classMetadata);
			batchPointsBuilder.point(point);
		}
		influxDB.write(batchPointsBuilder.build());
	}

	private Point buildPoint(final Object entity, final MeasurementClassMetadata classMetadata) {
		final String measurementName = getShardingMeasurementName(classMetadata, entity);
		final Point.Builder pointBuilder = Point.measurement(measurementName);

		final Date time = readTimeField(classMetadata, entity);
		pointBuilder.time(DEFAULT_TIME_UNIT.toNanos(time.getTime()), TimeUnit.NANOSECONDS);

		resolveMeasurementTags(entity, classMetadata, pointBuilder);
		resolveMeasurementFields(entity, classMetadata, pointBuilder);
		final Point point = pointBuilder.build();
		return point;
	}

	private Date readTimeField(final MeasurementClassMetadata classMetadata, final Object entity) {
		final Method readMethod = classMetadata.getTimeFieldDescriptor().getReadMethod();
		try {
			final Object result = readMethod.invoke(entity);

			if (classMetadata.isJavaUtilDateTimeField()) {
				return (Date) result;
			} else if (classMetadata.isNumberTimeField()) {
				return new Date(((Number) result).longValue());
			} else if (classMetadata.isStringTimeField()) {
				return DateUtils.parseDate(result.toString(), classMetadata.getDatetimePatterns());
			}
			return null;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ParseException e) {
			throw new InfluxDBException("An error occurred while read time field value", e);
		}
	}

	private void resolveMeasurementFields(final Object entity, final MeasurementClassMetadata classMetadata, final Builder pointBuilder) {
		final Map<String, PropertyDescriptor> fieldColumnDescriptors = classMetadata.getFieldColumnDescriptors();
		Map<String, Object> fields = new HashMap<>(fieldColumnDescriptors.size());

		for (final Map.Entry<String, PropertyDescriptor> entry : fieldColumnDescriptors.entrySet()) {
			final String fieldName = entry.getKey();
			final PropertyDescriptor valueDescriptor = entry.getValue();
			final Method readMethod = valueDescriptor.getReadMethod();
			checkConvertableType(readMethod);
			final Object value = valueOfField(readMethod, entity);

			Class<?> fieldJavaType = readMethod.getReturnType();

			Object convertedValue = convertFieldValue(value, fieldJavaType);

			fields.put(fieldName, convertedValue);
		}
		pointBuilder.fields(fields);
	}

	private Object convertFieldValue(Object value, Class<?> fieldJavaType) {
		if (value == null) {
			return null;
		}
		Object convertedValue = null;
		if (fieldJavaType.isPrimitive()) {
			convertedValue = value;
		} else if (Number.class.isAssignableFrom(fieldJavaType)) {
			convertedValue = ((Number) value).doubleValue();
		} else {
			convertedValue = this.typeConverter.convertIfNecessary(value, String.class);
		}
		if (convertedValue == null && value != null) {
			throw new InfluxDBException("cannot convert type \"" + fieldJavaType + "\" to String");
		}
		return convertedValue;
	}

	private void resolveMeasurementTags(final Object entity, final MeasurementClassMetadata classMetadata, final Builder pointBuilder) {
		final Map<String, PropertyDescriptor> tagColumnDescriptors = classMetadata.getTagColumnDescriptors();
		for (final Map.Entry<String, PropertyDescriptor> entry : tagColumnDescriptors.entrySet()) {
			final String tagName = entry.getKey();
			final PropertyDescriptor valueDescriptor = entry.getValue();
			final Method readMethod = valueDescriptor.getReadMethod();
			checkConvertableType(readMethod);
			final Object value = valueOfTag(readMethod, entity);
			final String converted = this.typeConverter.convertIfNecessary(value, String.class);
			if (converted == null && value != null) {
				throw new InfluxDBException("cannot convert type \"" + readMethod.getReturnType() + "\" to String");
			}
			pointBuilder.tag(tagName, converted);
		}
	}

	private Object valueOfField(final Method readMethod, final Object entity) {
		try {
			return readMethod.invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new InfluxDBException("An error occorred while reading field value", e);
		}
	}

	private Object valueOfTag(final Method readMethod, final Object entity) {
		try {
			return readMethod.invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new InfluxDBException("An error occorred while reading tag field value", e);
		}
	}

	@SuppressWarnings("unchecked")
	private void checkConvertableType(final Method readMethod) {
		final Class<?> returnType = readMethod.getReturnType();
		if (!this.typeConverter.hasCustomEditorForElement(returnType, null) && EnumType.class.isAssignableFrom(returnType)) {
			this.typeConverter.registerCustomEditor(returnType, new EnumsValuePropertyEditor((Class<? extends EnumType>) returnType));
		}
	}

	public String getShardingMeasurementName(final MeasurementClassMetadata metadata, final Object entity) {
		final PropertyDescriptor shardingFieldDescriptor = metadata.getShardingFieldDescriptor();
		if (shardingFieldDescriptor == null) {
			return metadata.getMeasurementName();
		}
		final Method readMethod = shardingFieldDescriptor.getReadMethod();
		try {
			final Object value = readMethod.invoke(entity);
			return new StringBuilder() //
					.append(metadata.getMeasurementName()) //
					.append('_') //
					.append(value) //
					.toString();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new InfluxDBException("An error occurred while reading shard field value", e);
		}
	}

	public InfluxDB getInfluxDB() {
		return influxDB;
	}

	public InfluxQLStatement getStatement(String name) {
		return qlMapper.getStatement(name);
	}

	public MeasurementClassMetadata getClassMetadata(final String measurementClassName) {
		return classmetadata.get(measurementClassName);
	}

	public MeasurementClassMetadata getClassMetadata(final String measurementName, final String retentionPolicy) {
		return classmetadataMapByKey.get(new MeasurementKey(measurementName, retentionPolicy));
	}

	private MeasurementClassMetadata getClassMetadata(final MeasurementKey key) {
		return classmetadataMapByKey.get(key);
	}

	public MeasurementClassMetadata getClassMetadataByMeasurementName(final String measurementName) {
		return classmetadataMapByKey.get(new MeasurementKey(measurementName));
	}

	public MeasurementClassMetadata getClassMetadata(final Class<?> measurementClass) {
		return getClassMetadata(measurementClass.getName());
	}

	public Map<String, MeasurementClassMetadata> getAllClassMetadata() {
		return classmetadata;
	}

	public <T> T convert(Map<String, Object> map, Class<T> targetType) {
		MeasurementClassMetadata classMetadata = getClassMetadata(targetType);
		if (classMetadata != null) {
			return BeanConvertUtils.convert(map, classMetadata, this.typeConverter);
		}
		return BeanConvertUtils.convert(map, targetType, this.typeConverter);
	}

	public <T> T convert(Object source, Class<T> targetType) {
		return this.typeConverter.convertIfNecessary(source, targetType);
	}

	private static final class MeasurementKey {
		private final String measurementName;
		private final String retentionPolicy;
		private final int _hash;

		public MeasurementKey(final String measurementName, final String retentionPolicy) {
			super();
			this.measurementName = measurementName;
			this.retentionPolicy = retentionPolicy;
			this._hash = hash();
		}

		public MeasurementKey(final String measurementName) {
			this(measurementName, "autogen");
		}

		private int hash() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((measurementName == null) ? 0 : measurementName.hashCode());
			result = prime * result + ((retentionPolicy == null) ? 0 : retentionPolicy.hashCode());
			return result;
		}

		@Override
		public int hashCode() {
			return _hash;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final MeasurementKey other = (MeasurementKey) obj;
			if (measurementName == null) {
				if (other.measurementName != null)
					return false;
			} else if (!measurementName.equals(other.measurementName))
				return false;
			if (retentionPolicy == null) {
				if (other.retentionPolicy != null)
					return false;
			} else if (!retentionPolicy.equals(other.retentionPolicy))
				return false;
			return true;
		}

	}
}
