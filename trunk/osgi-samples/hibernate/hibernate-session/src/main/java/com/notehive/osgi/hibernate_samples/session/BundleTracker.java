package com.notehive.osgi.hibernate_samples.session;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleTracker implements BundleListener, BundleActivator {
	
	private Logger logger = LoggerFactory.getLogger(BundleTracker.class);

	public void start(BundleContext context) {
		logger.info("+++++++++++++++++++++++++++++STARTED!");
		context.addBundleListener(this);
	}
	 
	public void stop(BundleContext context) {
		 
	}

	public void bundleChanged(BundleEvent bundleEvent) {
		logger.info("+++++++++++++++++++++++++++++A bundle changed!");
		logger.info("BundleEvent: " + bundleEvent.getBundle().getSymbolicName());
		
	}
     
    

}
