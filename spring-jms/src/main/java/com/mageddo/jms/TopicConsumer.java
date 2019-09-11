package com.mageddo.jms;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;

import java.util.Optional;

import static com.mageddo.jms.RetryUtils.createConfiguredRetryTemplate;
import static com.mageddo.jms.RetryUtils.createDefaultRetryTemplate;

public interface TopicConsumer {

	TopicDefinition topic();

	default <T>T withRetry(RetryCallback<T, Exception> retryCallback) throws Exception {
		final RetryTemplate retryTemplate = getRetryTemplate();
		return retryTemplate.execute(retryCallback);
	}

	default RetryTemplate getRetryTemplate() {
		return Optional
			.ofNullable(topic().getRetryStrategy())
			.map(it -> createConfiguredRetryTemplate(topic().getRetryStrategy()))
			.orElseGet(() -> createDefaultRetryTemplate(topic()));
	}
}
