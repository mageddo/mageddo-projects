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
		repository().updateMetadata(metadata, null);
	}

	@Override
	public void activate(Feature feature, String value) {
		final FeatureMetadata metadata = findMetadata(feature, null)
		.set(FeatureKeys.STATUS, String.valueOf(Status.ACTIVE.getCode()))
		.set(FeatureKeys.VALUE, value)
		;
		repository().updateMetadata(metadata, null);
	}

	@Override
	public void userActivate(Feature feature, String user) {
		{
			final FeatureMetadata metadata = findMetadata(feature, user)
			.set(FeatureKeys.STATUS, String.valueOf(Status.RESTRICTED.getCode()))
			;

			repository().updateMetadata(metadata, null);
		}
		{
			FeatureMetadata metadata = repository().getMetadata(feature, user);
			if (metadata == null) {
				metadata = new DefaultFeatureMetadata(feature);
			}
			metadata
			.set(FeatureKeys.STATUS, String.valueOf(Status.ACTIVE.getCode()))
			;

			repository().updateMetadata(metadata, user);
		}
	}

	@Override
	public void userActivate(Feature feature, String user, String value) {
		{
			final FeatureMetadata metadata = findMetadata(feature, user)
			.set(FeatureKeys.STATUS, String.valueOf(Status.RESTRICTED.getCode()))
			;
			repository().updateMetadata(metadata, null);
		}
		{
			FeatureMetadata metadata = repository().getMetadata(feature, user);
			if (metadata == null) {
				metadata = new DefaultFeatureMetadata(feature);
			}
			metadata
			.set(FeatureKeys.STATUS, String.valueOf(Status.ACTIVE.getCode()))
			.set(FeatureKeys.VALUE, value);

			repository().updateMetadata(metadata, user);
		}
	}

	@Override
	public void deactivate(Feature feature) {
		userDeactivate(feature, null);
	}

	@Override
	public void userDeactivate(Feature feature, String user) {
		FeatureMetadata metadata = repository().getMetadata(feature, user);
		if (metadata == null) {
			metadata = new DefaultFeatureMetadata(feature);
		}
		metadata
		.set(FeatureKeys.STATUS, String.valueOf(Status.INACTIVE.getCode()))
		;
		repository().updateMetadata(metadata, user);
	}

	@Override
	public FeatureMetadata featureMetadata(Feature feature) {
		final FeatureMetadata metadata = repository().getMetadata(feature, null);
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
				return repository().getMetadata(feature, user);
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

	@Override
	public String value(Feature feature) {
		return value(feature , null);
	}

	@Override
	public String value(Feature feature, String user) {
		final FeatureMetadata metadata = featureMetadata(feature, user);
		return metadata == null ? null : metadata.get(FeatureKeys.VALUE);
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
		return repository().getMetadataOrDefault(feature, user, new DefaultFeatureMetadata(feature));
	}
}
