package com.mageddo.rawstringliterals;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class RawStringTest {

	@Test
	public void shouldCompileAndChangeFieldModifierToFinal() throws Exception {

		// arrange
		final InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();


		final String sourceCode = IOUtils.toString(getClass().getResourceAsStream("/TestClass.java"));

		// act
		final Class clazz = compiler.compile("TestClass", sourceCode);

		final Field nameField = clazz.getDeclaredField("name");
		Object o = clazz.newInstance();
		nameField.setAccessible(true);

		// assert
		assertEquals("Hello There", String.valueOf(nameField.get(o)).trim());
	}
}
