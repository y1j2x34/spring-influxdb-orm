package com.vgerbot.orm.influxdb.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vgerbot.orm.influxdb.factory.InfluxDBRepositoryFactoryBean;
import com.vgerbot.orm.influxdb.mapper.MapperScannerConfigurer;

public class InfluxDBBeanDefinitionParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {

		String repositoryName = parseRepository(element, parserContext);

		BeanDefinitionBuilder mscBuilder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);

		String daoBasePackage = element.getAttribute(InfluxDBNamespaceHandler.DAO_BASE_PACKAGE_ATTRIBUTE);

		mscBuilder.addPropertyValue(MapperScannerConfigurer.BASE_PACKAGE_FIELD, daoBasePackage);
		mscBuilder.addPropertyValue(MapperScannerConfigurer.REPOSITORY_NAME_FIELD, repositoryName);

		parserContext.getReaderContext().registerWithGeneratedName(mscBuilder.getBeanDefinition());

		return mscBuilder.getBeanDefinition();
	}

	private String parseRepository(Element element, ParserContext parserContext) {
		String measurementBasePackage = element.getAttribute(InfluxDBNamespaceHandler.MEASUREMENT_BASE_PACKAGE_ATTRIBUTE);

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(InfluxDBRepositoryFactoryBean.class);

		builder.addPropertyValue(InfluxDBRepositoryFactoryBean.MEASUREMENT_PACKAGE, measurementBasePackage);

		parseDatasource(builder, element, parserContext);

		parseConfig(builder, element, parserContext);

		return parserContext.getReaderContext().registerWithGeneratedName(builder.getBeanDefinition());
	}

	private void parseConfig(BeanDefinitionBuilder builder, Element mapperElement, ParserContext parserContext) {
		Element configElement = getChild(mapperElement, InfluxDBNamespaceHandler.CONFIG_ELEMENT);
		if (configElement == null) {
			return;
		}
		String enableBatch = configElement.getAttribute(InfluxDBNamespaceHandler.CONFIG_ENABLE_BATCH_ATTRIBUTE);
		String batchActions = configElement.getAttribute(InfluxDBNamespaceHandler.CONFIG_BATCH_ACTIONS_ATTRIBUTE);
		String batchFlushDuration = configElement.getAttribute(InfluxDBNamespaceHandler.CONFIG_BATCH_FLUSH_DURATION_ATTRIBUTE);
		String httpConnectTimeout = configElement.getAttribute(InfluxDBNamespaceHandler.CONFIG_HTTP_CONNECT_TIME_OUT);
		String httpReadTimeout = configElement.getAttribute(InfluxDBNamespaceHandler.CONFIG_HTTP_READ_TIME_OUT);
		String httpWriteTimeout = configElement.getAttribute(InfluxDBNamespaceHandler.CONFIG_HTTP_WRITE_TIME_OUT);
		String enableGzip = configElement.getAttribute(InfluxDBNamespaceHandler.CONFIG_ENABLE_GZIP);

		builder.addPropertyValue(InfluxDBRepositoryFactoryBean.ENABLE_BATCH_FIELD_NAME, enableBatch);
		builder.addPropertyValue(InfluxDBRepositoryFactoryBean.BATCH_ACTIONS_FIELD_NAME, batchActions);
		builder.addPropertyValue(InfluxDBRepositoryFactoryBean.BATCH_FLUSH_DURATION_FIELD_NAME, batchFlushDuration);
		builder.addPropertyValue(InfluxDBRepositoryFactoryBean.CONNECT_TIMEOUT_SECONDS, httpConnectTimeout);
		builder.addPropertyValue(InfluxDBRepositoryFactoryBean.READ_TIMEOUT_SECONDS, httpReadTimeout);
		builder.addPropertyValue(InfluxDBRepositoryFactoryBean.WRITE_TIMEOUT_SECONDS, httpWriteTimeout);
		builder.addPropertyValue(InfluxDBRepositoryFactoryBean.ENABLE_GZIP_FIELD_NAME, enableGzip);

	}

	private void parseDatasource(BeanDefinitionBuilder builder, Element mapperElement, ParserContext parserContext) {
		Element sourceElement = getChild(mapperElement, InfluxDBNamespaceHandler.DATASOURCE_ELEMENT);

		if (parserContext.isNested()) {
			builder.setScope(parserContext.getContainingBeanDefinition().getScope());
		}

		if (parserContext.isDefaultLazyInit()) {
			builder.setLazyInit(true);
		}

		String scheme = sourceElement.getAttribute(InfluxDBNamespaceHandler.DATASOURCE_SCHEME_ATTRIBUTE);
		String host = sourceElement.getAttribute(InfluxDBNamespaceHandler.DATASOURCE_HOST_ATTRIBUTE);
		String port = sourceElement.getAttribute(InfluxDBNamespaceHandler.DATASOURCE_PORT_ATTRIBUTE);
		String username = sourceElement.getAttribute(InfluxDBNamespaceHandler.DATASOURCE_USERNAME_ATTRIBUTE);
		String password = sourceElement.getAttribute(InfluxDBNamespaceHandler.DATASOURCE_PASSWORD_ATTRIBUTE);
		String database = sourceElement.getAttribute(InfluxDBNamespaceHandler.DATASOURCE_DATABASE);

		builder.addPropertyValue(InfluxDBRepositoryFactoryBean.SCHEME_FIELD_NAME, scheme);
		builder.addPropertyValue(InfluxDBRepositoryFactoryBean.HOST_FIELD_NAME, host);
		builder.addPropertyValue(InfluxDBRepositoryFactoryBean.PORT_FIELD_NAME, port);
		builder.addPropertyValue(InfluxDBRepositoryFactoryBean.USERNAME_FIELD_NAME, username);
		builder.addPropertyValue(InfluxDBRepositoryFactoryBean.PASSWORD_FIELD_NAME, password);
		builder.addPropertyValue(InfluxDBRepositoryFactoryBean.DATABASE_FIELD_NAME, database);
	}

	@SuppressWarnings("unused")
	private static List<Element> getChildren(Element parent, String name) {
		NodeList children = parent.getElementsByTagNameNS(InfluxDBNamespaceHandler.NAMESPACE, name);
		int length = children.getLength();
		List<Element> ret = new ArrayList<>(length);
		for (int i = 0; i < length; i++) {
			Node item = children.item(i);
			ret.add((Element) item);
		}
		return ret;
	}

	private static Element getChild(Element element, String name) {
		NodeList children = element.getElementsByTagNameNS(InfluxDBNamespaceHandler.NAMESPACE, name);
		if (children.getLength() > 0) {
			return (Element) children.item(0);
		}
		return null;
	}

}
