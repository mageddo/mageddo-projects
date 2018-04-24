package com.mageddo.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mageddo.kafka.HeaderKeys;
import com.mageddo.kafka.KafkaUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

import java.util.Collection;

import static org.springframework.transaction.interceptor.TransactionAspectSupport.currentTransactionStatus;
import static org.springframework.transaction.support.TransactionSynchronizationManager.isSynchronizationActive;
import static org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization;

@Component
public class MessageSenderImpl implements MessageSender {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public MessageSenderImpl(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = new ObjectMapper();
	}

	@Override
	public void send(ProducerRecord r){
		if(isSynchronizationActive() && !currentTransactionStatus().isRollbackOnly()){
			registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override
				public void afterCommit() {
					logger.debug("m=send, status=transactional");
					doSend(r);
				}
			});
		}else{
			logger.debug("m=send, status=no-transactional");
			doSend(r);
		}
	}

	private void doSend(ProducerRecord r) {
		try {
			kafkaTemplate.send(r).get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(String topic, Collection list){
		for (Object o : list) {
			send(topic, o);
		}
	}

	@Override
	public void send(String topic, Versioned o){
		send(topic, o, null);
	}

	@Override
	public void send(String topic, Versioned o, String key){
		try {
			final ProducerRecord r = new ProducerRecord<>(topic, key, objectMapper.writeValueAsBytes(o));
			r.headers().add(new RecordHeader(HeaderKeys.VERSION, o.version().toFullString().getBytes()));
			send(r);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(String topic, String o){
		send(new ProducerRecord(topic, o));
	}

	@Override
	public void send(String topic, ConsumerRecord r){
		send(new ProducerRecord<>(topic, null, r.key(), r.value(), r.headers()));
	}

	@Override
	public void sendDLQ(ConsumerRecord r){
		send(new ProducerRecord<>(KafkaUtils.getDLQ(r.topic()), null, r.key(), r.value(), r.headers()));
	}

	@Override
	public void send(String topic, Object o){
		try {
			send(topic, o, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(String topic, Object o, String key){
		try {
			send(new ProducerRecord<>(topic, key, objectMapper.writeValueAsBytes(o)));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
