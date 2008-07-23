package com.notehive.osgi.hibernate_samples.session;

import javax.transaction.SystemException;

import junit.framework.TestCase;

import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.osgi.framework.BundleEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.osgi.mock.MockBundle;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.notehive.osgi.hibernate_samples.dao.z.Z1Dao;
import com.notehive.osgi.hibernate_samples.dao.z.Z2Dao;
import com.notehive.osgi.hibernate_samples.model.z.Z1;
import com.notehive.osgi.hibernate_samples.model.z.Z2;

public class TestBundleTracker extends TestCase { 

	private Z1Dao z1Dao;

	private Z2Dao z2Dao;

	private DynamicConfiguration dynamicConfiguration;

	protected String[] getConfigLocations() {
		return new String[] { "/META-INF/spring/bundle-context.xml",
				"/test-context.xml" };
	}

	@SuppressWarnings("unchecked")
	public void testDynamicallyAddClass() throws SystemException {
		
		// must create an entirely new context from scratch, otherwise spring/junit
		// somehow uses the context from another test which already has class Z2
		// added to the hibernate configuration
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(getConfigLocations());
		z1Dao = (Z1Dao) applicationContext.getBean("z1Dao");
		z2Dao = (Z2Dao) applicationContext.getBean("z2Dao");
		dynamicConfiguration = (DynamicConfiguration) applicationContext.getBean("dynamicConfiguration");
		
		// add a class to the configuration and test crud on the old and
		// new classes
		performZ1Operations();
		
		try {
			performZ2Operations();
			fail("Expected exception");
		} catch (HibernateSystemException hse) {
			// Z2 should not be in the configuration
			assertEquals(MappingException.class, hse.getCause().getClass());
		}
		
		BundleTracker bundleTracker = new BundleTracker();
		bundleTracker.setDynamicConfiguration(dynamicConfiguration);
		MockBundle bundle = new MockBundle();
		bundle.getHeaders().put(
				"Hibernate-Contribution", "db-connection; classes=\"com.notehive.osgi.hibernate_samples.model.z.Z2\"");
		BundleEvent bundleEvent = new BundleEvent(BundleEvent.STARTED, bundle);
		bundleTracker.bundleChanged(bundleEvent);
		
		// need to update the session factory in the dao
		z2Dao.setSessionFactory((SessionFactory) dynamicConfiguration.getSessionFactory());
		
		performZ1Operations();
		performZ2Operations();
		
		// now remove a bundle
		bundle = new MockBundle();
		bundle.getHeaders().put(
				"Hibernate-Contribution", "db-connection; classes=\"com.notehive.osgi.hibernate_samples.model.z.Z1\"");
		bundleEvent = new BundleEvent(BundleEvent.STOPPED, bundle);
		bundleTracker.bundleChanged(bundleEvent);
		
		try {
			performZ1Operations();
			fail("Expected exception");
		} catch (HibernateSystemException hse) {
			// Z2 should not be in the configuration
			assertEquals(MappingException.class, hse.getCause().getClass());
		}
	}

	private void performZ1Operations() throws SystemException {

		Z1 saved = new Z1();
		saved.setString1("String 1, value 1");
		saved.setString2("String 2, value 1");

		z1Dao.save(saved);

		// load and test
		Z1 loaded = z1Dao.get(saved.getId());

		assertEquals(saved.getId(), loaded.getId());
		assertEquals(saved.getString1(), loaded.getString1());
		assertEquals(saved.getString2(), loaded.getString2());

		// update
		saved.setString1("String 1, value 2");
		saved.setString2("String 2, value 2");

		z1Dao.save(saved);

		// load and test
		loaded = z1Dao.get(saved.getId());

		assertEquals(saved.getId(), loaded.getId());
		assertEquals(saved.getString1(), loaded.getString1());
		assertEquals(saved.getString2(), loaded.getString2());
	}

	private void performZ2Operations() throws SystemException {

		Z2 saved = new Z2();
		saved.setString1("String 1, value 1");
		saved.setString2("String 2, value 1");

		z2Dao.save(saved);

		// load and test
		Z2 loaded = z2Dao.get(saved.getId());

		assertEquals(saved.getId(), loaded.getId());
		assertEquals(saved.getString1(), loaded.getString1());
		assertEquals(saved.getString2(), loaded.getString2());

		// update
		saved.setString1("String 1, value 2");
		saved.setString2("String 2, value 2");

		z2Dao.save(saved);

		// load and test
		loaded = z2Dao.get(saved.getId());

		assertEquals(saved.getId(), loaded.getId());
		assertEquals(saved.getString1(), loaded.getString1());
		assertEquals(saved.getString2(), loaded.getString2());
	}

}
