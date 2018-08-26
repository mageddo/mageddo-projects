package com.mageddo.featureswitch;

import java.util.Map;

public interface FeatureMetadata {

	Feature feature();

	Map<String, String> parameters();

	void set(String k, String v);

	String get(String k);

	void remove(String k);

	default Status status(){
		return Status.fromCode(Integer.valueOf(get(FeatureKeys.STATUS)));
	}
}
