package com.mageddo.featureswitch;

import com.mageddo.featureswitch.repository.InMemoryFeatureRepository;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DefaultFeatureManagerTest {

	@Test
	public void mustMergeFeatureMetadata(){

		// arrange
		final BasicFeature feature = new BasicFeature("MY_FEATURE");
		final DefaultFeatureManager featureManager = new DefaultFeatureManager()
		.featureRepository(new InMemoryFeatureRepository())
		;
		final String expectedValue = "ABCD";
		final Map<String, String> newProperties = new LinkedHashMap<>();
		newProperties.put("K1", "val1");
		newProperties.put("K2", "val2");
		newProperties.put("status", "2");

		// act
		featureManager.activate(feature, expectedValue);
		featureManager.updateMetadata(feature, newProperties);

		// assert
		final FeatureMetadata metadata = featureManager.metadata(feature);
		assertEquals(false, featureManager.isActive(feature));
		assertEquals(Status.RESTRICTED, metadata.status());
		assertEquals(expectedValue, metadata.get("value"));
		assertEquals("val1", metadata.get("K1"));
		assertEquals("val2", metadata.get("K2"));

		assertEquals(null, featureManager.metadata(feature, "user1").get("K2"));

	}

}