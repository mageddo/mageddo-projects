package com.mageddo.featureswitch;

public interface InteractiveFeature extends Feature {

	default Integer asInteger(){
		return asInteger(null);
	}

	default Integer asInteger(String user){
		final String v = value(user);
		if(v == null){
			return null;
		}
		return Integer.valueOf(v);
	}

	default String value(){
		return value(null);
	}

	default String value(String user){
		final FeatureMetadata metadata = metadata(user);
		return metadata == null ? null : metadata.get(FeatureKeys.VALUE);
	}

	default boolean isActive(){
		return isActive(null);
	}

	default boolean isActive(String user){
		return manager().isActive(this, user);
	}

	default Boolean asBoolean(){
		return asBoolean(null);
	}

	default Boolean asBoolean(String user){
		return "1".equals(value(user));
	}

	default FeatureMetadata metadata(){
		return metadata(null);
	}

	default FeatureMetadata metadata(String user){
		return manager().featureMetadata(this, user);
	}

	default FeatureMetadata featureMetadata() {
		return manager().featureMetadata(this);
	}

	FeatureManager manager();
}
