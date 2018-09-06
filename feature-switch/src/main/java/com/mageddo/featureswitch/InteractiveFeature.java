package com.mageddo.featureswitch;

import com.mageddo.featureswitch.utils.StringUtils;

public interface InteractiveFeature extends Feature {

	default Integer asInteger(){
		return asInteger(null, null);
	}

	default Integer asInteger(Integer defaultValue){
		return asInteger(null, defaultValue);
	}

	default Integer asInteger(String user, Integer defaultValue){
		final String v = value(user);
		if(StringUtils.isBlank(v)){
			return defaultValue;
		}
		return Integer.valueOf(v);
	}

	default String value(){
		return value(null);
	}

	default String value(String user){
		return manager().value(this, user);
	}

	default boolean isActive(){
		return isActive(null);
	}

	default boolean isActive(String user){
		return manager().isActive(this, user);
	}

	default Boolean asBoolean(){
		return asBoolean(null, null);
	}

	default Boolean asBoolean(Boolean defaultValue){
		return asBoolean(null, defaultValue);
	}

	default Boolean asBoolean(String user){
		return asBoolean(user, null);
	}

	default Boolean asBoolean(String user, Boolean defaultValue){
		final String v = value(user);
		if(StringUtils.isBlank(v)) {
			return defaultValue;
		}
		return "1".equals(v);
	}

	default FeatureMetadata metadata(){
		return metadata(null);
	}

	default FeatureMetadata metadata(String user){
		return manager().metadata(this, user);
	}

	FeatureManager manager();
}
