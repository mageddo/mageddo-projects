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
		assertEquals(null, feature.value("Maria"));
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
		feature.manager().repository().updateMetadata(new DefaultFeatureMetadata(feature, m), null);

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
		feature.manager().repository().updateMetadata(new DefaultFeatureMetadata(feature, m), expectedUser);

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

		// act
		feature.manager().userActivate(feature, expectedUser, expectedUserValue);

		// assert
		assertFalse(feature.isActive());
		assertEquals(String.valueOf(Status.RESTRICTED.getCode()), feature.metadata().get(FeatureKeys.STATUS));
		assertNull(feature.value());

		assertTrue(feature.isActive(expectedUser));
		assertEquals(expectedUserValue, feature.value(expectedUser));
		assertNull(feature.value("Barbara"));
	}

	@Test
	public void mustActivateFeatureForAllUsers(){

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
		final String expectedValue = "abc";

		// act
		feature.manager().activate(feature, expectedValue);

		// assert
		assertTrue(feature.isActive());
		assertEquals(expectedValue, feature.value());

		assertTrue(feature.isActive(expectedUser));
		assertEquals(expectedValue, feature.value(expectedUser));

	}

	@Test
	public void mustActivateForAllUsersAndKeepOriginalValue(){

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
		final String expectedValue = "abc";

		// act
		feature.manager().activate(feature, expectedValue);
		feature.manager().activate(feature);

		// assert
		assertTrue(feature.isActive());
		assertEquals(expectedValue, feature.value());

		assertTrue(feature.isActive(expectedUser));
		assertEquals(expectedValue, feature.value(expectedUser));

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
