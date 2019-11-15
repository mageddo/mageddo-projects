package com.mageddo.kafka;

import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter;
import org.springframework.retry.RetryContext;

@UtilityClass
public class SpringKafkaUtils {
	public static <K, V>ConsumerRecord<K, V> getRecord(RetryContext retryContext){
		return (ConsumerRecord<K, V>)retryContext.getAttribute(RetryingMessageListenerAdapter.CONTEXT_RECORD);
	}
}
