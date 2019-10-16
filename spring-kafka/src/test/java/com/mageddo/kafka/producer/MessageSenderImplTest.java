package com.mageddo.kafka.producer;

import com.mageddo.kafka.SpringKafkaConfig;
import com.mageddo.kafka.producer.handler.EnsureKafkaPost;
import com.mageddo.kafka.producer.handler.KafkaPost;
import com.mageddo.kafka.producer.handler.KafkaPostChecker;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootApplication
@SpringBootTest(classes = {
	SpringKafkaConfig.class, MessageSenderImplTest.Conf.class
})
@RunWith(SpringRunner.class)
public class MessageSenderImplTest {

	@ClassRule
	public static final EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1)
		.kafkaPorts(9092);

	@SpyBean
	private KafkaPostChecker kafkaPostChecker;

	@Autowired
	private Conf conf;

	@Autowired
	private MessageSender messageSender;

	@Test
	public void mustSendAndWaitForMessageCommitOnKafka() {

		// arrange

		// act
		conf.send(new ProducerRecord<>(
			"myTopic", "value".getBytes()
		));

		// assert
		final var captor = ArgumentCaptor.forClass(KafkaPost.class);
		verify(kafkaPostChecker).ensureKafkaPost(captor.capture());
		assertEquals(0, captor.getValue().getError());
		assertEquals(1, captor.getValue().getSuccess());
		assertEquals(1, captor.getValue().getTotal());
		assertEquals(1, captor.getValue().getExpectToSend());

	}

	@Test
	public void mustSendAndWaitForMessageCommitOnKafkaForTwoMessagesOnDifferentMethodsCalls() {

		// arrange

		// act
		conf.send(new ProducerRecord<>(
			"myTopic", "value".getBytes()
		));
		conf.send(new ProducerRecord<>(
			"myTopic", "value 2".getBytes()
		));

		// assert
		final var captor = ArgumentCaptor.forClass(KafkaPost.class);
		verify(kafkaPostChecker, times(2)).ensureKafkaPost(captor.capture());
		final var values = captor.getAllValues();
		assertEquals(2, values.size());

	}

	@Test
	public void mustSendAndWaitForMessageCommitOnKafkaForTwoMessages() {

		// arrange

		// act
		conf.send(List.of(
			new ProducerRecord<>(
			"myTopic", "value".getBytes()
			),
			new ProducerRecord<>(
				"myTopic", "value 2".getBytes()
			)
		));

		// assert
		final var captor = ArgumentCaptor.forClass(KafkaPost.class);
		verify(kafkaPostChecker).ensureKafkaPost(captor.capture());
		assertEquals(0, captor.getValue().getError());
		assertEquals(2, captor.getValue().getSuccess());
		assertEquals(2, captor.getValue().getTotal());
		assertEquals(2, captor.getValue().getExpectToSend());

	}

	@Test
	public void mustSendAndDonWaitForMessageCommitOnKafka() {

		// arrange

		// act
		messageSender.send(new ProducerRecord<>(
			"myTopic", "value".getBytes()
		));

		// assert
		final var captor = ArgumentCaptor.forClass(KafkaPost.class);
		verify(kafkaPostChecker, never()).ensureKafkaPost(captor.capture());

	}

	@Configuration
	static class Conf {

		@Autowired
		private MessageSender messageSender;

		@EnsureKafkaPost
		public void send(List<ProducerRecord> records) {
			records.forEach(messageSender::send);
		}

		@EnsureKafkaPost
		public ListenableFuture send(ProducerRecord r) {
			return messageSender.send(r);
		}
	}

}
