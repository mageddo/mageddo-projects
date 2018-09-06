package com.mageddo.featureswitch;

import com.mageddo.featureswitch.utils.StringUtils;

import java.util.Map;

public interface FeatureMetadata {

	Feature feature();

	Map<String, String> parameters();

	FeatureMetadata set(String k, String v);

	String get(String k);

	void remove(String k);

	default Status status(){
		return Status.fromCode(get(FeatureKeys.STATUS));
	}

	default boolean isActive(){
		return get(FeatureKeys.VALUE).equals(String.valueOf(Status.ACTIVE.getCode()));
	}

	default String value(){
		return value("");
	}

	default String value(String defaultValue){
		final String v = get(FeatureKeys.VALUE);
		return StringUtils.isBlank(v) ? defaultValue : v;
	}

	default Integer asInteger(String key){
		return asInteger(key, null);
	}

	default Integer asInteger(String key, Integer defaultValue){
		final String v = get(key);
		if(StringUtils.isBlank(v)){
			return defaultValue;
		}
		return Integer.valueOf(v);
	}

	default Boolean asBoolean(String key){
		return asBoolean(key, null);
	}

	default Boolean asBoolean(String key, Boolean defaultValue){
		final String v = get(key);
		if(StringUtils.isBlank(v)) {
			return defaultValue;
		}
		return "1".equals(v);
	}

}
