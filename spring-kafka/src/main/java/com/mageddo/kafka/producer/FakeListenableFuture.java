package com.mageddo.kafka.producer;

import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import java.util.concurrent.TimeUnit;

class FakeListenableFuture implements ListenableFuture {

	private void throwAfterCommitError() {
		throw new UnsupportedOperationException("This is a fake record because the SendResult only will be generated after the transaction commit");
	}

	@Override
	public void addCallback(ListenableFutureCallback callback) {
		throwAfterCommitError();
	}

	@Override
	public void addCallback(SuccessCallback successCallback, FailureCallback failureCallback) {
		throwAfterCommitError();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		throwAfterCommitError();
		return false;
	}

	@Override
	public boolean isCancelled() {
		throwAfterCommitError();
		return false;
	}

	@Override
	public boolean isDone() {
		throwAfterCommitError();
		return false;
	}

	@Override
	public Object get() {
		throwAfterCommitError();
		return null;
	}

	@Override
	public Object get(long timeout, TimeUnit unit) {
		throwAfterCommitError();
		return null;
	}
}
