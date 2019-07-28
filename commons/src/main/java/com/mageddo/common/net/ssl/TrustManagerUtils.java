package com.mageddo.common.net.ssl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class TrustManagerUtils {

	private static final String TRUST_STORE_FILE_PREFIX = "mageddo-commons-cacerts";
	private static final String TRUST_STORE_PROPERTY_NAME = "javax.net.ssl.trustStore";

	/**
	 * Path containing a truststore copied from <br/><br/>
	 * java version "1.8.0_201"<br>
	 * Java(TM) SE Runtime Environment (build 1.8.0_201-b09) <br>
	 * Java HotSpot(TM) 64-Bit Server VM (build 25.201-b09, mixed mode) <br>
	 */
	private static final String TRUST_STORE_PATH = "/cacerts";

	private static final Class<?> CLAZZ = LoggerFactory.class;
	private static final Logger LOG = LoggerFactory.getLogger(CLAZZ);

	private TrustManagerUtils() {
	}

	/**
	 * configure trust store to current jdk copying from {@link #TRUST_STORE_PATH} in the resources, this method is idempotent
	 */
	public static void installTrustStore() {
		if(!shouldInstall(System.getProperty(TRUST_STORE_PROPERTY_NAME))){
			return;
		}
		installTrustStore0();
	}

	private static void installTrustStore0() {
		try {
			final Path tempCacerts = Files.createTempFile(TRUST_STORE_FILE_PREFIX, ".tmp");
			Files.copy(CLAZZ.getResourceAsStream(TRUST_STORE_PATH), tempCacerts, StandardCopyOption.REPLACE_EXISTING);
			System.setProperty(TRUST_STORE_PROPERTY_NAME, tempCacerts.toString());
		} catch (IOException e){
			throw new UncheckedIOException(e);
		}
	}

	private static boolean shouldInstall(String actualTrustStoreValue) {
		if(StringUtils.isNotBlank(actualTrustStoreValue)){
			if(actualTrustStoreValue.startsWith(TRUST_STORE_FILE_PREFIX)){
				LOG.debug("trustStore already installed by mageddo-commons, value={}", actualTrustStoreValue);
				return false;
			} else {
				LOG.warn("another trust store is already specified, value={}", actualTrustStoreValue);
				return false;
			}
		}
		return true;
	}
}
