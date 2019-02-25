package com.mageddo.rawstringliterals.commons;

import static org.apache.commons.lang3.StringUtils.*;

public final class StringUtils {

	private StringUtils() {
	}

	public static String align(String value) {

		int minWhitesSpaces = Integer.MAX_VALUE;
		final String[] lines = value.trim().split("\n");
		for (final String line : lines) {
			if(isBlank(line)){
				continue;
			}
			final int whiteSpaces = countWhiteSpaces(line);
			if(whiteSpaces < minWhitesSpaces && whiteSpaces > 0){
				minWhitesSpaces = whiteSpaces;
			}
		}

		final StringBuilder sb = new StringBuilder();
		for (final String line : lines) {
			if(isNotBlank(line) && isWhitespace(String.valueOf(line.charAt(0)))){
				sb.append(line.substring(minWhitesSpaces));
			} else {
				sb.append(line);
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	private static int countWhiteSpaces(String s) {
		int count = 0;
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if(!Character.isWhitespace(c)){
				break;
			}
			count++;
		}
		return count;
	}

}
