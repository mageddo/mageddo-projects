package com.mageddo.micronaut.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

public interface KafkaProducer {
	Future<RecordMetadata> send(ProducerRecord<String, byte[]> record);
}
