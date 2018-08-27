package com.mageddo.featureswitch.jmx;

import com.mageddo.featureswitch.BasicFeature;
import com.mageddo.featureswitch.FeatureContext;
import com.mageddo.featureswitch.FeatureManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class FeatureSwitchJMX {

	private static final Logger logger = LoggerFactory.getLogger(FeatureSwitchJMX.class);
	private final FeatureManager featureManager;

	public FeatureSwitchJMX() {
		this(FeatureContext.getFeatureManager());
	}

	public FeatureSwitchJMX(FeatureManager featureManager) {
		this.featureManager = featureManager;
	}

	public void activate(String name){
		featureManager.activate(new BasicFeature(name));
	}

	public void activate(String name, String value){
		featureManager.activate(new BasicFeature(name), value);
	}

	public void userActivate(String name, String user){
		featureManager.userActivate(new BasicFeature(name), user);
	}

	public void userActivate(String name, String user, String value){
		featureManager.userActivate(new BasicFeature(name), user, value);
	}

	public void deactivate(String name){
		featureManager.deactivate(new BasicFeature(name));
	}

	public void userDeactivate(String name, String user){
		featureManager.userDeactivate(new BasicFeature(name), user);
	}

	public static void register() throws JMXRegistrationException {
		try {
			final FeatureSwitchJMX jmx = new FeatureSwitchJMX();
			ObjectName name = new ObjectName(String.format(
			"%s:type=%s", jmx.getClass().getPackage().getName(), jmx.getClass().getSimpleName()
			));
			final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			if (!mbs.isRegistered(name)) {
				mbs.registerMBean(jmx, name);
				logger.info("status=registered, name={}", name);
			} else {
				logger.info("status=already-registered, name={}", name);
			}
		} catch (Exception e){
			throw new JMXRegistrationException(e.getMessage(), e);
		}
	}

	static class JMXRegistrationException extends RuntimeException {
		public JMXRegistrationException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
