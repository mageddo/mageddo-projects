package com.mageddo.kafka.producer.handler;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

@Aspect
@RequiredArgsConstructor
public class KafkaPostCheckerAspect {

	private final ThreadLocal<KafkaPost> kafkaPostThreadLocal;
	private final KafkaPostChecker kafkaPostChecker;

	@Around("@annotation(EnsureKafkaPost)")
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

	@Around("execution(* *..*MessageSender.*(..))")
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
