package com.mageddo.kafka.producer;

import com.mageddo.kafka.SpringKafkaConfig;
import com.mageddo.kafka.exception.KafkaPostException;
import com.mageddo.kafka.producer.handler.EnsureKafkaPost;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TimeoutException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.Executors;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = MessageSenderImplTest.Conf.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableTransactionManagement
@SpringBootApplication(exclude = JsonbAutoConfiguration.class)
@SpringBootTest(classes = {SpringKafkaConfig.class})
@RunWith(SpringRunner.class)
public class MessageSenderImplTest {

	@Autowired
	private KafkaTemplate kafkaTemplate;

	@Autowired
	private MessageSender messageSender;

	@Autowired
	private Conf conf;

	@Before
	public void before(){
		reset(kafkaTemplate);
	}

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
	public void mustSendAndWaitMessageCommitWhenTransactionIsActive() throws Exception {

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
	public void mustThrowExceptionAndRollbackTransactionWhenKafkaPostFail() throws Exception {

		// arrange
		final SettableListenableFuture<SendResult> future = spy(new SettableListenableFuture());
		doThrow(TimeoutException.class).when(future).get();

		doReturn(future).when(kafkaTemplate).send(any(ProducerRecord.class));
		Executors.newSingleThreadScheduledExecutor().submit(() -> {
			sleep(500);
			future.setException(new TimeoutException("fail to post message"));
		});

		// act
		try {
			conf.send();
			fail("transaction must rollback");
		} catch (KafkaPostException e){
			// assert
			verify(future, times(1)).get();
		}

	}

	static class Conf {

		@Autowired
		private MessageSender messageSender;

		@Bean
		@Primary
		public KafkaTemplate kafkaTemplate() {
			return mock(KafkaTemplate.class);
		}

		@Bean
		@Primary
		public MessageSender messageSender(KafkaTemplate kafkaTemplate) {
			return new MessageSenderImpl(kafkaTemplate);
		}

		@Transactional
		public void send(int times) {
			for (int i = 0; i < times; i++) {
				messageSender.send(new ProducerRecord("myTopic", "value"));
			}
		}

		@EnsureKafkaPost
		public ListenableFuture send() {
			return messageSender.send(new ProducerRecord<>("myTopic", "value"));
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
