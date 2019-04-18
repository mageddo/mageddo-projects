package com.mageddo.common.resteasy;

import javax.ws.rs.client.Client;

/**
 * Simple wrapper for ReasyEasy where it is AutoClosable
 */
public class RestEasyClient implements AutoCloseable {

	private final Client client;

	public RestEasyClient(Client client) {
		this.client = client;
	}

	public Client getClient() {
		return client;
	}

	@Override
	public void close() {
		client.close();
	}
}
