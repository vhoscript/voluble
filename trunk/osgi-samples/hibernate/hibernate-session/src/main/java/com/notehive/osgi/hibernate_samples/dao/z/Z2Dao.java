package com.notehive.osgi.hibernate_samples.dao.z;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import com.notehive.osgi.hibernate_samples.model.z.Z2;

@Transactional
public class Z2Dao extends HibernateDaoSupport {

	public void save(Z2 z2) {
		getHibernateTemplate().save(z2);
	}

	/**
	 * 'load' returns a proxy with only the id populated.
	 * To access members in the proxy requires a Hibernate
	 * session.  Haven't figured out how to do transactions
	 * between bundles yet... 
	 */
	public Z2 load(long id) {
		return (Z2) getHibernateTemplate().load(Z2.class, id);
	}

	public Z2 get(long id) {
		return (Z2) getHibernateTemplate().get(Z2.class, id);
	}

	public void update(Z2 z2) {
		getHibernateTemplate().update(z2);
	}

	public void delete(Z2 z2) {
		getHibernateTemplate().delete(z2);
	}

}
