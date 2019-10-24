package com.vgerbot.orm.influxdb.utils;

import com.vgerbot.orm.influxdb.result.ResultContext;
import com.vgerbot.orm.influxdb.result.ResultResolver;
import com.vgerbot.orm.influxdb.result.resolver.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ResultResolvers {
    private static final ResultResolver<Object> DEFAULT_RESULT_RESOLVER;
    private static final ResultResolver<Object> SINGLE_RESULT_RESOLVER = new SingleResultResolver();
    private static final ResultResolver<Object> NATIVE_RESULT_RESOLVER = new NativeResultResolver();
    private static final ResultResolver<Void> VOID_RESULT_RESOLVER = new VoidResultResolver();
    private static final ResultResolver<Object> WRAPPER_RESULT_RESOLVER = new WrapperResultResolver();
    private static final List<ResultResolver<?>> DEFAULT_RESULT_RESOLVER_LIST = Arrays.asList(
            SINGLE_RESULT_RESOLVER,
            NATIVE_RESULT_RESOLVER,
            VOID_RESULT_RESOLVER,
            WRAPPER_RESULT_RESOLVER
    );
    static {
        CompositeResultResolver compositeResultResolver = new CompositeResultResolver();
        compositeResultResolver.setResolvers(DEFAULT_RESULT_RESOLVER_LIST);
        DEFAULT_RESULT_RESOLVER = compositeResultResolver;
    }

    @SuppressWarnings("unchecked")
    public static <T> ResultResolver<T> getDefaultResolver() {
        return (ResultResolver<T>) DEFAULT_RESULT_RESOLVER;
    }

    public static List<ResultResolver<?>> getDefaultResultResolverList() {
        return new LinkedList<>(DEFAULT_RESULT_RESOLVER_LIST);
    }
}
