package com.mageddo.jms;

import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;

import java.time.Duration;
import java.util.Optional;

import static com.mageddo.common.retry.RetryUtils.retryTemplate;
import static com.mageddo.jms.RetryUtils.createConfiguredRetryTemplate;

public interface TopicConsumer {

	TopicDefinition topic();

	default <T>T withRetry(RetryCallback<T, Exception> retryCallback) throws Exception {
		return withRetry(retryCallback, null);
	}

	default <T>T withRetry(RetryCallback<T, Exception> retryCallback, RecoveryCallback<T> recoveryCallback) throws Exception {
		final RetryTemplate retryTemplate = getRetryTemplate();
		return retryTemplate.execute(retryCallback, recoveryCallback);
	}

	default RetryTemplate getRetryTemplate() {
		return Optional
			.ofNullable(topic().getRetryStrategy())
			.map(it -> createConfiguredRetryTemplate(topic().getRetryStrategy()))
			.orElseGet(() -> retryTemplate(
				topic().getMaxTries(), Duration.ofMillis(topic().getMaxInterval()), 1.5, Exception.class
			));
	}
}
