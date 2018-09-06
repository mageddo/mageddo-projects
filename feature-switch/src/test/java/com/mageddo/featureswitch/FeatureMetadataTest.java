package com.mageddo.featureswitch;

import com.mageddo.featureswitch.repository.InMemoryFeatureRepository;
import org.junit.Test;

import static org.junit.Assert.*;

public class FeatureMetadataTest {

	@Test
	public void mustBeInactiveAndReturnDefaultValues(){

		// arrange
		final BasicFeature feature = new BasicFeature("MY_FEATURE");

		final DefaultFeatureManager featureManager = new DefaultFeatureManager()
		.featureRepository(new InMemoryFeatureRepository())
		;

		// act
		final FeatureMetadata metadata = featureManager.metadata(feature);

		// assert
		assertFalse(metadata.isActive());
		assertEquals("", metadata.value());
		assertEquals("ABC", metadata.value("ABC"));

		assertEquals(null, metadata.asBoolean(FeatureKeys.VALUE));
		assertEquals(true, metadata.asBoolean(FeatureKeys.VALUE, true));

		assertEquals(null, metadata.asInteger(FeatureKeys.VALUE));
		assertEquals(Integer.valueOf(10), metadata.asInteger(FeatureKeys.VALUE, 10));

	}


	@Test
	public void mustBeActiveAndReturnPersistedValues(){

		// arrange
		final BasicFeature feature = new BasicFeature("MY_FEATURE");
		final String expectedValue = "999";

		final DefaultFeatureManager featureManager = new DefaultFeatureManager()
		.featureRepository(new InMemoryFeatureRepository())
		;

		// act
		featureManager.activate(feature, expectedValue);
		final FeatureMetadata metadata = featureManager.metadata(feature);

		// assert
		assertTrue(metadata.isActive());
		assertEquals(expectedValue, metadata.value());
		assertEquals(expectedValue, metadata.value("ABC"));

		assertEquals(false, metadata.asBoolean(FeatureKeys.VALUE));
		assertEquals(false, metadata.asBoolean(FeatureKeys.VALUE, true));

		assertEquals(Integer.valueOf(expectedValue), metadata.asInteger(FeatureKeys.VALUE));
		assertEquals(Integer.valueOf(expectedValue), metadata.asInteger(FeatureKeys.VALUE, 10));

		assertEquals(null, metadata.asInteger("k1"));
		assertEquals(Integer.valueOf(465), metadata.asInteger("k1", 465));

	}

}