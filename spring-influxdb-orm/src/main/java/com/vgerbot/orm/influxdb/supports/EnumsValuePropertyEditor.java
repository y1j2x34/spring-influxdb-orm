package com.vgerbot.orm.influxdb.supports;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

import com.vgerbot.orm.influxdb.EnumType;

public class EnumsValuePropertyEditor implements PropertyEditor {
	private EnumType value;
	private final Class<? extends EnumType> enumType;
	private final Map<Object, EnumType> enumValueMap;
	public EnumsValuePropertyEditor(Class<? extends EnumType> enumType) {
		this.enumType = enumType;
		EnumType[] constants = (EnumType[])enumType.getEnumConstants();
		enumValueMap = new HashMap<>(constants.length);
		for(EnumType constant: constants) {
			enumValueMap.put(constant.getValue(), constant);
		}
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof EnumType) {
			this.value = (EnumType) value;
		} else if(value instanceof CharSequence) {
			EnumType foundEnumValue = enumValueMap.get(value.toString());
			if(foundEnumValue != null) {
				this.value = foundEnumValue;
			} else {
				throw new IllegalArgumentException("enum constant not found: " + value);
			}
		} else {
			throw new IllegalArgumentException("not instanceof EnumsValue: " + value);
		}
	}

	@Override
	public Object getValue() {
		return this.value;
	}

	@Override
	public boolean isPaintable() {
		return false;
	}

	@Override
	public void paintValue(Graphics gfx, Rectangle box) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getJavaInitializationString() {
		return null;
	}

	@Override
	public String getAsText() {
		return value == null ? null : String.valueOf(value.getValue());
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setAsText(String text) throws IllegalArgumentException {
		if(!this.enumValueMap.containsKey(text)) {
			throw new IllegalArgumentException("No enum value \""+text+"\" found of " + enumType);
		}
		Object value = this.enumValueMap.get(text);
		this.value = (EnumType) value;
	}

	@Override
	public String[] getTags() {
		return null;
	}

	@Override
	public Component getCustomEditor() {
		return null;
	}

	@Override
	public boolean supportsCustomEditor() {
		return false;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {

	}

}
