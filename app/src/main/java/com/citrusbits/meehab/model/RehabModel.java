package com.citrusbits.meehab.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RehabModel implements Serializable{
	private String phone;
	private String about;
	private String state;
	
	private List<String> rehabVideoList = new ArrayList<>();
	private String userEmail;

	private String city;
	private int id;
	private String approved;
	private List<String> rehabPhotosList = new ArrayList<>();
	private String name;
	private String insuranceAccepted;
	private String otherServices;
	private String status;
	private String website;
	private String hours;
	private String zipCode;
	private String dateTimeAdded;
	private String relation;
	private String userName;
	private List<String> rehabPaymentList = new ArrayList<>();

	private String address;
	private String email;
	private List<String> rehabPackagesList = new ArrayList<>();
	private String userPhone;
	private String typeName;
	private List<RehabDayModel> rehabDaysList = new ArrayList<>();

	private double latitude;
	private double longitude;
	
	private double distance;

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	private List<String> rehabInsurances = new ArrayList<>();
	private boolean favorite;
//	private String days;
	private String rehabPackageName;

	

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<String> getRehabVideos() {
		return rehabVideoList;
	}

	public void addRehabVideos(String rehabVideos) {
		this.rehabVideoList.add(rehabVideos);
	}

	public void setRehabVideos(List<String> rehabVideos) {
		this.rehabVideoList = rehabVideos;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getApproved() {
		return approved;
	}

	public void setApproved(String approved) {
		this.approved = approved;
	}

	public List<String> getRehabPhotos() {
		return rehabPhotosList;
	}

	public void setRehabPhotos(List<String> rehabPhotoes) {
		this.rehabPhotosList = rehabPhotoes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInsuranceAccepted() {
		return insuranceAccepted;
	}

	public void setInsuranceAccepted(String insuranceAccepted) {
		this.insuranceAccepted = insuranceAccepted;
	}

	public String getOtherServices() {
		return otherServices;
	}

	public void setOtherServices(String otherServices) {
		this.otherServices = otherServices;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getDateTimeAdded() {
		return dateTimeAdded;
	}

	public void setDateTimeAdded(String dateTimeAdded) {
		this.dateTimeAdded = dateTimeAdded;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<String> getRehabPayments() {
		return rehabPaymentList;
	}

	public void setRehabPayments(List<String> rehabPayments) {
		this.rehabPaymentList = rehabPayments;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getRehabPackages() {
		return rehabPackagesList;
	}

	public void setRehabPackages(List<String> rehabPackages) {
		this.rehabPackagesList = rehabPackages;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String codes) {
		this.typeName = codes;
	}

	public List<RehabDayModel> getRehabDays() {
		return rehabDaysList;
	}

	public void setRehabDays(List<RehabDayModel> rehabDays) {
		this.rehabDaysList = rehabDays;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public List<String> getRehabInsurances() {
		return rehabInsurances;
	}

	public void setRehabInsurances(List<String> rehabInsurances) {
		this.rehabInsurances = rehabInsurances;
	}

	public boolean isFavorite() {
		return favorite;
	}
	
	public void setFavorite(boolean isFav) {
		favorite = isFav;
	}

	public void setPackageName(String rehabPackageName) {
		this.rehabPackageName = rehabPackageName;
	}
	
	public String getPackageName() {
		return this.rehabPackageName;
	}


//	public void setDays(String days) {
//		this.days = days;
//	}
//
//	public String getDays() {
//		return this.days;
//	}
	

	// private String List<> rehabDays=new ArrayLi

}
