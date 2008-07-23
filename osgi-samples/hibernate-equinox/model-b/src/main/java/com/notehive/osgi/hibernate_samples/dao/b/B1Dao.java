package com.notehive.osgi.hibernate_samples.dao.b;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.notehive.osgi.hibernate_samples.model.b.B1;

public class B1Dao extends HibernateDaoSupport {

	public void save(B1 z1) {
		getHibernateTemplate().save(z1);
	}

	/**
	 * 'load' returns a proxy with only the id populated.
	 * To access members in the proxy requires a Hibernate
	 * session.  Haven't figured out how to do transactions
	 * between bundles yet... 
	 */
	public B1 load(long id) {
		return (B1) getHibernateTemplate().load(B1.class, id);
	}

	public B1 get(long id) {
		return (B1) getHibernateTemplate().get(B1.class, id);
	}

	public void update(B1 z1) {
		getHibernateTemplate().update(z1);
	}

	public void delete(B1 z1) {
		getHibernateTemplate().delete(z1);
	}

}
