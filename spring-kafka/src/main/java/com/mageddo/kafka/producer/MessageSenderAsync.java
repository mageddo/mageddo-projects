package com.mageddo.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

public interface MessageSenderAsync {

	/**
	 * Send message without block and without wait server commit ACK
	 */
	ListenableFuture<SendResult> sendAsync(ProducerRecord r);
}
