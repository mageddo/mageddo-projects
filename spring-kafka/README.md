[Here a working sample](https://github.com/mageddo/java-examples/tree/871c002/spring-kafka/src/main/java/com/mageddo/kafka)

Topic Definition
```java
public enum TopicEnum {

	USER_CRATED(new Topic("USER_CREATED")
		.autoConfigure(true)
		.factory(USER_CREATED_FACTORY)
		.consumers(5)
		.interval(Duration.ofSeconds(60))
		.maxTries(10)
		.props(map().prop("", false))
	);

	private final Topic topic;

	TopicEnum(Topic topic) {
		this.topic = topic;
	}

	public Topic getTopic() {
		return topic;
	}

	public static class Constants {
		public static final String USER_CREATED_FACTORY = "USER_CREATED_FACTORY";
	}

}
```

Config.java
```java
public class Config implements InitializingBean {

	@Autowired
	private ConsumerDeclarer consumerDeclarer;

	@Override
	public void afterPropertiesSet() {
		consumerDeclarer.declare(Stream.of(TopicEnum.values()).map(i -> i.getTopic()).collect(Collectors.toList()));
	}

	@Bean
	public ConsumerDeclarer consumerDeclarer(
		ConfigurableBeanFactory beanFactory, 
		KafkaProperties kafkaProperties,
		@Value("${spring.kafka.consumer.autostartup:true}") boolean autostartup /* startup consumer automatically */
	) {
		return new ConsumerDeclarer(beanFactory, kafkaProperties, autostartup);
	}

	@Bean
	public MessageSender messageSender(KafkaTemplate<String, byte[]> template) {
		return new MessageSenderImpl(template);
	}
```

Automatically created factory MDB
```java
@Component
public class ProfessionalCreatedMDB implements RecoveryCallback, TopicConsumer {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MessageSender messageSender;

	@Autowired
	private ObjectMapper objectMapper;

	@KafkaListener(topics = "#{__listener.topic().getName()}", containerFactory = USER_CREATED_FACTORY)
	public void consume(ConsumerRecord<?, byte[]> record) throws IOException {
		logger.info(objectMapper.readValue(record.value(), User.class));
	}

	@Override
	public TopicDefinition topic() {
		return TopicEnum.USER_CREATED.getTopic();
	}

	@Override
	public Object recover(RetryContext context) throws Exception {
		final ConsumerRecord record = KafkaUtils.getRecord(context);
		logger.error("status=send-dlq, topic={}, record={}", record.topic(), record, context.getLastThrowable());
		messageSender.sendDLQ(record);
		return null;
	}
}

```


Manuallly created factory MDB

Set `.autoConfigure(false)` then

```java
@Component
public class ProfessionalCreatedMDB implements RecoveryCallback, TopicConsumer {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MessageSender messageSender;

	@Autowired
	private ObjectMapper objectMapper;

	@KafkaListener(topics = "#{__listener.topic().getName()}", containerFactory = USER_CREATED_FACTORY)
	public void consume(ConsumerRecord<?, byte[]> record) throws IOException {
		logger.info(objectMapper.readValue(record.value(), User.class));
	}

	@Override
	public TopicDefinition topic() {
		return TopicEnum.USER_CREATED.getTopic();
	}

	@Override
	public Object recover(RetryContext context) throws Exception {
		final ConsumerRecord record = KafkaUtils.getRecord(context);
		logger.error("status=send-dlq, topic={}, record={}", record.topic(), record, context.getLastThrowable());
		messageSender.sendDLQ(record);
		return null;
	}

	@Bean(USER_CREATED_FACTORY)
	public KafkaListenerContainerFactory factory(KafkaProperties kafkaProperties,
		@Value("${spring.kafka.consumer.autostartup:true}") boolean autostartup){
		final ConcurrentKafkaListenerContainerFactory factory = new RetryableKafkaListenerContainerFactory();
		factory.setConcurrency(1);
		factory.setAutoStartup(autostartup);
		factory.getContainerProperties().setAckMode(topic().getAckMode());
		factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties()));
		factory.setRetryTemplate(getRetryTemplate());
		return factory;
	}

	RetryTemplate getRetryTemplate() {

		final ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(Duration.ofMinutes(1).toMillis());
		backOffPolicy.setMaxInterval(Duration.ofMinutes(20).toMillis());

		final RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.registerListener(new SimpleRetryListener());
		retryTemplate.setRetryPolicy(new SimpleRetryPolicy(10));
		retryTemplate.setBackOffPolicy(backOffPolicy);

		return retryTemplate;
	}
}

```


application.properties

```
spring.kafka.bootstrap-servers=kafka.intranet:9092

spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.ByteArraySerializer
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.ByteArrayDeserializer
spring.kafka.consumer.group-id=mg-mining
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.enable-auto-commit=false

spring.kafka.properties.session.timeout.ms=30000
spring.kafka.properties.max.poll.interval.ms=2147483647
```
