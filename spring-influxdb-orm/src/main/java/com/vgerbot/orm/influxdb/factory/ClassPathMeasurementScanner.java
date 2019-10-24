package com.vgerbot.orm.influxdb.factory;

import com.vgerbot.orm.influxdb.annotations.InfluxDBMeasurement;
import com.vgerbot.orm.influxdb.utils.ClasspathScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

public class ClassPathMeasurementScanner extends ClasspathScanner {
    private String[] entityPackages;
    public ClassPathMeasurementScanner(Environment environment) {
        super(environment);
        this.addIncludeFilter(new AnnotationTypeFilter(InfluxDBMeasurement.class));
        this.addIncludeFilter(new AssignableTypeFilter(Serializable.class));
        this.addExcludeFilter(new NegateTypeFilter(new AnnotationTypeFilter(InfluxDBMeasurement.class)));
    }

    public Set<Class<?>> scan() {
        return super.scan(this.entityPackages);
    }

    public void setEntityPackages(String[] entityPackages) {
        this.entityPackages = entityPackages;
    }

    public static class NegateTypeFilter implements TypeFilter {
        private TypeFilter filter;
        public NegateTypeFilter(TypeFilter filter) {
            this.filter = filter;
        }

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
            return !this.filter.match(metadataReader, metadataReaderFactory);
        }
    }
}
