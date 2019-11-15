package com.mageddo.kafka.consumer;

import com.mageddo.kafka.Topics;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

@Getter
@AllArgsConstructor
public enum TopicEnum {

	FRUIT(Topics
			.builder()
			.name(Constants.FRUIT_TOPIC)
			.factory(Constants.FRUIT_FACTORY)
			.autoGroupId()
			.maxInterval(Duration.ofSeconds(1))
			.consumers(1)
			.maxTries(1)
			.build()
	),

	;

	private final Topics topic;

	public static class Constants {
		public static final String FRUIT_FACTORY = "fruit_factory";
		public static final String FRUIT_TOPIC = "fruit";
	}
}
