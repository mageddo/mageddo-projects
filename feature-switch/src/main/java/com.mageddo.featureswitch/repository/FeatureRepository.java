package com.mageddo.featureswitch.repository;

import com.mageddo.featureswitch.Feature;
import com.mageddo.featureswitch.FeatureMetadata;

public interface FeatureRepository {
	FeatureMetadata getFeature(Feature feature, String user);
	int updateFeature(FeatureMetadata featureMetadata, String user);
}
