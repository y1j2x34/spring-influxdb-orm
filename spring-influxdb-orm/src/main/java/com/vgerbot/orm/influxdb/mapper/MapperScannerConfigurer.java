package com.vgerbot.orm.influxdb.mapper;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class MapperScannerConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean {

	public static final String BASE_DAO_PACKAGE_FIELD = "baseDaoPackage";
	public static final String REPOSITORY_NAME_FIELD = "repositoryName";

	private String baseDaoPackage;

	private String includeDaoClassNameRegex;

	private String excludeDaoClassNameRegex;

	private String repositoryName;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(baseDaoPackage, "Property 'baseDaoPackage' is required");
		Assert.notNull(repositoryName, "Property 'repositoryName' is required");

	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
		scanner.setIncludeDaoClassNameRegex(includeDaoClassNameRegex);
		scanner.setExcludeDaoClassNameRegex(excludeDaoClassNameRegex);
		scanner.registerFilters();
		scanner.setRepositoryName(repositoryName);
		scanner.scan(StringUtils.tokenizeToStringArray(baseDaoPackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	}

	public void setBaseDaoPackage(String baseDaoPackage) {
		this.baseDaoPackage = baseDaoPackage;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public void setIncludeDaoClassNameRegex(String includeDaoClassNameRegex) {
		this.includeDaoClassNameRegex = includeDaoClassNameRegex;
	}

	public void setExcludeDaoClassNameRegex(String excludeDaoClassNameRegex) {
		this.excludeDaoClassNameRegex = excludeDaoClassNameRegex;
	}
}
