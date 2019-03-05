package com.mageddo.rawstringliterals;

public final class RawStrings {

	/**
	 * This method requires RSL to work otherwise an exception will be thrown
	 *
	 * @throws IllegalStateException if RSL didn't inject the variable
	 * @return Throws an exception if called
	 */
	public static String lateInit(){
		throw new IllegalStateException("RSL should inject this variable");
	}
}
