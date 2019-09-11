package com.mageddo.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

import static com.mageddo.jms.TopicEnum.Constants.FRUIT_FACTORY;
import static com.mageddo.jms.TopicEnum.Constants.FRUIT_TOPIC;

@Component
public class FruitMDB {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@JmsListener(containerFactory = FRUIT_FACTORY, destination = FRUIT_TOPIC)
	public void consume(Message message) throws JMSException {
		logger.info("msg={}", new String(message.getBody(byte[].class)));
	}
}
