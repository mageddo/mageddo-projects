package com.mageddo.common.web;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class UriUtils {
	public static URI valueOf(String uri) {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static URL urlValue(URI uri){
		try {
			return uri.toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}
}
