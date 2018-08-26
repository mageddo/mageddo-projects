package com.mageddo.featureswitch.spring;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(prefix = "feature-switch", name = "enabled", matchIfMissing = true)
@Configuration
public class ApplicationContextProvider implements ApplicationContextAware {

	private static ApplicationContext context;

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}
}
