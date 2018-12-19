package com.mageddo.kafka.exception;

public class KafkaPostException extends RuntimeException {

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
