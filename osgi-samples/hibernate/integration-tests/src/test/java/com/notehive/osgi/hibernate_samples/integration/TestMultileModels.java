package com.notehive.osgi.hibernate_samples.integration;

import com.notehive.osgi.hibernate_samples.dao.z.Z1Dao;
import com.notehive.osgi.hibernate_samples.model.z.Z1;

public class TestMultileModels extends BundleCreatorTest {

	/**
	 * Make sure that multiple models can be run in the
	 * same test case.
	 */
	public void testMultipleModels() {
		Z1Dao z1Dao = (Z1Dao) applicationContext.getBean("z1Dao");

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
