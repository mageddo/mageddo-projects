package com.mageddo.rawstringliterals;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.io.IOException;
import java.util.Date;

import static com.mageddo.rawstringliterals.commons.StringUtils.align;
import static org.junit.Assert.assertEquals;

public class RawStringTest {

	@Test
	public void shouldCompileAndAndSetCommentTextToVariableValue() throws Exception {

		// arrange
		final InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();


		final String sourceCode = toString("/TestClass.java");

		// act
		final Class clazz = compiler.compile("TestClass", sourceCode);

		final Object o = clazz.newInstance();

		final String sayHelloWord = String.valueOf(clazz.getMethod("sayHello").invoke(o));
		assertEquals("SELECT\n\tNAME, AGE\nFROM CUSTOMER\n", align(sayHelloWord));

		final String sayHello2Word = String.valueOf(clazz.getMethod("sayHello2").invoke(o));
		assertEquals("UPDATE TABLE SET NAME='MATEUS' WHERE ID = 5\n", align(sayHello2Word));

		assertEquals("SAY_HELLO_3\n", align(String.valueOf(clazz.getMethod("sayHello3").invoke(o))));

	}

	@Test
	public void shouldInterpretOverloadMethods() throws Exception {

		// arrange
		final InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();
		final String sourceCode = toString("/OverloadingMethodsTestClass.java");

		// act
		final Class clazz = compiler.compile("OverloadingMethodsTestClass", sourceCode);

		final Object o = clazz.newInstance();

		assertEquals("foo", String.valueOf(clazz.getMethod("findCustomers").invoke(o)));
		assertEquals(
			"SELECT\n\tNAME, AGE\nFROM CUSTOMER\nWHERE CREATE > :from\n",
			align(String.valueOf(clazz.getMethod("findCustomers", Date.class).invoke(o, new Date())))
		);
		assertEquals(
			"SELECT\n\tNAME, AGE\nFROM CUSTOMER\nWHERE NAME = :name\n",
			align(String.valueOf(clazz.getMethod("findCustomers", String.class).invoke(o, "")))
		);

	}

	private String toString(String s) throws IOException {
		return IOUtils.toString(getClass().getResourceAsStream(s));
	}

}
