package com.vgerbot.test.orm.influxdb.testcase;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.vgerbot.test.orm.influxdb.dao.CensusDao;
import com.vgerbot.test.orm.influxdb.entity.CensusMeasurement;

public class Main {
	public static void main(String[] args) {
//		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml")) {
//			context.refresh();
//			CensusDao dao = context.getBean(CensusDao.class);
//			List<CensusMeasurement> measurements = dao.selectByScientist("lanstroth");
//			System.out.println(measurements);
//
//			dao.deleteSeries("lastroth", 12);
//
//			List<Map<String, Integer>> result = dao.selectButterflies("lanstroth", 10);
//			System.out.println(result);
//
//			dao.hello();
//
//			List<Map<String, Object>> data = dao.findByScientist("lanstroth");
//			System.out.println(data);
//		}
		ProxyFactory factory = new ProxyFactory();
		factory.setInterfaces(new Class[]{
				A.class
		});
		factory.setFilter(new MethodFilter() {
            @Override
            public boolean isHandled(Method m) {
                System.out.println(m);
                System.out.println(m.getDeclaringClass());
                System.out.println(Modifier.isAbstract(m.getModifiers()));
                return m.getDeclaringClass() != Object.class || Modifier.isAbstract(m.getModifiers());
            }
        });
		Class clazz = factory.createClass();
		System.out.println(clazz);
		try {
			A a = (A)clazz.newInstance();
			((Proxy)a).setHandler(new MethodHandler() {
				@Override
				public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
					System.out.println("handle this method " + thisMethod);
					System.out.println("hand proceed method " + proceed);
					return null;
				}
			});
			a.method();
            System.out.println(a.hashCode() + "  " + a.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	interface A {
		void method();
		default void dm() {
            System.out.println("dm");
        }
	}
}
