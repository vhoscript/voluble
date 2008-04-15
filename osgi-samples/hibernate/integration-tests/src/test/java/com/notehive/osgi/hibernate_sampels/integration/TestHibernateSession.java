package com.notehive.osgi.hibernate_sampels.integration;

import javax.annotation.Resource;

import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;

import com.notehive.osgi.hibernate_samples.dao.z.Z1Dao;
import com.notehive.osgi.hibernate_samples.model.z.Z1;

public class TestHibernateSession extends
		AbstractConfigurableBundleCreatorTests {
	
	@Resource
	private Z1Dao z1Dao;

	protected String[] getTestBundlesNames() {
		return new String[] {
				"com.notehive.osgi.hibernate-samples hibernate-classes 1.0.0.SNAPSHOT",
				"com.notehive.osgi.hibernate-samples hsqldb 1.0.0.SNAPSHOT",
				"com.notehive.osgi.hibernate-samples hibernate-classes 1.0.0.SNAPSHOT" };
	}
	
	public void testZ1DaoCrud() {

		Z1 saved = new Z1();
		saved.setString1("String 1, value 1");
		saved.setString2("String 2, value 1");
		
		z1Dao.save(saved);
		
		// load and test
		Z1 loaded = z1Dao.load(saved.getId());

		assertEquals(saved.getId(), loaded.getId());
		assertEquals(saved.getString1(), loaded.getString1());
		assertEquals(saved.getString2(), loaded.getString2());
		
		// update
		saved.setString1("String 1, value 2");
		saved.setString2("String 2, value 2");
		
		z1Dao.save(saved);
		
		// load and test
		loaded = z1Dao.load(saved.getId());

		assertEquals(saved.getId(), loaded.getId());
		assertEquals(saved.getString1(), loaded.getString1());
		assertEquals(saved.getString2(), loaded.getString2());
	}
	

}
