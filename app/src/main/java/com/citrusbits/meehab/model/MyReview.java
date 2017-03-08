/**
 * 
 */
package com.citrusbits.meehab.model;

import java.io.Serializable;

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
	
	private String datetimeUpdated;
	
	private String userId;
	private String meetingId;
	private String image;


	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDatetimeUpdated() {
		return datetimeUpdated;
	}
	public void setDatetimeUpdated(String datetimeUpdated) {
		this.datetimeUpdated = datetimeUpdated;
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


	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	public String getMeetingId() {
		return meetingId;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
