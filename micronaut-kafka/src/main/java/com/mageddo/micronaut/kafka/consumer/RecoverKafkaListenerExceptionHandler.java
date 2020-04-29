package com.mageddo.micronaut.kafka.consumer;

import com.mageddo.kafka.producer.MessageSender;
import io.micronaut.configuration.kafka.exceptions.KafkaListenerException;
import io.micronaut.configuration.kafka.exceptions.KafkaListenerExceptionHandler;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.mageddo.micronaut.kafka.ApplicationContextProvider.context;

public interface RecoverKafkaListenerExceptionHandler extends KafkaListenerExceptionHandler {

	Logger LOG = LoggerFactory.getLogger("RecoverKafkaListenerExceptionHandler");

	@SneakyThrows
	default void handle(KafkaListenerException exception){
		if(exception.getConsumerRecord().isPresent()){
			final ConsumerRecord<String, byte[]> consumerRecord = (ConsumerRecord<String, byte[]>) exception
				.getConsumerRecord()
				.get()
			;
			try {
				final Consumer c = (Consumer) exception.getKafkaListener();
				producer()
				.send(new ProducerRecord<>(c.topic().getDlq(), consumerRecord.key(), consumerRecord.value()))
				.get()
				;
				final Map<TopicPartition, OffsetAndMetadata> offsetToCommit = Collections.singletonMap(
					new TopicPartition(consumerRecord.topic(), consumerRecord.partition()),
					new OffsetAndMetadata(consumerRecord.offset() + 1));
				exception.getKafkaConsumer().commitSync(offsetToCommit, Duration.ofSeconds(5));
				LOG.error(
					"status=recovering, partition={}, offset={}, key={}, value={}",
					consumerRecord.partition(), consumerRecord.offset(),
					consumerRecord.key(), new String(consumerRecord.value()), exception
				);
			} catch (InterruptedException | ExecutionException e) {
				LOG.error(
					"status=cant-send-to-dlq, partition={}, offset={}, key={}, value={}",
					consumerRecord.partition(), consumerRecord.offset(),
					consumerRecord.key(), new String(consumerRecord.value()), exception
				);
				throw e;
			}
		} else {
			LOG.error("status=fatal", exception);
		}
	}

	default MessageSender producer() {
		return context()
		.getBean(MessageSender.class);
	}
}
