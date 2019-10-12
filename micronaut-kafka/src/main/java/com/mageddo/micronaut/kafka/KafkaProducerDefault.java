package com.mageddo.micronaut.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import javax.inject.Singleton;
import java.util.concurrent.Future;

@Singleton
public class KafkaProducerDefault implements KafkaProducer {

	@KafkaClient("vanilla")
	private final Producer<String, byte[]>  producer;

	public KafkaProducerDefault(@KafkaClient("vanilla") Producer<String, byte[]> producer) {
		this.producer = producer;
	}

	@Override
	public Future<RecordMetadata> send(ProducerRecord<String, byte[]> record){
		return producer.send(record);
	}
}
