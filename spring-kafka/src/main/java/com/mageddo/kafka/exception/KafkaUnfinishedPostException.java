package com.mageddo.kafka.exception;

import com.mageddo.kafka.producer.MessageStatus;

public class KafkaUnfinishedPostException extends KafkaPostException {
	public KafkaUnfinishedPostException(MessageStatus status) {
		super(String.format("expected=%d, actual=%d", status.getExpectToSend(), status.getTotal()));
	}
}
