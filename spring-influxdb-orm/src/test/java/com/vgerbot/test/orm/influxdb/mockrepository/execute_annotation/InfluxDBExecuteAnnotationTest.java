package com.vgerbot.test.orm.influxdb.mockrepository.execute_annotation;

import com.vgerbot.orm.influxdb.factory.InfluxDBRepositoryFactoryBean;
import com.vgerbot.orm.influxdb.param.ParameterValue;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.ResultContext;
import com.vgerbot.test.orm.influxdb.common.MockParameterMap;
import com.vgerbot.test.orm.influxdb.common.MockitoMatchers;
import com.vgerbot.test.orm.influxdb.mockrepository.MockInfluxDBRepositoryFactoryBean;
import com.vgerbot.test.orm.influxdb.mockrepository.execute_annotation.dao.TrafficInfoDao;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.HashMap;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-execute-application-context.xml")
public class InfluxDBExecuteAnnotationTest {
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
    private TrafficInfoDao dao;
    @Test
    public void shouldRepositoryExecuteBeCalled() {
        MockParameterMap parameterMap = new MockParameterMap() {{
            this.putParameter(0, "page", String.class, "http://www.vgerbot.com");
            this.putParameter(0, "arg0", String.class, "http://www.vgerbot.com");
            this.putParameter(1, "count", Integer.class, 10);
            this.putParameter(1, "arg1", Integer.class, 10);
        }};

        doNothing().when(generator.repository).execute(eq(TrafficInfoDao.EXECUTE_RECORD_TRAFIC_QL), MockitoMatchers.parameters(parameterMap));
        dao.recordTraffic("http://www.vgerbot.com", 10);
        verify(generator.repository).execute(eq(TrafficInfoDao.EXECUTE_RECORD_TRAFIC_QL), MockitoMatchers.parameters(parameterMap));
    }
}
