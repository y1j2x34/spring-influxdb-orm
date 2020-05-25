package com.vgerbot.orm.influxdb.factory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.vgerbot.orm.influxdb.utils.ResultResolvers;
import javassist.util.proxy.MethodFilter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.vgerbot.orm.influxdb.InfluxDBDao;
import com.vgerbot.orm.influxdb.binding.MapperMethod;
import com.vgerbot.orm.influxdb.binding.MethodSignature;
import com.vgerbot.orm.influxdb.exec.Executor;
import com.vgerbot.orm.influxdb.param.ParameterValue;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;
import com.vgerbot.orm.influxdb.result.ResultContext;
import com.vgerbot.orm.influxdb.result.ResultResolver;
import com.vgerbot.orm.influxdb.result.resolver.CompositeResultResolver;
import com.vgerbot.orm.influxdb.result.resolver.NativeResultResolver;
import com.vgerbot.orm.influxdb.result.resolver.SingleResultResolver;
import com.vgerbot.orm.influxdb.result.resolver.VoidResultResolver;
import com.vgerbot.orm.influxdb.result.resolver.WrapperResultResolver;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

public class MapperFactoryBean<T extends InfluxDBDao<?>> implements FactoryBean<T>, InitializingBean {
	private Class<T> mapperInterface;
	private T object;
	private InfluxDBRepository repository;

	private ResultResolver<?> resultResolver;

	@Override
	public void afterPropertiesSet() throws Exception {
		ProxyFactory factory = new ProxyFactory();
		factory.setInterfaces(new Class[] { mapperInterface });
		factory.setFilter(new MethodFilter() {
            @Override
            public boolean isHandled(Method m) {
                Class<?> declaringClass = m.getDeclaringClass();
                boolean isAbstract = Modifier.isAbstract(m.getModifiers());
                return declaringClass != Object.class || isAbstract;
            }
        });
		@SuppressWarnings("unchecked")
		Class<T> cls = factory.createClass();
		T instance = cls.newInstance();
		((Proxy) instance).setHandler(new MapperMethodHandler());

		this.object = instance;

		CompositeResultResolver compositeResultResolver = new CompositeResultResolver();
		compositeResultResolver.setResolvers(ResultResolvers.getDefaultResultResolverList());

		resultResolver = compositeResultResolver;

	}

	public void setMapperInterface(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	public void setRepository(InfluxDBRepository repository) {
		this.repository = repository;
	}

	@Override
	public T getObject() throws Exception {
		return object;
	}

	@Override
	public Class<?> getObjectType() {
		return mapperInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	private final class MapperMethodHandler implements MethodHandler {
		private final ConcurrentMap<Method, MapperMethod> methods = new ConcurrentHashMap<>();
		@Override
		public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
			Class<?> declaringClass = thisMethod.getDeclaringClass();
			MapperMethod mapperMethod = methods.get(thisMethod);
			if (mapperMethod == null) {
				MethodSignature signature = new MethodSignature(declaringClass, thisMethod);
				mapperMethod = new MapperMethod(mapperInterface, repository, signature);

				MapperMethod previous = methods.putIfAbsent(thisMethod, mapperMethod);
				if (previous != null) {
					mapperMethod = previous;
				}
			}
			Map<String, ParameterValue> parameters = mapperMethod.getProducer().produce(args);
			Executor executor = mapperMethod.getExecutor();
			ResultContext context = executor.execute(mapperMethod, parameters);
			// TODO: Add supports to customize result resolver via annotation
			return resultResolver.resolve(context, mapperMethod.getMethodSignature(), repository);
		}
	}
}