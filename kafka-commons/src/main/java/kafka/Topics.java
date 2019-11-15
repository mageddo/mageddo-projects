package kafka;

import kafka.internals.ObjectUtils;
import kafka.internals.Validate;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Value
@Builder(builderClassName = "TopicBuilder")
public class Topics implements Topic {

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
	private String groupId;

	private boolean autoConfigure = true;

	public static class TopicBuilder {

		public TopicBuilder name(String name){
			this.name = name;
			dlq(getDLQ(name));
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

	public static String nextTopic(String topic){
		return nextTopic(topic, 1);
	}

	public static String nextTopic(String topic, int retriesTopics){
		final int i = topic.toUpperCase().indexOf("_RETRY");
		if(i >= 0){
			return getDLQ(topic.substring(0, i));
		}
		return topic + "_RETRY";
	}

	public static String getDLQ(String topic){
		return String.format("%s_DLQ", topic);
	}

}
