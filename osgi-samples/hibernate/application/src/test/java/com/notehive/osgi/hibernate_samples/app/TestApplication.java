package com.notehive.osgi.hibernate_samples.app;

import javax.annotation.Resource;
import javax.transaction.SystemException;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import com.notehive.osgi.hibernate_samples.model.z.Z1;

@Transactional
public class TestApplication extends AbstractTransactionalDataSourceSpringContextTests {
	
	@Resource
	private Application application;
	
	@Resource
	private SessionFactory dynamicSessionFactory;
	
	@Override
	protected String[] getConfigLocations() {
		return new String[] {"/META-INF/spring/bundle-context.xml",
				"/test-context.xml"};
	}
	
	public void testHql() throws SystemException, InterruptedException {
		
		String result;
		
		application.run();
		
		Session session = dynamicSessionFactory.openSession();
		session.createQuery("delete from Z1").executeUpdate();
		
		application.getHqlTextArea().setText("from Z1");
		application.getExecuteHQLButton().doClick();
		
		result = application.getResultTextArea().getText();
		Assert.assertTrue(result.startsWith("Rows returned: 0"));

		Z1 z1 = new Z1();
		z1.setString1("string 1");
		z1.setString2("string 2");
		session.save(z1);
		session.close();
		
		application.getExecuteHQLButton().doClick();
		
		result = application.getResultTextArea().getText();
		Assert.assertTrue(result.startsWith("Rows returned: 1"));
		
		application.stop();
	}
	
	public void testSql() throws SystemException, InterruptedException {
		
		String result;
		
		application.run();
		
		Session session = dynamicSessionFactory.openSession();
		session.createQuery("delete from Z1").executeUpdate();
		
		application.getHqlTextArea().setText("select * from Z1");
		application.getExecuteSQLButton().doClick();
		
		result = application.getResultTextArea().getText();
		Assert.assertTrue(result.startsWith("Rows returned: 0"));
		
		Z1 z1 = new Z1();
		z1.setString1("string 1");
		z1.setString2("string 2");
		session.save(z1);
		session.close();
		
		application.getExecuteSQLButton().doClick();
		
		result = application.getResultTextArea().getText();
		Assert.assertTrue(result.startsWith("Rows returned: 1"));
		
		application.stop();
	}

	public void testShowHibernateConfig() throws SystemException, InterruptedException {
		String result;
		
		application.run();
		application.getShowHibernateConfigButton().doClick();
		
		result = application.getResultTextArea().getText();
		System.out.println(result);
		Assert.assertTrue(result.startsWith("com.notehive.osgi.hibernate_samples.model.z.Z1 "));
	}
	
//	public void testManually() throws SystemException, InterruptedException {
//		application.run();
//		Thread.sleep(Integer.MAX_VALUE);
//	}

}

