package com.mageddo.featureswitch;

import java.lang.annotation.Annotation;

public final class AnnotationUtils {

	private AnnotationUtils() {}

	public static <A extends Annotation> A getAnnotation(Feature feature, Class<A> annotationType) {
		try {
			Class<? extends Feature> featureClass = feature.getClass();
			A fieldAnnotation = featureClass.getField(feature.name()).getAnnotation(annotationType);
			A classAnnotation = featureClass.getAnnotation(annotationType);
			return fieldAnnotation != null ? fieldAnnotation : classAnnotation;
		} catch (SecurityException e) {
			// ignore
		} catch (NoSuchFieldException e) {
			// ignore
		}
		return null;
	}
}
