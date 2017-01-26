package com.citrusbits.meehab.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.managers.RCChip;
import com.citrusbits.meehab.managers.RCChip.RCChipType;

import org.joda.time.Period;
import org.joda.time.PeriodType;

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
			e.printStackTrace();
			return null;
		}
	}

	public static Calendar getCalendarFromDayMonthYear(String dateSelected) {
		SimpleDateFormat dateFormate = new SimpleDateFormat("MM/dd/yyyy");
		try {
			Date date = dateFormate.parse(dateSelected);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getDateWithMonthNameNextChip(Date date) {
		SimpleDateFormat dateFormate = new SimpleDateFormat("MM/dd/yyyy");

		date.setMonth(date.getMonth());
		return fomateRecoverClockDate(date);

	}

	public static String fomateRecoverClockDate(Date date) {
		SimpleDateFormat newFormater = new SimpleDateFormat("MMM dd, yyyy");
		return newFormater.format(date);
	}

	public static boolean isValidSoberDate(String dateInserted) {
		SimpleDateFormat dateFormate = new SimpleDateFormat("MM/dd/yyyy");
		try {
			Date date = dateFormate.parse(dateInserted);
			long diff = Calendar.getInstance().getTimeInMillis()
					- date.getTime();
			return diff > 0;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getSoberDifference(String dateInserted,boolean profile, Context context) {
		SimpleDateFormat dateFormate = new SimpleDateFormat("MM/dd/yyyy");
		
		if(dateInserted == null||dateInserted.isEmpty()){
			return "";
		}

		try {
			Date date = dateFormate.parse(dateInserted);
			return getDifference(date, Calendar.getInstance().getTime(),profile,
					context);
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getDateWithMonthName(String dateInserted) {
		SimpleDateFormat dateFormate = new SimpleDateFormat("MM/dd/yyyy");

		try {
			Date date = dateFormate.parse(dateInserted);
			return fomateRecoverClockDate(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}

	private static String getDifference(Date startDate, Date endDate,boolean profile,
			Context context) {

		// milliseconds
		long different = endDate.getTime() - startDate.getTime();

		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;
		long monthInMilli = daysInMilli * 30;
		// long yearsInMilli = monthInMilli * 12;

		long yearsInMilli = daysInMilli * 365;

//		long elapsedYears = different / yearsInMilli;
//		different = different % yearsInMilli;

//		Calendar startCalender = Calendar.getInstance();
//		startCalender.setTime(startDate);
//		Calendar endCalender = Calendar.getInstance();
//		startCalender.setTime(endDate);
//		startCalender.add(Calendar.MONTH,1);


//		int diffYear = endCalender.get(Calendar.YEAR) - startCalender.get(Calendar.YEAR);
//		int elapsedMonths = diffYear * 12 + endCalender.get(Calendar.MONTH) - startCalender.get(Calendar.MONTH);
//		/*
//		 * long elapsedMonths = different / monthInMilli; different = different
//		 * % monthInMilli;
//		 */
//
//		long elapsedDays = different / daysInMilli;
//		different = different % daysInMilli;
//
//		long elapsedHours = different / hoursInMilli;
//		different = different % hoursInMilli;
//
//		long elapsedMinutes = different / minutesInMilli;
//		different = different % minutesInMilli;
//
//		long elapsedSeconds = different / secondsInMilli;
//
//		System.out.printf("%d days, %d hours, %d minutes, %d seconds%n",
//				elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
		
//		String soberStr= profile ? context.getString(R.string.sober_date_diff_formate_profile):context.getString(R.string.sober_date_diff_formate);

//		String soberStr = "%1$d Y, %2$d M, %2$d D, %3$d H, %4$d M, %5$d S";

		Period period = new Period(startDate.getTime(), endDate.getTime(), PeriodType.yearMonthDayTime());
		int elapsedYears = period.getYears();
		int elapsedMonths = period.getMonths();
		int elapsedDays = period.getDays();
		int elapsedHours = period.getHours();
		int elapsedMinutes = period.getMinutes();
		int elapsedSeconds = period.getSeconds();

		StringBuilder soberDateBuilder = new StringBuilder();

		int count = 0;
		boolean commaNeeded = false;
		if(elapsedYears != 0) {
			count++;
			commaNeeded = true;
			soberDateBuilder.append(elapsedYears+" Year" + (elapsedYears > 1 ? "s" : ""));
		}
		if(elapsedMonths != 0){
			count++;
			soberDateBuilder
					.append(commaNeeded? ", " : "")
					.append(elapsedMonths+" Month" + (elapsedMonths > 1 ? "s" : ""));
			commaNeeded = true;
		}
		if(elapsedDays != 0){
			count++;
			soberDateBuilder
					.append(commaNeeded? ", " : "")
					.append(elapsedDays+" Day" + (elapsedDays > 1 ? "s" : ""));
			commaNeeded = true;
		}

		if(elapsedHours != 0){
			soberDateBuilder
					.append(commaNeeded? ", " : "")
					.append(count > 2? "\n" : "")
					.append(elapsedHours+" H");
			commaNeeded = true;
		}
		if (elapsedMinutes != 0) {
			soberDateBuilder
					.append(commaNeeded? ", " : "")
					.append(elapsedMinutes+" M");
			commaNeeded = true;
		}
		if (elapsedSeconds != 0) {
			soberDateBuilder
					.append(commaNeeded? ", " : "")
					.append(elapsedSeconds+" S");
		}
		return soberDateBuilder.toString();

	}

	public static Period getRcpChip(String dateInserted) {
		SimpleDateFormat dateFormate = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

		try {
			Date date = dateFormate.parse(dateInserted);
			return getRCChip(date, Calendar.getInstance().getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Period getRCChip(Date startDate, Date endDate) {

		Period period = new Period(startDate.getTime(), endDate.getTime(), PeriodType.yearMonthDayTime());

		return period;
//		RCChip rcChip = new RCChip();
//
//		// milliseconds
//		long different = endDate.getTime() - startDate.getTime();
//
//		long secondsInMilli = 1000;
//		long minutesInMilli = secondsInMilli * 60;
//		long hoursInMilli = minutesInMilli * 60;
//		long daysInMilli = hoursInMilli * 24;
//		long monthInMilli = daysInMilli * 30;
//		long yearsInMilli = monthInMilli * 12;
//
//		long elapsedYears = different / yearsInMilli;
//		different = different % yearsInMilli;
//
//		long elapsedDays = different / daysInMilli;
//		different = different % daysInMilli;
//
//		/*
//		 * long elapsedMonths = different / monthInMilli; different = different
//		 * % monthInMilli;
//		 */
//		if (elapsedYears > 0) {
//
//			rcChip.setRcChipType(RCChipType.YEARS);
//			rcChip.setNumbers((int) elapsedYears);
//			return rcChip;
//
//		} else if (elapsedDays <= 90) {
//
//			rcChip.setRcChipType(RCChipType.DAYS);
//			rcChip.setNumbers((int) elapsedDays);
//			return rcChip;
//
//		} else if (elapsedDays > 90) {
//
//			Calendar startCal = Calendar.getInstance();
//			startCal.setTime(startDate);
//			Calendar endCal = Calendar.getInstance();
//			endCal.setTime(endDate);
//
//			int monthPassed = 0;
//
//			while (startCal.compareTo(endCal) <= 0) {
//				startCal.add(Calendar.MONTH, 1);
//				monthPassed++;
//			}
//
//			rcChip.setRcChipType(RCChipType.MONTH);
//			rcChip.setNumbers((int) monthPassed);
//			return rcChip;
//
//		} else {
//			return rcChip;
//		}

	}
}
