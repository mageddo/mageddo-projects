package com.mageddo.kafka;

import com.mageddo.kafka.consumer.RetryStrategy;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

import java.util.Map;

public interface TopicDefinition {

	String getName();

	int getConsumers();

	String getFactory();

	long getInterval();

	int getMaxTries();

	boolean isAutoConfigure();

	AckMode getAckMode();

	Map<String, Object> getProps();

	long getMaxInterval();

	RetryStrategy getRetryStrategy();
}
