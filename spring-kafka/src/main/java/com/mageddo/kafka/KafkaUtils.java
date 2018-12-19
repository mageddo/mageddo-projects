package com.mageddo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter;
import org.springframework.retry.RetryContext;

public class KafkaUtils {

	private KafkaUtils() {}

	public static String nextTopic(String topic){
		return nextTopic(topic, 1);
	}

	public static String nextTopic(String topic, int retriesTopics){
		final int i = topic.toUpperCase().indexOf("_RETRY");
		if(i >= 0){
			return getDLQ(topic.substring(0, i));
		}
		return topic + "_RETRY";
	}

	public static String getDLQ(String topic){
		return String.format("%s_DLQ", topic);
	}

	public static <K, V>ConsumerRecord<K, V> getRecord(RetryContext retryContext){
		return (ConsumerRecord<K, V>)retryContext.getAttribute(RetryingMessageListenerAdapter.CONTEXT_RECORD);
	}
}
