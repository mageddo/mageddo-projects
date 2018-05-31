package com.mageddo.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.ListenableFuture;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {})
@ContextConfiguration(classes = MessageSenderImplTest.class)
public class MessageSenderImplTest {

	@Autowired
	private KafkaTemplate kafkaTemplate;

	@Autowired
	private MessageSender messageSender;

	@Test
	public void mustSendWithWithoutWaitMessageCommitWhenThereIsNoDatabaseTransaction() throws Exception {

		// arrange
		doReturn(mock(ListenableFuture.class)).when(kafkaTemplate).send(any(ProducerRecord.class));

		// act
		final ListenableFuture<SendResult> listenableFuture = messageSender.send(new ProducerRecord("myTopic", "value"));

		// assert
		verify(listenableFuture, never()).get();
	}


	@Bean
	@Primary
	public KafkaTemplate kafkaTemplate(){
		return mock(KafkaTemplate.class);
	}

	@Bean
	@Primary
	public MessageSender messageSender(){
		return new MessageSenderImpl(kafkaTemplate);
	}
}
