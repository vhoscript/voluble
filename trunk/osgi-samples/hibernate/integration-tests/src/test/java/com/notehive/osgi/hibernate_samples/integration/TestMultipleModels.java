package com.notehive.osgi.hibernate_samples.integration;

import com.notehive.osgi.hibernate_samples.dao.a.A1Dao;
import com.notehive.osgi.hibernate_samples.dao.z.Z1Dao;
import com.notehive.osgi.hibernate_samples.model.a.A1;
import com.notehive.osgi.hibernate_samples.model.z.Z1;

public class TestMultipleModels extends BundleCreatorTest {

	/**
	 * Make sure that multiple models can be run in the
	 * same test case.
	 */
	public void testMultipleModels() {
		A1Dao a1Dao = (A1Dao) applicationContext.getBean("a1Dao");
		Z1Dao z1Dao = (Z1Dao) applicationContext.getBean("z1Dao");

		Z1 z1Saved = new Z1();
		z1Saved.setString1("String 1, value 1");
		z1Saved.setString2("String 2, value 1");
		
		A1 a1Saved = new A1();
		a1Saved.setString1("String 1, value 1");
		a1Saved.setString2("String 2, value 1");

		z1Dao.save(z1Saved);
		a1Dao.save(a1Saved);

		// load and test
		Z1 z1Loaded = z1Dao.get(z1Saved.getId());
		A1 a1Loaded = a1Dao.get(a1Saved.getId());

		assertEquals(z1Saved.getId(), z1Loaded.getId());
		assertEquals(z1Saved.getString1(), z1Loaded.getString1());
		assertEquals(z1Saved.getString2(), z1Loaded.getString2());

		assertEquals(a1Saved.getId(), a1Loaded.getId());
		assertEquals(a1Saved.getString1(), a1Loaded.getString1());
		assertEquals(a1Saved.getString2(), a1Loaded.getString2());
		
		// update
		z1Saved.setString1("String 1, value 2");
		z1Saved.setString2("String 2, value 2");
		z1Dao.save(z1Saved);

		a1Saved.setString1("String 1, value 2");
		a1Saved.setString2("String 2, value 2");
		a1Dao.save(a1Saved);

		// load and test
		z1Loaded = z1Dao.get(z1Saved.getId());
		a1Loaded = a1Dao.get(a1Saved.getId());

		assertEquals(z1Saved.getId(), z1Loaded.getId());
		assertEquals(z1Saved.getString1(), z1Loaded.getString1());
		assertEquals(z1Saved.getString2(), z1Loaded.getString2());

		assertEquals(a1Saved.getId(), a1Loaded.getId());
		assertEquals(a1Saved.getString1(), a1Loaded.getString1());
		assertEquals(a1Saved.getString2(), a1Loaded.getString2());

		// delete
		z1Dao.delete(z1Saved);
		a1Dao.delete(a1Saved);
		
		// check that it's been deleted
		z1Loaded = z1Dao.get(z1Saved.getId());
		a1Loaded = a1Dao.get(a1Saved.getId());
		
	}
}
