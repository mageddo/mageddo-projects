package com.mageddo.kafka;

import com.mageddo.kafka.consumer.RetryStrategy;
import kafka.Topic;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

import java.time.Duration;
import java.util.Map;

public interface SpringTopic extends Topic {

	Integer getConsumers();

	String getFactory();

	Duration getInterval();

	Duration getMaxInterval();

	Integer getMaxTries();

	boolean isAutoConfigure();

	AckMode getAckMode();

	Map<String, Object> getProps();

	RetryStrategy getRetryStrategy();

	String getGroupId();
}
