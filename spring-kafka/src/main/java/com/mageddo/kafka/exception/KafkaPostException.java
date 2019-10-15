package com.mageddo.kafka.exception;

import com.mageddo.kafka.handler.KafkaPost;
import org.apache.kafka.common.KafkaException;

public class KafkaPostException extends KafkaException {

	public KafkaPostException(Throwable cause) {
		super(cause);
	}

	public KafkaPostException(KafkaPost kafkaPost) {
		super(String.format(
			"an error occurred on message post, errors=%d, msg=%s", kafkaPost.getError(), kafkaPost.getLastError()
		), kafkaPost.getLastError());
	}

	public KafkaPostException(String message, Throwable cause) {
		super(message, cause);
	}
}
