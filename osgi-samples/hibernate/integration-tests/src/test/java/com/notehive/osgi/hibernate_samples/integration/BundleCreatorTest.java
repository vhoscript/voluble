package com.notehive.osgi.hibernate_samples.integration;

import org.osgi.framework.Constants;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;

import org.springframework.osgi.test.platform.Platforms;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import java.util.Arrays;
import java.util.List;

public class BundleCreatorTest extends AbstractConfigurableBundleCreatorTests {

	protected String[] getConfigLocations() {
		return new String[] { "META-INF/spring/bundle-context-osgi.xml" };
	}
	
	protected String[] getTestBundlesNames() {
		return new String[] {
				"org.springframework, spring-core, 2.5.4",
				"org.springframework, spring-tx, 2.5.4",
				"org.springframework, spring-jdbc, 2.5.4",
				"org.springframework, spring-orm, 2.5.4",
				"com.notehive.osgi.hibernate-samples, jta, 1.0.1",
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
				"org.apache.log4j.spi",
				"javax.transaction;version=\"[1.0.1,1.0.1]\""
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

	public void testOsgiPlatformStarts() throws Exception {
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_VENDOR));
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_VERSION));
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
	}
	
	/*
	@Override
	protected List getBootDelegationPackages() {

		List l = super.getBootDelegationPackages();

		for (Object b : l) {
			logger.info(b);
		}
		
		// must explicitly state which system packages to expose so that javax.transaction is NOT exposed
		String [] bootPackages = { "javax.naming.*" };
		
		//, javax.naming.spi, javax.naming.event, javax.management, javax.management.loading, javax.management.modelmbean, javax.net, javax.net.ssl, javax.crypto, javax.crypto.interfaces, javax.crypto.spec, javax.security.auth, javax.security.auth.spi, javax.security.auth.callback, javax.security.auth.login, javax.security.cert, javax.xml.parsers,  javax.xml.xpath, javax.xml.transform.sax, javax.xml.transform.dom, javax.xml.namespace, javax.xml.transform, javax.xml.transform.stream, javax.xml.validation, org.xml.sax, org.xml.sax.helpers, org.xml.sax.ext, com.sun.org.apache.xalan.internal, com.sun.org.apache.xalan.internal.res, com.sun.org.apache.xml.internal.utils, com.sun.org.apache.xpath.internal, com.sun.org.apache.xpath.internal.jaxp, com.sun.org.apache.xpath.internal.objects, com.sun.org.apache.xml.internal, org.w3c.dom, org.w3c.dom.traversal, org.w3c.dom.ls, javax.sql, sun.misc, javax.swing, javax.swing.event, javax.sql.rowset, javax.sql.rowset.spi, javax.imageio, javax.swing.text, javax.swing.tree

		return Arrays.asList(bootPackages);
	}
	*/

}
