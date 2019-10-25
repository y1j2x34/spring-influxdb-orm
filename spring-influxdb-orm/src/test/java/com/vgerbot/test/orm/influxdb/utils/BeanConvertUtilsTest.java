package com.vgerbot.test.orm.influxdb.utils;

import com.vgerbot.orm.influxdb.EnumType;
import com.vgerbot.orm.influxdb.annotations.FieldColumn;
import com.vgerbot.orm.influxdb.annotations.InfluxDBMeasurement;
import com.vgerbot.orm.influxdb.annotations.TagColumn;
import com.vgerbot.orm.influxdb.metadata.MeasurementClassMetadata;
import com.vgerbot.orm.influxdb.supports.DatePropertyEditor;
import com.vgerbot.orm.influxdb.supports.EnumsValuePropertyEditor;
import com.vgerbot.orm.influxdb.utils.BeanConvertUtils;
import org.junit.Test;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class BeanConvertUtilsTest {

    @Test
    public void convertByClass_shouldConvertWrappedPrimitivesCorrectly() {
        TypeConverter converter = new SimpleTypeConverter();
        Map<String, Object> map = new HashMap<>();

        final String str = "string value";
        final Byte byteValue = 12;
        final Short srt = 56;
        final Integer itg = 225;
        final Long lng = 1048576L;
        final Float flt = 3.14f;
        final Double dbl = 3.141592653d;
        map.put("str", str);
        map.put("bt", byteValue);
        map.put("srt", srt);
        map.put("itgr", itg);
        map.put("lng", lng);
        map.put("flt", flt);
        map.put("dbl", dbl);
        ClassWithWrappedPrimitiveFields converted = BeanConvertUtils.convert(map, ClassWithWrappedPrimitiveFields.class, converter);

        assertThat(converted.getStr(), is(equalTo(str)));
        assertThat(converted.getBt(), is(equalTo(byteValue)));
        assertThat(converted.getSrt(), is(equalTo(srt)));
        assertThat(converted.getItgr(), is(equalTo(itg)));
        assertThat(converted.getLng(), is(equalTo(lng)));
        assertThat(converted.getFlt(), is(equalTo(flt)));
        assertThat(converted.getDbl(), is(equalTo(dbl)));
    }
    @Test
    public void convertByClass_shouldConvertPrimitivesCorrectly() {
        TypeConverter converter = new SimpleTypeConverter();
        Map<String, Object> map = new HashMap<>();

        final byte byteValue = 12;
        final short srt = 56;
        final int itg = 225;
        final long lng = 1048576L;
        final float flt = 3.14f;
        final double dbl = 3.141592653d;
        map.put("bt", byteValue);
        map.put("srt", srt);
        map.put("itgr", itg);
        map.put("lng", lng);
        map.put("flt", flt);
        map.put("dbl", dbl);
        ClassWithPrimitiveFields converted = BeanConvertUtils.convert(map, ClassWithPrimitiveFields.class, converter);

        assertThat(converted.getBt(), is(equalTo(byteValue)));
        assertThat(converted.getSrt(), is(equalTo(srt)));
        assertThat(converted.getItgr(), is(equalTo(itg)));
        assertThat(converted.getLng(), is(equalTo(lng)));
        assertThat(converted.getFlt(), is(equalTo(flt)));
        assertThat(converted.getDbl(), is(equalTo(dbl)));
    }
    @Test
    public void convertByClass_shouldIgnoreNotExistFields() {
        TypeConverter converter = new SimpleTypeConverter();
        Map<String, Object> map = new HashMap<>();
        final String anyValue = "any value";
        map.put("field not exists", anyValue);

        ClassWithWrappedPrimitiveFields converted = BeanConvertUtils.convert(map, ClassWithWrappedPrimitiveFields.class, converter);
        assertThat(converted.getStr(), is(nullValue()));
        assertThat(converted.getBt(), is(nullValue()));
        assertThat(converted.getSrt(), is(nullValue()));
        assertThat(converted.getItgr(), is(nullValue()));
        assertThat(converted.getLng(), is(nullValue()));
        assertThat(converted.getFlt(), is(nullValue()));
        assertThat(converted.getDbl(), is(nullValue()));
    }
    @Test
    public void convertByClass_shouldDateConverterWorkCorrectly() {
        SimpleTypeConverter converter = new SimpleTypeConverter();
        converter.registerCustomEditor(Date.class, new DatePropertyEditor());
        Map<String, Object> map = new HashMap<>();
        Date now = new Date();
        map.put("time", now.getTime());

        ClassWithDateTypeFields converted = BeanConvertUtils.convert(map, ClassWithDateTypeFields.class, converter);
        assertThat(converted.getTime(), is(equalTo(now)));
    }
    @Test
    public void convertByClass_shouldEnumTypeConverterWorkCorrectly() {
        SimpleTypeConverter converter = new SimpleTypeConverter();
        converter.registerCustomEditor(ColorEnum.class, new EnumsValuePropertyEditor(ColorEnum.class));
        Map<String, Object> map = new HashMap<>();
        map.put("color", "#00FF00");

        ClassWithEnumTypeFields converted = BeanConvertUtils.convert(map, ClassWithEnumTypeFields.class, converter);

        assertThat(converted.getColor(), is(equalTo(ColorEnum.GREEN)));
    }

    @Test
    public void convertMeasurementMetadata_shouldWorkCorrectly() {
        MeasurementClassMetadata classMetadata = new MeasurementClassMetadata(SimpleMeasurementEntity.class, "");
        SimpleTypeConverter converter = new SimpleTypeConverter();
        converter.registerCustomEditor(Date.class, new DatePropertyEditor());

        Map<String, Object> map = new HashMap<>();
        final Date now = new Date();
        final String serverIP = "0.0.0.0";
        final Long bandwidth = 500 * 1024 * 1024L;
        final Long qps = 170000000L;

        map.put("time", now.getTime());
        map.put("serverIP", serverIP);
        map.put("bandwidth", bandwidth);
        map.put("qps", qps);

        Object converted = BeanConvertUtils.convert(map, classMetadata, converter);
        assertThat(converted, is(instanceOf(SimpleMeasurementEntity.class)));

        SimpleMeasurementEntity entity = (SimpleMeasurementEntity)converted;

        assertThat(entity.getTime(), is(equalTo(now)));
        assertThat(entity.getServerIP(), is(equalTo(serverIP)));
        assertThat(entity.getBandwidth(), is(equalTo(bandwidth)));
        assertThat(entity.getQps(), is(equalTo(qps)));
    }
    @InfluxDBMeasurement("casual")
    public static class SimpleMeasurementEntity implements Serializable {
        private Date time;
        @TagColumn("server-ip")
        private String serverIP;
        @FieldColumn("bandwidth")
        private Long bandwidth;
        @FieldColumn("qps")
        private Long qps;

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public String getServerIP() {
            return serverIP;
        }

        public void setServerIP(String serverIP) {
            this.serverIP = serverIP;
        }

        public Long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Long bandwidth) {
            this.bandwidth = bandwidth;
        }

        public Long getQps() {
            return qps;
        }

        public void setQps(Long qps) {
            this.qps = qps;
        }
    }
    public static class ClassWithEnumTypeFields {
        private ColorEnum color;

        public ColorEnum getColor() {
            return color;
        }

        public void setColor(ColorEnum color) {
            this.color = color;
        }
    }
    public enum ColorEnum implements EnumType {
        RED("#FF0000"), BLUE("#0000FF"), GREEN("#00FF00");
        private final String value;
        ColorEnum(String value){
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getDisplayName() {
            return this.name();
        }
    }
    public static final class ClassWithDateTypeFields {
        private Date time;

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }
    }
    public static final class ClassWithPrimitiveFields {
        private byte bt;
        private int itgr;
        private short srt;
        private long lng;
        private float flt;
        private double dbl;
        private boolean bool;

        public byte getBt() {
            return bt;
        }

        public void setBt(byte bt) {
            this.bt = bt;
        }

        public int getItgr() {
            return itgr;
        }

        public void setItgr(int itgr) {
            this.itgr = itgr;
        }

        public short getSrt() {
            return srt;
        }

        public void setSrt(short srt) {
            this.srt = srt;
        }

        public long getLng() {
            return lng;
        }

        public void setLng(long lng) {
            this.lng = lng;
        }

        public float getFlt() {
            return flt;
        }

        public void setFlt(float flt) {
            this.flt = flt;
        }

        public double getDbl() {
            return dbl;
        }

        public void setDbl(double dbl) {
            this.dbl = dbl;
        }

        public boolean isBool() {
            return bool;
        }

        public void setBool(boolean bool) {
            this.bool = bool;
        }
    }
    public static final class ClassWithWrappedPrimitiveFields {
        private String str;
        private Byte bt;
        private Integer itgr;
        private Short srt;
        private Long lng;
        private Float flt;
        private Double dbl;
        private Boolean bool;

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public Byte getBt() {
            return bt;
        }

        public void setBt(Byte bt) {
            this.bt = bt;
        }

        public Short getSrt() {
            return srt;
        }

        public void setSrt(Short srt) {
            this.srt = srt;
        }

        public Integer getItgr() {
            return itgr;
        }

        public void setItgr(Integer itgr) {
            this.itgr = itgr;
        }

        public Long getLng() {
            return lng;
        }

        public void setLng(Long lng) {
            this.lng = lng;
        }

        public Float getFlt() {
            return flt;
        }

        public void setFlt(Float flt) {
            this.flt = flt;
        }

        public Double getDbl() {
            return dbl;
        }

        public void setDbl(Double dbl) {
            this.dbl = dbl;
        }

        public Boolean getBool() {
            return bool;
        }

        public void setBool(Boolean bool) {
            this.bool = bool;
        }
    }
}
