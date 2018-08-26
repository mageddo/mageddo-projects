package com.mageddo.featureswitch;

import com.mageddo.featureswitch.repository.FeatureRepository;

public interface FeatureManager {
	FeatureRepository repository();
	FeatureMetadataProvider featureMetadataProvider();
}
