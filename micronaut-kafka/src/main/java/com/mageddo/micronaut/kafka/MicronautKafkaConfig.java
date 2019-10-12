package com.mageddo.micronaut.kafka;

import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor
public class MicronautKafkaConfig {
	private final ApplicationContextProvider applicationContextProvider;
}
