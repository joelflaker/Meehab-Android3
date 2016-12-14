package com.citrusbits.meehab.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RehabDayModel implements Serializable{
	private String onThisDay;
	private String endTime;
	private String startTime;
	private int dayId;
	private String dayName;

	public String getDayName() {
		return dayName;
	}

	public void setDayName(String dayName) {
		this.dayName = dayName;
	}

	public String getOnThisDay() {
		return onThisDay;
	}

	public void setOnThisDay(String onThisDay) {
		this.onThisDay = onThisDay;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public int getDayId() {
		return this.dayId;
	}

	public void setDayId(int dayId) {
		this.dayId = dayId;
	}

	public Date getOpenDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
	        Date date = null;
	        try {
	            date = dateFormat.parse(startTime);
	        }catch(Exception ex){
	            ex.printStackTrace();
	        }
		return date;
	}

	public Date getCloseDate() {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        Date date = null;
        try {
            date = dateFormat.parse(endTime);
        }catch(Exception ex){
            ex.printStackTrace();
			date = new Date();
        }
	return date;
	}
}
