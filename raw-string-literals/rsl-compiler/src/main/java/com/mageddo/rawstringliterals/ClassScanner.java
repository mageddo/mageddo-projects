package com.mageddo.rawstringliterals;

import com.mageddo.rawstringliterals.javac.Method;
import com.sun.tools.javac.code.Symbol;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface ClassScanner {

	String findMultilineVar(Symbol.ClassSymbol classSymbol, Method method, String varName, String annotationName);

	String findMultilineVar(Reader r, Method method, String varName, String annotationName) throws IOException;

	/**
	 * Lê o arquivo fonte, processa os comentarios e injecta o valor deles nas variáveis anotadas com @{@link RawString}
	 */
	void processMultilineVars(Reader reader, Writer writer);
}
