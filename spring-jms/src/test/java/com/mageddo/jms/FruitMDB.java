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

import static com.mageddo.jms.TopicEnum.Constants.FRUIT_FACTORY;
import static com.mageddo.jms.TopicEnum.Constants.FRUIT_TOPIC;

@Component
public class FruitMDB {

	public static final List<String> messages = new ArrayList<>();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@JmsListener(containerFactory = FRUIT_FACTORY, destination = FRUIT_TOPIC)
	public void consume(ActiveMQTextMessage message) throws JMSException {
		final var msg = message.getText();
		messages.add(msg);
		logger.info("msg={}", msg);
	}
}
