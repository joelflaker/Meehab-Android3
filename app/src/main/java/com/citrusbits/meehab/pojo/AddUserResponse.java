/**
 * 
 */
package com.citrusbits.meehab.pojo;

import com.citrusbits.meehab.model.UserAccount;
import com.google.gson.annotations.Expose;

/**
 * @author Qamar
 *
 */


public class AddUserResponse {
	@Expose
	private AccessToken accessToken;
	@Expose
	private String message;
	@Expose
	private Boolean type;
	@Expose
	private UserAccount user;

	/**
	* 
	* @return
	* The accessToken
	*/
	public AccessToken getAccessToken() {
	return accessToken;
	}

	/**
	* 
	* @param accessToken
	* The accessToken
	*/
	public void setAccessToken(AccessToken accessToken) {
	this.accessToken = accessToken;
	}

	/**
	* 
	* @return
	* The message
	*/
	public String getMessage() {
	return message;
	}

	/**
	* 
	* @param message
	* The message
	*/
	public void setMessage(String message) {
	this.message = message;
	}

	/**
	* 
	* @return
	* The type
	*/
	public Boolean getType() {
	return type;
	}

	/**
	* 
	* @param type
	* The type
	*/
	public void setType(Boolean type) {
	this.type = type;
	}

	/**
	* 
	* @return
	* The user
	*/
	public UserAccount getUser() {
	return user;
	}

	/**
	* 
	* @param user
	* The user
	*/
	public void setUser(UserAccount user) {
	this.user = user;
	}

}
