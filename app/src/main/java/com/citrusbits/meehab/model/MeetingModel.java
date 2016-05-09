/**
 * 
 */
package com.citrusbits.meehab.model;

/**
 * @author Xamar
 *
 */
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MeetingModel implements Serializable {

	@SerializedName("is_rsvp")
	@Expose
	private Integer rsvp;

	public Integer getRsvp() {
		return rsvp;
	}

	public void setRsvp(Integer rsvp) {
		this.rsvp = rsvp;
	}

	@SerializedName("added_byID")
	@Expose
	private Integer addedByID;

	@SerializedName("rsvp_count")
	@Expose
	private Integer rsvpCount;

	@SerializedName("favorite")
	@Expose
	private Integer favouriteMeeting;

	public Integer getFavouriteMeeting() {
		return favouriteMeeting;
	}

	public void setFavouriteMeeting(Integer favouriteMeeting) {
		this.favouriteMeeting = favouriteMeeting;
	}

	@SerializedName("meeting_checkin")
	@Expose
	private Integer checkInMeeting;

	public Integer getCheckInMeeting() {
		return checkInMeeting;
	}

	public void setCheckInMeeting(Integer checkInMeeting) {
		this.checkInMeeting = checkInMeeting;
	}

	public Integer getRsvpCount() {
		return rsvpCount;
	}

	public void setRsvpCount(Integer rsvpCount) {
		this.rsvpCount = rsvpCount;
	}

	@SerializedName("zip_code")
	@Expose
	private String zipCode;
	@Expose
	private String status;
	@SerializedName("on_day")
	@Expose
	private String onDay;
	@SerializedName("datetime_added")
	@Expose
	private String datetimeAdded;
	@SerializedName("building_type")
	@Expose
	private String buildingType;
	@SerializedName("on_time")
	@Expose
	private String onTime;
	@Expose
	private String message;
	@Expose
	private Integer id;
	@SerializedName("on_date")
	@Expose
	private String onDate;
	@Expose
	private String address;
	@Expose
	private String name;
	@SerializedName("reviews_count")
	@Expose
	private Integer reviewsCount;
	@Expose
	private String codes;
	@Expose
	private Double longitude;
	@SerializedName("in_city")
	@Expose
	private String inCity;
	@SerializedName("meeting_id")
	@Expose
	private Integer meetingId;
	@Expose
	private Double latitude;
	@SerializedName("datetime_updated")
	@Expose
	private String datetimeUpdated;
	@SerializedName("reviews_avg")
	@Expose
	private float reviewsAvg;

	private MarkerColorType markerTypeColor;

	public float getReviewsAvg() {
		return reviewsAvg;
	}

	public void setReviewsAvg(float reviewsAvg) {
		this.reviewsAvg = reviewsAvg;
	}

	boolean isChecked;
	boolean isCheckBoxVisible;
	boolean dateHeaderVisible;
	private double distanceInMiles;

	boolean todayMeeting;

	public boolean isTodayMeeting() {
		return todayMeeting;
	}

	public void setTodayMeeting(boolean todayMeeting) {
		this.todayMeeting = todayMeeting;
	}

	private boolean favourite;

	private String onDateOrigin;

	private String startInTime;

	private String nearestTime;

	private Date dateObj;

	public void setNearestTime(String nearestTime) {
		this.nearestTime = nearestTime;
	}

	public String getNearestTime() {
		return this.nearestTime;
	}

	public Date getDateObj() {
		return dateObj;
	}

	public void setDateObj(Date dateObj) {
		this.dateObj = dateObj;
	}

	public void setOnDateOrigin(String onDateOrigin) {
		this.onDateOrigin = onDateOrigin;
	}

	public String getOnDateOrigion() {
		return this.onDateOrigin;
	}

	public void setStartInTime(String startInTime) {
		this.startInTime = startInTime;
	}

	public String getStartInTime() {
		return this.startInTime;
	}

	public void setFavourite(boolean favouirte) {
		this.favourite = favouirte;
	}

	public boolean isFavourite() {
		return this.favourite;
	}

	public void setDistanceInMiles(double distanceInMiles) {
		this.distanceInMiles = distanceInMiles;
	}

	public double getDistanceInMiles() {
		return this.distanceInMiles;

	}

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

	public void setDateHeaderVisibility(boolean dateHeaderVisibility) {
		this.dateHeaderVisible = dateHeaderVisibility;
	}

	public boolean isDateHeaderVisible() {
		return dateHeaderVisible;
	}

	/**
	 * 
	 * @return The addedByID
	 */
	public Integer getAddedByID() {
		return addedByID;
	}

	/**
	 * 
	 * @param addedByID
	 *            The added_byID
	 */
	public void setAddedByID(Integer addedByID) {
		this.addedByID = addedByID;
	}

	/**
	 * 
	 * @return The zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * 
	 * @param zipCode
	 *            The zip_code
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * 
	 * @return The status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 
	 * @param status
	 *            The status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 
	 * @return The onDay
	 */
	public String getOnDay() {
		return onDay;
	}

	/**
	 * 
	 * @param onDay
	 *            The on_day
	 */
	public void setOnDay(String onDay) {
		this.onDay = onDay;
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
	 * @return The buildingType
	 */
	public String getBuildingType() {
		return buildingType;
	}

	/**
	 * 
	 * @param buildingType
	 *            The building_type
	 */
	public void setBuildingType(String buildingType) {
		this.buildingType = buildingType;
	}

	/**
	 * 
	 * @return The onTime
	 */
	public String getOnTime() {
		return onTime;
	}

	/**
	 * 
	 * @param onTime
	 *            The on_time
	 */
	public void setOnTime(String onTime) {
		this.onTime = onTime;
	}

	/**
	 * 
	 * @return The message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * 
	 * @param message
	 *            The message
	 */
	public void setMessage(String message) {
		this.message = message;
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
	 * @return The onDate
	 */
	public String getOnDate() {
		return onDate;
	}

	/**
	 * 
	 * @param onDate
	 *            The on_date
	 */
	public void setOnDate(String onDate) {
		this.onDate = onDate;
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
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return The reviewsCount
	 */
	public Integer getReviewsCount() {
		return reviewsCount;
	}

	/**
	 * 
	 * @param reviewsCount
	 *            The reviews_count
	 */
	public void setReviewsCount(Integer reviewsCount) {
		this.reviewsCount = reviewsCount;
	}

	/**
	 * 
	 * @return The codes
	 */
	public String getCodes() {
		return codes;
	}

	/**
	 * 
	 * @param codes
	 *            The codes
	 */
	public void setCodes(String codes) {
		this.codes = codes;
	}

	/**
	 * 
	 * @return The longitude
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * 
	 * @param longitude
	 *            The longitude
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * 
	 * @return The inCity
	 */
	public String getInCity() {
		return inCity;
	}

	/**
	 * 
	 * @param inCity
	 *            The in_city
	 */
	public void setInCity(String inCity) {
		this.inCity = inCity;
	}

	/**
	 * 
	 * @return The meetingId
	 */
	public Integer getMeetingId() {
		return meetingId;
	}

	/**
	 * 
	 * @param meetingId
	 *            The meeting_id
	 */
	public void setMeetingId(Integer meetingId) {
		this.meetingId = meetingId;
	}

	/**
	 * 
	 * @return The latitude
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * 
	 * @param latitude
	 *            The latitude
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
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

	/**
	 * @return LatLong
	 */
	public LatLng getPosition() {
		return new LatLng(latitude, longitude);
	}

	public void setMarkerTypeColor(MarkerColorType markerColor) {
		this.markerTypeColor = markerColor;
	}

	public MarkerColorType getMarkertypeColor() {
		return this.markerTypeColor;
	}

	public enum MarkerColorType {

		BLUE, GREEN, ORANGE, RED;

	}

}