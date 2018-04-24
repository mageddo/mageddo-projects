package com.mageddo.kafka.consumer;

import com.mageddo.kafka.TopicDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Arrays;
import java.util.List;

public class ConsumerDeclarer {

	private ConfigurableBeanFactory beanFactory;
	private KafkaProperties kafkaProperties;
	private boolean autostartup;

	public ConsumerDeclarer(ConfigurableBeanFactory beanFactory, KafkaProperties kafkaProperties, boolean autostartup) {
		this.beanFactory = beanFactory;
		this.kafkaProperties = kafkaProperties;
		this.autostartup = autostartup;
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
		factory.setAutoStartup(autoStartup);
//		factory.getContainerProperties().setAckOnError(false);
		factory.getContainerProperties().setAckMode(topic.getAckMode());
		factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties()));

		final RetryStrategy retryStrategy = topic.getRetryStrategy();
		if(retryStrategy == null){
			final ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
			policy.setInitialInterval(topic.getInterval());
			policy.setMultiplier(1.0);
			policy.setMaxInterval(topic.getMaxInterval());

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

	private RetryTemplate getRetryTemplate(BackOffPolicy policy, RetryPolicy retryPolicy) {
		final RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setBackOffPolicy(policy);
		retryTemplate.setRetryPolicy(retryPolicy);
		retryTemplate.setThrowLastExceptionOnExhausted(true);
		retryTemplate.registerListener(new SimpleRetryListener());
		return retryTemplate;
	}
}
