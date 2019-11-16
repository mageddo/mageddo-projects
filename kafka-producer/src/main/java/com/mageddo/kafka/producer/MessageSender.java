package com.mageddo.kafka.producer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

public interface MessageSender {

	Future<RecordMetadata> send(ProducerRecord r);
	Future<RecordMetadata> send(ProducerRecord r, Callback callback);

	Future<RecordMetadata> send(ProducerRecord r, Throwable t);
	Future<RecordMetadata> send(ProducerRecord r, Throwable t, Callback callback);

	Future<RecordMetadata> sendDLQ(String dlqTopic, ConsumerRecord r, Throwable t);
	Future<RecordMetadata> sendDLQ(String dlqTopic, ConsumerRecord r, Throwable t, Callback callback);

}
