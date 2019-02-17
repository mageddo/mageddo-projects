package com.mageddo.common.time;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateUtils {

	public static ZoneId DEFAULT_DISPLAY_TIMEZONE = ZoneId.of("America/Sao_Paulo");
	public static ZoneId DEFAULT_TIMEZONE = ZoneId.of("UTC");

	private DateUtils() {
	}

	/**
	 * Format to display timezone
	 */
	public static String localFormat(LocalDateTime occurrence) {
		if(occurrence == null){
			return null;
		}
		return occurrence
			.atZone(DEFAULT_TIMEZONE)
			.withZoneSameInstant(DateUtils.DEFAULT_DISPLAY_TIMEZONE)
			.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}

	public static String format(LocalDateTime occurrence) {
		return format(occurrence, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}

	public static String format(LocalDateTime occurrence, DateTimeFormatter formatter) {
		return occurrence.atZone(DateUtils.DEFAULT_TIMEZONE).format(formatter);
	}

	public static LocalDateTime now(){
		return LocalDateTime.now(DEFAULT_TIMEZONE);
	}

	public static LocalDateTime fromEpochSecond(int epochSecond){
		return LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC);
	}
}
