package com.vgerbot.orm.influxdb.utils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

public class ClasspathScanner {

	private static final String RESOURCE_PATTERN = "**/*.class";
	private Environment environment;

	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
	private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);

	private final List<TypeFilter> includeFilters = new LinkedList<TypeFilter>();

	private final List<TypeFilter> excludeFilters = new LinkedList<TypeFilter>();

	public ClasspathScanner(Environment environment) {
		super();
		this.environment = environment;
	}

	public Set<Class<?>> scan(String... basePackage) {
		Set<Class<?>> classes = new LinkedHashSet<>();
		for (int i = 0; i < basePackage.length; i++) {
			classes.addAll(findCandidateComponents(basePackage[i]));
		}
		return classes;
	}

	protected Set<Class<?>> findCandidateComponents(String basePackage) {
		String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + "/"
				+ RESOURCE_PATTERN;

		Set<Class<?>> classes = new LinkedHashSet<>();
		try {
			Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);

			for (Resource resource : resources) {
				if (resource.isReadable()) {
					MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
					if (isCandidateComponent(metadataReader)) {
						ClassMetadata classMetadata = metadataReader.getClassMetadata();
						String className = classMetadata.getClassName();
						ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
						if (classLoader == null) {
							classLoader = getClass().getClassLoader();
						}
						try {
							Class<?> clazz = Class.forName(className, true, classLoader);
							classes.add(clazz);
						} catch (ClassNotFoundException e) {
							// ignore
						}
					}
				}
			}

		} catch (IOException e) {
			throw new RuntimeException("I/O failure during classpath scanning", e);
		}
		return classes;
	}

	protected String resolveBasePackage(String basePackage) {
		return ClassUtils.convertClassNameToResourcePath(this.environment.resolveRequiredPlaceholders(basePackage));
	}

	protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
		for (TypeFilter tf : this.excludeFilters) {
			if (tf.match(metadataReader, this.metadataReaderFactory)) {
				return false;
			}
		}
		for (TypeFilter tf : this.includeFilters) {
			if (tf.match(metadataReader, this.metadataReaderFactory)) {
				return true;
			}
		}
		return false;
	}

	public void addIncludeFilter(TypeFilter includeFilter) {
		this.includeFilters.add(includeFilter);
	}

	public void addExcludeFilter(TypeFilter excludeFilter) {
		this.excludeFilters.add(0, excludeFilter);
	}
}
