package com.notehive.osgi.hibernate_samples.dao.z;

import javax.annotation.Resource;
import javax.transaction.SystemException;

import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import com.notehive.osgi.hibernate_samples.db.DatabaseLauncher;
import com.notehive.osgi.hibernate_samples.model.z.Z1;

@Transactional
public class TestZ1Dao extends
		AbstractTransactionalSpringContextTests {

	@Resource
	private Z1Dao z1Dao;
	
	@Resource
	private DatabaseLauncher databaseLauncher;

	@Override
	protected String[] getConfigLocations() {
		return new String[] { "/META-INF/spring/bundle-context.xml",
				"/test-context.xml" };
	}

	/**
	 * Test Create, read, update and delete.
	 * 
	 * @throws SystemException
	 */
	public void testCrud() throws SystemException {
		
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

}
