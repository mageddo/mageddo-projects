package com.mageddo.featureswitch.jmx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mageddo.featureswitch.BasicFeature;
import com.mageddo.featureswitch.FeatureContext;
import com.mageddo.featureswitch.FeatureManager;
import com.mageddo.featureswitch.FeatureMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedOperation;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

public class FeatureSwitchJMX implements FeatureSwitchJMXMBean {

	private static final Logger logger = LoggerFactory.getLogger(FeatureSwitchJMX.class);
	private final FeatureManager featureManager;
	private final ObjectMapper mapper;

	public FeatureSwitchJMX() {
		this(FeatureContext.getFeatureManager());
	}

	public FeatureSwitchJMX(FeatureManager featureManager) {
		this.featureManager = featureManager;
		this.mapper = new ObjectMapper()
		.enable(SerializationFeature.INDENT_OUTPUT)
		.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
		;
	}

	@ManagedOperation
	@Override
	public String getMetadata(String feature) throws Exception {
		return handle(() -> toJson(featureManager.featureMetadata(new BasicFeature(feature))));
	}

	@ManagedOperation
	@Override
	public String getMetadata(String feature, String user) throws Exception {
		return handle(() -> toJson(featureManager.featureMetadata(new BasicFeature(feature), user)));
	}

	@ManagedOperation
	@Override
	public void activate(String name){
		handle(() -> {
			featureManager.activate(new BasicFeature(name));
			return name + " activated";
		});
	}

	@ManagedOperation
	@Override
	public void activate(String name, String value){
		handle(() -> {
			featureManager.activate(new BasicFeature(name), value);
			return name + " activated";
		});

	}

	@ManagedOperation
	@Override
	public void userActivate(String name, String user){
		handle(() -> {
			featureManager.userActivate(new BasicFeature(name), user);
			return name + " activated to user " + user;
		});
	}

	@ManagedOperation
	@Override
	public void userActivate(String name, String user, String value){
		handle(() -> {
			featureManager.userActivate(new BasicFeature(name), user, value);
			return name + " activated to user " + user;
		});
	}

	@ManagedOperation
	@Override
	public void deactivate(String name){
		handle(() -> {
			featureManager.deactivate(new BasicFeature(name));
			return name + " deactivated";
		});
	}

	@ManagedOperation
	@Override
	public void userDeactivate(String name, String user){
		handle(() -> {
			featureManager.userDeactivate(new BasicFeature(name), user);
			return name + " deactivated to user " + user;
		});
	}

	public static void register() throws JMXRegistrationException {
		register(new FeatureSwitchJMX());
	}

	public static void register(Object jmx) throws JMXRegistrationException {
		try {
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

	String toJson(FeatureMetadata metadata) {
		try {
			if(metadata == null){
				return null;
			}
			final LinkedHashMap<String, Object> m = new LinkedHashMap<>();
			m.put("feature", metadata.feature().name());
			m.put("parameters", metadata.parameters());
			return mapper.writeValueAsString(m);
		} catch (JsonProcessingException e) {
			logger.error("status=cant-convert-to-json", e);
			throw new RuntimeException(e);
		}
	}

	String handle(Supplier<String> supplier){
		try {
			return supplier.get();
		} catch (Exception e){
			logger.error("status=jmx-failed", e);
			throw e;
		}
	}

}
