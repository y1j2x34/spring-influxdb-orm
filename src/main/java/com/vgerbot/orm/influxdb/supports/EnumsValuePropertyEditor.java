package com.vgerbot.orm.influxdb.supports;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;

import com.vgerbot.orm.influxdb.EnumType;

public class EnumsValuePropertyEditor implements PropertyEditor {
	private EnumType value;
	private final Class<? extends EnumType> enumType;

	public EnumsValuePropertyEditor(Class<? extends EnumType> enumType) {
		this.enumType = enumType;
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof EnumType) {
			this.value = (EnumType) value;
		} else {
			throw new IllegalArgumentException("not instanceof EnumsValue");
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
		Object value = Enum.valueOf((Class) this.enumType, text);
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
