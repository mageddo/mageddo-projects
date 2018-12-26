package com.mageddo.kafka.exception;

import com.mageddo.kafka.producer.MessageStatus;
import org.apache.kafka.common.KafkaException;

public class KafkaUnfinishedPostException extends KafkaException {
	public KafkaUnfinishedPostException(MessageStatus status) {
		super(String.format("expected=%d, actual=%d", status.getExpectToSend(), status.getTotal()));
	}
}
