/**
 * 
 */
package com.citrusbits.meehab.pojo;

import com.google.gson.annotations.Expose;

/**
 * @author Qamar
 *
 */
public class AccessToken {
	@Expose
	private String accessToken;
	@Expose
	private Integer id;
	@Expose
	private Integer userID;

	/**
	* 
	* @return
	* The accessToken
	*/
	public String getAccessToken() {
	return accessToken;
	}

	/**
	* 
	* @param accessToken
	* The accessToken
	*/
	public void setAccessToken(String accessToken) {
	this.accessToken = accessToken;
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
}
