package com.mageddo.kafka.producer;

import com.mageddo.kafka.HeaderKeys;
import com.mageddo.kafka.exception.KafkaPostException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
public class MessageSenderImpl implements MessageSender {

	private final KafkaTemplate<String, byte[]> kafkaTemplate;

	@Override
	public ListenableFuture<SendResult> send(ProducerRecord r) {
		return kafkaTemplate.send(r);
	}

	@Override
	public SendResult sendSync(ProducerRecord r) {
		try {
			return send(r).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new KafkaPostException(e);
		}
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
