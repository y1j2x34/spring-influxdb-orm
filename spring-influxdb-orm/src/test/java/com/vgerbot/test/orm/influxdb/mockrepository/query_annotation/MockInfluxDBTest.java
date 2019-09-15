package com.vgerbot.test.orm.influxdb.mockrepository.query_annotation;

import com.vgerbot.orm.influxdb.factory.InfluxDBRepositoryFactoryBean;
import com.vgerbot.orm.influxdb.param.ParameterSignature;
import com.vgerbot.orm.influxdb.param.ParameterValue;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.NativeResultContext;
import com.vgerbot.test.orm.influxdb.common.MockDatas;
import com.vgerbot.test.orm.influxdb.common.MockitoMatchers;
import com.vgerbot.test.orm.influxdb.mockrepository.MockInfluxDBRepositoryFactoryBean;
import com.vgerbot.test.orm.influxdb.mockrepository.query_annotation.dao.BandwithMonitorDao;
import com.vgerbot.test.orm.influxdb.mockrepository.query_annotation.entity.BandwithMonitorEntity;
import static org.hamcrest.CoreMatchers.*;
import org.influxdb.dto.QueryResult;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.mockito.ArgumentMatcher;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-query-application-context.xml")
public class MockInfluxDBTest {
    private static final class MockRepositoryGenerator implements MockInfluxDBRepositoryFactoryBean.RepositoryGenerator {

        @Mock
        private InfluxDBRepository repository;

        @Override
        public void setup() {
            MockitoAnnotations.initMocks(this);
        }

        @Override
        public InfluxDBRepository generate(InfluxDBRepositoryFactoryBean factoryBean) {
            return repository;
        }
    }
    private static final MockRepositoryGenerator generator = new MockRepositoryGenerator();
    @BeforeClass
    public static void prepareFactoryBean() {
        generator.setup();
        MockInfluxDBRepositoryFactoryBean.setGenerator(generator);
    }
    @Resource
    private BandwithMonitorDao dao;

    @Before
    public void setup() {
        Map<String, ParameterValue> parameters = new HashMap<String, ParameterValue>(){{
            ParameterValue value = mock(ParameterValue.class);
            ParameterSignature parameterSignature = createMockParameterSignature(0, "ipAddress", String.class);
            when(value.getSignature()).thenReturn(parameterSignature);
            when(value.getValue()).thenReturn("0.0.0.0");
            this.put("ipAddress", value);
            this.put("arg0", value);
        }};
        QueryResult result = MockDatas.createSingleMeasurementQueryResult(
                "bandwidth_monitor",
                Arrays.asList("ipAddress", "bandwidth"),
                Arrays.asList(
                        Arrays.asList("0.0.0.0", 12)
                )
        );

        when(
                generator
                        .repository
                        .query(eq(BandwithMonitorDao.SELECT_BANDWIDTH_BY_IP_ADDRESS), MockitoMatchers.parameters(parameters))
        ).thenReturn(new NativeResultContext(result));

    }
    @Test
    public void shouldRepositoryQueryBeCalled() {
        BandwithMonitorEntity mockResult = new BandwithMonitorEntity();
        when(
                generator
                        .repository
                        .convert(eq(new HashMap<String, Object>(){{
                            this.put("ipAddress", "0.0.0.0");
                            this.put("bandwidth", 12);
                        }}), refEq(BandwithMonitorEntity.class))
        ).thenReturn(mockResult);

        BandwithMonitorEntity result = dao.selectBandwidthByIpAddress("0.0.0.0");
        assertThat(result, equalTo(mockResult));
        verify(generator.repository).query(eq(BandwithMonitorDao.SELECT_BANDWIDTH_BY_IP_ADDRESS), any());
    }
    private static ParameterSignature createMockParameterSignature(int index, String name, Class<?> type) {
        ParameterSignature signature = mock(ParameterSignature.class);
        when(signature.getIndex()).thenReturn(index);
        when(signature.getName()).thenReturn(name);
        when((Object)signature.getType()).thenReturn(type);
        return signature;
    }
}
