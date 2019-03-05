package com.mageddo.rawstringliterals;

import com.sun.tools.javac.code.Symbol;

import java.io.IOException;
import java.io.Reader;

public interface ClassScanner {

	String findMultilineVar(Symbol.ClassSymbol classSymbol, String methodName, String varName, String annotationName);

	String findMultilineVar(Reader r, String methodName, String varName, String annotationName) throws IOException;
}
