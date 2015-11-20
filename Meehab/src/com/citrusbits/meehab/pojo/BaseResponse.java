package com.citrusbits.meehab.pojo;

import com.google.gson.annotations.Expose;

public class BaseResponse {

	@Expose
	private String message;
	@Expose
	private Boolean type;

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

}
