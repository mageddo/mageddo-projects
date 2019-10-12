package com.mageddo.micronaut.kafka.consumer;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Topic implements TopicDefinition {

	@NonNull
	private String name;

	@NonNull
	private String dlqName;

}
