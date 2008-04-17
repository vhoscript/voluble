package com.notehive.osgi.hibernate_samples.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.TransactionAwareDataSourceConnectionProvider;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

/**
 * Hibernate session factory that can get updated during the runtime of the
 * application.
 */
public class DynamicConfiguration  implements InitializingBean {

	private List<Class> annotatedClasses = new ArrayList<Class>();
	
	private Properties hibernateProperties;

	private SessionFactory sessionFactory;
	
	private DataSource dataSource;

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
		return sessionFactory;
	}
	
	public void init() {
		createNewSessionFactory();
	}

	private void createNewSessionFactory() {
		if (hibernateProperties == null) {
			throw new IllegalStateException("Hibernate properties have not been set yet");
		}
		AnnotationSessionFactoryBean asfb = new AnnotationSessionFactoryBean();
		asfb.setDataSource(dataSource);
		asfb.setHibernateProperties(hibernateProperties);
		asfb.setAnnotatedClasses(annotatedClasses.toArray(new Class[annotatedClasses.size()]));
		try {
			asfb.afterPropertiesSet();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		sessionFactory = (SessionFactory) asfb.getObject();
	}

	public void afterPropertiesSet() throws Exception {
		createNewSessionFactory();
	}

}
