package com.citrusbits.meehab.model;

public class RSVPModel {
	
	
	private int meetingId;
	private String calendarUri;
	private String meetingDay;
	private int checkIn;
	private int rsvp;

	public String getMeetingDay() {
		return meetingDay;
	}

	public void setMeetingDay(String meetingDay) {
		this.meetingDay = meetingDay;
	}

	public int getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(int meetingId) {
		this.meetingId = meetingId;
	}

	public String getCalendarUri() {
		return calendarUri;
	}
	
	public void setCheckIn(int checkIn){
		this.checkIn=checkIn;
	}
	
	public int getCheckIn(){
		return this.checkIn;
	}

	public int getRsvp() {
		return rsvp;
	}

	public void setRsvp(int rsvp) {
		this.rsvp = rsvp;
	}

	public void setCalendarUri(String calendarUri) {
		this.calendarUri = calendarUri;
	}

}
