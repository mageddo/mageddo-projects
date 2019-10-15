package com.mageddo.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mageddo.kafka.HeaderKeys;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

public class MessageSenderImpl implements MessageSender {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final KafkaTemplate<String, byte[]> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public MessageSenderImpl(KafkaTemplate<String, byte[]> kafkaTemplate) {
		this(kafkaTemplate, new ObjectMapper());
	}

	public MessageSenderImpl(KafkaTemplate<String, byte[]> kafkaTemplate, ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}


	private void waitSomeTime() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ListenableFuture<SendResult> send(ProducerRecord r) {
		return kafkaTemplate.send(r);
	}

	@Override
	public ListenableFuture<SendResult> send(ProducerRecord r, Throwable t) {
		r
		.headers()
		.add(HeaderKeys.STACKTRACE, ExceptionUtils.getStackTrace(t).getBytes())
		;
		return send(r);
	}

	@Override
	public ListenableFuture<SendResult> sendDLQ(String dlqTopic, ConsumerRecord r, Throwable t) {
		return send(new ProducerRecord<>(dlqTopic, null, r.key(), r.value(), r.headers()), t);
	}
}
