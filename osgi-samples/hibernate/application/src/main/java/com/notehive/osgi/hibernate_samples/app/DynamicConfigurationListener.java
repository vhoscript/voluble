package com.notehive.osgi.hibernate_samples.app;

import java.util.Map;

import org.springframework.orm.hibernate3.HibernateTransactionManager;

import com.notehive.osgi.hibernate_samples.session.DynamicConfiguration;

public class DynamicConfigurationListener {

    public void onBind(DynamicConfiguration dynamicConfiguration, Map properties) {
    }

    public void onUnbind(DynamicConfiguration dynamicConfiguration, Map properties) {
    }

}
