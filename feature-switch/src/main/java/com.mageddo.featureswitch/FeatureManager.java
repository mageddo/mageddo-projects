package com.mageddo.featureswitch;

import com.mageddo.featureswitch.repository.FeatureRepository;

public interface FeatureManager {

	FeatureRepository repository();

	FeatureMetadataProvider featureMetadataProvider();

	/**
	 * Just activate feature, should keep original feature parameters
	 * @see FeatureKeys
	 */
	void activate(Feature feature);

	/**
	 * Activate feature and set value parameter, keep original parameters untouched
	 * @see FeatureKeys
	 */
	void activate(Feature feature, String value);

	/**
	 * Set feature as restricted then activate for user, keep original parameters untouched
	 * @see FeatureKeys
	 */
	void userActivate(Feature feature, String user);

	/**
	 * Set feature as restricted then activate for user with specified value, keep original parameters untouched
	 * @see FeatureKeys
	 */
	void userActivate(Feature feature, String user, String value);

	/**
	 * Deactivate feature, should keep original feature parameters
	 * @see FeatureKeys
	 */
	void deactivate(Feature feature);

	/**
	 * Deactivate feature to user, should keep original feature parameters
	 * @see FeatureKeys
	 */
	void userDeactivate(Feature feature, String user);
}
