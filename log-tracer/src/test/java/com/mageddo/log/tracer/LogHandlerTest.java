package com.mageddo.log.tracer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootTest
public class LogHandlerTest {

	@Autowired
	private PersonService personService;

	@Autowired
	private LogHandler logHandler;

	@Before
	public void before(){
		Mockito.reset(logHandler.logger);
	}

	@Test
	public void handleAnnotatedMethod() throws Throwable {
		// act
		assertEquals("hello Elvis!!!", personService.sayHello("Elvis", new Person("Bruna", 21)));

		// assert
		final ArgumentCaptor<String> pattern = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> traceInfo = ArgumentCaptor.forClass(String.class);
		verify(logHandler.logger).info(pattern.capture(), anyLong(), traceInfo.capture());

		assertEquals("h_status=success, h_time={}, {}", pattern.getValue());
		assertEquals("h_clazz=PersonService, h_m=sayHello, h_args=[Elvis, {name: Bruna, }], h_return=hello Elvis!!!, h_line=0", traceInfo.getValue());

	}

	@Test
	public void dontHandleNOTAnnotatedMethod() throws Throwable {
		// act
		assertEquals("hello Elvis, I'm not handled!!!", personService.sayHelloNotHandle("Elvis"));

		// assert
		verify(logHandler.logger, never()).info(anyString(), anyLong(), anyString());

	}

	@Test
	public void shouldLogCollectionSizeInMethodReturn() throws Throwable {
		// act
		assertEquals("[1, 2, 3]", personService.findByName("Elvis").toString());

		// assert
		final ArgumentCaptor<String> pattern = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> traceInfo = ArgumentCaptor.forClass(String.class);
		verify(logHandler.logger).info(pattern.capture(), anyLong(), traceInfo.capture());

		assertEquals("h_status=success, h_time={}, {}", pattern.getValue());
		assertEquals("h_clazz=PersonService, h_m=findByName, h_args=[Elvis], h_return=3, h_line=0", traceInfo.getValue());
	}

	@Test
	public void shouldLogCollectionSizeInMethodArgument() throws Throwable {
		// act
		personService.update(Arrays.asList("Elvis"));

		// assert
		final ArgumentCaptor<String> pattern = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> traceInfo = ArgumentCaptor.forClass(String.class);
		verify(logHandler.logger).info(pattern.capture(), anyLong(), traceInfo.capture());

		assertEquals("h_status=success, h_time={}, {}", pattern.getValue());
		assertEquals("h_clazz=PersonService, h_m=update, h_args=[1], h_return=null, h_line=0", traceInfo.getValue());
	}

	public static class Person {
		private String name;
		private int age;

		public Person(String name, int age) {
			this.name = name;
			this.age = age;
		}
	}

	// arrange
	@Component
	public static class PersonService {

		@Bean
		public LogHandler aspectHandler(){
			return Mockito.spy(new LogHandler(Mockito.mock(Logger.class)));
		}

		@LogTracer
		public String sayHello(String name, Person bruna){
			return String.format("hello %s!!!", name);
		}

		public String sayHelloNotHandle(String name){
			return String.format("hello %s, I'm not handled!!!", name);
		}

		@LogTracer
		public List<String> findByName(String name){
			return Arrays.asList("1", "2", "3");
		}

		@LogTracer
		public void update(List<String> items) {

		}
	}

}
