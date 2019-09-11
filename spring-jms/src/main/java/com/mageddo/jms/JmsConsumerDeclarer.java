package com.mageddo.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
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
import java.util.Optional;

public class JmsConsumerDeclarer implements SchedulingConfigurer {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ConfigurableBeanFactory beanFactory;
	private final JmsListenerEndpointRegistry jmsListenerEndpoint;
	private final boolean autostartup;
	private final Trigger trigger;

	public JmsConsumerDeclarer(
		ConfigurableBeanFactory beanFactory, JmsListenerEndpointRegistry jmsListenerEndpoint, boolean autostartup
	) {
		this(beanFactory, jmsListenerEndpoint, autostartup, new CronTrigger("0 0/1 * * * *"));
	}

	public JmsConsumerDeclarer(
		ConfigurableBeanFactory beanFactory, JmsListenerEndpointRegistry jmsListenerEndpoint,
		boolean autostartup, Trigger trigger
	) {
		this.beanFactory = beanFactory;
		this.jmsListenerEndpoint = jmsListenerEndpoint;
		this.autostartup = autostartup;
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

		final DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		final boolean autoStartup = topic.getConsumers() > 0 && autostartup;
		if(autoStartup){
			factory.setConcurrency(String.valueOf(topic.getConsumers()));
		}
		factory.setAutoStartup(false);
		factory.setErrorHandler(t -> {
			logger.error("status=deu-erro", t);
		});
		beanFactory.registerSingleton(topic.getFactory(), factory);
	}

	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		if(!autostartup){
			logger.warn("status=autoStartup-disabled");
			return;
		}
		taskRegistrar.addTriggerTask(() -> {
			try {
				jmsListenerEndpoint.start();
			} catch (Exception e){
				logger.warn("status=consumer-declare-failed, msg={}", e.getMessage(), e);
			}
		}, trigger);
	}

}
