package com.mageddo.featureswitch.jmx;

public interface FeatureSwitchJMXMBean {

	String getMetadata(String feature) throws Exception;

	String getMetadata(String feature, String user) throws Exception;

	void activate(String name);

	void activate(String name, String value);

	void userActivate(String name, String user);

	void userActivate(String name, String user, String value);

	void deactivate(String name);

	void userDeactivate(String name, String user);
}
