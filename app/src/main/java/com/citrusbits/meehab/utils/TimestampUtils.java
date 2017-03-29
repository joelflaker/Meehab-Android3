package com.citrusbits.meehab.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Methods for dealing with timestamps
 */
public class TimestampUtils {

	/**
	 * Return an ISO 8601 combined date and time string for current date/time
	 * 
	 * @return String with format "yyyy-MM-dd'T'HH:mm:ss'Z'"
	 */
	public static String getISO8601StringForCurrentDate() {
		Date now = new Date();
		return getISO8601StringForDate(now);
	}

	/**
	 * Return an ISO 8601 combined date and time string for specified date/time
	 * 
	 * @param date
	 *            Date
	 * @return String with format "yyyy-MM-dd'T'HH:mm:ss'Z'"
	 */
	private static String getISO8601StringForDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}

	/**
	 * Private constructor: class cannot be instantiated
	 */
	private TimestampUtils() {
	}


	public static long getTimeZoneOffset() {
		TimeZone tz = TimeZone.getDefault();
		Calendar cal = GregorianCalendar.getInstance(tz);
		TimeZone mTimeZone = cal.getTimeZone();
		int offsetInMillis  = mTimeZone.getOffset(cal.getTimeInMillis());
		long hours = TimeUnit.HOURS.convert(offsetInMillis, TimeUnit.MILLISECONDS);
		String.format("GMT offset is %02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
		if(offsetInMillis >= 0){
			hours = +hours;
		}else {
			hours = -hours;
		}

		return hours;
	}

	public static Date localToUtc(Date localDate) {
		return new Date(localDate.getTime()-TimeZone.getDefault().getOffset(localDate.getTime()));
	}
	public static Date utcToLocal(Date utcDate) {
		return new Date(utcDate.getTime()+TimeZone.getDefault().getOffset(utcDate.getTime()));
	}
}
