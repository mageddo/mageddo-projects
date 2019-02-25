package com.mageddo.rawstringliterals;

public final class RawStrings {

	public static final String lateInit = "This string will be lately injected by raw string literals";

	/**
	 * This methods grants RSL will work otherwise a exception will be thrown
	 *
	 * @throws IllegalStateException if RSL didn't inject the variable
	 */
	public static String lateInit(){
		throw new IllegalStateException("raw string literals should inject this variable");
	}
}
