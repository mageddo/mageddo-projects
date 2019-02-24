package com.mageddo.rawstringliterals;

import org.junit.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;

import static java.lang.reflect.Modifier.isFinal;
import static org.junit.Assert.assertFalse;

public class RawStringTest {

	@Test
	public void shouldCompileAndChangeFieldModifierToFinal() throws Exception {

		// arrange
		final InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();
		final StringBuilder sourceCode = new StringBuilder()
			.append("import com.mageddo.rawstringliterals.RawString; \n")
			.append(" \n")
			.append("@RawString \n")
			.append("public class TestClass { \n")
			.append(" \n")
			.append("	private String name; \n")
			.append(" \n")
			.append("	public TestClass(String name) { \n")
			.append("		this.name = name; \n")
			.append("	} \n")
			.append(" \n")
			.append("	public String getName() { \n")
			.append("		return name; \n")
			.append("	} \n")
			.append("} \n")
			.append(" \n");

		// act
		final Class clazz = compiler.compile("TestClass", sourceCode.toString());

		// assert
		assertFalse(isFinal(clazz.getDeclaredField("name").getModifiers()));
	}
}
