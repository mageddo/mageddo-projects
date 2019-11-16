package com.mageddo.kafka.producer;

import com.mageddo.kafka.HeaderKeys;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Future;

@RequiredArgsConstructor
public class MessageSenderImpl implements MessageSender {

	private final Producer<String, byte[]> kafkaProducer;

	@Override
	public Future<RecordMetadata> send(ProducerRecord r) {
		return send(r, (Callback) null);
	}

	@Override
	public Future<RecordMetadata> send(ProducerRecord r, Callback callback) {
		return kafkaProducer.send(r, callback);
	}

	@Override
	public Future<RecordMetadata> send(ProducerRecord r, Throwable t) {
		return send(r, t, null);
	}

	@Override
	public Future<RecordMetadata> send(ProducerRecord r, Throwable t, Callback callback) {
		r
			.headers()
			.add(HeaderKeys.STACKTRACE, getStackTrace(t).getBytes())
		;
		return send(r, callback);
	}

	@Override
	public Future<RecordMetadata> sendDLQ(String dlqTopic, ConsumerRecord r, Throwable t) {
		return sendDLQ(dlqTopic, r, t, null);
	}

	@Override
	public Future<RecordMetadata> sendDLQ(String dlqTopic, ConsumerRecord r, Throwable t, Callback callback) {
		return send(
			new ProducerRecord<>(dlqTopic, null, r.key(), r.value(), r.headers()),
			t, callback
		);
	}

	private String getStackTrace(final Throwable throwable) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}
}
