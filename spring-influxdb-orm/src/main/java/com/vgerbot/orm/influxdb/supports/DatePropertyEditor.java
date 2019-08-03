package com.vgerbot.orm.influxdb.supports;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.Date;

import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;

public class DatePropertyEditor implements PropertyEditor {
	private Date date;

	@Override
	public void setValue(Object value) {
		if (value instanceof Number) {
			long timestamp = ((Number) value).longValue();
			date = new Date(InfluxDBRepository.DEFAULT_TIME_UNIT.toMillis(timestamp));
		} else if (value instanceof Date) {
			date = (Date) value;
		}
	}

	@Override
	public Object getValue() {
		return date;
	}

	@Override
	public boolean isPaintable() {
		return false;
	}

	@Override
	public void paintValue(Graphics gfx, Rectangle box) {

	}

	@Override
	public String getJavaInitializationString() {
		return null;
	}

	@Override
	public String getAsText() {
		return null;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		System.out.println(text);
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
