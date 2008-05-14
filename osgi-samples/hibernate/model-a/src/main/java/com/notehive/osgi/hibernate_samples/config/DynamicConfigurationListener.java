package com.notehive.osgi.hibernate_samples.config;

import java.util.Map;

import com.notehive.osgi.hibernate_samples.model.a.A1;
import com.notehive.osgi.hibernate_samples.session.DynamicConfiguration;
import org.springframework.orm.hibernate3.HibernateTransactionManager;

public class DynamicConfigurationListener {

	private HibernateTransactionManager transactionManager;
	
	public void setTransactionManager(HibernateTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
    public void onBind(DynamicConfiguration dynamicConfiguration, Map properties) {
    	dynamicConfiguration.addAnnotatedClass(A1.class);
		// tell txManager about updated session factory
		transactionManager.setSessionFactory(dynamicConfiguration.getSessionFactory());
    }

    public void onUnbind(DynamicConfiguration dynamicConfiguration, Map properties) {
    	dynamicConfiguration.removeAnnotatedClass(A1.class);
    }

}
