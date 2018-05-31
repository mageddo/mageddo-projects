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
import org.springframework.kafka.support.SendResult;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StopWatch;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.transaction.support.TransactionSynchronizationManager.*;

public class MessageSenderImpl implements MessageSender {

	public static final String KAFKA_TRANSACTION = "kafka_transaction";
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private KafkaTemplate<String, byte[]> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public MessageSenderImpl(KafkaTemplate<String, byte[]> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = new ObjectMapper();
	}

	@Override
	public ListenableFuture<SendResult> send(ProducerRecord r) {

		if (!isSynchronizationActive()) {
			try {
				return kafkaTemplate.send(r);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		if (!hasResource(KAFKA_TRANSACTION)) {
			bindResource(KAFKA_TRANSACTION, new LinkedList<>());
			registerSynchronization(new TransactionSynchronization() {
				@Override
				public void beforeCommit(boolean readOnly) {
				final StopWatch stopWatch = new StopWatch();
				stopWatch.start();
				final List<ListenableFuture> transactions = getTransactions();
				try {
					for (final ListenableFuture listenableFuture : transactions) {
						listenableFuture.get();
					}
				} catch (Exception e) {
					logger.info("m=send, status=rollback, records={}, time={}", transactions.size(), stopWatch.getTotalTimeMillis());
					throw new RuntimeException(e);
				} finally {
					unbindResource(KAFKA_TRANSACTION);
				}
				logger.info("m=send, status=committed, records={}, time={}", transactions.size(), stopWatch.getTotalTimeMillis());
				}
			});
		}
		final ListenableFuture<SendResult> listenableFuture = kafkaTemplate.send(r);
		getTransactions().add(listenableFuture);
		return listenableFuture;
	}

	@Override
	public void send(String topic, Collection list){
		for (Object o : list) {
			send(topic, o);
		}
	}

	@Override
	public void send(String topic, Versioned o){
		send(topic, null, o);
	}

	@Override
	public void send(String topic, String key, Versioned o){
		try {
			final ProducerRecord r = new ProducerRecord<>(topic, key, objectMapper.writeValueAsBytes(o));
			r.headers().add(new RecordHeader(HeaderKeys.VERSION, o.version().toFullString().getBytes()));
			send(r);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(String topic, String v){
		send(new ProducerRecord(topic, v));
	}

	@Override
	public void send(String topic, ConsumerRecord r){
		send(new ProducerRecord<>(topic, null, r.key(), r.value(), r.headers()));
	}

	@Override
	public void sendDLQ(ConsumerRecord r){
		sendDLQ(KafkaUtils.getDLQ(r.topic()), r);
	}

	@Override
	public void sendDLQ(String dlqTopic, ConsumerRecord r){
		send(new ProducerRecord<>(dlqTopic, null, r.key(), r.value(), r.headers()));
	}

	@Override
	public void send(String topic, Object v){
		try {
			send(topic, null, v);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(String topic, String key, Object v){
		try {
			send(new ProducerRecord<>(topic, key, objectMapper.writeValueAsBytes(v)));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private List<ListenableFuture> getTransactions() {
		return (List<ListenableFuture>) TransactionSynchronizationManager.getResource(KAFKA_TRANSACTION);
	}
}
