package com.mageddo.kafka;

import com.mageddo.kafka.consumer.ConsumerDeclarer;
import com.mageddo.kafka.producer.MessageSenderImpl;
import com.mageddo.kafka.producer.handler.KafkaPost;
import com.mageddo.kafka.producer.handler.KafkaPostChecker;
import com.mageddo.kafka.producer.handler.KafkaPostCheckerAspect;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.support.CronTrigger;

@EnableKafka
@EnableScheduling
@Configuration
public class SpringKafkaConfig implements ApplicationContextAware {

	private static ApplicationContext context;

	@Bean
	public KafkaPostChecker kafkaPostChecker(){
		return new KafkaPostChecker();
	}

	@Bean
	public KafkaPostCheckerAspect kafkaPostCheckerAspect(final KafkaPostChecker kafkaPostChecker){
		return new KafkaPostCheckerAspect(
			ThreadLocal.withInitial(KafkaPost::new), kafkaPostChecker
		);
	}

	@Bean
	public MessageSenderImpl messageSender(KafkaTemplate kafkaTemplate){
		return new MessageSenderImpl(kafkaTemplate);
	}

	@Bean
	public ConsumerDeclarer consumerDeclarer(
		final ConfigurableBeanFactory beanFactory,
		final KafkaListenerEndpointRegistry endpointRegistry,
		final KafkaProperties kafkaProperties,
		final @Value("${spring.kafka.consumer.auto-startup:false}") boolean autoStartup,
		final @Value("${spring.kafka.consumer.cron-trigger:0 0/1 * * * *}") String cronTrigger
	){
		return new ConsumerDeclarer(
			beanFactory, endpointRegistry, kafkaProperties, autoStartup, new CronTrigger(cronTrigger)
		);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	public static ApplicationContext context() {
		return context;
	}
}
