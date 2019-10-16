package com.mageddo.kafka.producer;

import com.mageddo.kafka.HeaderKeys;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

@RequiredArgsConstructor
public class MessageSenderImpl implements MessageSender {

	private final KafkaTemplate<String, byte[]> kafkaTemplate;

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
