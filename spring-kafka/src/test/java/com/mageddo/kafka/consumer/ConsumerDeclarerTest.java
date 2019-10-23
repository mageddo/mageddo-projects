package com.mageddo.kafka.consumer;

import com.mageddo.kafka.SpringKafkaConfig;
import com.mageddo.kafka.producer.MessageSender;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

import static com.mageddo.kafka.consumer.TopicEnum.Constants.FRUIT_TOPIC;
import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = {ConsumerDeclarerTest.Conf.class})
@SpringBootApplication
@RunWith(SpringRunner.class)
public class ConsumerDeclarerTest {

	@ClassRule
	public static final EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(
		1, false, FRUIT_TOPIC
	)
	.kafkaPorts(9092);

	@Autowired
	private MessageSender messageSender;

	@Autowired
	private FruitConsumerMDB fruitConsumerMDB;

	@Test
	public void mustPostToKafkaAndConsume() throws InterruptedException {

		// arrange

		// act
		messageSender.send(new ProducerRecord<>(FRUIT_TOPIC, "Hello World".getBytes()));

		// assert
		Thread.sleep(Duration.ofSeconds(2).toMillis());
		assertEquals(1, fruitConsumerMDB.getConsumedRecords().size());

	}


	@Configuration
	@Import(SpringKafkaConfig.class)
	public static class Conf implements InitializingBean {

		@Autowired
		private ConsumerDeclarer consumerDeclarer;

		@Override
		public void afterPropertiesSet() throws Exception {
			consumerDeclarer.declare(TopicEnum.FRUIT.getTopic());
		}
	}
}
