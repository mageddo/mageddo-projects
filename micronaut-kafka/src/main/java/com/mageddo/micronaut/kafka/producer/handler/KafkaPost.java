//package com.mageddo.micronaut.kafka.producer.handler;
//
//import org.apache.kafka.clients.producer.RecordMetadata;
//
//import java.util.concurrent.Future;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class KafkaPost {
//
//	private final AtomicInteger expectToSend;
//	private final AtomicInteger success;
//	private final AtomicInteger error;
//	private Future<RecordMetadata> lastMessageSent;
//	private boolean enabled;
//	private Throwable lastError;
//
//	public KafkaPost() {
//		this.error = new AtomicInteger();
//		this.success = new AtomicInteger();
//		this.expectToSend = new AtomicInteger();
//		this.enabled = false;
//	}
//
//	public KafkaPost addExpectSent(Future<RecordMetadata> lastSentRecord){
//		this.expectToSend.incrementAndGet();
//		this.lastMessageSent = lastSentRecord;
//		return this;
//	}
//
//	public KafkaPost addError(Throwable t){
//		error.incrementAndGet();
//		this.lastError = t;
//		return this;
//	}
//
//	public KafkaPost addSuccess(){
//		success.incrementAndGet();
//		return this;
//	}
//
//	public int getExpectToSend() {
//		return expectToSend.get();
//	}
//
//	public int getSuccess() {
//		return success.get();
//	}
//
//	public int getError() {
//		return error.get();
//	}
//
//	public int getTotal(){
//		return getError() + getSuccess();
//	}
//
//	public boolean allProcessed(){
//		return getExpectToSend() == getTotal();
//	}
//
//	public Future<RecordMetadata> getLastMessageSent() {
//		return lastMessageSent;
//	}
//
//	public boolean isEnabled() {
//		return enabled;
//	}
//
//	public KafkaPost enable(){
//		this.enabled = true;
//		return this;
//	}
//
//	public KafkaPost disable(){
//		this.enabled = false;
//		return this;
//	}
//
//	public Throwable getLastError() {
//		return lastError;
//	}
//}
