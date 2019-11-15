package com.mageddo.kafka.consumer;

import com.mageddo.common.retry.RetryUtils;
import com.mageddo.kafka.SpringKafkaConfig;
import com.mageddo.kafka.SpringKafkaUtils;
import com.mageddo.kafka.exception.KafkaPostException;
import com.mageddo.kafka.producer.MessageSender;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryContext;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

public interface DefaultConsumer extends RecoveryCallback<String>, Consumer {

	Logger LOG = LoggerFactory.getLogger(DefaultConsumer.class);

	default String recover(final RetryContext context) throws Exception {
		final ConsumerRecord<String, byte[]> record = SpringKafkaUtils.getRecord(context);
		RetryUtils
		.retryTemplate(Integer.MAX_VALUE, Duration.ofSeconds(30), 1.0, Exception.class)
		.execute(ctx -> {
			try {
				LOG.warn(
					"status=recovered, record={}, {}", new String(record.value()), RetryUtils.format(ctx)
				);
				return getMessageSender().sendDLQ(topic().getDlq(), record, context.getLastThrowable()).get();
			} catch (InterruptedException | ExecutionException e) {
				throw new KafkaPostException(e);
			}
		});
		return null;
	}

	default MessageSender getMessageSender() {
		return SpringKafkaConfig.context().getBean(MessageSender.class);
	}
}
