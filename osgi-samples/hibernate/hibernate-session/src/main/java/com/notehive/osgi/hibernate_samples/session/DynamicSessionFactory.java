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
public class DynamicSessionFactory implements FactoryBean, InitializingBean {

	private DynamicConfiguration configuration;

	public void setConfiguration(DynamicConfiguration configuration) {
		this.configuration = configuration;
	}

	public Object getObject() throws Exception {
		return configuration.getSessionFactory();
	}

	public Class getObjectType() {
		return null;
	}

	public boolean isSingleton() {
		return true;
	}

	public void afterPropertiesSet() throws Exception {
	}

}
