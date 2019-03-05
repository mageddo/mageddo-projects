package com.mageddo.rawstringliterals.exception;

import com.github.javaparser.ParseException;

public class UnchekedParseException extends RuntimeException {
	public UnchekedParseException(ParseException e) {
		super(e);
	}
}
