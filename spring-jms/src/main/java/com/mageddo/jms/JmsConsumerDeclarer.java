package com.mageddo.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import javax.jms.ConnectionFactory;
import java.util.Arrays;
import java.util.List;

public class JmsConsumerDeclarer implements SchedulingConfigurer {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ConfigurableBeanFactory beanFactory;
	private final JmsListenerEndpointRegistry jmsListenerEndpointRegistry;
	private final ConnectionFactory connectionFactory;
	private final DefaultJmsListenerContainerFactoryConfigurer configurer;
	private final boolean autostartup;
	private final Trigger trigger;

	public JmsConsumerDeclarer(
		ConfigurableBeanFactory beanFactory, JmsListenerEndpointRegistry jmsListenerEndpointRegistry,
		ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer,
		boolean autostartup
	) {
		this(
			beanFactory, jmsListenerEndpointRegistry, connectionFactory, configurer,
			autostartup, new CronTrigger("0 0/1 * * * *")
		);
	}

	public JmsConsumerDeclarer(
		ConfigurableBeanFactory beanFactory, JmsListenerEndpointRegistry jmsListenerEndpointRegistry,
		ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer,
		boolean autostartup, Trigger trigger
	) {
		this.beanFactory = beanFactory;
		this.jmsListenerEndpointRegistry = jmsListenerEndpointRegistry;
		this.connectionFactory = connectionFactory;
		this.configurer = configurer;
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
		configurer.configure(factory, connectionFactory);
		final boolean autoStartup = topic.getConsumers() > 0 && autostartup;
		if(autoStartup){
			factory.setConcurrency(String.valueOf(topic.getConsumers()));
		}
		factory.setAutoStartup(false);
		factory.setErrorHandler(t -> {
			logger.error("status=failed-on-consume", t);
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
				jmsListenerEndpointRegistry.start();
			} catch (Exception e){
				logger.warn("status=consumer-declare-failed, msg={}", e.getMessage(), e);
			}
		}, trigger);
	}

}
