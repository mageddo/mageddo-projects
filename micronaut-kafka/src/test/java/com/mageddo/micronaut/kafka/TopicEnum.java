package com.mageddo.micronaut.kafka;

import com.mageddo.micronaut.kafka.consumer.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TopicEnum {
	FRUIT(
		Topic
		.builder()
		.name(Constants.FRUIT_TOPIC)
		.dlqName(Constants.FRUIT_DLQ_TOPIC)
		.build()
	);

	private final Topic topic;

	public static class Constants {
		public static final String FRUIT_TOPIC = "fruit";
		public static final String FRUIT_DLQ_TOPIC = "fruit_dlq";
	}
}
