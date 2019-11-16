package com.mageddo.micronaut.kafka;

import kafka.Topic;
import kafka.Topics;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TopicEnum {
	FRUIT(
		Topics
		.builder()
		.name(Constants.FRUIT_TOPIC)
		.dlq(Constants.FRUIT_DLQ_TOPIC)
		.build()
	);

	private final Topic topic;

	public static class Constants {
		public static final String FRUIT_TOPIC = "fruit";
		public static final String FRUIT_DLQ_TOPIC = "fruit_dlq";
	}
}
