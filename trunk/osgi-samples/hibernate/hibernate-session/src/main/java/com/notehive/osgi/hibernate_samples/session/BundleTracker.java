package com.notehive.osgi.hibernate_samples.session;

import java.util.Dictionary;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleTracker implements BundleListener, BundleActivator {
	
	private String HIBERNATE_CONTRIBUTION = "Hibernate-Contribution";
	
	private static DynamicConfiguration dynamicConfiguration;
	
	private Logger logger = LoggerFactory.getLogger(BundleTracker.class);
	
	public void start(BundleContext context) {
		logger.info("+++++++++++++++++++++++++++++STARTED!");
		context.addBundleListener(this);
	}
	 
	public void stop(BundleContext context) {
		 
	}

	public void bundleChanged(BundleEvent bundleEvent) {
		logger.info("+++++++++++++++++++++++++++++A bundle changed!");
		if (bundleEvent.getType() == BundleEvent.STARTED) {
			logger.info("Starting bundle: " + bundleEvent.getBundle().getSymbolicName());
			Object hibernateContribution = bundleEvent.getBundle().getHeaders().get(HIBERNATE_CONTRIBUTION);
			if (hibernateContribution != null) {
				addClasses(bundleEvent.getBundle(), (String) hibernateContribution);
			}
		} else if (bundleEvent.getType() == BundleEvent.STOPPED) {
			logger.info("Stopped bundle: " + bundleEvent.getBundle().getSymbolicName());
		}
	}

	private void addClasses(Bundle sourceBundle, String hibernateContribution) {
		String [] hcSplit = hibernateContribution.split(";");
		if (hcSplit.length != 2) {
			throwIllegalArgumentException();
		}
		String dbConnection = hcSplit[0].trim();
		String classesString = hcSplit[1].trim();
		Pattern p = Pattern.compile("class *= *\"(.*)\"");		
		Matcher m = p.matcher(classesString);
		if (!m.find()) {
			throwIllegalArgumentException();
		}
		String[] classes = m.group(1).split(",");
		for(String s : classes) {
			try {
				dynamicConfiguration.addAnnotatedClass(
						sourceBundle.loadClass(s));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void throwIllegalArgumentException() {
		throw new IllegalArgumentException("Hibernate-Contribution must be of the form " +
		"Hibernate-Contribution: db-connection; class=\"org.example.model.SomeEntity\"");
	}

	public static void setDynamicConfiguration(
			DynamicConfiguration dynamicConfiguration) {
    	BundleTracker.dynamicConfiguration = dynamicConfiguration;
	}
     
    

}
