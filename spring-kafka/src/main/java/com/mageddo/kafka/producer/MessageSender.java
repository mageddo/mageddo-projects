package com.mageddo.kafka.producer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

public interface MessageSender {

	ListenableFuture<SendResult> send(ProducerRecord r);

	ListenableFuture<SendResult> send(ProducerRecord r, Throwable t);

	ListenableFuture<SendResult> sendDLQ(String dlqTopic, ConsumerRecord r, Throwable t);

}
