package com.mageddo.jms;

import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

public class RetryUtils {

	public static RetryTemplate createConfiguredRetryTemplate(RetryStrategy retryStrategy) {
		RetryTemplate retryTemplate = retryStrategy.getRetryTemplate();
		if(retryTemplate == null){
			retryTemplate = getRetryTemplate(retryStrategy.getBackOffPolicy(), retryStrategy.getRetryPolicy());
		}
		return retryTemplate;
	}

	public static RetryTemplate createDefaultRetryTemplate(TopicDefinition topic) {
		final ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
		policy.setInitialInterval(topic.getInterval());
		policy.setMultiplier(1.0);
		policy.setMaxInterval(topic.getMaxInterval());
		final SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		retryPolicy.setMaxAttempts(topic.getMaxTries());
		return getRetryTemplate(policy, retryPolicy);
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
