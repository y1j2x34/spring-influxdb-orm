package com.vgerbot.orm.influxdb.exec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.vgerbot.orm.influxdb.InfluxDBDao;
import com.vgerbot.orm.influxdb.annotations.AnnotateExecutor;
import com.vgerbot.orm.influxdb.binding.MethodSignature;
import com.vgerbot.orm.influxdb.exception.InfluxDBException;
import com.vgerbot.orm.influxdb.repo.InfluxDBRepository;

public class ExecutorFactory {
	private static final ExecutorFactory INSTANCE = new ExecutorFactory();

	private ExecutorFactory() {
	}

	public static ExecutorFactory getInstance() {
		return INSTANCE;
	}

	public Executor executorOf(MethodSignature signature, InfluxDBRepository repository) {
		Annotation[] annotations = signature.getAnnotations();
		Class<? extends Executor> executorClass = findExecutorInAnnotations(annotations);
		if (executorClass != null) {
			return newAnnotationExecutorInstance(executorClass, signature, repository);
		}
		executorClass = findXmlExecutor(signature);
		if (executorClass != null) {
			return newXmlExecutorInstance(signature, repository);
		}
		executorClass = findImplementationExecutor(signature);
		if (executorClass == null) {
			throw new InfluxDBException("No executor found for method: " + signature.getMethod());
		}
		return newImplementationExecutorInstance(executorClass, repository);
	}

	private Class<? extends Executor> findXmlExecutor(MethodSignature signature) {
		// TODO xml support
		return null;
	}

	private Executor newXmlExecutorInstance(MethodSignature signature, InfluxDBRepository repository) {
		// TODO xml support
		return null;
	}

	private Class<? extends Executor> findImplementationExecutor(MethodSignature signature) {
		Class<?> declaringClass = signature.getMethod().getDeclaringClass();
		if (InfluxDBDao.class == declaringClass) {
			return ImplementationExecutor.class;
		}
		return null;
	}

	private Executor newImplementationExecutorInstance(Class<? extends Executor> executorClass, InfluxDBRepository repository) {
		try {
			Constructor<? extends Executor> constructor = executorClass.getDeclaredConstructor(InfluxDBRepository.class);
			if (!constructor.isAccessible()) {
				constructor.setAccessible(true);
			}
			return constructor.newInstance(repository);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new InfluxDBException("invalid executor constructor", e);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new InfluxDBException("an error occured while initializing executor", e);
		}
	}

	private Executor newAnnotationExecutorInstance(Class<? extends Executor> executorClass, MethodSignature signature,
			InfluxDBRepository repository) {
		Annotation executableAnnotation = findExecutableAnnotation(signature.getAnnotations());
		AnnotateExecutor executorAnnotation = executableAnnotation.annotationType().getDeclaredAnnotation(AnnotateExecutor.class);
		Class<? extends AnnotationExecutor<?>> executorType = executorAnnotation.value();
		try {
			Constructor<? extends AnnotationExecutor<?>> constructor = executorType.getDeclaredConstructor(InfluxDBRepository.class,
					executableAnnotation.annotationType());
			if (!constructor.isAccessible()) {
				constructor.setAccessible(true);
			}
			return constructor.newInstance(repository, executableAnnotation);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new InfluxDBException("invalid annotation executor constructor", e);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new InfluxDBException("an error occured while initializing annotation executor", e);
		}
	}

	private Annotation findExecutableAnnotation(Annotation[] annotations) {
		for (int i = 0; i < annotations.length; i++) {
			Annotation annotation = annotations[i];
			AnnotateExecutor executorAnnotation = annotation.annotationType().getDeclaredAnnotation(AnnotateExecutor.class);
			if (executorAnnotation != null) {
				return annotation;
			}
		}
		return null;
	}

	private Class<? extends Executor> findExecutorInAnnotations(Annotation[] annotations) {
		Annotation executableAnnotation = findExecutableAnnotation(annotations);
		if (executableAnnotation == null) {
			return null;
		}
		AnnotateExecutor executorAnnotation = executableAnnotation.annotationType().getDeclaredAnnotation(AnnotateExecutor.class);
		return executorAnnotation.value();
	}
}
