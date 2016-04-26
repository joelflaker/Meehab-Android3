package com.citrusbits.meehab.model;

import java.util.Date;

public class MessageModel {

	private String message;
	private int id;
	private String image;
	private String username;
	private String messageTime;

	private int fromID;
	private int toID;
	private String checkInType;

	private boolean checked;

	private int userCheckIn;
	private int rsvpUser;
	private int favourite;

	private Date messageDate;

	public int getUserCheckIn() {
		return userCheckIn;
	}

	public void setUserCheckIn(int userCheckIn) {
		this.userCheckIn = userCheckIn;
	}

	public int getRsvpUser() {
		return rsvpUser;
	}

	public void setRsvpUser(int rsvpUser) {
		this.rsvpUser = rsvpUser;
	}

	public int getFavourite() {
		return favourite;
	}

	public void setFavourite(int favourite) {
		this.favourite = favourite;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getFromID() {
		return fromID;
	}

	public void setFromID(int fromID) {
		this.fromID = fromID;
	}

	public int getToID() {
		return toID;
	}

	public void setToID(int toID) {
		this.toID = toID;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isChecked() {
		return this.checked;
	}

	public String getCheckInType() {
		return checkInType;
	}

	public void setCheckInType(String checkInType) {
		this.checkInType = checkInType;
	}

	public String getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(String messageTime) {
		this.messageTime = messageTime;
	}

	public Date getMessageDate() {
		return messageDate;
	}

	public void setMessageDate(Date messageDate) {
		this.messageDate = messageDate;
	}

}
