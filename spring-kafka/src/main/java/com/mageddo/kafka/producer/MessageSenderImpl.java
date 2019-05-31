package com.mageddo.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mageddo.kafka.CommitPhase;
import com.mageddo.kafka.HeaderKeys;
import com.mageddo.kafka.KafkaUtils;
import com.mageddo.kafka.exception.KafkaPostException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.springframework.transaction.support.TransactionSynchronizationManager.isSynchronizationActive;
import static org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization;
import static org.springframework.util.Assert.isTrue;

/**
 * All messages sent to kafka on this Service are transactional and just will permit the database transaction
 * to commit after Kafka ACK for all those sent messages. If you wanna to send messages without a transaction please
 * do not open a transaction or check {@link MessageSenderAsync} methods, they are not transactional
 */
public class MessageSenderImpl implements MessageSender, MessageSenderAsync {

	private final ThreadLocal<MessageStatus> messageStatusThreadLocal = ThreadLocal.withInitial(MessageStatus::new);
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final KafkaTemplate<String, byte[]> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public MessageSenderImpl(KafkaTemplate<String, byte[]> kafkaTemplate) {
		this(kafkaTemplate, new ObjectMapper());
	}

	public MessageSenderImpl(KafkaTemplate<String, byte[]> kafkaTemplate, ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}

	@Override
	public ListenableFuture<SendResult> send(ProducerRecord r) {
		return send(r, CommitPhase.BEFORE_COMMIT);
	}

	@Override
	public ListenableFuture<SendResult> send(ProducerRecord r, CommitPhase commitPhase) {
		if (!isSynchronizationActive()) {
			return kafkaTemplate.send(r);
		}

		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		final MessageStatus messageStatus = messageStatusThreadLocal.get();
		final ListenableFuture<SendResult> sendResultListenableFuture;
		if(commitPhase == CommitPhase.AFTER_COMMIT){
			registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override
				public void afterCommit() {
					processMessageSend(r, messageStatus);
				}
			});
			sendResultListenableFuture = new FakeListenableFuture();
		} else {
			sendResultListenableFuture = processMessageSend(r, messageStatus);
		}
		registerPostCheck(commitPhase, messageStatus);
		return sendResultListenableFuture;
	}

	private void registerPostCheck(CommitPhase commitPhase, MessageStatus messageStatus) {
		if(!messageStatus.getSynchronizationRegistered()) {
			registerSynchronization(new TransactionSynchronizationAdapter() {
				@Override
				public void beforeCommit(boolean readOnly) {
					if(commitPhase == CommitPhase.BEFORE_COMMIT){
						kafkaPostEnsure(messageStatus);
					}
				}

				@Override
				public void afterCommit() {
					if(commitPhase == CommitPhase.AFTER_COMMIT){
						kafkaPostEnsure(messageStatus);
					}
				}

				@Override
				public void afterCompletion(int status) {
					messageStatusThreadLocal.remove();
				}
			});
			messageStatus.setSynchronizationRegistered(true);
		}
	}

	private ListenableFuture<SendResult> processMessageSend(ProducerRecord r, MessageStatus messageStatus) {
		final ListenableFuture<SendResult> listenableFuture = kafkaTemplate.send(r);
		messageStatus.setLastMessageSent(listenableFuture);
		messageStatus.addExpectSent();

		listenableFuture.addCallback(
		it -> {
			messageStatus.addSuccess();
		},
		throwable -> {
			messageStatus.addError();
		});
		return listenableFuture;
	}

	@Override
	public List<ListenableFuture<SendResult>> send(String topic, Collection list){
		final List<ListenableFuture<SendResult>> results = new ArrayList<>();
		for (Object o : list) {
			results.add(send(topic, o));
		}
		return results;
	}

	@Override
	public ListenableFuture<SendResult> send(String topic, Versioned o){
		return send(topic, null, o);
	}

	@Override
	public ListenableFuture<SendResult> send(String topic, String key, Versioned o){
		try {
			final ProducerRecord r = new ProducerRecord<>(topic, key, objectMapper.writeValueAsBytes(o));
			r.headers().add(new RecordHeader(HeaderKeys.VERSION, o.version().toFullString().getBytes()));
			return send(r);
		} catch (JsonProcessingException e) {
			throw new KafkaPostException(e);
		}
	}

	@Override
	public ListenableFuture<SendResult> send(String topic, String v){
		return send(new ProducerRecord<>(topic, v.getBytes()));
	}

	@Override
	public ListenableFuture<SendResult> send(String topic, ConsumerRecord r){
		return send(new ProducerRecord<>(topic, null, r.key(), r.value(), r.headers()));
	}

	@Override
	public ListenableFuture<SendResult> sendDLQ(ConsumerRecord r){
		return sendDLQ(KafkaUtils.getDLQ(r.topic()), r);
	}

	@Override
	public ListenableFuture<SendResult> sendDLQ(String dlqTopic, ConsumerRecord r){
		return send(new ProducerRecord<>(dlqTopic, null, r.key(), r.value(), r.headers()));
	}

	@Override
	public ListenableFuture<SendResult> send(String topic, Object v){
		return send(topic, null, v);
	}

	@Override
	public ListenableFuture<SendResult> send(String topic, String key, Object v){
		try {
			return send(new ProducerRecord<>(topic, key, objectMapper.writeValueAsBytes(v)));
		} catch (JsonProcessingException e) {
			throw new KafkaPostException(e);
		}
	}

	@Override
	public ListenableFuture<SendResult> sendAsync(ProducerRecord r) {
		return kafkaTemplate.send(r);
	}

	private void kafkaPostEnsure(MessageStatus messageStatus) {
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		try {
			isTrue(messageStatus.getExpectToSend() > 0, "At least one message should be expected to be sent");
			for(; !messageStatus.allProcessed() ;) {

				if (messageStatus.getError() != 0) {
					throw new KafkaPostException(String.format("an error occurred on message post, errors=%d", messageStatus.getError()));
				}
				if(messageStatus.getLastMessageSent().isDone()){
					waitSomeTime();
				} else {
					try {
						messageStatus.getLastMessageSent().get();
					} catch (Exception e) {
						throw new KafkaPostException(e);
					}
				}
			}
			logger.debug(
				"m=send, status=committed, expectToSend={}, sent={}, error={}, time={}",
				messageStatus.getExpectToSend(), messageStatus.getSuccess(), messageStatus.getError(), stopWatch.getTotalTimeMillis()
			);
		} catch (KafkaPostException e) {
			logger.warn(
				"m=send, status=rollback, expectToSend={}, sent={}, error={}, time={}",
				messageStatus.getExpectToSend(), messageStatus.getSuccess(), messageStatus.getError(), stopWatch.getTotalTimeMillis()
			);
			throw e;
		}
	}

	private void waitSomeTime() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
