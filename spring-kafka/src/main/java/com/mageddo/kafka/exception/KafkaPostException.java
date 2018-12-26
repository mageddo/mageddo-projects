package com.mageddo.kafka.exception;

import org.apache.kafka.common.KafkaException;

public class KafkaPostException extends KafkaException {

	public KafkaPostException(Throwable cause) {
		super(cause);
	}

	public KafkaPostException(String message) {
		super(message);
	}

	public KafkaPostException(String message, Throwable cause) {
		super(message, cause);
	}
}
