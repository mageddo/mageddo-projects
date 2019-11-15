package kafka;

import kafka.internals.ObjectUtils;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.Duration;

@Value
@Builder(builderClassName = "TopicBuilder")
public class Topics implements Topic {

	@NonNull
	private String name;

	@NonNull
	private String dlq;

	public static class TopicBuilder {

		public TopicBuilder name(String name){
			this.name = name;
			dlq(getDLQ(name));
			return this;
		}

		private Duration max(Duration a, Duration b) {
			return ObjectUtils.max(a, b);
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
