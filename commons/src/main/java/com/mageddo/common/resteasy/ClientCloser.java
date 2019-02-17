package com.mageddo.common.resteasy;

import javax.ws.rs.client.Client;
import java.util.function.Consumer;

public class ClientCloser {
	public static void with(Client client, Consumer<Client> consumer){
		try {
			consumer.accept(client);
		} finally {
			client.close();
		}
	}
}
