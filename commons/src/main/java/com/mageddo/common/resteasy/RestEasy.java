package com.mageddo.common.resteasy;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configuration;
import java.time.Duration;

public final class RestEasy {

	private RestEasy() {
	}

	public static final String SOCKET_TIMEOUT = "SOCKET_TIMEOUT";
	public static final String CONNECT_TIMEOUT = "CONNECT_TIMEOUT";
	public static final String CONNECTION_REQUEST_TIMEOUT = "CONNECTION_REQUEST_TIMEOUT";
	public static final String FOLLOW_REDIRECTS = "FOLLOW_REDIRECTS";

	public static Client newClient(){
		return newClient(10);
	}

	public static ResteasyClientBuilder newRestEasyBuilder(int poolSize){
		final PoolingHttpClientConnectionManager pool = new PoolingHttpClientConnectionManager();
		pool.setMaxTotal(poolSize);
		// Senao vai estrangular o pool e deixar apenas duas conexoes serem usadas por host
		pool.setDefaultMaxPerRoute(poolSize);
		final CloseableHttpClient httpClient = HttpClientBuilder
			.create()
			.setDefaultRequestConfig(
				RequestConfig.custom()
					.setConnectionRequestTimeout(3_000)
					.setConnectTimeout(500)
					.setSocketTimeout(30_000)
					.setRedirectsEnabled(false)
					.build()
			)
			.setConnectionManager(pool)
			.build();
		return new ResteasyClientBuilder()
			.httpEngine(withPerRequestTimeout(httpClient))
		;
	}

	public static RestEasyClient newRestEasyClient(int poolSize){
		return new RestEasyClient(newRestEasyBuilder(poolSize).build());
	}

	public static Client newClient(int poolSize){
		return newRestEasyClient(poolSize).getClient();
	}

	static Integer parseIntegerOrNull(Configuration conf, String key) {
		final Object property = conf.getProperty(key);
		if(property == null){
			return null;
		}
		if(property instanceof Duration){
			return Long.valueOf(((Duration) property).toMillis()).intValue();
		}
		return ((Number) property).intValue();
	}

	static Boolean parseBooleanOrNull(Configuration conf, String key) {
		final Object property = conf.getProperty(key);
		if(property == null){
			return null;
		}
		return (Boolean) property;
	}

	/**
	 * Allow to set timeout per request
	 * @see #CONNECTION_REQUEST_TIMEOUT
	 * @see #CONNECT_TIMEOUT
	 * @see #SOCKET_TIMEOUT
	 */
	static ClientHttpEngine withPerRequestTimeout(HttpClient httpClient) {
		return new ApacheHttpClient43Engine(httpClient){
			@Override
			protected void loadHttpMethod(ClientInvocation request, HttpRequestBase httpMethod) throws Exception {
				super.loadHttpMethod(request, httpMethod);
				final RequestConfig.Builder reqConf = httpMethod.getConfig() != null ? RequestConfig.copy(httpMethod.getConfig()) : RequestConfig.custom();
				final Configuration conf = request.getConfiguration();

				final Integer connectionRequestTimeout = parseIntegerOrNull(conf, RestEasy.CONNECTION_REQUEST_TIMEOUT);
				if(connectionRequestTimeout != null){
					reqConf.setConnectionRequestTimeout(connectionRequestTimeout);
				}

				final Integer connectTimeout = parseIntegerOrNull(conf, RestEasy.CONNECT_TIMEOUT);
				if(connectTimeout != null) {
					reqConf.setConnectTimeout(connectTimeout);
				}

				final Integer socketTimeout = parseIntegerOrNull(conf, RestEasy.SOCKET_TIMEOUT);
				if(socketTimeout != null) {
					reqConf.setSocketTimeout(socketTimeout);
				}

				final Boolean followRedirects = parseBooleanOrNull(conf, RestEasy.FOLLOW_REDIRECTS);
				if(followRedirects != null) {
					reqConf.setRedirectsEnabled(followRedirects);
				}
				httpMethod.setConfig(reqConf.build());
			}
		};
	}
}
