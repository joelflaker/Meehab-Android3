package com.citrusbits.meehab.model;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class UserAccount implements Serializable {

	@SerializedName("about_story")
	@Expose
	private String aboutStory;
	@Expose
	private String phone;
	@Expose
	private String weight;

	@SerializedName("user_checkin")
	@Expose
	private Integer userCheckIn;

	@SerializedName("intrested_in")
	@Expose
	private String intrestedIn;
	@Expose
	private Integer id;
	//socail_id
	@SerializedName("socail_id")
	@Expose
	private String socailId;
	@SerializedName("sexual_orientation")
	@Expose
	private String sexualOrientation;
	@SerializedName("insurance")
	@Expose
	private String insurance;
	@Expose
	private String username;
	@SerializedName("checkin_type")
	@Expose
	private String checkinType;
	@Expose
	private String notifications;
	@Expose
	private String height;
	@SerializedName("willing_sponsor")
	@Expose
	private String willingSponsor;
	@Expose
	private String gender;
	@Expose
	private Integer longitude;
	@SerializedName("marital_status")
	@Expose
	private String maritalStatus;
	@SerializedName("have_kids")
	@Expose
	private String haveKids;
	@SerializedName("datetime_added")
	@Expose
	private String datetimeAdded;
	@SerializedName("date_of_birth")
	@Expose
	private String dateOfBirth;

	@SerializedName("meeting_home_group")
	@Expose
	private String meetingHomeGroup;

	@Expose
	private String image;
	@SerializedName("device_id")
	@Expose
	private String deviceId;
	@SerializedName("socket_id")
	@Expose
	private String socketId;
	@Expose
	private String address;
	@Expose
	private String email;
	@SerializedName("password_reset_token")
	@Expose
	private String passwordResetToken;
	@Expose
	private Integer latitude;
	@SerializedName("sober_sence")
	@Expose
	private String soberSence;
	@SerializedName("accupation")
	@Expose
	private String accupation;
	@Expose
	private String ethnicity;
	@SerializedName("datetime_updated")
	@Expose
	private String datetimeUpdated;

	@SerializedName("favorite_user")
	@Expose
	private int favourite;

	@SerializedName("rsvp_user")
	@Expose
	private int rsvpUser;

	public int getRsvpUser() {
		return rsvpUser;
	}

	public void setRsvpUser(int rsvpUser) {
		this.rsvpUser = rsvpUser;
	}

	@SerializedName("block_user")
	@Expose
	private int blocked;

	private int age;
	
	private boolean checked;

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/**
	 * 
	 * @return The aboutStory
	 */
	public String getAboutStory() {
		return aboutStory;
	}

	/**
	 * 
	 * @param aboutStory
	 *            The about_story
	 */
	public void setAboutStory(String aboutStory) {
		this.aboutStory = aboutStory;
	}

	/**
	 * 
	 * @return The phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * 
	 * @param phone
	 *            The phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * 
	 * @return The weight
	 */
	public String getWeight() {
		return weight;
	}

	/**
	 * 
	 * @param string
	 *            The weight
	 */
	public void setWeight(String string) {
		this.weight = string;
	}

	/**
	 * 
	 * @return The intrestedIn
	 */
	public String getIntrestedIn() {
		return intrestedIn;
	}

	/**
	 * 
	 * @param intrestedIn
	 *            The intrested_in
	 */
	public void setIntrestedIn(String intrestedIn) {
		this.intrestedIn = intrestedIn;
	}

	/**
	 * 
	 * @return The id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 *            The id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 
	 * @return The sexualOrientation
	 */
	public String getSexualOrientation() {
		return sexualOrientation;
	}

	/**
	 * 
	 * @param sexualOrientation
	 *            The sexual_orientation
	 */
	public void setSexualOrientation(String sexualOrientation) {
		this.sexualOrientation = sexualOrientation;
	}

	/**
	 * 
	 * @return The username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 
	 * @param username
	 *            The username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 
	 * @return The checkinType
	 */
	public String getCheckinType() {
		return checkinType;
	}

	/**
	 * 
	 * @param checkinType
	 *            The checkin_type
	 */
	public void setCheckinType(String checkinType) {
		this.checkinType = checkinType;
	}

	/**
	 * 
	 * @return The height
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * 
	 * @param string
	 *            The height
	 */
	public void setHeight(String string) {
		this.height = string;
	}

	/**
	 * 
	 * @return The willingSponsor
	 */
	public String getWillingSponsor() {
		return willingSponsor;
	}

	/**
	 * 
	 * @param willingSponsor
	 *            The willing_sponsor
	 */
	public void setWillingSponsor(String willingSponsor) {
		this.willingSponsor = willingSponsor;
	}

	/**
	 * 
	 * @return The gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * 
	 * @param gender
	 *            The gender
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * 
	 * @return The longitude
	 */
	public Integer getLongitude() {
		return longitude;
	}

	/**
	 * 
	 * @param longitude
	 *            The longitude
	 */
	public void setLongitude(Integer longitude) {
		this.longitude = longitude;
	}

	/**
	 * 
	 * @return The maritalStatus
	 */
	public String getMaritalStatus() {
		return maritalStatus;
	}

	/**
	 * 
	 * @param maritalStatus
	 *            The marital_status
	 */
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	/**
	 * 
	 * @return The haveKids
	 */
	public String getHaveKids() {
		return haveKids;
	}

	/**
	 * 
	 * @param haveKids
	 *            The have_kids
	 */
	public void setHaveKids(String haveKids) {
		this.haveKids = haveKids;
	}

	/**
	 * 
	 * @return The datetimeAdded
	 */
	public String getDatetimeAdded() {
		return datetimeAdded;
	}

	/**
	 * 
	 * @param datetimeAdded
	 *            The datetime_added
	 */
	public void setDatetimeAdded(String datetimeAdded) {
		this.datetimeAdded = datetimeAdded;
	}

	/**
	 * 
	 * @return The dateOfBirth
	 */
	public String getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * 
	 * @param dateOfBirth
	 *            The date_of_birth
	 */
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * 
	 * @return The image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * 
	 * @param image
	 *            The image
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * 
	 * @return The deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * 
	 * @param deviceId
	 *            The device_id
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * 
	 * @return The socketId
	 */
	public String getSocketId() {
		return socketId;
	}

	/**
	 * 
	 * @param socketId
	 *            The socket_id
	 */
	public void setSocketId(String socketId) {
		this.socketId = socketId;
	}

	/**
	 * 
	 * @return The address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * 
	 * @param address
	 *            The address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 
	 * @return The email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 
	 * @param email
	 *            The email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 
	 * @return The passwordResetToken
	 */
	public String getPasswordResetToken() {
		return passwordResetToken;
	}

	/**
	 * 
	 * @param passwordResetToken
	 *            The password_reset_token
	 */
	public void setPasswordResetToken(String passwordResetToken) {
		this.passwordResetToken = passwordResetToken;
	}

	/**
	 * 
	 * @return The latitude
	 */
	public Integer getLatitude() {
		return latitude;
	}

	/**
	 * 
	 * @param latitude
	 *            The latitude
	 */
	public void setLatitude(Integer latitude) {
		this.latitude = latitude;
	}

	/**
	 * 
	 * @return The soberSence
	 */
	public String getSoberSence() {
		return soberSence;
	}

	/**
	 * 
	 * @param soberSence
	 *            The sober_sence
	 */
	public void setSoberSence(String soberSence) {
		this.soberSence = soberSence;
	}

	/**
	 * 
	 * @return The accupation
	 */
	public String getAccupation() {
		return accupation;
	}

	/**
	 * 
	 * @param accupation
	 *            The accupation
	 */
	public void setAccupation(String accupation) {
		this.accupation = accupation;
	}

	/**
	 * 
	 * @return The ethnicity
	 */
	public String getEthnicity() {
		return ethnicity;
	}

	/**
	 * 
	 * @param ethnicity
	 *            The ethnicity
	 */
	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}

	/**
	 * 
	 * @return The datetimeUpdated
	 */
	public String getDatetimeUpdated() {
		return datetimeUpdated;
	}

	/**
	 * 
	 * @param datetimeUpdated
	 *            The datetime_updated
	 */
	public void setDatetimeUpdated(String datetimeUpdated) {
		this.datetimeUpdated = datetimeUpdated;
	}

	public void setFavourite(int favourite) {
		this.favourite = favourite;
	}

	public int isFavourite() {
		return this.favourite;
	}

	public void setBlocked(int blocked) {
		this.blocked = blocked;
	}

	public int isBlocked() {
		return this.blocked;
	}

	public void setAge(int age) {

		this.age = age;
	}

	public int getAge() {

		return this.age;

	}

	public String getMeetingHomeGroup() {
		return meetingHomeGroup;
	}

	public void setMeetingHomeGroup(String meetingHomeGroup) {
		this.meetingHomeGroup = meetingHomeGroup;
	}

	public Integer getUserCheckIn() {
		return userCheckIn;
	}

	public void setUserCheckIn(Integer userCheckIn) {
		this.userCheckIn = userCheckIn;
	}

	/**
	 * @return the insurance
	 */
	public String getInsurance() {
		return insurance;
	}

	/**
	 * @param insurance the insurance to set
	 */
	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}

	/**
	 * @return the notification
	 */
	public String getNotification() {
		return notifications;
	}

	/**
	 * @param notification the notification to set
	 */
	public void setNotification(String notification) {
		this.notifications = notification;
	}

	/**
	 * @return the socailId
	 */
	public String getSocailId() {
		return socailId;
	}

	/**
	 * @param socailId the socailId to set
	 */
	public void setSocailId(String socailId) {
		this.socailId = socailId;
	}
}
