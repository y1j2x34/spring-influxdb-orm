package com.vgerbot.test.orm.influxdb.exec;

import com.vgerbot.orm.influxdb.annotations.InfluxDBSelect;
import com.vgerbot.orm.influxdb.binding.MapperMethod;
import com.vgerbot.orm.influxdb.binding.MethodSignature;
import com.vgerbot.orm.influxdb.exec.ExecuteExecutor;
import com.vgerbot.orm.influxdb.exec.SelectExecutor;
import com.vgerbot.orm.influxdb.param.ParameterValue;
import com.vgerbot.orm.influxdb.ql.InfluxQLMapper;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.NativeResultContext;
import com.vgerbot.orm.influxdb.result.ResultContext;
import com.vgerbot.orm.influxdb.result.WrapperResultContext;
import org.influxdb.InfluxDB;
import org.influxdb.dto.QueryResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.management.Query;
import java.awt.event.WindowFocusListener;
import java.lang.reflect.Array;
import java.util.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
public class SelectExecutorTest {
    @Mock
    private InfluxDB influxdb;
    @Mock
    private InfluxQLMapper mapper;
    @Spy
    private InfluxDBRepository repository = new InfluxDBRepository(influxdb, "test-db", new HashMap<>(), mapper);

    public interface QueryDao {
        @InfluxDBSelect("select * from users")
        public Map<String, Object> selectAllUsers();
    }


    @Test
    public void test() throws Exception {
        MethodSignature selectAllUsersMethodSignature = new MethodSignature(QueryDao.class, QueryDao.class.getMethod("selectAllUsers"));
        InfluxDBSelect selectAnnotation = selectAllUsersMethodSignature.getAnnotation(InfluxDBSelect.class);
        Map<String, ParameterValue> parameters = new HashMap<>();

        ResultContext mockResultContext = createMockResultContext(
                "users",
                Arrays.asList("name", "age", "nickname"),
                Arrays.asList(
                        Arrays.asList("Bruce Banner", "Tony Stark", "Natalia", "Stephen Strange"),
                        Arrays.asList(40, 40, 18, 40, 30),
                        Arrays.asList("Hulk", "Tony Stark", "Black Widow", "Doctor Strange")
                )
        );

        doReturn(mockResultContext).when(repository).query(selectAnnotation.value(), parameters);

        SelectExecutor executor = new SelectExecutor(repository, selectAnnotation);
        MapperMethod selectAllUsersMapperMethod = new MapperMethod(QueryDao.class, repository, selectAllUsersMethodSignature);

        executor.execute(selectAllUsersMapperMethod, parameters);

        verify(repository).query(selectAnnotation.value(), parameters);
    }
    private static ResultContext createMockResultContext(String name, List<String> columns, List<List<Object>> data) {
        QueryResult.Series series = new QueryResult.Series();
        series.setName(name);
        series.setColumns(columns);
        series.setValues(data);

        QueryResult.Result res = new QueryResult.Result();
        res.setSeries(Arrays.asList(series));
        res.setError(null);

        QueryResult mockResult = mock(QueryResult.class);
        when(mockResult.getResults()).thenReturn(Arrays.asList(res));
        when(mockResult.getError()).thenReturn(null);
        return new NativeResultContext(mockResult);
    }
}
