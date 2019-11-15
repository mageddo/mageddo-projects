package com.mageddo.micronaut.kafka;

import com.mageddo.kafka.producer.MessageSender;
import io.micronaut.context.annotation.Primary;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@MicronautTest
class FruitMDBTest {

	@Inject
	private MessageSender messageSender;

	@Inject
	private FruitMDB fruitMDB;

	@Inject
	private MicronautKafkaConfig micronautKafkaConfig;

	@BeforeEach
	public void before() throws InterruptedException {
		// wait kafka startup
		TimeUnit.SECONDS.sleep(5);
	}

	@Test
	void shouldPostAndConsume() throws InterruptedException {
		// arrange

		// act
		messageSender.send(new ProducerRecord<>(TopicEnum.Constants.FRUIT_TOPIC, "Banana".getBytes()));
		// waiting consuming
		TimeUnit.SECONDS.sleep(2);

		// assert
		assertEquals(1, fruitMDB.records.size());
		assertEquals("Banana", new String(fruitMDB.records.get(0).value()));
	}


	@Test
	void shouldRetryAndSendToDlqWhenExhausted() throws InterruptedException {
		// arrange

		// act
		messageSender.send(new ProducerRecord<>(TopicEnum.Constants.FRUIT_TOPIC, "Apple".getBytes()));
		// waiting consuming
		TimeUnit.SECONDS.sleep(2);

		// assert
		assertTrue(fruitMDB.records.isEmpty());
		assertEquals(2, fruitMDB.retried.size());
		verify(messageSender, times(2)).send(any());
	}

	@Primary
	@MockBean(MessageSender.class)
	public MessageSender mockKafkaProducer(MessageSender messageSender) {
		return Mockito.spy(messageSender);
	}
}
