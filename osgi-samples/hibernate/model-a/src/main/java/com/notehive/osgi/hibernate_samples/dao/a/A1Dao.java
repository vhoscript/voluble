package com.notehive.osgi.hibernate_samples.dao.a;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.notehive.osgi.hibernate_samples.model.a.A1;

public class A1Dao extends HibernateDaoSupport {

	public void save(A1 z1) {
		getHibernateTemplate().save(z1);
	}

	/**
	 * 'load' returns a proxy with only the id populated.
	 * To access members in the proxy requires a Hibernate
	 * session.  Haven't figured out how to do transactions
	 * between bundles yet... 
	 */
	public A1 load(long id) {
		return (A1) getHibernateTemplate().load(A1.class, id);
	}

	public A1 get(long id) {
		return (A1) getHibernateTemplate().get(A1.class, id);
	}

	public void update(A1 z1) {
		getHibernateTemplate().update(z1);
	}

	public void delete(A1 z1) {
		getHibernateTemplate().delete(z1);
	}

}
