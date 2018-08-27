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
		final FeatureMetadata metadata = metadata(user);
		return metadata != null && metadata.status() == Status.ACTIVE;
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
		if(user == null){
			return featureMetadata();
		}
		final FeatureMetadata metadata = featureMetadata();
		if(metadata == null){
			return null;
		}
		switch (metadata.status()){
			case ACTIVE:
				return metadata;
			case INACTIVE:
				return metadata;
			case RESTRICTED:
				return manager().repository().getFeature(this, user);
		}
		return null;
	}

	default FeatureMetadata featureMetadata() {
		final FeatureMetadata metadata = manager().repository().getFeature(this, null);
		if(metadata != null){
			return metadata;
		}
		final FeatureMetadataProvider provider = manager().featureMetadataProvider();
		if(provider == null){
			return null;
		}
		return provider.getMetadata(this);
	}

	FeatureManager manager();
}
