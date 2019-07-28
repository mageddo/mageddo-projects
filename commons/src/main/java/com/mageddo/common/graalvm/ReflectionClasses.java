package com.mageddo.common.graalvm;

import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectionClasses {

	public static final boolean PROCESS_CONSTRUCTORS = true;
	public static final boolean PROCESS_METHODS = true;
	public static final boolean PROCESS_FIELDS = false;

	public static void process(Class<?> ... classes) {
		process(PROCESS_CONSTRUCTORS, PROCESS_METHODS, PROCESS_FIELDS, classes);
	}

	public static void process(boolean constructors, boolean methods, boolean fields, Class<?> ... classes) {
		for (Class<?> clazz : classes) {
			process(clazz, constructors, methods, fields);
		}
	}

	public static void process(Class<?> clazz) {
		process(clazz, PROCESS_CONSTRUCTORS, PROCESS_METHODS, PROCESS_FIELDS);
	}

	/**
	 * Register all constructors and methods on graalvm to reflection support at runtime
	 */
	public static void process(Class<?> clazz, boolean constructors, boolean methods, boolean fields) {
		try {
			System.out.println("> Declaring class: " + clazz.getCanonicalName());
			RuntimeReflection.register(clazz);
			if(constructors){
				for (final Constructor<?> constructor : clazz.getDeclaredConstructors()) {
					System.out.println("\t constructor: " + toSignature(constructor));
					RuntimeReflection.register(constructor);
				}
			}
			if(methods) {
				for (final Method method : clazz.getMethods()) {
					System.out.println("\t method: " + toSignature(method));
					RuntimeReflection.register(method);
				}
			}
			if(fields){
				for (final Field field : clazz.getDeclaredFields()) {
					System.out.println("\t field: " + field.getName());
					RuntimeReflection.register(field);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private static String toSignature(Executable executable){
		final String parameters = Stream
			.of(executable.getParameterTypes())
			.map(Class::getSimpleName)
			.collect(Collectors.toList())
			.toString();
		return String.format(
			"%s(%s)", executable.getName(), parameters.substring(1, parameters.length() - 1)
		);
	}
}
