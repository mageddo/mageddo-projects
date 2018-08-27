package com.mageddo.featureswitch.repository;

import com.mageddo.featureswitch.DefaultFeatureMetadata;
import com.mageddo.featureswitch.Feature;
import com.mageddo.featureswitch.FeatureMetadata;

import java.util.HashMap;
import java.util.Map;

public class InMemoryFeatureRepository implements FeatureRepository {

	private final Map<String, Map<String, String>> featureMap = new HashMap<>();
	private final Map<String, Map<String, String>> featureByUserMap = new HashMap<>();

	@Override
	public FeatureMetadata getMetadata(Feature feature, String user) {
		if(user == null){
			if(!featureMap.containsKey(feature.name())){
				return null;
			}
			return new DefaultFeatureMetadata(feature, featureMap.get(feature.name()));
		}
		if(!featureByUserMap.containsKey(toKey(feature, user))){
			return null;
		}
		return new DefaultFeatureMetadata(feature, featureByUserMap.get(toKey(feature, user)));
	}

	@Override
	public int updateMetadata(FeatureMetadata featureMetadata, String user) {
		if(user == null){
			featureMap.put(featureMetadata.feature().name(), featureMetadata.parameters());
		} else {
			featureByUserMap.put(toKey(featureMetadata.feature(), user), featureMetadata.parameters());
		}
		return 1;
	}

	public String toKey(Feature feature, String user){
		return feature.name() + user;
	}
}
