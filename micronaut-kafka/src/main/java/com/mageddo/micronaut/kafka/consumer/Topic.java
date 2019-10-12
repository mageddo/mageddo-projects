package com.mageddo.micronaut.kafka.consumer;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Topic implements TopicDefinition {

	private String name;
	private String dlqName;

}
