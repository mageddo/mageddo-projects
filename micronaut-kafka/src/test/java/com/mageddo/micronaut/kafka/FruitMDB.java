package com.mageddo.micronaut.kafka;

import com.mageddo.micronaut.kafka.consumer.RecoverConsumer;
import com.mageddo.micronaut.kafka.consumer.TopicDefinition;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.OffsetStrategy;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.retry.annotation.Retryable;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@KafkaListener(
	groupId = "fruitGroupId", clientId = "vanilla",
	offsetStrategy = OffsetStrategy.ASYNC,
	offsetReset = OffsetReset.EARLIEST,
	threads = 2
)
@Slf4j
@Singleton
public class FruitMDB implements RecoverConsumer {

	public final List<ConsumerRecord<String, byte[]>> records = new ArrayList<>();
	public final List<ConsumerRecord<String, byte[]>> retried = new ArrayList<>();

	@Topic(TopicEnum.Constants.FRUIT_TOPIC)
	@Retryable(attempts = "1", delay = "200ms")
	public void consume(ConsumerRecord<String, byte[]> record){
		final String value = new String(record.value());
		if(value.equals("Apple")){
			retried.add(record);
			throw new IllegalArgumentException("apples are not accepted");
		} else {
			log.info("status=success");
			records.add(record);
		}
	}

	@Override
	public TopicDefinition topic() {
		return TopicEnum.FRUIT.getTopic();
	}
}
