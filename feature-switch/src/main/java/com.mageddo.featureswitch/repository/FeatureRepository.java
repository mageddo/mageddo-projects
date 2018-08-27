package com.mageddo.featureswitch.repository;

import com.mageddo.featureswitch.Feature;
import com.mageddo.featureswitch.FeatureMetadata;

public interface FeatureRepository {

	FeatureMetadata getFeature(Feature feature, String user);

	int updateFeature(FeatureMetadata featureMetadata, String user);

	default FeatureMetadata getFeatureOrDefault(Feature feature, String user, FeatureMetadata metadata){
		final FeatureMetadata f = getFeature(feature, user);
		if(f != null){
			return f;
		}
		return metadata;
	}
}
