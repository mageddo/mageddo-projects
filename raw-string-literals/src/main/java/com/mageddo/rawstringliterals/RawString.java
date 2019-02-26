package com.mageddo.rawstringliterals;

import java.lang.annotation.*;

/**
 * Indicates a variable to inject comment in value
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.LOCAL_VARIABLE})
public @interface RawString {

	/**
	 * Removes vertical and horizontal white space margins from around the
	 * essential body of a multi-line string, while preserving relative
	 * indentation.
	 *
	 * <p>This featuree is not supported yet</p>
	 * <p>
	 * see String#align() since java 12
	 * <p>
	 */
	boolean align() default false;
}
