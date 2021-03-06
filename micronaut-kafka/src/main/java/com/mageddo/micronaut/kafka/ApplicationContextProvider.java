package com.mageddo.micronaut.kafka;

import io.micronaut.context.ApplicationContext;

public class ApplicationContextProvider  {

	private static ApplicationContext context;

	public ApplicationContextProvider(final ApplicationContext context) {
		ApplicationContextProvider.context = context;
	}

	public static ApplicationContext context() {
		return context;
	}
}
