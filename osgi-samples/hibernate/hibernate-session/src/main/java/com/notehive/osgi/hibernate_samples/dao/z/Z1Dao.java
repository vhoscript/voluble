package com.notehive.osgi.hibernate_samples.dao.z;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.notehive.osgi.hibernate_samples.model.z.Z1;

public class Z1Dao extends HibernateDaoSupport {

	// TODO: Don't like this method, but will Spring automatically 
	// inject a resource into parent's "setSessionFactory"??
	@Resource(name="sessionFactory")
	public void setSF(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	public void save(Z1 z1) {
		getHibernateTemplate().save(z1);
	}

	public Z1 load(long id) {
		return (Z1) getHibernateTemplate().load(Z1.class, id);
	}

	public void update(Z1 z1) {
		getHibernateTemplate().update(z1);
	}

	public void delete(Z1 z1) {
		getHibernateTemplate().delete(z1);
	}

}
