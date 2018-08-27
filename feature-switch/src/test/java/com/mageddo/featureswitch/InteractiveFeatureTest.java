package com.mageddo.featureswitch;

import com.mageddo.featureswitch.repository.InMemoryFeatureRepository;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class InteractiveFeatureTest {

	@Test
	public void mustBeDisabledWithNoValuesByDefault(){

		// arrange
		final InteractiveFeature feature = new InteractiveFeature() {
			public String name() {
				return "MY_FEATURE";
			}
			public DefaultFeatureManager manager() {
				return new DefaultFeatureManager()
					.featureRepository(new InMemoryFeatureRepository())
					.featureMetadataProvider(new EnumFeatureMetadataProvider());
			}
		};

		// act
		// assert
		assertFalse(feature.isActive());
		assertEquals("", feature.value());
		assertEquals("", feature.value("Maria"));
	}

	@Test
	public void mustBeEnabledByDefaultWhenUsingDefaultsAnnotation(){

		// arrange
		final InteractiveFeature feature = MyFirstFeatures.FEATURE_ONE;
		// act
		// assert
		assertTrue(feature.isActive());
		assertEquals("Activated", feature.value());
		assertEquals("Activated", feature.value("Maria"));
	}

	@Test
	public void mustBeDisabledWhenRestricted(){

		// arrange
		final InteractiveFeature feature = MySecondFeatures.FEATURE_ONE;
		// act
		// assert
		assertFalse(feature.isActive());
		assertEquals("Activated", feature.value());
		assertEquals(null, feature.value("Maria"));
	}


	@Test
	public void mustChangeValueForFeatureAndAllUsers(){

		// arrange
		final String expectedValue = "56";
		final DefaultFeatureManager featureManager = new DefaultFeatureManager()
			.featureRepository(new InMemoryFeatureRepository())
			.featureMetadataProvider(new EnumFeatureMetadataProvider());

		final InteractiveFeature feature = new InteractiveFeature() {
			public String name() {
				return "MY_FEATURE";
			}
			public DefaultFeatureManager manager() {
				return featureManager;
			}
		};

		final Map<String, String> m = new HashMap<>();
		m.put(FeatureKeys.STATUS, String.valueOf(Status.ACTIVE.getCode()));
		m.put(FeatureKeys.VALUE, expectedValue);
		feature.manager().repository().updateFeature(new DefaultFeatureMetadata(feature, m), null);

		// act
		// assert
		assertTrue(feature.isActive());
		assertEquals(expectedValue, feature.value());
		assertEquals(Integer.valueOf(expectedValue), feature.asInteger());
		assertEquals(expectedValue, feature.value("Maria"));
	}

	@Test
	public void mustLoadUserMetadataFromRepository(){

		// arrange
		final InteractiveFeature feature = MySecondFeatures.FEATURE_ONE;
		final String expectedUser = "Maria";
		final String expectedUserValue = "abc";

		final Map<String, String> m = new HashMap<>();
		m.put(FeatureKeys.STATUS, String.valueOf(Status.ACTIVE.getCode()));
		m.put(FeatureKeys.VALUE, expectedUserValue);
		feature.manager().repository().updateFeature(new DefaultFeatureMetadata(feature, m), expectedUser);

		// act
		// assert
		assertFalse(feature.isActive());
		assertTrue(feature.isActive(expectedUser));
		assertEquals("Activated", feature.value());
		assertEquals(expectedUserValue, feature.value(expectedUser));
		assertEquals(null, feature.value("Barbara"));
	}

	@Test
	public void mustActivateFeatureForSpecificUser(){

		// arrange
		final DefaultFeatureManager featureManager = new DefaultFeatureManager()
		.featureRepository(new InMemoryFeatureRepository())
		.featureMetadataProvider(new EnumFeatureMetadataProvider());

		final InteractiveFeature feature = new InteractiveFeature() {
			public String name() {
				return "MY_FEATURE";
			}
			public DefaultFeatureManager manager() {
				return featureManager;
			}
		};

		final String expectedUser = "Maria";
		final String expectedUserValue = "abc";

		feature.manager().userActivate(feature, expectedUser, expectedUserValue);

		// act
		// assert
		assertFalse(feature.isActive());
		assertTrue(feature.isActive(expectedUser));
		assertNull(feature.value());
		assertEquals(expectedUserValue, feature.value(expectedUser));
		assertNull(feature.value("Barbara"));
	}


	enum MyFirstFeatures implements InteractiveFeature {

		@FeatureDefaults(value = "Activated")
		FEATURE_ONE
		;
		public DefaultFeatureManager manager() {
			return new DefaultFeatureManager()
				.featureRepository(new InMemoryFeatureRepository())
				.featureMetadataProvider(new EnumFeatureMetadataProvider());
		}
	}

	enum MySecondFeatures implements InteractiveFeature {

		@FeatureDefaults(value = "Activated", status = Status.RESTRICTED)
		FEATURE_ONE
		;

		public static final DefaultFeatureManager FEATURE_MANAGER = new DefaultFeatureManager()
			.featureRepository(new InMemoryFeatureRepository())
			.featureMetadataProvider(new EnumFeatureMetadataProvider());

		public DefaultFeatureManager manager() {
			return FEATURE_MANAGER;
		}
	}
}
