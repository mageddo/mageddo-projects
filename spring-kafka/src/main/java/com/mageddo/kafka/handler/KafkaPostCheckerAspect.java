package com.mageddo.kafka.handler;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
@Aspect
public class KafkaPostCheckerAspect {

	private final ThreadLocal<KafkaPost> kafkaPostThreadLocal;
	private final KafkaPostChecker kafkaPostChecker;

	public KafkaPostCheckerAspect() {
		this.kafkaPostThreadLocal = ThreadLocal.withInitial(KafkaPost::new);
		this.kafkaPostChecker = new KafkaPostChecker();
	}

	@Around("")
	public Object enableEnsureKafkaPost(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			final KafkaPost kafkaPost = kafkaPostThreadLocal.get();
			kafkaPost.enable();
			final Object proceed = joinPoint.proceed();
			kafkaPostChecker.ensureKafkaPost(kafkaPost);
			return proceed;
		} finally {
			kafkaPostThreadLocal.remove();
		}
	}

	@Around("")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		final KafkaPost kafkaPost = kafkaPostThreadLocal.get();
		if(!kafkaPost.isEnabled()){
			return joinPoint.proceed();
		}
		final ListenableFuture<SendResult> proceed = (ListenableFuture<SendResult>) joinPoint.proceed();
		kafkaPost.addExpectSent(proceed);
		proceed.addCallback(it -> kafkaPost.addSuccess(), kafkaPost::addError);
		return proceed;
	}

}
