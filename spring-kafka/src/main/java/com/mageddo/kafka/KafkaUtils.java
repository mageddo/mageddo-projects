package com.mageddo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter;
import org.springframework.retry.RetryContext;

public interface KafkaUtils {

	static String nextTopic(String topic){
		return nextTopic(topic, 1);
	}
	static String nextTopic(String topic, int retriesTopics){
		final int i = topic.toUpperCase().indexOf("_RETRY");
		if(i >= 0){
			return getDLQ(topic.substring(0, i));
		}
		return topic + "_RETRY";
	}

	static String getDLQ(String topic){
		return String.format("%s_DLQ", topic);
	}

	static <K, V>ConsumerRecord<K, V> getRecord(RetryContext retryContext){
		return (ConsumerRecord<K, V>)retryContext.getAttribute(RetryingMessageListenerAdapter.CONTEXT_RECORD);
	}
}
