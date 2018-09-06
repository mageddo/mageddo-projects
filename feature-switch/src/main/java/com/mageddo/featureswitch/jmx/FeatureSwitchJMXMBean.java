package com.mageddo.featureswitch.jmx;

public interface FeatureSwitchJMXMBean {

	String getMetadata(String feature) throws Exception;

	String setMetadata(String featureName, String jsonMetadata) throws Exception;

	String getMetadata(String feature, String user) throws Exception;

	String activate(String name);

	String activate(String name, String value);

	String userActivate(String name, String user);

	String userActivate(String name, String user, String value);

	String deactivate(String name);

	String userDeactivate(String name, String user);
}
