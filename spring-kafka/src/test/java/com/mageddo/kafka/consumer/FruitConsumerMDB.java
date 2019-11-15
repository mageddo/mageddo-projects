package com.mageddo.kafka.consumer;

import com.mageddo.kafka.SpringTopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.mageddo.kafka.consumer.TopicEnum.Constants.FRUIT_FACTORY;
import static com.mageddo.kafka.consumer.TopicEnum.Constants.FRUIT_TOPIC;

@Slf4j
@Component
public class FruitConsumerMDB implements RecoveryCallback, Consumer {

	private final List<ConsumerRecord> consumedRecords = new ArrayList<>();

	@KafkaListener(topics = FRUIT_TOPIC, containerFactory = FRUIT_FACTORY)
	public void consume(final ConsumerRecord<String, byte[]> record){
		log.info("status=consumed");
		consumedRecords.add(record);
	}

	public List<ConsumerRecord> getConsumedRecords() {
		return consumedRecords;
	}

	@Override
	public Object recover(RetryContext context) throws Exception {
		log.info("status=recover", context.getLastThrowable());
		return null;
	}

	@Override
	public SpringTopic topic() {
		return TopicEnum.FRUIT.getTopic();
	}
}
