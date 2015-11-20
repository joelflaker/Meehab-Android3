package com.citrusbits.meehab.pojo;

import com.google.gson.annotations.Expose;

public class ContactSyncResponse extends BaseResponse {

	@Expose
	private ContactSyncData data = new ContactSyncData();

	/**
	 * 
	 * @return The data
	 */
	public ContactSyncData getData() {
		return data;
	}

	/**
	 * 
	 * @param data
	 *            The data
	 */
	public void setData(ContactSyncData data) {
		this.data = data;
	}

}