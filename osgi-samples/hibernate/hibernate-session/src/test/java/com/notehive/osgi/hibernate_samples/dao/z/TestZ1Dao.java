package com.notehive.osgi.hibernate_samples.dao.z;

import javax.annotation.Resource;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.notehive.osgi.hibernate_samples.model.z.Z1;

import junit.framework.TestCase;

@Transactional
public class TestZ1Dao extends AbstractTransactionalDataSourceSpringContextTests {
	
	private ApplicationContext context;
	
	@Resource
	private Z1Dao z1Dao;

	@Override
	protected String[] getConfigLocations() {
		return new String[] {"/META-INF/spring/bundle-context.xml"};
	}
	
	/**
	 * Test Create, read, update and delete.
	 * @throws SystemException 
	 */
	@Transactional
	public void testCrud() throws SystemException {
		
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
