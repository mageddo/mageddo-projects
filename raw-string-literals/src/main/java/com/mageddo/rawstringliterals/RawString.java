package com.mageddo.rawstringliterals;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.LOCAL_VARIABLE, ElementType.TYPE, ElementType.FIELD})
public @interface RawString {

	boolean minify() default false;

	boolean indent() default false;
}
