package com.notehive.osgi.hibernate_samples.integration;

import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;

import org.springframework.osgi.test.platform.Platforms;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class BundleCreatorTest extends AbstractConfigurableBundleCreatorTests {

	protected String[] getConfigLocations() {
		return new String[] { "META-INF/spring/bundle-context-osgi.xml" };
	}
	
	protected String[] getTestBundlesNames() {
		return new String[] {
				"org.springframework, spring-tx, 2.5.1",
				"org.springframework, spring-jdbc, 2.5.1",
				"org.springframework, spring-orm, 2.5.1",
				"com.notehive.osgi.hibernate-samples, jta, 1.0.1B",
				"com.notehive.osgi.hibernate-samples, hsqldb, 1.8.0.7",
				"com.notehive.osgi.hibernate-samples, hibernate-classes, 3.2.6.ga",
				"com.notehive.osgi.hibernate-samples, hibernate-session, 1.0.0.SNAPSHOT",
				"com.notehive.osgi.hibernate-samples, model-a, 1.0.0.SNAPSHOT" 
				};
	}

	/**
	 * It seems that Spring's AbstractConfigurableBundleCreatorTests does a good
	 * job of figuring out what the test manifest should look like if there is
	 * only one test class. When I have multiple test classes, the auto
	 * generated manifest leaves out some required packages. This is a
	 * quick-and-dirty way to add those classes to the manifest file. A better
	 * solution may be to use the maven-bundle-plugin to generate the manifest
	 * automatically, and then tell Spring where to find that manifest file.
	 * 
	 * @return parent manifest with additional packages added if needed
	 */
	protected Manifest getManifest() {
		Manifest manifest = super.getManifest();
		Attributes attributes = manifest.getMainAttributes();

		// printManifestContents(attributes);

		Attributes.Name importPackageAttributeName = new Attributes.Name(
				"Import-Package");
		String importedPackages = (String) attributes
				.get(importPackageAttributeName);

		String[] requiredPackages = new String[] { 
				// sometimes an exception is accompanied with "class not found"
				// (I think) because the exception class is not imported by this
				// bundle.  Adding the package that exception belongs to here 
				// allows this bundle to see what the exception is.
//				"net.sf.cglib.proxy;version=\"2.1.3\""
				"org.apache.log4j.spi"
				};

		for (String requiredPackage : requiredPackages) {
			if (!importedPackages.contains(requiredPackage)) {
				importedPackages += "," + requiredPackage;
			}
		}

		attributes.put(importPackageAttributeName, importedPackages);
		
		printManifestContents(attributes);

		return manifest;
	}

	private void printManifestContents(Attributes attributes) {
		logger.debug("Manifets Attributes");
		logger.debug("-------------------");

		for (Object key : attributes.keySet()) {
			logger.debug("[" + key + "]: " + attributes.get(key));
		}

		logger
				.debug("---------------------------------------------------------");
	}

	protected String getPlatformName() {
		return Platforms.EQUINOX;
	}

	/**
	 * A test method to keep surefire happy - without this method I get an
	 * error: "junit.framework.AssertionFailedError: No tests found in
	 * BundleCreatorTest"
	 */
	public void testNothing() {
	}

}
