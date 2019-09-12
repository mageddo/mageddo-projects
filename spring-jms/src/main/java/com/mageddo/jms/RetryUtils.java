package com.mageddo.jms;

import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

public class RetryUtils {

	public static RetryTemplate createConfiguredRetryTemplate(RetryStrategy retryStrategy) {
		RetryTemplate retryTemplate = retryStrategy.getRetryTemplate();
		if(retryTemplate == null){
			retryTemplate = getRetryTemplate(retryStrategy.getBackOffPolicy(), retryStrategy.getRetryPolicy());
		}
		return retryTemplate;
	}

	private static RetryTemplate getRetryTemplate(BackOffPolicy policy, RetryPolicy retryPolicy) {
		final RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setBackOffPolicy(policy);
		retryTemplate.setRetryPolicy(retryPolicy);
		retryTemplate.setThrowLastExceptionOnExhausted(true);
		retryTemplate.registerListener(new SimpleRetryListener());
		return retryTemplate;
	}
}
