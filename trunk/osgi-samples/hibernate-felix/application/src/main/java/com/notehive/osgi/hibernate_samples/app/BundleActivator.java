package com.notehive.osgi.hibernate_samples.app;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

public class BundleActivator implements org.osgi.framework.BundleActivator {
	
	private static Logger logger = Logger.getLogger(BundleActivator.class);

	private Application application = new Application();
	
	public void start(BundleContext context) {
		logger.info("Starting application");
		application.run();
	}
	 
	public void stop(BundleContext context) {
		application.stop();
	}

}