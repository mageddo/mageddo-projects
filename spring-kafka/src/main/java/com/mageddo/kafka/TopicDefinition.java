package com.mageddo.kafka;

import com.mageddo.kafka.consumer.RetryStrategy;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

import java.time.Duration;
import java.util.Map;

public interface TopicDefinition {

	String getName();

	int getConsumers();

	String getFactory();

	Duration getInterval();

	Duration getMaxInterval();

	int getMaxTries();

	boolean isAutoConfigure();

	AckMode getAckMode();

	Map<String, Object> getProps();

	RetryStrategy getRetryStrategy();

	String getGroupId();
}
