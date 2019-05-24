package com.vgerbot.orm.influxdb.factory;

import com.vgerbot.orm.influxdb.InfluxDBException;
import com.vgerbot.orm.influxdb.metadata.MeasurementClassMetadata;
import com.vgerbot.orm.influxdb.ql.InfluxQLMapper;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class InfluxDBRepositoryFactoryBean implements FactoryBean<InfluxDBRepository>, InitializingBean {

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
	public static final String INFLUX_QL_RESOURCES = "influxQLResources";


	public static final String MEASUREMENT_SCANNER = "measurementScanner";

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
	private String[] influxQLResources;


	private InfluxDBRepository repository;
	private ClassPathMeasurementScanner measurementScanner;

	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() {

		String url = String.format("%s://%s:%d", scheme, host, port);
		Builder builder = new OkHttpClient.Builder() //
				.connectTimeout(connectTimeoutSeconds, TimeUnit.MILLISECONDS) //
				.readTimeout(readTimeoutSeconds, TimeUnit.MILLISECONDS) //
				.writeTimeout(writeTimeoutSeconds, TimeUnit.MILLISECONDS);

		InfluxDB influxDB = InfluxDBFactory.connect(url, username, password, builder);

		if (enableBatch) {
			batchActions = ObjectUtils.defaultIfNull(batchActions, 2000);
			batchFlushDuration = ObjectUtils.defaultIfNull(batchFlushDuration, 100);
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

		Set<Class<?>> measurementClasses = measurementScanner.scan();

		Map<String, MeasurementClassMetadata> metadatas = new HashMap<>(measurementClasses.size());

		String defaultRetentionPolicy = null;

		String influxDBVersion = influxDB.version();

		if ("1.0".compareTo(influxDBVersion) >= 0) {
			defaultRetentionPolicy = "default";
		} else {
			defaultRetentionPolicy = "autogen";
		}

		for (Class<?> measurementClass : measurementClasses) {
			metadatas.put(measurementClass.getName(),
					new MeasurementClassMetadata((Class<? extends Serializable>) measurementClass, defaultRetentionPolicy));
		}

		InfluxQLMapper mapper = InfluxQLMapper.empty();

		if (influxQLResources != null) {
			for (String resourceLocation : influxQLResources) {
				InfluxQLMapper parsed = InfluxQLMapper.parseFrom(resourceLocation);
				mapper = mapper.union(parsed);
			}
		}

		repository = new InfluxDBRepository(influxDB, databaseName, metadatas, mapper);
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

	public void setMeasurementScanner(ClassPathMeasurementScanner measurementScanner) {
		this.measurementScanner = measurementScanner;
	}

	public void setInfluxQLResources(String[] influxQLResources) {
		this.influxQLResources = influxQLResources;
	}

	public void setInfluxQLResource(String influxQLResource) {
		this.setInfluxQLResources(new String[] { influxQLResource });
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
}
