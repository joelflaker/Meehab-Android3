package com.citrusbits.meehab.utils;

import android.util.Log;

import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.model.NearestDateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MeetingUtils {


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

	public static void setMeetingTimingStatus(MeetingModel model, Date nearestDateTime) {
		Calendar meetingDate = Calendar.getInstance();

		meetingDate.setTime(nearestDateTime);
        meetingDate.set(Calendar.SECOND, 0);

		Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.SECOND, 0);

		long differenceMillis = nearestDateTime.getTime()
				- currentCalendar.getTimeInMillis();

		long diffSec = differenceMillis != 0 ? differenceMillis / 1000 : 0;// - 3600;
		Log.e("Difference in sec", diffSec + "sec");

		long days = diffSec / /*sec in 24h */ 86400;

		//remaining seconds
		diffSec = diffSec - days * /*sec in 24h */ 86400;

		long hours = diffSec / 3600;

		//remaining seconds
		diffSec = diffSec - hours * /*sec in 1h */ 3600;

		long minutes = diffSec / 60;

		SimpleDateFormat mmmDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String mDate = mmmDate.format(meetingDate.getTime());
		String mNow = mmmDate.format(currentCalendar.getTime());
		Log.i("Meeting Name ", model.getName());
		Log.i("Meeting date time ", mDate);
		Log.i("Now date time ", mNow);
		Log.i("Difference", "days: "+days);
		Log.i("Difference", "hours: "+hours);
		Log.i("Difference", "minutes: "+minutes);

		if (days > 0) {
			model.setMarkerTypeColor(MeetingModel.MarkerColorType.GREEN);
			model.setStartInTime("AFTER " + days + " "
					+ (days == 1 ? "DAY" : "DAYS"));
			//Qamar - change old if (hours == 0 && minutes > 0 || (hours == 0 && minutes == 0 && seconds > 1))
		}else if (hours <= -1/* || minutes <= -60*/) {
            model.setMarkerTypeColor(MeetingModel.MarkerColorType.RED);
            model.setStartInTime("COMPLETED");
        }else if (hours >= 1) {
            model.setMarkerTypeColor(MeetingModel.MarkerColorType.GREEN);
            model.setStartInTime("AFTER " + hours + " "
                    + (hours == 1 ? "HOUR" : "HOURS"));

            //meeting will start under an hour 60 minutes
        }else if (hours == 0 && (minutes > 0 && minutes < 60)) {
			model.setMarkerTypeColor(MeetingModel.MarkerColorType.ORANGE);
			model.setStartInTime("STARTS IN UNDER AN HOUR");
		} else if ((hours <= 0 && hours >= -1) && (minutes < 0 && minutes > -60)) {
			model.setMarkerTypeColor(MeetingModel.MarkerColorType.RED);
			model.setStartInTime("ONGOING");
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

	public static final NearestDateTime getNearestDate(String days, String times) {

//		final List<String> daysInWeek = Arrays.asList(new String[]{"monday", "tuesday", "wednesday", "thursday",
//				"friday", "saturday", "sunday"});

		final SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a", Locale.US);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

		final String dayArray[] = days.split(",");
		final String timeArray[] = times.split(",");

		Calendar todayOrMeetingDay = Calendar.getInstance();

		final List<String> daysList = Arrays.asList(dayArray);
		final List<String> timesList = Arrays.asList(timeArray);

		final String todayLongName = todayOrMeetingDay.getDisplayName(
				Calendar.DAY_OF_WEEK, Calendar.LONG,
				Locale.getDefault());
		int weekDayCount = 0;
        Calendar now = Calendar.getInstance();
		do{
			final String dayLongName = todayOrMeetingDay.getDisplayName(
					Calendar.DAY_OF_WEEK, Calendar.LONG,
					Locale.getDefault());

			final int indexOfNearToday = daysList.indexOf(dayLongName);
			if(indexOfNearToday == -1){
				//add 1 day
				todayOrMeetingDay.add(Calendar.DAY_OF_MONTH,1);
				continue;
			}else {
				try {
					String onTime = timesList.get(indexOfNearToday);
					final Date dateObj = _12HourSDF.parse(onTime);

                    //+1 is for meeting will be ongoing for one hour
					todayOrMeetingDay.set(Calendar.HOUR_OF_DAY, dateObj.getHours() + 1);
					todayOrMeetingDay.set(Calendar.MINUTE, dateObj.getMinutes());
					todayOrMeetingDay.set(Calendar.SECOND,0);

					if(todayOrMeetingDay.before(now) && weekDayCount != 7){
						//add 1 day
						todayOrMeetingDay.add(Calendar.DAY_OF_MONTH,1);
						continue;
					}

					//restore meeting starting time
                    todayOrMeetingDay.add(Calendar.HOUR_OF_DAY,- 1);

                    String onDate = dateFormat.format(todayOrMeetingDay.getTime());

					NearestDateTime nearDateTime = new NearestDateTime();
//					nearDateTime.setIsToday(todayLongName.equalsIgnoreCase(dayLongName));
					nearDateTime.setIsToday(todayOrMeetingDay.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)
                    && todayOrMeetingDay.get(Calendar.YEAR) == now.get(Calendar.YEAR));
					nearDateTime.setDate(onDate);
					nearDateTime.setTime(onTime);
					nearDateTime.setDateTime(todayOrMeetingDay.getTime());

					return nearDateTime;

				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		}while(++weekDayCount != 8);
		return null;
	}

	public static final void sortByDate(ArrayList<MeetingModel> meetings) {
		Collections.sort(meetings, new Comparator<MeetingModel>() {
			public int compare(MeetingModel o1, MeetingModel o2) {
				if (o1.getNearestDateTime() == null || o2.getNearestDateTime() == null)
					return 0;
				return o1.getNearestDateTime().compareTo(o2.getNearestDateTime());
			}
		});
	}

	public static void sortByDistance(ArrayList<MeetingModel> meetings) {
		Collections.sort(meetings, new Comparator<MeetingModel>() {
			@Override
			public int compare(MeetingModel lhs, MeetingModel rhs) {
				if(lhs.getDistanceInMiles() == rhs.getDistanceInMiles()) return 0;
				return lhs.getDistanceInMiles() < rhs.getDistanceInMiles() ? -1: 1;
			}
		});
	}
}
