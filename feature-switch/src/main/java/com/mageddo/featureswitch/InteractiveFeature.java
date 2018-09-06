package com.mageddo.featureswitch;

public interface InteractiveFeature extends Feature {

	default Integer asInteger(){
		return metadata().asInteger(FeatureKeys.VALUE);
	}

	default Integer asInteger(Integer defaultValue){
		return metadata().asInteger(FeatureKeys.VALUE, defaultValue);
	}

	default Integer asInteger(String user){
		return asInteger(user, null);
	}

	default Integer asInteger(String user, Integer defaultValue){
		return metadata(user).asInteger(FeatureKeys.VALUE, defaultValue);
	}

	default String value(){
		return metadata().value();
	}

	default String value(String user){
		return metadata(user).value();
	}

	default boolean isActive(){
		return isActive(null);
	}

	default boolean isActive(String user){
		return manager().isActive(this, user);
	}

	default Boolean asBoolean(){
		return metadata().asBoolean(FeatureKeys.VALUE);
	}

	default Boolean asBoolean(Boolean defaultValue){
		return metadata().asBoolean(FeatureKeys.VALUE, defaultValue);
	}

	default Boolean asBoolean(String user){
		return metadata(user).asBoolean(FeatureKeys.VALUE, null);
	}

	default Boolean asBoolean(String user, Boolean defaultValue){
		return metadata(user).asBoolean(FeatureKeys.VALUE, defaultValue);
	}

	default FeatureMetadata metadata(){
		return metadata(null);
	}

	default FeatureMetadata metadata(String user){
		return manager().metadata(this, user);
	}

	FeatureManager manager();
}
