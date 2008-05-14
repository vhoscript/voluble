package com.notehive.osgi.hibernate_samples.integration;

import com.notehive.osgi.hibernate_samples.dao.a.A1Dao;
import com.notehive.osgi.hibernate_samples.model.a.A1;

public class TestModelA extends BundleCreatorTest {

	public void testA1DaoCrud() {
		A1Dao a1Dao = (A1Dao) applicationContext.getBean("a1Dao");
		
		A1 saved = new A1();
		saved.setString1("String 1, value 1");
		saved.setString2("String 2, value 1");

		a1Dao.save(saved);

		// load and test
		A1 loaded = a1Dao.get(saved.getId());

		assertEquals(saved.getId(), loaded.getId());
		assertEquals(saved.getString1(), loaded.getString1());
		assertEquals(saved.getString2(), loaded.getString2());

		// update
		saved.setString1("String 1, value 2");
		saved.setString2("String 2, value 2");

		a1Dao.save(saved);

		// load and test
		loaded = a1Dao.get(saved.getId());

		assertEquals(saved.getId(), loaded.getId());
		assertEquals(saved.getString1(), loaded.getString1());
		assertEquals(saved.getString2(), loaded.getString2());
	}

}
