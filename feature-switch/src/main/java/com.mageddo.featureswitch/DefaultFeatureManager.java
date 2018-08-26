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

	public DefaultFeatureManager featureRepository(FeatureRepository featureRepository) {
		this.featureRepository = featureRepository;
		return this;
	}

	public DefaultFeatureManager featureMetadataProvider(FeatureMetadataProvider featureMetadataProvider) {
		this.featureMetadataProvider = featureMetadataProvider;
		return this;
	}
}
