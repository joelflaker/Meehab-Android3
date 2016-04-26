/**
 * 
 */
package com.citrusbits.meehab.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Xamar
 *
 */
public class MeetingFilterModel implements Serializable{
	public enum TimeRange{
		_6AM_9AM,
		_9AM_12AM,
		_12AM_3PM,
		_3PM_6PM,
		_6PM_9PM,
		_9PM_12AM
	};
	public enum MeetingCode {
		_Beginners,
		_Book_Study,
		_Closed_Meeting,
		_Child_Care_Availabe,
		_Sign_Language_Available,
		_Lesbian_Gay_Bisexual_Transgender,
		_Handicapped_Accessible,
		_Men_Stag,
		_Open,
		_Participation,
		_Speakers,
		_Women_Stag,
		_Young_People
	};
	private boolean favorite;
	private int[] days = new int[7];
	private ArrayList<TimeRange> times;
	private ArrayList<MeetingCode> codes;
	private String zipcode = new String();
	private int distance;
	private int rating;
	
	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public MeetingFilterModel() {
		favorite = false;
		times = new ArrayList<>();
		codes = new ArrayList<>();
	}
	
	public void addTimeRange(TimeRange timeRange){
		if(!times.contains(timeRange)){
			times.add(timeRange);
		}
	}
	
	public void addMeetingCode(MeetingCode meetingCode){
		if(!codes.contains(meetingCode)){
			codes.add(meetingCode);
		}
	}

	public boolean isFavorite() {
		return favorite;
	}

	public int[] getDays() {
		return days;
	}

	public ArrayList<TimeRange> getTimes() {
		return times;
	}

	public ArrayList<MeetingCode> getCodes() {
		return codes;
	}
	

}
