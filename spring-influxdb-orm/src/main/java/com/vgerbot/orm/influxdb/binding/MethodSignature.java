package com.vgerbot.orm.influxdb.binding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import com.vgerbot.orm.influxdb.annotations.CoberturaIgnore;
import com.vgerbot.orm.influxdb.utils.TypeParameterResolver;

public class MethodSignature {
	private final boolean returnsMany;
	private final boolean returnsMap;
	private final boolean returnsVoid;
	private final boolean returnsBoolean;
	private final Class<?> returnType;
	private final Method method;
	private final Annotation[] annotations;
	private final Parameter[] parameters;

	public MethodSignature(Class<?> mapperInterface, Method method) {
		Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
		if (resolvedReturnType instanceof Class<?>) {
			this.returnType = (Class<?>) resolvedReturnType;
		} else if (resolvedReturnType instanceof ParameterizedType) {
			this.returnType = (Class<?>) ((ParameterizedType) resolvedReturnType).getRawType();
		} else {
			this.returnType = method.getReturnType();
		}
		this.returnsVoid = void.class.isAssignableFrom(returnType);
		this.returnsMany = Collection.class.isAssignableFrom(returnType) || returnType.isArray();
		this.returnsMap = Map.class.isAssignableFrom(returnType);
		this.returnsBoolean = Boolean.class == returnType || boolean.class == returnType;
		this.method = method;
		annotations = method.getDeclaredAnnotations();
		parameters = method.getParameters();
	}

	public boolean returnsBoolean() {
		return returnsBoolean;
	}

	public boolean returnsMany() {
		return returnsMany;
	}

	public boolean returnsMap() {
		return returnsMap;
	}

	public boolean returnsVoid() {
		return returnsVoid;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public Method getMethod() {
		return method;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return method.getAnnotation(annotationClass);
	}

	public Parameter[] getParameters() {
		return parameters;
	}

	@Override
	@CoberturaIgnore
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		return result;
	}

	@Override
	@CoberturaIgnore
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodSignature other = (MethodSignature) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		return true;
	}

}