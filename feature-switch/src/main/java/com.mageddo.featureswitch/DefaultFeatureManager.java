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
		final FeatureMetadata metadata = getMetadata(feature, null)
		.set(FeatureKeys.STATUS, String.valueOf(Status.ACTIVE.getCode()))
		;
		repository().updateFeature(metadata, null);
	}

	@Override
	public void activate(Feature feature, String value) {
		final FeatureMetadata metadata = getMetadata(feature, null)
		.set(FeatureKeys.STATUS, String.valueOf(Status.ACTIVE.getCode()))
		.set(FeatureKeys.VALUE, value)
		;
		repository().updateFeature(metadata, null);
	}

	@Override
	public void userActivate(Feature feature, String user) {
		{
			final FeatureMetadata metadata = getMetadata(feature, user)
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
			final FeatureMetadata metadata = getMetadata(feature, user)
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

	public DefaultFeatureManager featureRepository(FeatureRepository featureRepository) {
		this.featureRepository = featureRepository;
		return this;
	}

	public DefaultFeatureManager featureMetadataProvider(FeatureMetadataProvider featureMetadataProvider) {
		this.featureMetadataProvider = featureMetadataProvider;
		return this;
	}

	FeatureMetadata getMetadata(Feature feature, String user) {
		return repository().getFeatureOrDefault(feature, user, new DefaultFeatureMetadata(feature));
	}
}
