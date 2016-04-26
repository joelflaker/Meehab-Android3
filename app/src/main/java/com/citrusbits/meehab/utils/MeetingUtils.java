package com.citrusbits.meehab.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MeetingUtils {

	
	public static Date getDateObject(String date) {
		SimpleDateFormat prevFormate = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date date2 = prevFormate.parse(date);
			return date2;
		} catch (ParseException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			return null;
		}
	}
	
	public static String formateDate(Date date) {
		SimpleDateFormat newFormate = new SimpleDateFormat("EEEE dd MMM yyyy");
		return newFormate.format(date);
	}
	
	public static int calculateAge(String dateOfBirth) {

		// dateOfBirth = "02/28/1990";
		SimpleDateFormat dateFormate = new SimpleDateFormat("MM/dd/yy");

		try {
			Date dateOfBirthDate = dateFormate.parse(dateOfBirth);
			Date currentDate = Calendar.getInstance().getTime();
			/*
			 * Calendar calDateOfBirth = Calendar.getInstance();
			 * calDateOfBirth.setTime(dateOfBirthDate);
			 * 
			 * calDateOfBirth.set(Calendar.HOUR_OF_DAY, 0); // set hour to
			 * midnight calDateOfBirth.set(Calendar.MINUTE, 0); // set minute in
			 * hour calDateOfBirth.set(Calendar.SECOND, 0); // set second in
			 * minute calDateOfBirth.set(Calendar.MILLISECOND, 0);
			 */
			return getDiffYears(dateOfBirthDate, currentDate);

		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}
	
	private static int getDiffYears(Date first, Date last) {
		Calendar a = getCalendar(first);
		Calendar b = getCalendar(last);
		int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
		if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH)
				|| (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a
						.get(Calendar.DATE) > b.get(Calendar.DATE))) {
			diff--;
		}
		return diff;
	}
	
	public static Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	
	public static long getTimeZoneOffset() {
		Calendar mCalendar = new GregorianCalendar();
		TimeZone mTimeZone = mCalendar.getTimeZone();
		int mGMTOffset = mTimeZone.getRawOffset();
		long hours = TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
		System.out.printf("GMT offset is %s hours", hours);
		return hours;
	}
}
