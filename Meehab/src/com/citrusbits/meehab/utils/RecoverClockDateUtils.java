package com.citrusbits.meehab.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.citrusbits.meehab.R;

import android.content.Context;

public class RecoverClockDateUtils {

	private RecoverClockDateUtils() {
		throw new AssertionError("Utility class ca not be inistialized");
	}

	public static Calendar getCalendarFromDate(String dateInserted) {
		SimpleDateFormat dateFormate = new SimpleDateFormat("MMM dd, yyyy");
		try {
			Date date = dateFormate.parse(dateInserted);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String getDateWithMonthNameNextChip(Date date) {
		SimpleDateFormat dateFormate = new SimpleDateFormat("dd/MM/yyyy");

		date.setMonth(date.getMonth() + 1);
		return fomateRecoverClockDate(date);

	}

	public static String fomateRecoverClockDate(Date date) {
		SimpleDateFormat newFormater = new SimpleDateFormat("MMM dd, yyyy");
		return newFormater.format(date);
	}

	public static boolean isValidSoberDate(String dateInserted) {
		SimpleDateFormat dateFormate = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date date = dateFormate.parse(dateInserted);
			long diff = Calendar.getInstance().getTimeInMillis()
					- date.getTime();
			return diff > 0;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public static String getSoberDifference(String dateInserted, Context context) {
		SimpleDateFormat dateFormate = new SimpleDateFormat("dd/MM/yyyy");

		try {
			Date date = dateFormate.parse(dateInserted);
			return getDifference(date, Calendar.getInstance().getTime(),
					context);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public static String getDateWithMonthName(String dateInserted) {
		SimpleDateFormat dateFormate = new SimpleDateFormat("dd/MM/yyyy");

		try {
			Date date = dateFormate.parse(dateInserted);
			return fomateRecoverClockDate(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	private static String getDifference(Date startDate, Date endDate,
			Context context) {

		// milliseconds
		long different = endDate.getTime() - startDate.getTime();

		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;
		long monthInMilli = daysInMilli * 30;
		long yearsInMilli = monthInMilli * 12;

		long elapsedYears = different / yearsInMilli;
		different = different % yearsInMilli;

		/*
		 * long elapsedMonths = different / monthInMilli; different = different
		 * % monthInMilli;
		 */

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;

		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;

		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long elapsedSeconds = different / secondsInMilli;

		System.out.printf("%d days, %d hours, %d minutes, %d seconds%n",
				elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

		return String.format(
				context.getString(R.string.sober_date_diff_formate),
				elapsedYears, elapsedDays, elapsedHours, elapsedMinutes,
				elapsedSeconds);

	}
}
