/**
 * 
 */
package com.citrusbits.meehab.model;

/**
 * @author Xamar
 *
 */
import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MeetingReviewModel implements Serializable {

	@Expose
	private String status;
	@SerializedName("datetime_added")
	@Expose
	private String datetimeAdded;
	@Expose
	private String image;
	@Expose
	private String deleted;
	@SerializedName("on_time")
	@Expose
	private String onTime;
	@Expose
	private Integer id;
	@Expose
	private Integer userID;
	@SerializedName("on_date")
	@Expose
	private String onDate;
	@Expose
	private String username;
	@Expose
	private String title;
	@Expose
	private Integer stars;
	@SerializedName("user_id")
	@Expose
	private Integer userId;
	@Expose
	private Integer meetingID;
	@SerializedName("datetime_updated")
	@Expose
	private String datetimeUpdated;
	@Expose
	private String comments;
	boolean isChecked;
	boolean isCheckBoxVisible;
	public boolean isChecked() {
		return isChecked;
	}

	public boolean isCheckBoxVisible() {
		return isCheckBoxVisible;
	}
	public void setCheckBoxVisible(boolean isCheckBoxVisible) {
		this.isCheckBoxVisible = isCheckBoxVisible;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	/**
	 * 
	 * @return
	 * The status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 
	 * @param status
	 * The status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 
	 * @return
	 * The datetimeAdded
	 */
	public String getDatetimeAdded() {
		return datetimeAdded;
	}

	/**
	 * 
	 * @param datetimeAdded
	 * The datetime_added
	 */
	public void setDatetimeAdded(String datetimeAdded) {
		this.datetimeAdded = datetimeAdded;
	}

	/**
	 * 
	 * @return
	 * The image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * 
	 * @param image
	 * The image
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * 
	 * @return
	 * The deleted
	 */
	public String getDeleted() {
		return deleted;
	}

	/**
	 * 
	 * @param deleted
	 * The deleted
	 */
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

	/**
	 * 
	 * @return
	 * The onTime
	 */
	public String getOnTime() {
		return onTime;
	}

	/**
	 * 
	 * @param onTime
	 * The on_time
	 */
	public void setOnTime(String onTime) {
		this.onTime = onTime;
	}

	/**
	 * 
	 * @return
	 * The id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 * The id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 * The userID
	 */
	public Integer getUserID() {
		return userID;
	}

	/**
	 * 
	 * @param userID
	 * The userID
	 */
	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	/**
	 * 
	 * @return
	 * The onDate
	 */
	public String getOnDate() {
		return onDate;
	}

	/**
	 * 
	 * @param onDate
	 * The on_date
	 */
	public void setOnDate(String onDate) {
		this.onDate = onDate;
	}

	/**
	 * 
	 * @return
	 * The username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 
	 * @param username
	 * The username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 
	 * @return
	 * The title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 
	 * @param title
	 * The title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 
	 * @return
	 * The stars
	 */
	public Integer getStars() {
		return stars;
	}

	/**
	 * 
	 * @param stars
	 * The stars
	 */
	public void setStars(Integer stars) {
		this.stars = stars;
	}

	/**
	 * 
	 * @return
	 * The userId
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * 
	 * @param userId
	 * The user_id
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * 
	 * @return
	 * The meetingID
	 */
	public Integer getMeetingID() {
		return meetingID;
	}

	/**
	 * 
	 * @param meetingID
	 * The meetingID
	 */
	public void setMeetingID(Integer meetingID) {
		this.meetingID = meetingID;
	}

	/**
	 * 
	 * @return
	 * The datetimeUpdated
	 */
	public String getDatetimeUpdated() {
		return datetimeUpdated;
	}

	/**
	 * 
	 * @param datetimeUpdated
	 * The datetime_updated
	 */
	public void setDatetimeUpdated(String datetimeUpdated) {
		this.datetimeUpdated = datetimeUpdated;
	}

	/**
	 * 
	 * @return
	 * The comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * 
	 * @param comments
	 * The comments
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

}