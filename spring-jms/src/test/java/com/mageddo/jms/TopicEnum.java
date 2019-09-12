package com.mageddo.jms;

import com.mageddo.common.retry.RetryUtils;

import java.time.Duration;

public enum TopicEnum {

	FRUIT(
		new Topic(Constants.FRUIT_TOPIC)
		.factory(Constants.FRUIT_FACTORY)
		.consumers(1)
		.interval(Duration.ofSeconds(1))
		.retryStrategy(
			RetryStrategy
				.builder()
				.setRetryTemplate(RetryUtils.retryTemplate(3, Duration.ofMillis(300), 1.5, Exception.class))
		)
	),

	;

	private final Topic topic;

	TopicEnum(Topic topic) {
		this.topic = topic;
	}

	public Topic getTopic() {
		return topic;
	}

	public static class Constants {
		public static final String FRUIT_TOPIC = "fruit";
		public static final String FRUIT_FACTORY = "fruitFactory";
	}
}
