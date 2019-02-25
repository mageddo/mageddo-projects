package com.mageddo.rawstringliterals;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.lang.reflect.Method;

import static com.mageddo.rawstringliterals.commons.StringUtils.align;
import static org.junit.Assert.assertEquals;

public class RawStringTest {

	@Test
	public void shouldCompileAndChangeFieldModifierToFinal() throws Exception {

		// arrange
		final InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();


		final String sourceCode = IOUtils.toString(getClass().getResourceAsStream("/TestClass.java"));

		// act
		final Class clazz = compiler.compile("TestClass", sourceCode);

		final Method method = clazz.getMethod("sayHello");
		Object o = clazz.newInstance();

		// assert	convertToFixedWidth
		String value = String.valueOf(method.invoke(o)).trim();

		assertEquals("SELECT\n\tNAME, AGE\nFROM CUSTOMER\n", align(value));

	}

}
