package com.notehive.osgi.hibernate_samples.session;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

/**
 * Hibernate session factory that can get updated during the runtime of the
 * application.
 */
public class DynamicConfiguration implements InitializingBean {

	private Logger logger = LoggerFactory.getLogger(DynamicConfiguration.class);

	private List<Class> annotatedClasses = new ArrayList<Class>();

	private Properties hibernateProperties;

	private SessionFactory proxiedSessionFactory;

	private DataSource dataSource;

	private int myhashCode;

	public void setHibernateProperties(Properties hibernateProperties) {
		this.hibernateProperties = hibernateProperties;
	}

	public void setAnnotatedClasses(List<Class> annotatedClasses) {
		this.annotatedClasses = annotatedClasses;
	}

	public void addAnnotatedClass(Class anotadedClass) {
		annotatedClasses.add(anotadedClass);
		createNewSessionFactory();
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public SessionFactory getSessionFactory() {
		return proxiedSessionFactory;
	}

	private void createNewSessionFactory() {
		if (hibernateProperties == null) {
			throw new IllegalStateException(
					"Hibernate properties have not been set yet");
		}
		AnnotationSessionFactoryBean asfb = new AnnotationSessionFactoryBean();
		asfb.setDataSource(dataSource);
		asfb.setHibernateProperties(hibernateProperties);
		asfb.setAnnotatedClasses(annotatedClasses
				.toArray(new Class[annotatedClasses.size()]));
		try {
			asfb.afterPropertiesSet();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		final SessionFactory sessionFactory = (SessionFactory) asfb.getObject();
		proxiedSessionFactory = (SessionFactory) Proxy.newProxyInstance(
				SessionFactory.class.getClassLoader(),
				new Class[] { SessionFactory.class }, new InvocationHandler() {
					public Object invoke(Object proxy, Method method,
							Object[] args) throws Throwable {
						return method.invoke(sessionFactory, args);
					}
				});
	}

	public void afterPropertiesSet() throws Exception {
		createNewSessionFactory();
		BundleTracker.setDynamicConfiguration(this);
	}

}
