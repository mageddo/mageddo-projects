package com.mageddo.common.monetary;

public class MonetaryMath {

	/**
	 * Calculate the percentIncrease rate e.g. lower=5, higher=10, percentIncrease = 1.0
	 * @param lower the value you had before
	 * @param higher the value you have now
	 */
	public static Monetary percentIncrease(Monetary lower, Monetary higher){
		return higher.divide(lower).substract(Monetary.ONE);
	}

	public static Monetary margin(Monetary startValue, Monetary finalValue){
		return finalValue.substract(startValue).divide(finalValue);
	}

	public static Monetary min(Monetary a, Monetary b) {
		if(a.compareTo(b) < 0) {
			return a;
		}
		return b;
	}

	public static Monetary max(Monetary a, Monetary b) {
		if(a.compareTo(b) > 0) {
			return a;
		}
		return b;
	}

	public static Monetary pow(Monetary a, Monetary b){
		return Monetary.valueOf(Math.pow(a.doubleValue(), b.doubleValue()));
	}

	public static Monetary pow(Monetary a, int n){
		return Monetary.valueOf(a.decimalOf().pow(n));
	}

	public static Monetary abs(Monetary value) {
		if(value.signum() == 0){
			return value;
		}
		return value.multiply(Monetary.valueOf(-1));
	}
}
