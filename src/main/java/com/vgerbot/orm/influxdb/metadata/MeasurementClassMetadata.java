package com.vgerbot.orm.influxdb.metadata;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import com.vgerbot.orm.influxdb.InfluxDBException;
import com.vgerbot.orm.influxdb.annotations.FieldColumn;
import com.vgerbot.orm.influxdb.annotations.InfluxDBMeasurement;
import com.vgerbot.orm.influxdb.annotations.TagColumn;

public class MeasurementClassMetadata {

	private final String measurementName;
	private final String retentionPolicy;

	private final Class<? extends Serializable> measurementClass;
	private final PropertyDescriptor shardingFieldDescriptor;
	private final PropertyDescriptor timeFieldDescriptor;
	/**
	 * map<数据库名称, java字段名称>
	 */
	private final Map<String, String> tagColumnJavaFieldNameMap;
	/**
	 * map<数据库字段名称, java字段名称>
	 */
	private final Map<String, String> fieldColumnJavaFieldNameMap;
	/**
	 * map<数据库字段名称, PropertyDescriptor>
	 */
	private final Map<String, PropertyDescriptor> tagColumnDescriptors;
	/**
	 * map<数据库字段名称, PropertyDescriptor>
	 */
	private final Map<String, PropertyDescriptor> fieldColumnDescriptors;

	private boolean isJavaUtilDateTimeField;
	private boolean isNumberTimeField;
	private boolean isStringTimeField;
	private String[] datetimePatterns;
	private final int _hashcode;

	public MeasurementClassMetadata(Class<? extends Serializable> measurementClass, String defaultRetentionPolicy) {
		this.measurementClass = measurementClass;
		tagColumnJavaFieldNameMap = new HashMap<>();
		fieldColumnJavaFieldNameMap = new HashMap<>();
		tagColumnDescriptors = new HashMap<>();
		fieldColumnDescriptors = new HashMap<>();

		Map<String, PropertyDescriptor> descriptorsMap = descriptorsMap(measurementClass);

		timeFieldDescriptor = descriptorsMap.get("time");
		assertTimeFieldDescriptorNotNull();

		Class<?> timeFieldType = timeFieldDescriptor.getPropertyType();

		isJavaUtilDateTimeField = Date.class.isAssignableFrom(timeFieldType);
		isNumberTimeField = Number.class.isAssignableFrom(timeFieldType);
		isStringTimeField = CharSequence.class.isAssignableFrom(timeFieldType);

		if (!isJavaUtilDateTimeField && !isNumberTimeField && !isStringTimeField) {
			throw new InfluxDBException("Unsupported time field type: " + timeFieldType);
		}

		InfluxDBMeasurement annotation = measurementClass.getAnnotation(InfluxDBMeasurement.class);
		assertMeasurementAnnotationNotNull(annotation);

		this.measurementName = annotation.value();
		this.retentionPolicy = StringUtils.isBlank(annotation.retentionPolicy()) ? defaultRetentionPolicy : annotation.retentionPolicy();
		this.shardingFieldDescriptor = descriptorsMap.get(annotation.shardingField());

		findFields(descriptorsMap);
		findTags(descriptorsMap);
		_hashcode = hash();
	}

	private void assertTimeFieldDescriptorNotNull() {
		if (timeFieldDescriptor == null) {
			throw new InfluxDBException("Invalid measurement class: time field not found!");
		}
	}

	private void assertMeasurementAnnotationNotNull(InfluxDBMeasurement annotation) {
		if (annotation == null) {
			throw new InfluxDBException("Invalid measurement class: must annotated by @InfluxDBMeasurement");
		}
	}

	private void findTags(Map<String, PropertyDescriptor> descriptorsMap) {

		for (Map.Entry<String, PropertyDescriptor> entry : descriptorsMap.entrySet()) {

			PropertyDescriptor descriptor = entry.getValue();
			String fieldName = descriptor.getName();
			Field field = ReflectionUtils.findField(this.measurementClass, fieldName);

			TagColumn tagAnnotation = field.getAnnotation(TagColumn.class);

			if (tagAnnotation != null) {
				tagColumnJavaFieldNameMap.put(tagAnnotation.value(), fieldName);
				tagColumnDescriptors.put(fieldName, descriptor);
			}
		}
	}

	private void findFields(Map<String, PropertyDescriptor> descriptorsMap) {

		for (Map.Entry<String, PropertyDescriptor> entry : descriptorsMap.entrySet()) {

			PropertyDescriptor descriptor = entry.getValue();
			String fieldName = descriptor.getName();
			Field field = ReflectionUtils.findField(this.measurementClass, fieldName);

			FieldColumn fieldAnnotation = field.getAnnotation(FieldColumn.class);

			if (fieldAnnotation != null) {
				fieldColumnJavaFieldNameMap.put(fieldAnnotation.value(), fieldName);
				fieldColumnDescriptors.put(fieldName, descriptor);
			}
		}
	}

	private static final Map<String, PropertyDescriptor> descriptorsMap(Class<?> measurementClass) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(measurementClass);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			Map<String, PropertyDescriptor> descriptorsMap = new HashMap<>();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				String name = propertyDescriptors[i].getName();
				descriptorsMap.put(name, propertyDescriptors[i]);
			}
			descriptorsMap.remove("class");
			return descriptorsMap;
		} catch (IntrospectionException e) {
			throw new InfluxDBException(e);
		}
	}

	public boolean isJavaUtilDateTimeField() {
		return isJavaUtilDateTimeField;
	}

	public boolean isNumberTimeField() {
		return isNumberTimeField;
	}

	public boolean isStringTimeField() {
		return isStringTimeField;
	}

	public String[] getDatetimePatterns() {
		return datetimePatterns;
	}

	public Class<? extends Serializable> getMeasurementClass() {
		return measurementClass;
	}

	public String getMeasurementName() {
		return measurementName;
	}

	public String getRetentionPolicy() {
		return retentionPolicy;
	}

	public PropertyDescriptor getShardingFieldDescriptor() {
		return shardingFieldDescriptor;
	}

	public Map<String, String> getTagColumnJavaFieldNameMap() {
		return tagColumnJavaFieldNameMap;
	}

	public Map<String, String> getFieldColumnJavaFieldNameMap() {
		return fieldColumnJavaFieldNameMap;
	}

	public PropertyDescriptor getTimeFieldDescriptor() {
		return timeFieldDescriptor;
	}

	public Map<String, PropertyDescriptor> getTagColumnDescriptors() {
		return tagColumnDescriptors;
	}

	public Map<String, PropertyDescriptor> getFieldColumnDescriptors() {
		return fieldColumnDescriptors;
	}

	private int hash() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((measurementClass == null) ? 0 : measurementClass.hashCode());
		result = prime * result + ((measurementName == null) ? 0 : measurementName.hashCode());
		result = prime * result + ((retentionPolicy == null) ? 0 : retentionPolicy.hashCode());
		return result;
	}

	@Override
	public int hashCode() {
		return _hashcode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MeasurementClassMetadata other = (MeasurementClassMetadata) obj;
		if (measurementClass == null) {
			if (other.measurementClass != null)
				return false;
		} else if (!measurementClass.equals(other.measurementClass))
			return false;
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
