package com.mageddo.kafka;

public enum CommitPhase {

	/**
	 * Producer will ensure message was sent to kafka first then commit the database, if some message
	 * wasn't sent to kafka then producer will rollback the database transaction and an exception will be thrown
	 */
	BEFORE_COMMIT,

	/**
	 * Producer will commit the database transaction then ensure all messages were posted to kafka, if some message wasn't
	 * sent to kafka an exception will be thrown
	 */
	AFTER_COMMIT,
}
