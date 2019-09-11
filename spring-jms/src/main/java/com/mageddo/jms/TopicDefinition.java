package com.mageddo.jms;

import java.util.Map;

public interface TopicDefinition {

	String getName();

	int getConsumers();

	String getFactory();

	long getInterval();

	int getMaxTries();

	boolean isAutoConfigure();

	Map<String, Object> getProps();

	long getMaxInterval();

	RetryStrategy getRetryStrategy();
}
