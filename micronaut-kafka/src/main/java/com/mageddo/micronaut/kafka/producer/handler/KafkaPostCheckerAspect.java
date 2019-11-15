//package com.mageddo.micronaut.kafka.producer.handler;
//
//import io.micronaut.aop.Around;
//import io.micronaut.aop.MethodInterceptor;
//import io.micronaut.aop.MethodInvocationContext;
//import io.micronaut.core.type.MutableArgumentValue;
//import lombok.RequiredArgsConstructor;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.util.concurrent.ListenableFuture;
//
//import javax.inject.Singleton;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Optional;
//
//@Singleton
//@RequiredArgsConstructor
//public class KafkaPostCheckerAspect implements MethodInterceptor<Object, Object> {
//
//	private final ThreadLocal<KafkaPost> kafkaPostThreadLocal;
//	private final KafkaPostChecker kafkaPostChecker;
//
//	@Override
//	public Object intercept(MethodInvocationContext<Object, Object> context) {
//
//
//
//		return context.proceed();
//
//	}
//
//	@Around("@annotation(EnsureKafkaPost)")
//	public Object enableEnsureKafkaPost(ProceedingJoinPoint joinPoint) throws Throwable {
//		try {
//			final KafkaPost kafkaPost = kafkaPostThreadLocal.get();
//			kafkaPost.enable();
//			final Object proceed = joinPoint.proceed();
//			kafkaPostChecker.ensureKafkaPost(kafkaPost);
//			return proceed;
//		} finally {
//			kafkaPostThreadLocal.remove();
//		}
//	}
//
//	@Around("execution(* *..*MessageSender.*(..))")
//	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//		final KafkaPost kafkaPost = kafkaPostThreadLocal.get();
//		if(!kafkaPost.isEnabled()){
//			return joinPoint.proceed();
//		}
//		final ListenableFuture<SendResult> proceed = (ListenableFuture<SendResult>) joinPoint.proceed();
//		kafkaPost.addExpectSent(proceed);
//		proceed.addCallback(it -> kafkaPost.addSuccess(), kafkaPost::addError);
//		return proceed;
//	}
//
//}
