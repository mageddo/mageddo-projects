package com.mageddo.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.ConnectionFactory;

@SpringBootTest(classes = FruitMDBTest.Config.class)
@RunWith(SpringRunner.class)
//@ContextConfiguration()
public class FruitMDBTest {

	@Autowired
	private JmsTemplate jmsTemplate;

	@Test
	public void shouldConsume() throws Exception {

		// arrange

		// act
		jmsTemplate.convertAndSend(TopicEnum.FRUIT.getTopic().getName(), "hello world".getBytes());

		// assert
		Thread.sleep(1000);
	}

	@EnableJms
	@SpringBootApplication
	public static class Config implements InitializingBean {

		@Autowired
		private ConnectionFactory connectionFactory;

		@Autowired
		private JmsConsumerDeclarer jmsConsumerDeclarer;

		@Bean
		public JmsConsumerDeclarer jmsConsumerDeclarer(
			ConfigurableBeanFactory configurableBeanFactory, JmsListenerEndpointRegistry jmsListenerEndpointRegistry){
			return new JmsConsumerDeclarer(
				configurableBeanFactory, jmsListenerEndpointRegistry,
				true, new CronTrigger("0/1 * * * * *")
			);
		}
//
//		@Bean
//		public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory){
//			return new JmsTemplate(connectionFactory);
//		}

		@Override
		public void afterPropertiesSet() throws Exception {
			jmsConsumerDeclarer.declare(TopicEnum.FRUIT.getTopic());
		}

//		@Bean
//		@ConfigurationProperties(prefix = "spring.activemq")
//		public ActiveMQConnectionFactory activeMQConnectionFactory(ActiveMQProperties properties){
//
//			final ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(
//				properties.getUser(), properties.getPassword(), properties.getBrokerUrl()
//			);
//			cf.setUseAsyncSend(true);
//			cf.setDispatchAsync(true);
//			cf.setUseCompression(true);
//			return cf;
//		}
	}
}
