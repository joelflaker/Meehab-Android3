package com.citrusbits.meehab.ui.fragments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FilterResultHolder implements Serializable {

	private boolean anyDays = true;
	private boolean anyTime = true;
	private boolean anyType = true;
	
	private boolean anyCode = true;
	private boolean anyDistance = true;
	private boolean anyStar = true;

	private boolean favourite;
	List<String> days = new ArrayList<>();
	List<String> times = new ArrayList<>();
	List<String> types = new ArrayList<>();

	private String zipCode;

	private String distance;

	private int rating;

	public void setAnyDay(boolean anyDay) {
		this.anyDays = anyDay;
	}

	public boolean getAnyDay() {
		return this.anyDays;
	}

	public void setAnyType(boolean anyType) {

		this.anyType = anyType;
	}

	public boolean getAnyType() {
		return this.anyType;

	}
	
	
	public void setAnyCode(boolean anyCode) {
		this.anyCode = anyCode;
	}

	public boolean getAnyCode() {
		return this.anyCode;
	}
	
	public void setAnyDistance(boolean anyDistance) {
		this.anyDistance = anyDistance;
	}

	public boolean isAnyDistance() {
		return this.anyDistance;
	}

	
	public void setAnyStar(boolean anyStar) {
		this.anyStar = anyStar;
	}

	public boolean getAnyStar() {
		return this.anyStar;
	}


	

	public void setFavourite(boolean favourite) {
		this.favourite = favourite;
	}

	public boolean isFavourites() {

		return this.favourite;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public List<String> getTypes() {
		return this.types;
	}

	public void addTypes(String type) {
		types.add(type);

	}
	
	public void removeType(String type){
		types.remove(type);
	}

	public void setDays(List<String> days) {
		this.days = days;
	}

	public List<String> getDays() {
		return this.days;
	}

	public void addDay(String day) {
		days.add(day);
	}

	public void removeDay(String day) {
		days.remove(day);
	}

	public void setAnyTime(boolean anyTime) {
		this.anyTime = anyTime;
	}

	public boolean getAnyTime() {
		return this.anyTime;
	}

	public void setTimes(List<String> times) {
		this.times = times;
	}

	public List<String> getTimes() {

		return this.times;
	}

	public void addTime(String time) {
		this.times.add(time);
	}

	public void removeTime(String time) {
		times.remove(time);
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getZipCode() {
		return this.zipCode;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getDistance() {

		return this.distance;

	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public int getRating() {
		return this.rating;
	}

	public List<FilterTime> applyMappingOnTimings() {
		List<FilterTime> filterTimes = new ArrayList<>();
		for (int i = 0; i < times.size(); i++) {
			String time = times.get(i);
			if (time.equals("6 AM - 9 AM")) {
				Calendar startTime = Calendar.getInstance();
				startTime.set(Calendar.HOUR_OF_DAY, 6);
				startTime.set(Calendar.MINUTE, 0);
				startTime.set(Calendar.SECOND, 0);

				
				Calendar endTime = Calendar.getInstance();
				endTime.set(Calendar.HOUR_OF_DAY, 9);
				endTime.set(Calendar.MINUTE, 0);
				endTime.set(Calendar.SECOND, 0);

				FilterTime filterTime=new FilterTime(startTime, endTime);
				filterTimes.add(filterTime);
				
				

			} else if (time.equals("9 AM - 12 PM")) {
				Calendar startTime = Calendar.getInstance();
				startTime.set(Calendar.HOUR_OF_DAY, 9);
				startTime.set(Calendar.MINUTE, 0);
				startTime.set(Calendar.SECOND, 0);

				
				Calendar endTime = Calendar.getInstance();
				endTime.set(Calendar.HOUR_OF_DAY, 12);
				endTime.set(Calendar.MINUTE, 0);
				endTime.set(Calendar.SECOND, 0);
			//	endTime.set(Calendar.AM_PM, Calendar.PM);
				
				FilterTime filterTime=new FilterTime(startTime, endTime);
				filterTimes.add(filterTime);

			} else if (time.equals("12 PM - 3 PM")) {
				Calendar startTime = Calendar.getInstance();
				startTime.set(Calendar.HOUR_OF_DAY, 12);
				startTime.set(Calendar.MINUTE, 0);
				startTime.set(Calendar.SECOND, 0);
			//	startTime.set(Calendar.AM_PM, Calendar.PM);
				
				
				Calendar endTime = Calendar.getInstance();
				endTime.set(Calendar.HOUR_OF_DAY, 15);
				endTime.set(Calendar.MINUTE, 0);
			//	endTime.set(Calendar.AM_PM, Calendar.PM);
				endTime.set(Calendar.SECOND, 0);
				FilterTime filterTime=new FilterTime(startTime, endTime);
				filterTimes.add(filterTime);

			} else if (time.equals("3 PM - 6 PM")) {
				Calendar startTime = Calendar.getInstance();
				startTime.set(Calendar.HOUR_OF_DAY, 15);
				startTime.set(Calendar.MINUTE, 0);
				startTime.set(Calendar.SECOND, 0);
				//startTime.set(Calendar.AM_PM, Calendar.PM);
				
				
				Calendar endTime = Calendar.getInstance();
				endTime.set(Calendar.HOUR_OF_DAY, 18);
				endTime.set(Calendar.MINUTE, 0);
			//	endTime.set(Calendar.AM_PM, Calendar.PM);
				endTime.set(Calendar.SECOND, 0);
				FilterTime filterTime=new FilterTime(startTime, endTime);
				filterTimes.add(filterTime);

			} else if (time.equals("6 PM - 9 PM")) {
				Calendar startTime = Calendar.getInstance();
				startTime.set(Calendar.HOUR_OF_DAY, 18);
				startTime.set(Calendar.MINUTE, 0);
				startTime.set(Calendar.SECOND, 0);
			//	startTime.set(Calendar.AM_PM, Calendar.PM);
				
				
				Calendar endTime = Calendar.getInstance();
				endTime.set(Calendar.HOUR_OF_DAY, 21);
				endTime.set(Calendar.MINUTE, 0);
				//endTime.set(Calendar.AM_PM, Calendar.PM);
				endTime.set(Calendar.SECOND, 0);
				FilterTime filterTime=new FilterTime(startTime, endTime);
				filterTimes.add(filterTime);

			} else if (time.equals("9 PM - 12 AM")) {
				Calendar startTime = Calendar.getInstance();
				startTime.set(Calendar.HOUR_OF_DAY, 21);
				startTime.set(Calendar.MINUTE, 0);
				startTime.set(Calendar.SECOND, 0);
				//startTime.set(Calendar.AM_PM, Calendar.PM);
				
				
				Calendar endTime = Calendar.getInstance();
				endTime.set(Calendar.HOUR_OF_DAY, 24);
				endTime.set(Calendar.MINUTE, 0);
				endTime.set(Calendar.SECOND, 0);
				//endTime.set(Calendar.AM_PM, Calendar.AM);
				
				FilterTime filterTime=new FilterTime(startTime, endTime);
				filterTimes.add(filterTime);

			}

		}
		return filterTimes;
	}

	public class FilterTime {
		Calendar startTime;
		Calendar endTime;
		
		public FilterTime(Calendar startTime,Calendar endTime){
			this.startTime=startTime;
			this.endTime=endTime;
		}

		public Calendar getStartTime() {
			
			return this.startTime;
		}

		public Calendar getEndTime() {
			
			return this.endTime;
		}
	}

}
