package com.mageddo.kafka.consumer;

import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

public interface RetryStrategy {

	RetryTemplate getRetryTemplate();
	BackOffPolicy getBackOffPolicy();
	RetryPolicy getRetryPolicy();

	static Map<Class<? extends Throwable>, Boolean> toMap(Class<? extends Throwable>... throwables){
		final Map<Class<? extends Throwable>, Boolean> m = new HashMap<>();
		for (Class<? extends Throwable> t : throwables) {
			m.put(t, true);
		}
		return m;
	}
}
