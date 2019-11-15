//package com.mageddo.micronaut.kafka.producer.handler;
//
//import com.mageddo.micronaut.kafka.exception.KafkaPostException;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.Validate;
//import org.apache.commons.lang3.time.StopWatch;
//
//@Slf4j
//public class KafkaPostChecker {
//
//	public void ensureKafkaPost(KafkaPost kafkaPost) {
//		final StopWatch stopWatch = new StopWatch();
//		stopWatch.start();
//		try {
//			Validate.isTrue(
//				kafkaPost.getExpectToSend() > 0,
//				"At least one message should be expected to be sent"
//			);
//			for (; !kafkaPost.allProcessed(); ) {
//
//				if (kafkaPost.getError() != 0) {
//					throw new KafkaPostException(kafkaPost);
//				}
//				if (kafkaPost.getLastMessageSent().isDone()) {
//					waitSomeTime();
//				} else {
//					try {
//						kafkaPost.getLastMessageSent().get();
//					} catch (Exception e) {
//						throw new KafkaPostException(e);
//					}
//				}
//			}
//			log.debug(
//				"status=committed, expectToSend={}, sent={}, error={}, time={}",
//				kafkaPost.getExpectToSend(), kafkaPost.getSuccess(), kafkaPost.getError(), stopWatch.getTime()
//			);
//		} catch (KafkaPostException e) {
//			log.warn(
//				"status=rollback, expectToSend={}, sent={}, error={}, time={}",
//				kafkaPost.getExpectToSend(), kafkaPost.getSuccess(), kafkaPost.getError(), stopWatch.getTime(), e
//			);
//			throw e;
//		}
//	}
//
//	private static void waitSomeTime() {
//		try {
//			Thread.sleep(1000 / 60);
//		} catch (InterruptedException e) {
//			throw new RuntimeException(e);
//		}
//	}
//}
