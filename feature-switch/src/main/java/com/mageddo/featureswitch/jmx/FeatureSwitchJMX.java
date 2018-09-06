package com.mageddo.featureswitch.jmx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mageddo.featureswitch.BasicFeature;
import com.mageddo.featureswitch.FeatureContext;
import com.mageddo.featureswitch.FeatureManager;
import com.mageddo.featureswitch.FeatureMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

@Component
@ManagedResource
@ConditionalOnProperty(prefix = "feature-switch", name = "enabled", matchIfMissing = true)
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

	@Override
	public String getMetadata(String feature) throws Exception {
		return handle(() -> toJson(featureManager.metadata(new BasicFeature(feature))));
	}

	@Override
	public String setMetadata(String featureName, String jsonMetadata) throws Exception {
		return handle(() -> {
			try {
				featureManager.updateMetadata(
					new BasicFeature(featureName),
					mapper.readValue(jsonMetadata, new TypeReference<LinkedHashMap<String, String>>(){})
				);
				return "metadata updated to " + jsonMetadata;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public String getMetadata(String feature, String user) throws Exception {
		return handle(() -> toJson(featureManager.metadata(new BasicFeature(feature), user)));
	}

	@Override
	public String activate(String name){
		return handle(() -> {
			featureManager.activate(new BasicFeature(name));
			return name + " activated";
		});
	}

	@Override
	public String activate(String name, String value){
		return handle(() -> {
			featureManager.activate(new BasicFeature(name), value);
			return name + " activated";
		});

	}

	@Override
	public String userActivate(String name, String user){
		return handle(() -> {
			featureManager.userActivate(new BasicFeature(name), user);
			return name + " activated to user " + user;
		});
	}

	@Override
	public String userActivate(String name, String user, String value){
		return handle(() -> {
			featureManager.userActivate(new BasicFeature(name), user, value);
			return name + " activated to user " + user;
		});
	}

	@Override
	public String deactivate(String name){
		return handle(() -> {
			featureManager.deactivate(new BasicFeature(name));
			return name + " deactivated";
		});
	}

	@Override
	public String userDeactivate(String name, String user){
		return handle(() -> {
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
