package com.mageddo.common.resteasy;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configuration;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
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

	public static Client newClient(int poolSize){
		return newRestEasyClient(poolSize).getClient();
	}

	public static Client newClient(int poolSize, boolean insecure){
		final HttpClientBuilder clientBuilder = defaultHttpClientBuilder(poolSize);
		if(insecure){
			clientBuilder
				.setSSLContext(createFakeSSLContext())
				.setSSLHostnameVerifier((a,b) -> true)
			;
		}
		return newRestEasyBuilder(clientBuilder).build();
	}

	public static SSLContext createFakeSSLContext() {
		try {
			final SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) {}
				public void checkServerTrusted(X509Certificate[] arg0, String arg1) {}
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			}}, new SecureRandom());
			return sslcontext;
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException(e);
		}
	}

	public static RestEasyClient newRestEasyClient(int poolSize){
		return new RestEasyClient(newRestEasyBuilder(poolSize).build());
	}

	public static ResteasyClientBuilder newRestEasyBuilder(int poolSize){
		return newRestEasyBuilder(defaultHttpClientBuilder(poolSize));
	}

	public static ResteasyClientBuilder newRestEasyBuilder(HttpClientBuilder clientBuilder) {
		return new ResteasyClientBuilder()
			.httpEngine(withPerRequestTimeout(clientBuilder.build()))
			;
	}

	public static HttpClientBuilder defaultHttpClientBuilder(int poolSize) {
		return HttpClientBuilder
			.create()
			.setDefaultRequestConfig(
				RequestConfig.custom()
					.setConnectionRequestTimeout(3_000)
					.setConnectTimeout(500)
					.setSocketTimeout(30_000)
					.setRedirectsEnabled(false)
					.build()
			)
			.setMaxConnTotal(poolSize)
			.setMaxConnPerRoute(poolSize)
		;
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
	public static ApacheHttpClient43Engine withPerRequestTimeout(HttpClient httpClient) {
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
