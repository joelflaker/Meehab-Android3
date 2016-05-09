package com.citrusbits.meehab.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class DateTimeUtils {

	public static String ISODateFormate(String pickupDate) {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		Date now = new Date();
		String nowAsISO = sdf.format(now.getTime());

		DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		String iso = timeFormat.format(now);

		Date now1 = new Date();
		try {
			String date = sdf2.format(now1);
			now1 = format.parse(pickupDate + " " + iso);
			pickupDate = sdf1.format(now1.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return pickupDate;

	}

	public static String[] generate24HoursTime(String timeSlot) {

		// String variables
		String[] timeSlots = new String[2];
		String startTime = null;
		String endTime = null;
		String temp = null;

		// split values and assign it to variables
		timeSlots = timeSlot.split("-", 0);
		startTime = timeSlots[0].trim();
		endTime = timeSlots[1].trim();

		if (startTime.contains("am") && endTime.contains("am")) {
			startTime = startTime.substring(0, startTime.indexOf("am"));

			if (startTime.length() > 1) {
				if (startTime.length() > 1 && startTime.contains("12")) {
					endTime = "01:00:00";
					startTime = "00:00:00";

				} else {
					// formate the time to 24 hours by appending 00:00
					endTime = "" + (Integer.parseInt(startTime) + 1) + ":00:00";
					startTime += ":00:00";
				}

			} else {
				if (Integer.parseInt(startTime) < 9) {
					endTime = "0" + (Integer.parseInt(startTime) + 1)
							+ ":00:00";
					startTime = "0" + startTime + ":00:00";
				} else {
					endTime = "" + (Integer.parseInt(startTime) + 1) + ":00:00";
					startTime = "0" + startTime + ":00:00";

				}
			}

		} else if (startTime.contains("pm") && endTime.contains("pm")) {
			startTime = startTime.substring(0, startTime.indexOf("pm"));

			if (startTime.length() > 1) {
				if (startTime.length() > 1 && startTime.contains("12")) {
					endTime = "" + (Integer.parseInt(startTime) + 1) + ":00:00";
					startTime += ":00:00";

				} else {
					// formate the time to 24 hours by appending 00:00
					endTime = "" + (Integer.parseInt(startTime) + 13)
							+ ":00:00";
					startTime = "" + (Integer.parseInt(startTime) + 12)
							+ ":00:00";
				}

			} else {
				endTime = "" + (Integer.parseInt(startTime) + 13) + ":00:00";
				startTime = "" + (Integer.parseInt(startTime) + 12) + ":00:00";
			}

		} else if (startTime.contains("am") && endTime.contains("pm")) {
			startTime = startTime.substring(0, startTime.indexOf("am"));

			if (startTime.length() > 1) {
				if (startTime.length() > 1 && startTime.contains("12")) {
					endTime = "" + (Integer.parseInt(startTime) + 1) + ":00:00";
					startTime += ":00:00";

				} else {
					// formate the time to 24 hours by appending 00:00
					endTime = "" + (Integer.parseInt(startTime) + 1) + ":00:00";
					startTime += ":00:00";
				}

			} else {
				endTime = "" + (Integer.parseInt(startTime) + 13) + ":00:00";
				startTime = "" + (Integer.parseInt(startTime) + 12) + ":00:00";
			}

		} else if (startTime.contains("pm") && endTime.contains("am")) {
			startTime = startTime.substring(0, startTime.indexOf("pm"));

			if (startTime.length() > 1) {
				// formate the time to 24 hours by appending 00:00
				endTime = "00:00:00";
				startTime = "" + (Integer.parseInt(startTime) + 12) + ":00:00";

			} else if (startTime.length() > 1 && startTime.contains("12")) {
				endTime = "" + (Integer.parseInt(startTime) + 12) + ":00:00";
				startTime += ":00:00";

			} else {
				endTime = "" + (Integer.parseInt(startTime) + 13) + ":00:00";
				startTime = "" + (Integer.parseInt(startTime) + 12) + ":00:00";
			}

		}

		timeSlots[0] = startTime;
		timeSlots[1] = endTime;

		return timeSlots;
	}

	// public static String getSimpleDateWithDays(String isoDate){
	// String date = null;
	//
	// String simpleDate = isoDate.substring(0,10);
	// int diff = DateDifference.dateDifference(simpleDate, null);
	//
	// if(diff > -1 && diff < 7) {
	// date =
	// SmartDateTimeUtil.getDayString(SmartDateTimeUtil.getFutureDay(diff));
	// } else {
	// date = simpleDate;
	// }
	//
	// return date;
	// }

	public static String getSimpleDate(String isoDate) {
		String simpleDate = isoDate.substring(0, 10);

		return simpleDate;
	}

	public static String getStringDate(int year, int month, int day) {

		String date = null;

		if (month > 12) {
			date = "" + (year + 1) + "-" + "01" + "-" + checkDigit(day);

		} else {
			date = "" + year + "-" + checkDigit(month) + "-" + checkDigit(day);
		}

		return date;
	}

	public static String checkDigit(int number) {
		return number <= 9 ? "0" + number : String.valueOf(number);
	}

	public static String generate12HoursTime(String time) {
		final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
		Date dateObj = null;
		try {
			dateObj = sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Date _24HourDt;
		// // String _24HourTime = "22:15";
		// SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
		// SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm:ss a");
		//

		if (time.contains("12")) {
			return "12:00:00 PM";

		} else {
			return new SimpleDateFormat("h:mm:ss a").format(dateObj);
		}
	}

	public static String getDurationString(int seconds) {

		int hours = seconds / 3600;
		int minutes = (seconds % 3600) / 60;
		seconds = seconds % 60;

		return twoDigitString(hours) + ":" + twoDigitString(minutes);
	}

	private static String twoDigitString(int number) {

		if (number == 0) {
			return "00";
		}

		if (number / 10 == 0) {
			return "0" + number;
		}

		return String.valueOf(number);
	}

	public static String increamentDay(String dateStr) {
		String outputDateStr = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			c.setTime(sdf.parse(dateStr));
			c.add(Calendar.DATE, 1); // number of days to add
			outputDateStr = sdf.format(c.getTime()); // dt is now the new date
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return outputDateStr;
	}

	public static boolean isTimeBetweenTwoTime(String initialTime,
			String finalTime, String currentTime) throws ParseException {
		String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
		if (initialTime.matches(reg) && finalTime.matches(reg)
				&& currentTime.matches(reg)) {
			boolean valid = false;
			// Start Time
			java.util.Date inTime = new SimpleDateFormat("HH:mm:ss")
					.parse(initialTime);
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(inTime);

			// Current Time
			java.util.Date checkTime = new SimpleDateFormat("HH:mm:ss")
					.parse(currentTime);
			Calendar calendar3 = Calendar.getInstance();
			calendar3.setTime(checkTime);

			// End Time
			java.util.Date finTime = new SimpleDateFormat("HH:mm:ss")
					.parse(finalTime);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(finTime);

			if (finalTime.compareTo(initialTime) < 0) {
				calendar2.add(Calendar.DATE, 1);
				calendar3.add(Calendar.DATE, 1);
			}

			java.util.Date actualTime = calendar3.getTime();
			if ((actualTime.after(calendar1.getTime()) || actualTime
					.compareTo(calendar1.getTime()) == 0)
					&& actualTime.before(calendar2.getTime())) {
				valid = true;
			}
			return valid;
		} else {
			throw new IllegalArgumentException(
					"Not a valid time, expecting HH:MM:SS format");
		}

	}

	public static String getCurrentTime() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");// dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

	public static String getCurrentTimeWithOneHourIncrement() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");// dd/MM/yyyy
		Date now = new Date();
		now.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
		String strDate = sdfDate.format(now);
		return strDate;
	}

	public static String getFormatedDate(String date) {
		String formatedDate = null;
		String[] dateParts = null;

		if (date != null) {
			if (date.contains("-")) {
				dateParts = date.split("-");
				formatedDate = dateParts[1] + "-" + dateParts[2] + "-"
						+ dateParts[0];
			} else {
				formatedDate = date;
			}
		}

		return formatedDate;
	}

	// public static String compareTimeStamp(ArrayList<PlaceOrder> list){
	// float diff;
	// String startTimeStamp = null;
	// String endTimeStamp = null;
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	//
	// if(list.size() > 0) {
	//
	// startTimeStamp = list.get(0).getTimeStamp();
	// try {
	// Date start;
	// Date stop;
	//
	// for(int i=1; i<list.size(); i++){
	//
	// //assign start date
	// start = sdf.parse(startTimeStamp);
	//
	// //assign end date
	// endTimeStamp = list.get(i).getTimeStamp();
	// stop = sdf.parse(endTimeStamp);
	//
	// diff = start.getTime() - stop.getTime();
	//
	// if (diff > 0) {
	// startTimeStamp = startTimeStamp;
	// } else {
	// startTimeStamp = endTimeStamp;
	// }
	// }
	//
	// } catch (ParseException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// return startTimeStamp;
	// }

	public boolean timeStampDifference(String startTime, String endTime) {
		Date start;
		Date end;
		float diff;
		boolean flag = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

		try {
			start = sdf.parse(startTime);
			end = sdf.parse(endTime);

			diff = start.getTime() - end.getTime();

			if (diff < 0) {
				flag = true;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return flag;
	}

	public static int compareTime(String time) {
		int diff = 9;
		// String currentTime = getCurrentTime();
		String currentTime = getCurrentTime();
		// time = "17:18:00";

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			Date currTime = sdf.parse(currentTime);
			Date orderTime = sdf.parse(time);

			if (orderTime.compareTo(currTime) > 0) {
				diff = 1;
			} else if (orderTime.compareTo(currTime) < 0) {
				diff = -1;
			} else if (orderTime.compareTo(currTime) == 0) {
				diff = 0;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return diff;

	}

	public static String getDatetimeAdded(String dateTimeAdded, long timeZone) {

		// dateTimeAdded=dateTimeAdded.replace("T", " ");
		// String date111[]=dateTimeAdded.split("\\.");
		// dateTimeAdded=dateTimeAdded.replace("Z", "");
		SimpleDateFormat dateFormate = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat newDatetime = new SimpleDateFormat(
				"MMM dd, yyyy @ hh:mm a");

		try {
			if (dateTimeAdded.length() > 0) {
				Date date = dateFormate.parse(dateTimeAdded);
				date.setHours(date.getHours() + (int) timeZone);
				return newDatetime.format(date);
			} else {
				return dateTimeAdded;
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return dateTimeAdded;
		}

	}

	public static String[] days = new String[] { "SUNDAY", "MONDAY", "TUESDAY",
			"WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY" };

	public static int getDayIndex(String dayName) {
		int dayIndex = 1;

		for (int i = 0; i < days.length; i++) {
			if (days[i].toLowerCase().equals(dayName.toLowerCase())) {
				dayIndex = i + 1;
				break;
			}
		}

		return dayIndex;
	}

	public static String getDayTimeWRTTimeZone(String day, String onTime,
			long timeZone) {
		SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm aa");
		SimpleDateFormat _12HourSDFConvert = new SimpleDateFormat("EEEE#hh:mm aa");
		String dayTime;
		try {

			final Date dateObj = _12HourSDF.parse(onTime);
			// dateObj.setHours(dateObj.getHours() + (int) timeZone);
			dateObj.setSeconds(0);

			Calendar cal = Calendar.getInstance();
			// cal.setTime(dateObj);
			cal.set(Calendar.HOUR_OF_DAY, dateObj.getHours());
			cal.set(Calendar.MINUTE, dateObj.getMinutes());
			cal.set(Calendar.DAY_OF_WEEK, DateTimeUtils.getDayIndex(day));
			cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY)
					+ (int) timeZone);
			cal.set(Calendar.SECOND, 0);

			// time=_12HourSDF.format(dateObj);
			dayTime = _12HourSDFConvert.format(cal.getTime());
			Log.i("Date Time Made ", dayTime);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dayTime = day + " " + onTime;
		}
		return dayTime;
	}
	
	public static String[] getDayTimeArrWRTMinusTimeZone(String onDay,String onTime,long timeZone){
		return getDayTimeWRTMinusTimeZone(onDay, onTime, timeZone).split("#");
	}
	
	public static String getDayTimeWRTMinusTimeZone(String day, String onTime,
			long timeZone) {
		SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm aa");
		SimpleDateFormat _12HourSDFConvert = new SimpleDateFormat("EEEE#hh:mm aa");
		String dayTime;
		try {

			final Date dateObj = _12HourSDF.parse(onTime);
			// dateObj.setHours(dateObj.getHours() + (int) timeZone);
			dateObj.setSeconds(0);

			Calendar cal = Calendar.getInstance();
			// cal.setTime(dateObj);
			cal.set(Calendar.HOUR_OF_DAY, dateObj.getHours());
			cal.set(Calendar.MINUTE, dateObj.getMinutes());
			cal.set(Calendar.DAY_OF_WEEK, DateTimeUtils.getDayIndex(day));
			cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY)
					- (int) timeZone);
			cal.set(Calendar.SECOND, 0);

			// time=_12HourSDF.format(dateObj);
			dayTime = _12HourSDFConvert.format(cal.getTime());
			Log.i("Date Time Made ", dayTime);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dayTime = day + " " + onTime;
		}
		return dayTime;
	}


	public static String calendarToDate(Calendar calendear) {
		SimpleDateFormat dateFormate = new SimpleDateFormat("MM/dd/yyyy");
		return dateFormate.format(calendear.getTime());
	}

	public static Calendar dateToCalendar(String date) {
		SimpleDateFormat dateFormate = new SimpleDateFormat("MM/dd/yyyy");
		try {
			Date dateObj = dateFormate.parse(date);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateObj);
			return cal;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Calendar.getInstance();
		}

	}
}
