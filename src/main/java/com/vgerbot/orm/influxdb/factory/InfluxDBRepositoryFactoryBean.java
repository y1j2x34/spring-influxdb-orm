package com.vgerbot.orm.influxdb.factory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.vgerbot.orm.influxdb.InfluxDBException;
import com.vgerbot.orm.influxdb.annotations.InfluxDBMeasurement;
import com.vgerbot.orm.influxdb.metadata.MeasurementClassMetadata;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.utils.ClasspathScanner;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;

public class InfluxDBRepositoryFactoryBean implements FactoryBean<InfluxDBRepository>, InitializingBean, EnvironmentAware {

	public static final String SCHEME_FIELD_NAME = "scheme";
	public static final String HOST_FIELD_NAME = "host";
	public static final String PORT_FIELD_NAME = "port";
	public static final String USERNAME_FIELD_NAME = "username";
	public static final String DATABASE_FIELD_NAME = "databaseName";
	public static final String PASSWORD_FIELD_NAME = "password";
	public static final String ENABLE_GZIP_FIELD_NAME = "enableGzip";
	public static final String ENABLE_BATCH_FIELD_NAME = "enableBatch";
	public static final String BATCH_ACTIONS_FIELD_NAME = "batchActions";
	public static final String BATCH_FLUSH_DURATION_FIELD_NAME = "batchFlushDuration";
	public static final String CONNECT_TIMEOUT_SECONDS = "connectTimeoutSeconds";
	public static final String READ_TIMEOUT_SECONDS = "readTimeoutSeconds";
	public static final String WRITE_TIMEOUT_SECONDS = "writeTimeoutSeconds";

	public static final String MEASUREMENT_PACKAGE = "entityPackage";

	private String scheme = "http";
	private String host = "127.0.0.1";
	private Integer port = 8086;
	private String username;
	private String password;

	private boolean enableGzip = false;

	private boolean enableBatch;
	private Integer batchActions;
	private Integer batchFlushDuration;

	private Long connectTimeoutSeconds = 10_000L;
	private Long readTimeoutSeconds = 10_000L;
	private Long writeTimeoutSeconds = 10_000L;
	private String databaseName;
	private String entityPackage;

	private Environment environment;

	private InfluxDBRepository repository;

	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() throws Exception {

		String url = String.format("%s://%s:%d", scheme, host, port);
		Builder builder = new OkHttpClient.Builder() //
				.connectTimeout(connectTimeoutSeconds, TimeUnit.MILLISECONDS) //
				.readTimeout(readTimeoutSeconds, TimeUnit.MILLISECONDS) //
				.writeTimeout(writeTimeoutSeconds, TimeUnit.MILLISECONDS);

		InfluxDB influxDB = InfluxDBFactory.connect(url, username, password, builder);

		if (enableBatch) {
			batchActions = (Integer) ObjectUtils.defaultIfNull(batchActions, 2000);
			batchFlushDuration = (Integer) ObjectUtils.defaultIfNull(batchFlushDuration, 100);
			influxDB.enableBatch(batchActions, batchFlushDuration, TimeUnit.MILLISECONDS);
		}

		if (enableGzip) {
			influxDB.enableGzip();
		} else if (influxDB.isGzipEnabled()) {
			influxDB.disableGzip();
		}

		List<String> exitingDatabases = influxDB.describeDatabases();
		if (!exitingDatabases.contains(databaseName)) {
			throw new InfluxDBException(String.format("database not exit: %s, datasource: %s", databaseName, url));
		}
		ClasspathScanner scanner = new ClasspathScanner(environment);
		scanner.addIncludeFilter(new AnnotationTypeFilter(InfluxDBMeasurement.class));
		scanner.addIncludeFilter(new AssignableTypeFilter(Serializable.class));

		Set<Class<?>> measurementClasses = scanner.scan(this.entityPackage);

		Map<String, MeasurementClassMetadata> metadatas = new HashMap<>(measurementClasses.size());

		for (Class<?> measurementClass : measurementClasses) {
			metadatas.put(measurementClass.getName(), new MeasurementClassMetadata((Class<? extends Serializable>) measurementClass));
		}

		repository = new InfluxDBRepository(influxDB, databaseName, metadatas);
	}

	public void setScheme(String scheme) {
		if (StringUtils.isNotBlank(scheme)) {
			this.scheme = scheme;
		}
	}

	public void setHost(String host) {
		if (StringUtils.isNotBlank(host)) {
			this.host = host;
		}
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setEnableGzip(boolean enableGzip) {
		this.enableGzip = enableGzip;
	}

	public void setEnableBatch(boolean enableBatch) {
		this.enableBatch = enableBatch;
	}

	public void setBatchActions(Integer batchActions) {
		this.batchActions = batchActions;
	}

	public void setBatchFlushDuration(Integer batchFlushDuration) {
		this.batchFlushDuration = batchFlushDuration;
	}

	public void setConnectTimeoutSeconds(Long connectTimeoutSeconds) {
		this.connectTimeoutSeconds = connectTimeoutSeconds;
	}

	public void setReadTimeoutSeconds(Long readTimeoutSeconds) {
		this.readTimeoutSeconds = readTimeoutSeconds;
	}

	public void setWriteTimeoutSeconds(Long writeTimeoutSeconds) {
		this.writeTimeoutSeconds = writeTimeoutSeconds;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setRepository(InfluxDBRepository repository) {
		this.repository = repository;
	}

	public void setEntityPackage(String entityPackage) {
		this.entityPackage = entityPackage;
	}

	@Override
	public InfluxDBRepository getObject() throws Exception {
		return repository;
	}

	@Override
	public Class<?> getObjectType() {
		return InfluxDBRepository.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
