Trace methods execution and get time metrics

Config.java
```java
@Bean
public LogHandler aspectHandler(){
	new LogHandler();
}
```

MyService.java
```java
class MyService {

	@LogTracer
	public void traceMe(){
		System.out.println("trace me!");
	}
}
```