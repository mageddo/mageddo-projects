package com.mageddo.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

import static com.mageddo.kafka.producer.MessageSenderImpl.KAFKA_TRANSACTION;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.transaction.interceptor.TransactionAspectSupport.currentTransactionStatus;
import static org.springframework.transaction.support.TransactionSynchronizationManager.hasResource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {})
@ContextConfiguration(classes = MessageSenderImplTest.Conf.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableTransactionManagement
@EnableAutoConfiguration
public class MessageSenderImplTest {

	@Autowired
	private KafkaTemplate kafkaTemplate;

	@Autowired
	private MessageSender messageSender;

	@Autowired
	private Conf conf;

	@Test
	public void mustSendWithWithoutWaitMessageCommitWhenThereIsNoDatabaseTransaction() throws Exception {

		// arrange
		doReturn(mock(ListenableFuture.class)).when(kafkaTemplate).send(any(ProducerRecord.class));

		// act
		final ListenableFuture<SendResult> listenableFuture = messageSender.send(new ProducerRecord("myTopic", "value"));

		// assert
		verify(listenableFuture, never()).get();
		assertNull(MessageSenderImpl.getTransactions());
		assertFalse(hasResource(KAFKA_TRANSACTION));
	}

	@Test
	public void mustSendWithAndWaitMessageCommitWhenTransactionisActive() throws Exception {

		// arrange
		doReturn(mock(ListenableFuture.class)).when(kafkaTemplate).send(any(ProducerRecord.class));

		// act
		final ListenableFuture<SendResult> listenableFuture = conf.send();

		// assert
		verify(listenableFuture).get();
		assertNull(MessageSenderImpl.getTransactions());
		assertFalse(hasResource(KAFKA_TRANSACTION));
	}


	@Test
	public void mustNotPostMessageBecauseTransactionIsRollbackOnly() throws Exception {

		// arrange
		doReturn(mock(ListenableFuture.class)).when(kafkaTemplate).send(any(ProducerRecord.class));

		// act
		final ListenableFuture<SendResult> listenableFuture = conf.sendWithRollback();

		// assert
		verify(listenableFuture, never()).get();
		assertNull(MessageSenderImpl.getTransactions());
		assertFalse(hasResource(KAFKA_TRANSACTION));
	}


	static class Conf {

		@Autowired
		private MessageSender messageSender;

		@Bean
		@Primary
		public KafkaTemplate kafkaTemplate(){
			return mock(KafkaTemplate.class);
		}

		@Bean
		@Primary
		public MessageSender messageSender(KafkaTemplate kafkaTemplate){
			return new MessageSenderImpl(kafkaTemplate);
		}

		@Transactional
		public ListenableFuture send(){
			return messageSender.send(new ProducerRecord("myTopic", "value"));
		}

		@Transactional
		public ListenableFuture<SendResult> sendWithRollback() {
			currentTransactionStatus().setRollbackOnly();
			return send();
		}
	}
}
