package com.mageddo.rawstringliterals;

import org.junit.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class RawStringTest {

	@Test
	public void shouldCompileAndChangeFieldModifierToFinal() throws Exception {

		// arrange
		final InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();
		final StringBuilder sourceCode = new StringBuilder()
			.append("import com.mageddo.rawstringliterals.RawString; \n")
			.append(" \n")
//			.append("@RawString \n")
			.append("public class TestClass { \n")
			.append(" \n")

			.append("/** Hello There */ \n")
			.append("@RawString \n")
			.append("	private static String name; \n")
			.append(" \n")
			.append("} \n")
			.append(" \n");

		// act
		final Class clazz = compiler.compile("TestClass", sourceCode.toString());

		final Field nameField = clazz.getDeclaredField("name");
		Object o = clazz.newInstance();
		nameField.setAccessible(true);

		// assert
		assertEquals("Hello There", String.valueOf(nameField.get(o)).trim());
	}
}
