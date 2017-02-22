/**
 * 
 */
package com.citrusbits.meehab.model;

import java.util.Date;

/**
 * @author Qamar
 *
 */
public class NearestDateTime {
	String date;
	String time;
	private Date dateTime;
	private boolean isToday;

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return this.date;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTime() {
		return this.time;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setIsToday(boolean isToday) {
		this.isToday = isToday;
	}

	public boolean isToday() {
		return isToday;
	}

	public void setToday(boolean isToday) {
		this.isToday = isToday;
	}
}
