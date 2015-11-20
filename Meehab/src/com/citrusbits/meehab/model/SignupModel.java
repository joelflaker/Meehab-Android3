package com.citrusbits.meehab.model;

import java.io.Serializable;

public class SignupModel implements Serializable {
	
	
	private String userName;
	private String emil;
	private String password;
	private String fbId;
	private String phoneNumber;
	private String contacts;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmil() {
		return emil;
	}

	public void setEmil(String emil) {
		this.emil = emil;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	public String getFbId() {

		return fbId;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}
	
	public void setContact(String contacts){
		this.contacts=contacts;
	}
	
	public String getContact(){
		
		return this.contacts;
	}
	
}
