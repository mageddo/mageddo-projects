package com.mageddo.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration;
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
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.springframework.transaction.interceptor.TransactionAspectSupport.currentTransactionStatus;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {})
@ContextConfiguration(classes = MessageSenderImplTest.Conf.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableTransactionManagement
@SpringBootApplication(exclude = JsonbAutoConfiguration.class)
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
		final ListenableFuture result = mock(ListenableFuture.class);
		doReturn(result).when(kafkaTemplate).send(any(ProducerRecord.class));

		// act
		final ListenableFuture<SendResult> listenableFuture = messageSender.send(new ProducerRecord("myTopic", "value"));

		// assert
		verify(listenableFuture, never()).get();

	}

	@Test
	public void mustSendWithAndWaitMessageCommitWhenTransactionisActive() throws Exception {

		// arrange
		final SettableListenableFuture<SendResult> future = spy(new SettableListenableFuture());
		doReturn(future).when(kafkaTemplate).send(any(ProducerRecord.class));
		Executors.newSingleThreadScheduledExecutor().submit(() -> {
			sleep(500);
			future.set(new SendResult(null, null));
		});

		// act
		final ListenableFuture<SendResult> listenableFuture = conf.send();

		// assert
		verify(listenableFuture).get();
	}

	@Test
	@Transactional
	public void mustRegisterSynchronizationOnly() throws Exception {

		// arrange
		final SettableListenableFuture<SendResult> future = spy(new SettableListenableFuture());
		doReturn(future).when(kafkaTemplate).send(any(ProducerRecord.class));
		Executors.newSingleThreadScheduledExecutor().submit(() -> {
			sleep(500);
			future.set(new SendResult(null, null));
		});

		// act
		conf.send();
		conf.send();

		// assert

		assertEquals(1, TransactionSynchronizationManager.getSynchronizations().size());
	}

	@Test
	public void mustNotPostMessageBecauseTransactionIsRollbackOnly() throws Exception {

		// arrange
		doReturn(mock(ListenableFuture.class)).when(kafkaTemplate).send(any(ProducerRecord.class));

		// act
		final ListenableFuture<SendResult> listenableFuture = conf.sendWithRollback();

		// assert
		verify(listenableFuture, never()).get();
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


	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
