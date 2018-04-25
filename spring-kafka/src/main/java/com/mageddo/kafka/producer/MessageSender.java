package com.mageddo.kafka.producer;

import com.fasterxml.jackson.core.Versioned;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Collection;

public interface MessageSender {

	void sendAsync(ProducerRecord r);

	void send(ProducerRecord r);

	void send(String topic, Collection list);

	void send(String topic, Versioned o);

	void send(String topic, String key, Versioned o);

	void send(String topic, String o);

	void send(String topic, ConsumerRecord r);

	void sendDLQ(ConsumerRecord r);

	void sendDLQ(String dlqTopic, ConsumerRecord r);

	void send(String topic, Object o);

	void send(String topic, String key, Object o);
}
