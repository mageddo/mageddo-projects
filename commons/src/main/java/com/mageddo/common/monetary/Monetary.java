package com.mageddo.common.monetary;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * This is a wrapper to BigDecimal to have sure all instances are using right rounding mode, precision and scale
 * <p>
 * Reference
 * <p>
 * Have sure all BigDecimal instances are using right scale, precision
 * https://stackoverflow.com/questions/10060158/set-all-bigdecimal-operations-to-a-certain-precision
 * <p>
 * recommended precision
 * https://stackoverflow.com/questions/224462/storing-money-in-a-decimal-column-what-precision-and-scale
 */
public class Monetary implements Comparable<Monetary> {


	private static int PRECISION;
	private static int SCALE;
	private static int DISPLAY_SCALE;
	private static RoundingMode ROUNDING_MODE;
	private static MathContext MATH_CONTEXT;

	static {
		setup(23, 4, 2, RoundingMode.HALF_EVEN);
	}

	public static final Monetary ZERO = new Monetary(BigDecimal.ZERO);
	public static final Monetary ONE = new Monetary(BigDecimal.ONE);
	public static final Monetary TEN = new Monetary(BigDecimal.TEN);

	private BigDecimal decimal;

	public Monetary(BigDecimal decimal) {
		this.decimal = normalize(decimal);
	}

	public Monetary(String v) {
		this.decimal = new BigDecimal(v, mathContext()).setScale(scale(), roundingMode());
	}

	public static void setup(int precision, int scale, int displayScale, RoundingMode roundingMode) {
		PRECISION = precision;
		SCALE = scale;
		DISPLAY_SCALE = displayScale;
		ROUNDING_MODE = roundingMode;
		MATH_CONTEXT = new MathContext(PRECISION, ROUNDING_MODE);
	}

	protected int precision(){
		return PRECISION;
	}

	protected int scale(){
		return SCALE;
	}

	protected int displayScale(){
		return DISPLAY_SCALE;
	}

	protected RoundingMode roundingMode(){
		return ROUNDING_MODE;
	}

	protected MathContext mathContext(){
		return MATH_CONTEXT;
	}

	public static Monetary valueOf(Number number) {
		return valueOf(number.doubleValue());
	}

	public static Monetary valueOf(String str) {
		return new Monetary(str);
	}

	public static Monetary valueOf(long v) {
		return new Monetary(BigDecimal.valueOf(v));
	}

	public static Monetary valueOf(double v) {
		return new Monetary(new BigDecimal(v));
	}

	public static Monetary valueOf(BigDecimal v) {
		return new Monetary(v);
	}

	public long longValue() {
		return decimalOf().longValue();
	}

	public int intValue() {
		return decimalOf().intValue();
	}

	public double doubleValue() {
		return decimalOf().doubleValue();
	}

	public Monetary add(Monetary monetary) {
		return new Monetary(this.decimalOf().add(monetary.decimalOf()));
	}

	public Monetary substract(Monetary monetary) {
		return new Monetary(this.decimalOf().subtract(monetary.decimalOf()));
	}

	public Monetary divide(Monetary v) {
		return divide(v.decimalOf());
	}

	public Monetary multiply(Monetary v) {
		return multiply(v.decimalOf());
	}

	public Monetary multiply(BigDecimal decimal) {
		return new Monetary(decimalOf().multiply(decimal, mathContext()));
	}

	public Monetary divide(BigDecimal v) {
		return new Monetary(decimalOf().divide(v, scale(), roundingMode()));
	}

	BigDecimal normalize(BigDecimal v) {
		return normalize(v, scale());
	}

	BigDecimal normalize(BigDecimal v, int scale) {
		return v.setScale(scale, roundingMode());
	}

	@Override
	public String toString() {
		return decimalOf().toString();
	}

	public String toString(int scale) {
		return String.valueOf(normalize(decimalOf(), scale));
	}

	public String toDisplayString() {
		return toString(displayScale());
	}

	public int signum() {
		return decimalOf().signum();
	}

	@Override
	public int compareTo(Monetary o) {
		return decimalOf().compareTo(o.decimalOf());
	}

	public static BigDecimal decimalOf(Monetary m) {
		if (m == null) {
			return null;
		}
		return m.decimalOf();
	}

	public BigDecimal decimalOf() {
		return decimal;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Monetary monetary = (Monetary) o;
		return Objects.equals(decimal, monetary.decimal);
	}

	@Override
	public int hashCode() {
		return Objects.hash(decimal);
	}

	public Monetary pow(int n) {
		return MonetaryMath.pow(this, n);
	}

}
