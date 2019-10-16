package com.mageddo.kafka.consumer;

import com.mageddo.common.retry.RetryUtils;
import com.mageddo.kafka.KafkaUtils;
import com.mageddo.kafka.exception.KafkaPostException;
import com.mageddo.kafka.producer.MessageSender;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryContext;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

public abstract class DefaultConsumer implements RecoveryCallback<String>, Consumer {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	protected final MessageSender messageSender;

	protected DefaultConsumer(MessageSender messageSender) {
		this.messageSender = messageSender;
	}

	@Override
	public String recover(final RetryContext context) throws Exception {
		final ConsumerRecord<String, byte[]> record = KafkaUtils.getRecord(context);
		RetryUtils
		.retryTemplate(Integer.MAX_VALUE, Duration.ofSeconds(30), 1.0, Exception.class)
		.execute(ctx -> {
			try {
				logger.error(
					"status=recovered, record={}, {}", new String(record.value()), RetryUtils.format(ctx)
				);
				return messageSender.sendDLQ(topic().getDlqName(), record, context.getLastThrowable()).get();
			} catch (InterruptedException | ExecutionException e) {
				throw new KafkaPostException(e);
			}
		});
		return null;
	}
}
