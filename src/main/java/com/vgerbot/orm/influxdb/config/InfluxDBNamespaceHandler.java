package com.vgerbot.orm.influxdb.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class InfluxDBNamespaceHandler extends NamespaceHandlerSupport {
	public static final String NAMESPACE = "http://www.vgerbot.com/schema/influxdb";
	public static final String DATASOURCE_ELEMENT = "datasource";
	public static final String DATASOURCE_SCHEME_ATTRIBUTE = "scheme";
	public static final String DATASOURCE_HOST_ATTRIBUTE = "host";
	public static final String DATASOURCE_PORT_ATTRIBUTE = "port";
	public static final String DATASOURCE_USERNAME_ATTRIBUTE = "username";
	public static final String DATASOURCE_PASSWORD_ATTRIBUTE = "password";
	public static final String DATASOURCE_DATABASE = "database";

	public static final String CONFIG_ELEMENT = "config";
	public static final String CONFIG_ENABLE_GZIP_ATTRIBUTE = "enable-gzip";
	public static final String CONFIG_ENABLE_BATCH_ATTRIBUTE = "enable-batch";
	public static final String CONFIG_BATCH_ACTIONS_ATTRIBUTE = "batch-actions";
	public static final String CONFIG_BATCH_FLUSH_DURATION_ATTRIBUTE = "batch-flush-duration";

	public static final String CONFIG_HTTP_CONNECT_TIME_OUT = "http-connect-timeout";
	public static final String CONFIG_HTTP_READ_TIME_OUT = "http-read-timeout";
	public static final String CONFIG_HTTP_WRITE_TIME_OUT = "http-write-timeout";
	public static final String CONFIG_ENABLE_GZIP = "enable-gzip";

	public static final String DAO_BASE_PACKAGE_ATTRIBUTE = "dao-base-package";
	public static final String MEASUREMENT_BASE_PACKAGE_ATTRIBUTE = "entity-base-package";

	public static final String INFLUX_QL_ELEMENT = "influxql";
	public static final String INFLUX_QL_ATTR_PATH = "path";

	@Override
	public void init() {
		registerBeanDefinitionParser("mapper", new InfluxDBBeanDefinitionParser());
	}

}
