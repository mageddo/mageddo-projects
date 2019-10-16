package com.mageddo.kafka;

import com.mageddo.kafka.producer.MessageSenderImpl;
import com.mageddo.kafka.producer.handler.KafkaPost;
import com.mageddo.kafka.producer.handler.KafkaPostChecker;
import com.mageddo.kafka.producer.handler.KafkaPostCheckerAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;

@EnableKafka
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Configuration
public class SpringKafkaConfig {

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
}
