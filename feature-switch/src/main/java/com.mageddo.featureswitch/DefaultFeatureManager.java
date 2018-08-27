package com.mageddo.featureswitch;

import com.mageddo.featureswitch.repository.FeatureRepository;

public class DefaultFeatureManager implements FeatureManager {

	private FeatureRepository featureRepository;
	private FeatureMetadataProvider featureMetadataProvider;

	@Override
	public FeatureRepository repository() {
		return featureRepository;
	}

	@Override
	public FeatureMetadataProvider featureMetadataProvider() {
		return featureMetadataProvider;
	}

	@Override
	public void activate(Feature feature) {
		final FeatureMetadata metadata = findMetadata(feature, null)
		.set(FeatureKeys.STATUS, String.valueOf(Status.ACTIVE.getCode()))
		;
		repository().updateFeature(metadata, null);
	}

	@Override
	public void activate(Feature feature, String value) {
		final FeatureMetadata metadata = findMetadata(feature, null)
		.set(FeatureKeys.STATUS, String.valueOf(Status.ACTIVE.getCode()))
		.set(FeatureKeys.VALUE, value)
		;
		repository().updateFeature(metadata, null);
	}

	@Override
	public void userActivate(Feature feature, String user) {
		{
			final FeatureMetadata metadata = findMetadata(feature, user)
			.set(FeatureKeys.STATUS, String.valueOf(Status.RESTRICTED.getCode()))
			;

			repository().updateFeature(metadata, null);
		}
		{
			FeatureMetadata metadata = repository().getFeature(feature, user);
			if (metadata == null) {
				metadata = new DefaultFeatureMetadata(feature);
			}
			metadata
			.set(FeatureKeys.STATUS, String.valueOf(Status.ACTIVE.getCode()))
			;

			repository().updateFeature(metadata, user);
		}
	}

	@Override
	public void userActivate(Feature feature, String user, String value) {
		{
			final FeatureMetadata metadata = findMetadata(feature, user)
			.set(FeatureKeys.STATUS, String.valueOf(Status.RESTRICTED.getCode()))
			;
			repository().updateFeature(metadata, null);
		}
		{
			FeatureMetadata metadata = repository().getFeature(feature, user);
			if (metadata == null) {
				metadata = new DefaultFeatureMetadata(feature);
			}
			metadata
			.set(FeatureKeys.STATUS, String.valueOf(Status.ACTIVE.getCode()))
			.set(FeatureKeys.VALUE, value);

			repository().updateFeature(metadata, user);
		}
	}

	@Override
	public void deactivate(Feature feature) {
		userDeactivate(feature, null);
	}

	@Override
	public void userDeactivate(Feature feature, String user) {
		FeatureMetadata metadata = repository().getFeature(feature, user);
		if (metadata == null) {
			metadata = new DefaultFeatureMetadata(feature);
		}
		metadata
		.set(FeatureKeys.STATUS, String.valueOf(Status.INACTIVE.getCode()))
		;
		repository().updateFeature(metadata, user);
	}

	@Override
	public FeatureMetadata featureMetadata(Feature feature) {
		final FeatureMetadata metadata = repository().getFeature(feature, null);
		if(metadata != null){
			return metadata;
		}
		final FeatureMetadataProvider provider = featureMetadataProvider();
		if(provider == null){
			return null;
		}
		return provider.getMetadata(feature);
	}

	@Override
	public FeatureMetadata featureMetadata(Feature feature, String user) {
		if(user == null){
			return featureMetadata(feature);
		}
		final FeatureMetadata metadata = featureMetadata(feature);
		if(metadata == null){
			return null;
		}
		switch (metadata.status()){
			case ACTIVE:
				return metadata;
			case INACTIVE:
				return null;
			case RESTRICTED:
				return repository().getFeature(feature, user);
		}
		return null;
	}

	@Override
	public boolean isActive(Feature feature) {
		return isActive(feature, null);
	}

	@Override
	public boolean isActive(Feature feature, String user) {
		final FeatureMetadata metadata = featureMetadata(feature, user);
		return metadata != null && metadata.status() == Status.ACTIVE;
	}

	public DefaultFeatureManager featureRepository(FeatureRepository featureRepository) {
		this.featureRepository = featureRepository;
		return this;
	}

	public DefaultFeatureManager featureMetadataProvider(FeatureMetadataProvider featureMetadataProvider) {
		this.featureMetadataProvider = featureMetadataProvider;
		return this;
	}

	FeatureMetadata findMetadata(Feature feature, String user) {
		return repository().getFeatureOrDefault(feature, user, new DefaultFeatureMetadata(feature));
	}
}
