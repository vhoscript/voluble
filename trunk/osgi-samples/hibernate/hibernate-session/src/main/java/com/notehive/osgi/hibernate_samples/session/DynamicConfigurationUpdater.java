package com.notehive.osgi.hibernate_samples.session;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicConfigurationUpdater {
	
	private Logger logger = LoggerFactory.getLogger(DynamicConfigurationUpdater.class);
	
	private SessionFactory sessionFactory;

	private List sfu;

    private void updateSessionFactoryUsers() {
    	if (sfu == null || sessionFactory == null) {
    		// no-one to tell yet, or nothing to tell
    		return;
    	}
    	logger.info("\\\\\\\\ Telling session factory users about new classes.");
		try {
			for (Object o : sfu) {
		    	logger.info("\\\\\\\\ Telling class: " + o.getClass().getName());
				Method setSessionFactory = o.getClass().getMethod("setSessionFactory", new Class[] {SessionFactory.class});
				setSessionFactory.invoke(o, new Object[] {sessionFactory});
			}
		} catch (Throwable t) {
			logger.error("Could not update session factory users", t);
			throw new RuntimeException(t);
		}
    	logger.info("\\\\\\\\ Done telling session factory users about new classes.");
	}

    public void setSessionFactoryUsers(List sfu) {
    	this.sfu = sfu;
    	updateSessionFactoryUsers();
    }
    
    public void setSessionFactory(SessionFactory sf) {
    	this.sessionFactory = sf;
    	updateSessionFactoryUsers();
    }


}
