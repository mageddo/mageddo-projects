package com.mageddo.rawstringliterals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;


public final class StringProcessor {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private StringProcessor() {
	}

	public static String toString(String value, RawString annotation) {
		if (!annotation.minify() && !annotation.indent()) {
			return value;
		}

		try {

			final BufferedReader reader = new BufferedReader(new StringReader(value));
			final StringBuilder buf = new StringBuilder();
			String line = reader.readLine();

			while (line != null) {
				if (annotation.indent()) {
					line = line.trim();
				}
//				if (annotation.minify() && buf.length() > 0) {
//					if (annotation.mergeChar() != '\0') {
//						buf.append(annotation.mergeChar());
//					}
//				}
				buf.append(line);
				if (!annotation.minify()) {
					buf.append(LINE_SEPARATOR);
				}

				line = reader.readLine();
			}
			return buf.toString();
		} catch (IOException e) {
			return value;
		}
	}
}
