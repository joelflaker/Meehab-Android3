package com.citrusbits.meehab.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetFriendsResponse {
	
	private String message;
	private boolean type;
	@SerializedName("getAllFriends")
	@Expose
	private List<UserAccount> friends;

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isType() {
		return type;
	}
	public void setType(boolean type) {
		this.type = type;
	}
	public List<UserAccount> getFriends() {
		return friends;
	}
	public void setFriends(List<UserAccount> friends) {
		this.friends = friends;
	}
	
	
	

}
