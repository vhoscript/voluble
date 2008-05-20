package com.notehive.osgi.hibernate_samples.app;

import org.osgi.framework.BundleContext;

public class BundleActivator implements org.osgi.framework.BundleActivator {

	private Application application = new Application();
	
	public void start(BundleContext context) {
		application.run();
	}
	 
	public void stop(BundleContext context) {
		application.stop();
	}

}