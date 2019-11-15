package com.mageddo.kafka.consumer;

import com.mageddo.common.retry.RetryUtils;
import com.mageddo.kafka.SpringTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ConsumerDeclarer implements SchedulingConfigurer {

	private final ConfigurableBeanFactory beanFactory;
	private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
	private final KafkaProperties kafkaProperties;
	private final boolean autostartup;
	private final CronTrigger cronTrigger;

	public void declare(final SpringTopic... topics) {
		declare(Arrays.asList(topics));
	}

	public void declare(final List<SpringTopic> topics) {
		for (SpringTopic topic : topics) {
			declareConsumer(topic);
		}
	}

	public void declareConsumer(final SpringTopic topic) {

		if(!topic.isAutoConfigure()){
			return ;
		}

		final ConcurrentKafkaListenerContainerFactory factory = new RetryableKafkaListenerContainerFactory();
		final boolean autoStartup = topic.getConsumers() > 0 && autostartup;
		if(autoStartup){
			factory.setConcurrency(topic.getConsumers());
		}

		factory.setAutoStartup(false);
		factory.getContainerProperties().setAckOnError(false);
		factory.getContainerProperties().setAckMode(topic.getAckMode());

		final Map<String, Object> configs = kafkaProperties.buildConsumerProperties();
		configs.put(ConsumerConfig.GROUP_ID_CONFIG, ObjectUtils.firstNonNull(
			StringUtils.trimToNull(topic.getGroupId()),
			configs.get(ConsumerConfig.GROUP_ID_CONFIG)
		));

		factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(configs));
		factory.setRetryTemplate(setupRetryTemplate(topic));
		beanFactory.registerSingleton(topic.getFactory(), factory);
	}

	private RetryTemplate setupRetryTemplate(SpringTopic topic) {
		final RetryStrategy retryStrategy = topic.getRetryStrategy();
		if(retryStrategy == null){
			return RetryUtils.retryTemplate(
				topic.getMaxTries(), topic.getMaxInterval(), 1.5, Exception.class
			);
		} else {
			RetryTemplate retryTemplate = retryStrategy.getRetryTemplate();
			if(retryTemplate != null){
				return retryTemplate;
			} else {
				return getRetryTemplate(retryStrategy.getBackOffPolicy(), retryStrategy.getRetryPolicy());
			}
		}
	}

	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		if(!autostartup){
			log.warn("status=auto-startup-disabled");
			return;
		}
		taskRegistrar.addTriggerTask(() -> {
			try {
				kafkaListenerEndpointRegistry.start();
			} catch (Exception e){
				log.warn("status=consumer-declare-failed, msg={}", e.getMessage(), e);
			}
		}, cronTrigger);
	}

	private RetryTemplate getRetryTemplate(BackOffPolicy policy, RetryPolicy retryPolicy) {
		final RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setBackOffPolicy(policy);
		retryTemplate.setRetryPolicy(retryPolicy);
		retryTemplate.setThrowLastExceptionOnExhausted(true);
		retryTemplate.registerListener(new SimpleRetryListener());
		return retryTemplate;
	}
}
