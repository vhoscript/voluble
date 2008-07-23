package com.notehive.osgi.hibernate_samples.integration;

import com.notehive.osgi.hibernate_samples.dao.b.B1Dao;
import com.notehive.osgi.hibernate_samples.model.b.B1;

public class TestModelB extends BundleCreatorTest {

	public void testB1DaoCrud() {
		B1Dao b1Dao = (B1Dao) applicationContext.getBean("b1Dao");
		
		B1 saved = new B1();
		saved.setString1("String 1, value 1");
		saved.setString2("String 2, value 1");

		b1Dao.save(saved);

		// load and test
		B1 loaded = b1Dao.get(saved.getId());

		assertEquals(saved.getId(), loaded.getId());
		assertEquals(saved.getString1(), loaded.getString1());
		assertEquals(saved.getString2(), loaded.getString2());

		// update
		saved.setString1("String 1, value 2");
		saved.setString2("String 2, value 2");

		b1Dao.save(saved);

		// load and test
		loaded = b1Dao.get(saved.getId());

		assertEquals(saved.getId(), loaded.getId());
		assertEquals(saved.getString1(), loaded.getString1());
		assertEquals(saved.getString2(), loaded.getString2());
	}

}
