package com.citrusbits.meehab.model;

public class RehabDayModel {
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
}
