package com.mageddo.jms;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.ConnectionFactory;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = FruitMDBTest.Config.class)
@RunWith(SpringRunner.class)
public class FruitMDBTest {

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private FruitMDB fruitMDB;

	@Test
	public void shouldPublishAndConsumeMessageUsingMessageBroker() throws Exception {

		// arrange

		// act
		jmsTemplate.convertAndSend(TopicEnum.FRUIT.getTopic().getName(), "hello world");


		// assert
		TimeUnit.MILLISECONDS.sleep(500);
		assertEquals(1, FruitMDB.messages.size());
		assertEquals("hello world", FruitMDB.messages.get(0));
	}

	@Test
	public void shouldRetryFailedMessage() throws Exception {

		// arrange
		final var message = new ActiveMQTextMessage();

		// act
		final var spyMdb = Mockito.spy(fruitMDB);
		try {
			spyMdb.consume(message);
		} catch (NullPointerException e){
			assertEquals("message can't be null", e.getMessage());
		}

		// assert
		verify(spyMdb, times(3)).doConsume(Mockito.any());

	}

	@EnableJms
	@EnableScheduling
	@SpringBootApplication
	public static class Config implements InitializingBean {

		@Autowired
		private JmsConsumerDeclarer jmsConsumerDeclarer;

		@Bean
		public JmsConsumerDeclarer jmsConsumerDeclarer(
			ConfigurableBeanFactory configurableBeanFactory, JmsListenerEndpointRegistry jmsListenerEndpointRegistry,
			DefaultJmsListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory
		) {
			return new JmsConsumerDeclarer(
				configurableBeanFactory, jmsListenerEndpointRegistry,
				connectionFactory, configurer, true, new CronTrigger("0/1 * * * * *")
			);
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			jmsConsumerDeclarer.declare(TopicEnum.FRUIT.getTopic());
		}
	}
}
