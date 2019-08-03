package com.vgerbot.orm.influxdb.mapper;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import com.vgerbot.orm.influxdb.InfluxDBDao;
import com.vgerbot.orm.influxdb.factory.MapperFactoryBean;

public class ClassPathMapperScanner extends ClassPathBeanDefinitionScanner {
	private String includeDaoClassNameRegex;
	private String excludeDaoClassNameRegex;
	private String repositoryName;

	public ClassPathMapperScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}

	public void registerFilters() {
		addIncludeFilter(new AssignableTypeFilter(InfluxDBDao.class));
		addExcludeFilter(new TypeFilter() {
			@Override
			public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
				return metadataReader.getClassMetadata().getClassName().equals(InfluxDBDao.class);
			}
		});
		if (StringUtils.isNotBlank(includeDaoClassNameRegex)) {
			addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(includeDaoClassNameRegex)));
		}
		addExcludeFilter(new ExcludePackageInfoFilter());
		if (StringUtils.isNotBlank(excludeDaoClassNameRegex)) {
			addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(excludeDaoClassNameRegex)));
		}
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

	@Override
	protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Set<BeanDefinitionHolder> definitions = super.doScan(basePackages);
		if (definitions != null && !definitions.isEmpty()) {
			processDefinitions(definitions);
		}
		return definitions;
	}

	private void processDefinitions(Set<BeanDefinitionHolder> definitions) {
		for (BeanDefinitionHolder holder : definitions) {
			BeanDefinition definition = holder.getBeanDefinition();
			MutablePropertyValues values = definition.getPropertyValues();

			values.add("mapperInterface", definition.getBeanClassName());
			values.add("repository", new RuntimeBeanReference(repositoryName));

			definition.setBeanClassName(MapperFactoryBean.class.getName());
			definition.setAutowireCandidate(true);
		}
	}

	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isInterface();
	}

	private final class ExcludePackageInfoFilter implements TypeFilter {

		@Override
		public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
			final String className = metadataReader.getClassMetadata().getClassName();
			return className.endsWith("package-info");
		}

	}
}
