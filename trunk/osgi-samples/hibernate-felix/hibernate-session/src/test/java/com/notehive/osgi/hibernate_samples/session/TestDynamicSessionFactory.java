package com.notehive.osgi.hibernate_samples.session;

import javax.annotation.Resource;
import javax.transaction.SystemException;

import org.hibernate.MappingException;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.notehive.osgi.hibernate_samples.dao.z.Z1Dao;
import com.notehive.osgi.hibernate_samples.dao.z.Z2Dao;
import com.notehive.osgi.hibernate_samples.model.z.Z1;
import com.notehive.osgi.hibernate_samples.model.z.Z2;

public class TestDynamicSessionFactory extends
		AbstractDependencyInjectionSpringContextTests {

	@Resource
	private Z1Dao z1Dao;

	@Resource
	private Z2Dao z2Dao;

	@Resource
	private DynamicConfiguration dynamicConfiguration;

	@Override
	protected String[] getConfigLocations() {
		return new String[] { "/META-INF/spring/bundle-context.xml",
				"/test-context.xml" };
	}

	public void testDynamicallyAddClass() throws SystemException {
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
	
		dynamicConfiguration.addAnnotatedClass(com.notehive.osgi.hibernate_samples.model.z.Z2.class);
		
		performZ1Operations();
		performZ2Operations();

		// remove Z1
		dynamicConfiguration.removeAnnotatedClass(com.notehive.osgi.hibernate_samples.model.z.Z1.class);
		
		// now Z1 operations should fail, but Z2 should work
		try {
			performZ1Operations();
			fail("Expected exception");
		} catch (HibernateSystemException hse) {
			// Z1 should not be in the configuration
			assertEquals(MappingException.class, hse.getCause().getClass());
		}
		
		performZ2Operations();

		// finally, reset configuration back to how we started
		dynamicConfiguration.addAnnotatedClass(com.notehive.osgi.hibernate_samples.model.z.Z1.class);
		dynamicConfiguration.removeAnnotatedClass(com.notehive.osgi.hibernate_samples.model.z.Z2.class);

		// and a **last** check
		performZ1Operations();
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
