package com.mageddo.common.resteasy.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mageddo.common.jackson.JsonUtils;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes({"application/json", "application/*+json", "text/json"})
@Produces({"application/json", "application/*+json", "text/json"})
public class CustomJacksonJsonProvider extends ResteasyJackson2Provider {

	private final ObjectMapper objectMapper;

	public CustomJacksonJsonProvider() {
		this.objectMapper = JsonUtils.instance();
	}

	public CustomJacksonJsonProvider(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public ObjectMapper locateMapper(Class<?> type, MediaType mediaType) {
		return objectMapper;
	}
}
