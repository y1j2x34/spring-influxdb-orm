package com.vgerbot.test.orm.influxdb.utils;

import com.vgerbot.orm.influxdb.param.ParameterSignature;
import com.vgerbot.orm.influxdb.param.ParameterValue;
import com.vgerbot.orm.influxdb.utils.CommandUtils;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommandUtilsTest {
    private static final Date CONST_BIRTHDAY = Date.from(Instant.parse("2019-09-14T16:26:41.00Z"));
    private Map<String, ParameterValue> parameterValues = new HashMap<String, ParameterValue>() {{
        this.put("nickname", createParameterValue("nickname", String.class, "mario"));
        this.put("age", createParameterValue("age", Integer.class, 18));
        this.put("birthday", createParameterValue("birthday", Date.class, CONST_BIRTHDAY));
        this.put("null-value", createParameterValue("null-value", Object.class, null));
    }};

    @Test
    public void shouldReplaceToEmptyStringIfPlaceholderNotInParameterValuesMap(){
        String parsedCommand = CommandUtils.parseCommand("select * from table_name where nickname=${invalid_field}", parameterValues);
        assertThat(parsedCommand, equalTo("select * from table_name where nickname="));
    }
    @Test
    public void shouldReplaceToEmptyStringIfValueOfPlaceholderIsNull() {
        String parsedCommand = CommandUtils.parseCommand("select * from table_name where nv=${null-value}", parameterValues);
        assertThat(parsedCommand, equalTo("select * from table_name where nv="));
    }
    @Test
    public void shouldReplacePlaceholdersCorrect() {
        String parsedCommand = CommandUtils.parseCommand("select * from table_name where nv=${nickname} and age=${age}", parameterValues);
        assertThat(parsedCommand, equalTo("select * from table_name where nv=mario and age=18"));
    }

    @Test
    public void shouldReplaceCharSequenceRawValueToStringWithAPairOfQuotes() {
        String parsedCommand = CommandUtils.parseCommand("select * from table_name where nv=#{nickname}", parameterValues);
        assertThat(parsedCommand, equalTo("select * from table_name where nv='mario'"));
    }
    @Test
    public void shouldReplaceDateObjectToNanosNumeric() {
        String parsedCommand = CommandUtils.parseCommand("#{birthday}", parameterValues);
        assertThat(parsedCommand, equalTo(Long.toString(TimeUnit.MILLISECONDS.toNanos(CONST_BIRTHDAY.getTime()),10)));
    }

    private static <T> Parameter createParameter(String name, Class<T> type) {
        Parameter parameter = mock(Parameter.class);
        when(parameter.getName()).thenReturn(name);
        when((Object)parameter.getType()).thenReturn((Object)type);
        return parameter;
    }
    private static <T> ParameterValue createParameterValue(String name, Class<T> type, T value) {
        Parameter parameter = createParameter(name, type);
        return new ParameterValue(value, ParameterSignature.signature(0, parameter));
    }
}
