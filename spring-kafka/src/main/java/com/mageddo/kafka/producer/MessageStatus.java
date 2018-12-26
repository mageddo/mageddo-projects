package com.mageddo.kafka.producer;

import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MessageStatus {

	private boolean synchronizationRegistered;
	private AtomicInteger expectToSend;
	private AtomicInteger success;
	private AtomicInteger error;
	private AtomicReference<ListenableFuture<SendResult>> lastMessageSent;

	public MessageStatus() {
		this.error = new AtomicInteger();
		this.success = new AtomicInteger();
		this.expectToSend = new AtomicInteger();
		this.lastMessageSent = new AtomicReference<>();
		this.synchronizationRegistered = false;
	}

	public MessageStatus addExpectSent(){
		expectToSend.incrementAndGet();
		return this;
	}

	public MessageStatus addError(){
		error.incrementAndGet();
		return this;
	}

	public MessageStatus addSuccess(){
		success.incrementAndGet();
		return this;
	}

	public int getExpectToSend() {
		return expectToSend.get();
	}

	public int getSuccess() {
		return success.get();
	}

	public int getError() {
		return error.get();
	}

	public int getTotal(){
		return getError() + getSuccess();
	}

	public boolean allProcessed(){
		return getExpectToSend() == getSuccess();
	}

	public ListenableFuture<SendResult> getLastMessageSent() {
		return lastMessageSent.get();
	}

	public MessageStatus setLastMessageSent(ListenableFuture<SendResult> lastMessageSent) {
		this.lastMessageSent.set(lastMessageSent);
		return this;
	}

	public boolean getSynchronizationRegistered() {
		return synchronizationRegistered;
	}

	public MessageStatus setSynchronizationRegistered(boolean synchronizationRegistered) {
		this.synchronizationRegistered = synchronizationRegistered;
		return this;
	}
}
