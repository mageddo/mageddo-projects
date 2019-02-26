package com.mageddo.rawstringliterals;

import java.lang.annotation.*;

/**
 * Indicates a class which should be scanned for variables to have comments injected in
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Rsl {
}
