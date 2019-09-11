package com.mageddo.xpto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.ConnectionFactory;

@EnableJms
@SpringBootApplication
public class App {

	@Autowired
	ConnectionFactory connectionFactory;

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
