package com.mageddo.rawstringliterals.exception;

import java.io.IOException;

public class UncheckedIOException extends RuntimeException {
	public UncheckedIOException(IOException cause) {
		super(cause);
	}
}
