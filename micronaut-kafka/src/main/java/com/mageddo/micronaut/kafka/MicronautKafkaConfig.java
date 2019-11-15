package com.mageddo.micronaut.kafka;

import com.mageddo.kafka.producer.MessageSenderImpl;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.Producer;

import javax.inject.Singleton;

@Factory
@RequiredArgsConstructor
public class MicronautKafkaConfig {

	@Bean
	@Singleton
	public MessageSenderImpl messageSenderImpl(@KafkaClient("vanilla") Producer<String, byte[]> producer){
		return new MessageSenderImpl(producer);
	}

}
