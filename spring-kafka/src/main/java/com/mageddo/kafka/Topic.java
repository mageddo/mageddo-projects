package com.mageddo.kafka;

import com.mageddo.kafka.consumer.RetryStrategy;
import lombok.Builder;
import lombok.Value;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Value
@Builder(builderClassName = "TopicBuilder")
public class Topic implements TopicDefinition {

	private String name;
	private String dlq;
	private String factory;
	private int consumers;
	private Duration interval;
	private Duration maxInterval;
	private int maxTries;
	private AckMode ackMode = AckMode.RECORD;
	private boolean autoConfigure = true;
	private Map<String, Object> props;
	private RetryStrategy retryStrategy;
	private String groupId;

	public static class TopicBuilder {

		public TopicBuilder autoGroupId(){
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
			if(a.compareTo(b) < 0){
				return b;
			}
			return a;
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
