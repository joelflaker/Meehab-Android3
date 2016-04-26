package com.citrusbits.meehab.model;

public class FriendMessageModel {
	
	private int friendId;
	private String name;
	private int unreadMessageCount;

	public int getFriendId() {
		return friendId;
	}
	public void setFriendId(int friendId) {
		this.friendId = friendId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getUnreadMessageCount() {
		return unreadMessageCount;
	}
	public void setUnreadMessageCount(int unreadMessageCount) {
		this.unreadMessageCount = unreadMessageCount;
	}
	

}
