package com.mageddo.kafka.producer;

import com.fasterxml.jackson.core.Versioned;
import com.mageddo.kafka.CommitPhase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Collection;
import java.util.List;

public interface MessageSender {

	ListenableFuture<SendResult> send(ProducerRecord r);

	/**
	 * Send messages without blocking but granting server ACK and rollback database transaction throwing an exception
	 * when all messages were not acknowledged by the server
	 */
	ListenableFuture<SendResult> send(ProducerRecord r, CommitPhase commitPhase);

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
