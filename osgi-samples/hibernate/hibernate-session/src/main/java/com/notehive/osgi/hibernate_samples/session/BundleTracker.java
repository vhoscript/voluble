package com.notehive.osgi.hibernate_samples.session;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		context.addBundleListener(this);
	}
	 
	public void stop(BundleContext context) {
		 
	}

	public void bundleChanged(BundleEvent bundleEvent) {
		try {
			if (bundleEvent.getType() == BundleEvent.STARTED || bundleEvent.getType() == BundleEvent.STOPPED) {
				logger.info("BundleTracker received bundle event for " + bundleEvent.getBundle().getSymbolicName());
				Object hibernateContribution = bundleEvent.getBundle().getHeaders().get(HIBERNATE_CONTRIBUTION);
				if (hibernateContribution != null) {
					logger.info("Processing Hibernate-Contribution: " + hibernateContribution);
					updateClasses(bundleEvent, (String) hibernateContribution);
				}
			}
		} catch(RuntimeException re) {
			logger.error("Error processing bundle event", re);
			throw re;
		}
	}

	private void updateClasses(BundleEvent bundleEvent, String hibernateContribution) {
		String[] classes = parseHibernateClasses(hibernateContribution);
		if (bundleEvent.getType() == BundleEvent.STARTED) {
			logger.info("Adding classes from hibernate configuration: " + writeArray(classes));
			dynamicConfiguration.addAnnotatedClasses(bundleEvent.getBundle(), classes);
		} else if (bundleEvent.getType() == BundleEvent.STOPPED) {
			logger.info("Removing classes from hibernate configuration: " + writeArray(classes));
			dynamicConfiguration.removeAnnotatedClasses(bundleEvent.getBundle(), classes);
		}
	}

	private String writeArray(String[] classes) {
		if (classes.length == 0) return "";
		StringBuffer sb = new StringBuffer();
		for(String s : classes) {
			sb.append(s).append(", ");
		}
		return sb.toString().substring(0, sb.length()-2);
	}

	private String[] parseHibernateClasses(String hibernateContribution) {
		String [] hcSplit = hibernateContribution.split(";");
		if (hcSplit.length != 2) {
			throwIllegalArgumentException();
		}
		String dbConnection = hcSplit[0].trim();
		String classesString = hcSplit[1].trim();
		Pattern p = Pattern.compile("classes *= *\"(.*)\"");		
		Matcher m = p.matcher(classesString);
		if (!m.find()) {
			throwIllegalArgumentException();
		}
		String[] classes = m.group(1).split(",");
		return classes;
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
