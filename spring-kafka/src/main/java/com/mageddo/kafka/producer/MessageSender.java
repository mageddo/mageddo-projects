package com.mageddo.kafka.producer;

import com.fasterxml.jackson.core.Versioned;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Collection;
import java.util.List;

public interface MessageSender {

	/**
	 * Send messages grating server ACK and rollbacking database transaction and throws exception if not
	 * @param r
	 */
	ListenableFuture<SendResult> send(ProducerRecord r);

	List<ListenableFuture<SendResult>> send(String topic, Collection list);

	ListenableFuture<SendResult> send(String topic, Versioned o);

	ListenableFuture<SendResult> send(String topic, String key, Versioned o);

	ListenableFuture<SendResult> send(String topic, String o);

	ListenableFuture<SendResult> send(String topic, ConsumerRecord r);

	ListenableFuture<SendResult> sendDLQ(ConsumerRecord r);

	ListenableFuture<SendResult> sendDLQ(String dlqTopic, ConsumerRecord r);

	ListenableFuture<SendResult> send(String topic, Object o);

	ListenableFuture<SendResult> send(String topic, String key, Object o);
}
