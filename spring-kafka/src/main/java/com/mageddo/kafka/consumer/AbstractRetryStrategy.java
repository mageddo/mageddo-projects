package com.mageddo.kafka.consumer;

import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

public class AbstractRetryStrategy implements RetryStrategy {

	@Override
	public RetryTemplate getRetryTemplate() {
		return null;
	}

	@Override
	public BackOffPolicy getBackOffPolicy() {
		return null;
	}

	@Override
	public RetryPolicy getRetryPolicy() {
		return null;
	}
}
