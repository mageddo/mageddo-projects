package com.mageddo.featureswitch;

import java.util.Map;

import static com.mageddo.featureswitch.utils.StringUtils.isBlank;

public interface FeatureMetadata {

	Feature feature();

	Map<String, String> parameters();

	FeatureMetadata set(String k, String v);

	String get(String k);

	default String get(String k, String defaultValue){
		final String v = get(k);
		if(isBlank(v)){
			return defaultValue;
		}
		return v;
	}

	void remove(String k);

	default Status status(){
		return Status.fromCode(get(FeatureKeys.STATUS), Status.INACTIVE);
	}

	default boolean isActive(){
		return String.valueOf(Status.ACTIVE.getCode()).equals(get(FeatureKeys.STATUS));
	}

	default String value(){
		return value("");
	}

	default String value(String defaultValue){
		final String v = get(FeatureKeys.VALUE);
		return isBlank(v) ? defaultValue : v;
	}

	default Integer asInteger(String key){
		return asInteger(key, null);
	}

	default Integer asInteger(String key, Integer defaultValue){
		final String v = get(key);
		if(isBlank(v)){
			return defaultValue;
		}
		return Integer.valueOf(v);
	}

	default Long asLong(String key){
		return asLong(key, null);
	}

	default Long asLong(String key, Long defaultValue){
		final String v = get(key);
		if(isBlank(v)){
			return defaultValue;
		}
		return Long.valueOf(v);
	}

	default Boolean asBoolean(String key){
		return asBoolean(key, null);
	}

	default Boolean asBoolean(String key, Boolean defaultValue){
		final String v = get(key);
		if(isBlank(v)) {
			return defaultValue;
		}
		return "1".equals(v);
	}

}
