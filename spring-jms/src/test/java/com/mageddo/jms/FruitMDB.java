package com.mageddo.jms;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mageddo.jms.TopicEnum.Constants.FRUIT_FACTORY;
import static com.mageddo.jms.TopicEnum.Constants.FRUIT_TOPIC;

@Component
public class FruitMDB implements TopicConsumer {

	public static final List<String> messages = new ArrayList<>();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@JmsListener(containerFactory = FRUIT_FACTORY, destination = FRUIT_TOPIC)
	public void consume(ActiveMQTextMessage message) throws Exception {
		withRetry(ctx -> {
			doConsume(message);
			return null;
		});
	}

	void doConsume(ActiveMQTextMessage message) throws JMSException {
		final var msg = Objects.requireNonNull(message.getText(), "message can't be null");
		messages.add(msg);
		logger.info("msg={}", msg);
	}

	@Override
	public TopicDefinition topic() {
		return TopicEnum.FRUIT.getTopic();
	}
}
