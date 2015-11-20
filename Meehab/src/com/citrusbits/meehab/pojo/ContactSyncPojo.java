package com.citrusbits.meehab.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactSyncPojo {

	@Expose
	private Integer uid;
	@SerializedName("CONCAT('+1',mobile)")
	@Expose
	private String CONCAT1Mobile;
	@Expose
	private String photo;
	@Expose
	private String mobile;
	@SerializedName("user_balance")
	@Expose
	private Integer userBalance;
	@SerializedName("friend_balance")
	@Expose
	private Integer friendBalance;

	/**
	 * 
	 * @return The uid
	 */
	public Integer getUid() {
		return uid;
	}

	/**
	 * 
	 * @param uid
	 *            The uid
	 */
	public void setUid(Integer uid) {
		this.uid = uid;
	}

	/**
	 * 
	 * @return The CONCAT1Mobile
	 */
	public String getCONCAT1Mobile() {
		return CONCAT1Mobile;
	}

	/**
	 * 
	 * @param CONCAT1Mobile
	 *            The CONCAT('+1',mobile)
	 */
	public void setCONCAT1Mobile(String CONCAT1Mobile) {
		this.CONCAT1Mobile = CONCAT1Mobile;
	}

	/**
	 * 
	 * @return The photo
	 */
	public String getPhoto() {
		return photo;
	}

	/**
	 * 
	 * @param photo
	 *            The photo
	 */
	public void setPhoto(String photo) {
		this.photo = photo;
	}

	/**
	 * 
	 * @return The mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * 
	 * @param mobile
	 *            The mobile
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * 
	 * @return The userBalance
	 */
	public Integer getUserBalance() {
		return userBalance;
	}

	/**
	 * 
	 * @param userBalance
	 *            The user_balance
	 */
	public void setUserBalance(Integer userBalance) {
		this.userBalance = userBalance;
	}

	/**
	 * 
	 * @return The friendBalance
	 */
	public Integer getFriendBalance() {
		return friendBalance;
	}

	/**
	 * 
	 * @param friendBalance
	 *            The friend_balance
	 */
	public void setFriendBalance(Integer friendBalance) {
		this.friendBalance = friendBalance;
	}

}