package kafka;

import java.time.Duration;
import java.util.Map;

public interface Topic {

	String getName();

	String getDlq();

	Integer getConsumers();

	String getFactory();

	Duration getInterval();

	Duration getMaxInterval();

	Integer getMaxTries();

	boolean isAutoConfigure();

	Map<String, Object> getProps();

	String getGroupId();
}
