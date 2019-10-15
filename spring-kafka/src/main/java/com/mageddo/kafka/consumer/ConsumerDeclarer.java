package com.mageddo.kafka.consumer;

import com.mageddo.kafka.TopicDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Arrays;
import java.util.List;

public class ConsumerDeclarer implements SchedulingConfigurer {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ConfigurableBeanFactory beanFactory;
	private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
	private final KafkaProperties kafkaProperties;
	private final boolean autostartup;

	/**
	 * Trigger used to setup consumers
	 */
	private final Trigger trigger;

	public ConsumerDeclarer(ConfigurableBeanFactory beanFactory, KafkaProperties kafkaProperties, boolean autostartup) {
		this(beanFactory, kafkaProperties, autostartup, new CronTrigger("0 0/1 * * * *"));
	}

	public ConsumerDeclarer(ConfigurableBeanFactory beanFactory, KafkaProperties kafkaProperties, boolean autostartup, Trigger trigger) {
		this.beanFactory = beanFactory;
		this.kafkaProperties = kafkaProperties;
		this.autostartup = autostartup;
		this.kafkaListenerEndpointRegistry = beanFactory.getBean(KafkaListenerEndpointRegistry.class);
		this.trigger = trigger;
	}

	public void declare(final TopicDefinition... topics) {
		declare(Arrays.asList(topics));
	}

	public void declare(final List<TopicDefinition> topics) {
		for (TopicDefinition topic : topics) {
			declareConsumer(topic);
		}
	}

	public void declareConsumer(final TopicDefinition topic) {

		if(!topic.isAutoConfigure()){
			return ;
		}

		final ConcurrentKafkaListenerContainerFactory factory = new RetryableKafkaListenerContainerFactory();
		final boolean autoStartup = topic.getConsumers() > 0 && autostartup;
		if(autoStartup){
			factory.setConcurrency(topic.getConsumers());
		}

		// will startup using endpoint registry to prevent application startup fail when kafka is down
		factory.setAutoStartup(false);
//		factory.getContainerProperties().setAckOnError(false);
		factory.getContainerProperties().setAckMode(topic.getAckMode());
		factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties()));

		final RetryStrategy retryStrategy = topic.getRetryStrategy();
		if(retryStrategy == null){
			final ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
			policy.setInitialInterval(topic.getInterval().toMillis());
			policy.setMultiplier(1.0);
			policy.setMaxInterval(topic.getMaxInterval().toMillis());

			final SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
			retryPolicy.setMaxAttempts(topic.getMaxTries());
			factory.setRetryTemplate(getRetryTemplate(policy, retryPolicy));
		} else {
			RetryTemplate retryTemplate = retryStrategy.getRetryTemplate();
			if(retryTemplate == null){
				retryTemplate = getRetryTemplate(retryStrategy.getBackOffPolicy(), retryStrategy.getRetryPolicy());
			}
			factory.setRetryTemplate(retryTemplate);
		}
		beanFactory.registerSingleton(topic.getFactory(), factory);
	}

	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		if(!autostartup){
			logger.warn("status=autoStartup-disabled");
			return;
		}
		taskRegistrar.addTriggerTask(() -> {
			try {
				kafkaListenerEndpointRegistry.start();
			} catch (Exception e){
				logger.warn("status=consumer-declare-failed, msg={}", e.getMessage(), e);
			}
		}, trigger);
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
