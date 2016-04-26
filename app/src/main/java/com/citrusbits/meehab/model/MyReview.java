/**
 * 
 */
package com.citrusbits.meehab.model;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author Qamar
 * 
 */
public class MyReview implements Serializable {
	
	public static final String EXTRA_REVIEW="review";
	
	private String reviewTitle;
	private String meetingName;
	

	private int rating;
	private int reviewId;
	private String comment;
	
	private String onDate;
	private String onTime;
	
	private String dateTimeAdded;
	
	private String userId;
	

	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDateTimeAdded() {
		return dateTimeAdded;
	}
	public void setDateTimeAdded(String dateTimeAdded) {
		this.dateTimeAdded = dateTimeAdded;
	}
	public String getOnDate() {
		return onDate;
	}
	public void setOnDate(String onDate) {
		this.onDate = onDate;
	}
	public String getOnTime() {
		return onTime;
	}
	public void setOnTime(String onTime) {
		this.onTime = onTime;
	}
	public String getReviewTitle() {
		return reviewTitle;
	}
	public void setReviewTitle(String reviewTitle) {
		this.reviewTitle = reviewTitle;
	}
	public String getMeetingName() {
		return meetingName;
	}
	public void setMeetingName(String meetingName) {
		this.meetingName = meetingName;
	}

	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public int getReviewId() {
		return reviewId;
	}
	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
	
}
