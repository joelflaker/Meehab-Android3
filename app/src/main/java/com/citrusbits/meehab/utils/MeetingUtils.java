package com.citrusbits.meehab.utils;

import android.util.Log;

import com.citrusbits.meehab.model.MeetingModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MeetingUtils {


	private static final long MINUTES_BEFORE = 14;

	public static Date getDateObject(String date) {
		SimpleDateFormat prevFormate = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
		try {
			Date date2 = prevFormate.parse(date);
			return date2;
		} catch (ParseException e) {

			e.printStackTrace();
			return null;
		}
	}
	
	public static String formateDate(Date date) {
		SimpleDateFormat newFormate = new SimpleDateFormat("EEEE MMM dd, yyyy");
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

	public static void setStartInTime(MeetingModel model, String date, String onTime) {
		SimpleDateFormat prevFormate = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date date2 = prevFormate.parse(date);
			SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
			final Date dateObj = _12HourSDF.parse(onTime);
			Calendar calendar = Calendar.getInstance();

			calendar.setTime(date2);
			calendar.set(Calendar.HOUR_OF_DAY, dateObj.getHours());
			calendar.set(Calendar.MINUTE, dateObj.getMinutes());
			calendar.set(Calendar.SECOND, 0);

			Calendar currentCalendar = Calendar.getInstance();

			// currentCalendar.set(Calendar.SECOND, 0);

			long difference = calendar.getTimeInMillis()
					- currentCalendar.getTimeInMillis();

			long x = difference / 1000;
			Log.e("Difference ", x + "");

			long days = x / (60 * 60 * 24);

			long seconds = x % 60;
			x /= 60;
			long minutes = x % 60;
			x /= 60;
			long hours = x % 24;
			Log.i("Meeting Name ", model.getName());
			Log.i("Hours Difference ", hours + ":" + minutes);
			SimpleDateFormat mmmDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			String mDate = mmmDate.format(calendar.getTime());
			String mNow = mmmDate.format(currentCalendar.getTime());
			Log.e("Meeting date time ", mDate);
			Log.e("Nnow date time ", mNow);

			if (days > 0) {
				model.setMarkerTypeColor(MeetingModel.MarkerColorType.GREEN);
				model.setStartInTime("AFTER " + days + " "
						+ (days == 1 ? "DAY" : "DAYS"));
			} else if (hours > 1 || hours == 1 && minutes > 0) {
				model.setMarkerTypeColor(MeetingModel.MarkerColorType.GREEN);
				model.setStartInTime("AFTER " + hours + " "
						+ (hours == 1 ? "HOUR" : "HOURS"));

				//Qamar - change old if (hours == 0 && minutes > 0 || (hours == 0 && minutes == 0 && seconds > 1))
			} else if (hours == 0 && minutes > MINUTES_BEFORE) {
				model.setMarkerTypeColor(MeetingModel.MarkerColorType.ORANGE);
				model.setStartInTime("STARTS IN UNDER AN HOUR");
			} else if (hours == 0 && minutes <= MINUTES_BEFORE || hours < 0 && hours > -2) {
				model.setMarkerTypeColor(MeetingModel.MarkerColorType.RED);
				model.setStartInTime("ONGOING");
			} else if (hours <= -1) {
				model.setMarkerTypeColor(MeetingModel.MarkerColorType.RED);
				model.setStartInTime("COMPLETED");
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
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
