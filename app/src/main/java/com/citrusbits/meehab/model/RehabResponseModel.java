package com.citrusbits.meehab.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.utils.MeetingUtils;

import android.text.TextUtils;

public class RehabResponseModel {
private static RehabResponseModel instance;
//	HashMap<Integer, String> days = new HashMap<>();

	List<RehabModel> rehabModels = new ArrayList<>();

//	public void addDay(int dayId, String dayName) {
//		days.put(dayId, dayName);
//	}

//	public String getDay(int dayId) {
//		return days.get(dayId);
//	}
//
//	public HashMap<Integer, String> getDays() {
//		return days;
//	}
//
//	public void setDays(HashMap<Integer, String> days) {
//		this.days = days;
//	}
	
//	private RehabResponseModel(){
//	}
//	
//	public static RehabResponseModel getInstance(){
//		if (instance == null){
//			instance =  new RehabResponseModel();
//		}
//		
//		return instance;
//	} 
	
	public void addRehabModels(List<RehabModel> rehabModels) {
		this.rehabModels.addAll(rehabModels);
	}

	public void addRehabModel(RehabModel rehabModel) {
		this.rehabModels.add(rehabModel);
	}

	public RehabModel getRehabModel(int postion) {

		if (postion >= rehabModels.size() || postion < 0) {
			throw new IllegalArgumentException("Enter valid position");
		}
		return rehabModels.get(postion);
	}

	public List<RehabModel> getRehabs() {
		return this.rehabModels;
	}

	/**
	 * @param daysList 
	 * @return
	 */
	public static boolean isOpenNow(List<RehabDayModel> daysList) {
		boolean bool = false;
		if (daysList == null || daysList.size() == 0){
			return bool;
		}
		
		//getting today timing or near future day of week


		long timeZone = MeetingUtils.getTimeZoneOffset();
		Calendar now = Calendar.getInstance();
		Calendar onCal = Calendar.getInstance();
		Calendar offCal = Calendar.getInstance();

		RehabDayModel re = getTodayRehabTiming(daysList);
		
		//
		if(re == null){ 
			return bool;
		}

		//finding timing in between

		Date onDate = null;
		Date offDate = null;
		try {
			onDate = re.getOpenDate();
			offDate = re.getCloseDate();

			now.setTime(onDate);
			now.clear(Calendar.ZONE_OFFSET);
			now.clear(Calendar.ZONE_OFFSET);

			now.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
			now.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
			now.set(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND));
			Date nowDate = now.getTime();

			onCal.setTime(onDate);
			onCal.clear(Calendar.ZONE_OFFSET);
			//				offCal.setTime(offDate);
			//				add(Calendar.DAY_OF_WEEK, daysInWeek.indexOf(onDay.toLowerCase(Locale.US)) + 1)

			offCal.setTime(offDate);
			offCal.clear(Calendar.ZONE_OFFSET);

			return nowDate.after(onCal.getTime()) && nowDate.before(offCal.getTime());

		}catch(Exception e){
			e.printStackTrace();
		}

		return bool;
	}
//				ymFormat.parse(on.get(Calendar.DAY_OF_WEEK) + "/" + on.get(Calendar.))
//				
//				onDate.
//				Date offDate = Calendar.getInstance().getTime();
//
//				Calendar sCalendar = Calendar.getInstance();
//				String dayLongName = sCalendar.getDisplayName(
//						Calendar.DAY_OF_WEEK, Calendar.LONG,
//						Locale.getDefault());
//				int onDayPositon = -1;
//				int todayPosition = -1;

	/**
	 * @param daysList
	 * @return
	 */
	public static RehabDayModel getTodayRehabTiming(List<RehabDayModel> daysList) {
		ArrayList<String> daysInWeek = 
				new ArrayList(Arrays.asList("sunday","monday", "tuesday", "wednesday", "thursday",
				"friday", "saturday"));
		
		RehabDayModel re = null;
		Calendar now = Calendar.getInstance();
		for (int i = 0; i < daysList.size(); i++) {
			re = daysList.get(i);

			String dayName = re.getDayName();
			int weekDay = now.get(Calendar.DAY_OF_WEEK);
			int weekDayInt = (daysInWeek.indexOf(dayName.toLowerCase(Locale.US)) + 1);
			if(weekDay == weekDayInt && re.getOnThisDay().equalsIgnoreCase("on")){
				break;
			}
			re = null;
			
		}
		
		return re;
	}

	/**
	 * @param packageName
	 * @return
	 */
	public static int getMarkDrawableId(String packageName) {
		int resourceId = R.drawable.star_silver;
		
		if(TextUtils.isEmpty(packageName)){
			resourceId = R.drawable.star_silver;
		}else if (packageName.equalsIgnoreCase("platinum")){
			resourceId = R.drawable.star_plat;
		}else if (packageName.equalsIgnoreCase("gold")){
			resourceId = R.drawable.star_gold;
		}else if (packageName.equalsIgnoreCase("silver")){
			resourceId = R.drawable.star_silver;
		}else if (packageName.equalsIgnoreCase("bronze")){
			resourceId = R.drawable.star_bronze;
		}
		return resourceId;
	}

//				for (int i = 0; i < daysInWeek.length; i++) {
//					if (onDay.toLowerCase().equals(daysInWeek[i])) {
//						onDayPositon = i;
//					}
//
//					if (dayLongName.toLowerCase().equals(daysInWeek[i])) {
//						todayPosition = i;
//					}
//				}
//
//				if (onDayPositon > todayPosition) {
//					sCalendar.add(Calendar.DAY_OF_MONTH,
//							(onDayPositon - todayPosition));
//					int dayDiffer = onDayPositon - todayPosition;
//					if (minDayDiffer > dayDiffer) {
//						minDayDiffer = dayDiffer;
////						nearPosition = j;
//					}
//
//				} else if (onDayPositon < todayPosition) {
//					sCalendar.add(Calendar.DAY_OF_MONTH, ((daysInWeek.length)
//							- todayPosition + onDayPositon));
//
//					int dayDiffer = ((daysInWeek.length) - todayPosition + onDayPositon);
//					if (minDayDiffer > dayDiffer) {
//						minDayDiffer = dayDiffer;
////						nearPosition = j;
//					}
//
//				} else if (onDayPositon == todayPosition) {
//					SimpleDateFormat _12HourSDF = new SimpleDateFormat(
//							"hh:mm a");
//					try {
//						final Date dateObj = _12HourSDF.parse(onTime);
//						sCalendar.set(Calendar.HOUR_OF_DAY,
//								dateObj.getHours() + 1);
//						sCalendar.set(Calendar.MINUTE, dateObj.getMinutes());
//						if (sCalendar.before(Calendar.getInstance())) {
//							sCalendar.add(Calendar.DAY_OF_MONTH,
//									(daysInWeek.length));
//
//							int dayDiffer = daysInWeek.length;
//							if (minDayDiffer > dayDiffer) {
//								minDayDiffer = dayDiffer;
////								nearPosition = j;
//							}
//
//						} else {
////							meetings.get(position).setTodayMeeting(true);
//							int dayDiffer = 0;
//							if (minDayDiffer > dayDiffer) {
//								minDayDiffer = dayDiffer;
////								nearPosition = j;
//							}
//						}
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//				}
//
//				SimpleDateFormat dateFormate = new SimpleDateFormat(
//						"dd/MM/yyyy");
//				String dateMade = dateFormate.format(sCalendar.getTime());
//
//				NearesDateTime nearDateTime = new NearesDateTime();
//				nearDateTime.setDate(dateMade);
//				nearDateTime.setTime(onTime);
//				nearestDateTimes.add(nearDateTime);
//			}
//	}
}
