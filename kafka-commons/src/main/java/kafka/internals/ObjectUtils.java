package kafka.internals;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectUtils {
	/**
	 * <p>Null safe comparison of Comparables.</p>
	 *
	 * @param <T> type of the values processed by this method
	 * @param values the set of comparable values, may be null
	 * @return
	 *  <ul>
	 *   <li>If any objects are non-null and unequal, the greater object.
	 *   <li>If all objects are non-null and equal, the first.
	 *   <li>If any of the comparables are null, the greater of the non-null objects.
	 *   <li>If all the comparables are null, null is returned.
	 *  </ul>
	 */
	@SafeVarargs
	public static <T extends Comparable<? super T>> T max(final T... values) {
		T result = null;
		if (values != null) {
			for (final T value : values) {
				if (compare(value, result, false) > 0) {
					result = value;
				}
			}
		}
		return result;
	}

	/**
	 * <p>Null safe comparison of Comparables.</p>
	 *
	 * @param <T> type of the values processed by this method
	 * @param c1  the first comparable, may be null
	 * @param c2  the second comparable, may be null
	 * @param nullGreater if true {@code null} is considered greater
	 *  than a non-{@code null} value or if false {@code null} is
	 *  considered less than a Non-{@code null} value
	 * @return a negative value if c1 &lt; c2, zero if c1 = c2
	 *  and a positive value if c1 &gt; c2
	 * @see java.util.Comparator#compare(Object, Object)
	 */
	public static <T extends Comparable<? super T>> int compare(final T c1, final T c2, final boolean nullGreater) {
		if (c1 == c2) {
			return 0;
		} else if (c1 == null) {
			return nullGreater ? 1 : -1;
		} else if (c2 == null) {
			return nullGreater ? -1 : 1;
		}
		return c1.compareTo(c2);
	}
}
