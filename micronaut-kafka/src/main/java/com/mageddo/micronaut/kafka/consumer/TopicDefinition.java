package com.mageddo.micronaut.kafka.consumer;

public interface TopicDefinition {
	String getName();
	String getDlqName();
}
