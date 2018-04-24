package com.mageddo.kafka.consumer;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.retry.RecoveryCallback;

public class RetryableKafkaListenerContainerFactory<K, V> extends ConcurrentKafkaListenerContainerFactory<K, V> {

	private ConcurrentMessageListenerContainer<K, V> container;
	private Object bean;

	@Override
	protected ConcurrentMessageListenerContainer<K, V> createContainerInstance(KafkaListenerEndpoint endpoint) {
		container = super.createContainerInstance(endpoint);
		bean = ((MethodKafkaListenerEndpoint) endpoint).getBean();
		if(bean instanceof RecoveryCallback){
			setRecoveryCallback(((RecoveryCallback) bean));
		}
		return container;
	}

	public ConcurrentMessageListenerContainer<K, V> getContainer() {
		return container;
	}

	public Object getBean() {
		return bean;
	}
}
