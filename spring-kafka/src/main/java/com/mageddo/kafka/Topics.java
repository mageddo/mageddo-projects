package com.mageddo.kafka;

import com.mageddo.kafka.consumer.RetryStrategy;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Value
@Builder(builderClassName = "TopicBuilder")
public class Topics implements SpringTopic {

	@NonNull
	private String name;

	@NonNull
	private String dlq;

	@NonNull
	private String factory;

	@NonNull
	private Integer consumers;

	private Duration interval;

	@NonNull
	private Duration maxInterval;

	@NonNull
	private Integer maxTries;

	private Map<String, Object> props;
	private RetryStrategy retryStrategy;
	private String groupId;

	private AckMode ackMode = AckMode.RECORD;
	private boolean autoConfigure = true;

	public static class TopicBuilder {

		public TopicBuilder name(String name){
			this.name = name;
//			dlqName(KafkaUtils.getDLQ(name));
			return this;
		}

		public TopicBuilder autoGroupId(){
			Validate.notNull(this.name, "must set topic name first");
			final String groupIdPrefix = System.getProperty("kafka.group.id.prefix", "group");
			this.groupId = String.format("%s_%s", groupIdPrefix, this.name);
			return this;
		}

		public TopicBuilder interval(Duration interval) {
			this.interval = interval;
			maxInterval(max(maxInterval, interval));
			return this;
		}

		public TopicBuilder maxInterval(Duration maxInterval) {
			this.maxInterval = max(maxInterval, this.interval);
			return this;
		}

		private Duration max(Duration a, Duration b) {
			return ObjectUtils.max(a, b);
		}

	}

	public static class MapBuilder {
		private final Map<String, Object> map;

		public MapBuilder(Map<String, Object> map) {
			this.map = map;
		}

		public static MapBuilder map() {
			return new MapBuilder(new HashMap<>());
		}

		public MapBuilder prop(String k, Object v) {
			this.map.put(k, v);
			return this;
		}

		public Map<String, Object> get() {
			return this.map;
		}
	}

}
