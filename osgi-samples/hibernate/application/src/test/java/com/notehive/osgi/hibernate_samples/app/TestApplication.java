package com.notehive.osgi.hibernate_samples.app;

import javax.annotation.Resource;
import javax.transaction.SystemException;

import junit.framework.Assert;

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
	
	public void testApp() throws SystemException, InterruptedException {
		
		String result;
		
		application.run();
		application.getHqlTextArea().setText("from Z1");
		application.getExecuteQueryButton().doClick();
		
		Thread.sleep(100);
		
		result = application.getResultTextArea().getText();
		Assert.assertTrue(result.startsWith("Rows returned: 0"));
		
		Session session = dynamicSessionFactory.openSession();
		Z1 z1 = new Z1();
		z1.setString1("string 1");
		z1.setString2("string 2");
		session.save(z1);
		session.close();
		
		application.getExecuteQueryButton().doClick();
		
		Thread.sleep(100);
		
		result = application.getResultTextArea().getText();
		Assert.assertTrue(result.startsWith("Rows returned: 1"));
		
		application.stop();

	}

}

