package com.mageddo.featureswitch.repository;

import com.mageddo.featureswitch.Feature;
import com.mageddo.featureswitch.FeatureMetadata;

public interface FeatureRepository {

	FeatureMetadata getMetadata(Feature feature, String user);

	int updateMetadata(FeatureMetadata featureMetadata, String user);

	default FeatureMetadata getMetadataOrDefault(Feature feature, String user, FeatureMetadata metadata){
		final FeatureMetadata f = getMetadata(feature, user);
		if(f != null){
			return f;
		}
		return metadata;
	}
}
