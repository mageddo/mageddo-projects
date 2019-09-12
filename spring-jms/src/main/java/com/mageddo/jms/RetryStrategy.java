package com.mageddo.jms;

import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

public class RetryStrategy {

	private RetryTemplate retryTemplate;
	private BackOffPolicy backOffPolicy;
	private RetryPolicy retryPolicy;

	public RetryTemplate getRetryTemplate() {
		return retryTemplate;
	}

	public RetryStrategy setRetryTemplate(RetryTemplate retryTemplate) {
		this.retryTemplate = retryTemplate;
		return this;
	}

	public BackOffPolicy getBackOffPolicy() {
		return backOffPolicy;
	}

	public RetryStrategy setBackOffPolicy(BackOffPolicy backOffPolicy) {
		this.backOffPolicy = backOffPolicy;
		return this;
	}

	public RetryPolicy getRetryPolicy() {
		return retryPolicy;
	}

	public RetryStrategy setRetryPolicy(RetryPolicy retryPolicy) {
		this.retryPolicy = retryPolicy;
		return this;
	}

	public static Map<Class<? extends Throwable>, Boolean> toMap(Class<? extends Throwable>... throwables){
		final Map<Class<? extends Throwable>, Boolean> m = new HashMap<>();
		for (Class<? extends Throwable> t : throwables) {
			m.put(t, true);
		}
		return m;
	}

	public static RetryStrategy builder(){
		return new RetryStrategy();
	}
}
