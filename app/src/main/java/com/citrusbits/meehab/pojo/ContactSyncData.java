package com.citrusbits.meehab.pojo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class ContactSyncData {

	@Expose
	private List<ContactSyncPojo> uomers = new ArrayList<ContactSyncPojo>();
	@Expose
	private List<ContactSyncPojo> friends = new ArrayList<ContactSyncPojo>();

	/**
	 * 
	 * @return The uomers
	 */
	public List<ContactSyncPojo> getUomers() {
		return uomers;
	}

	/**
	 * 
	 * @param uomers
	 *            The uomers
	 */
	public void setUomers(List<ContactSyncPojo> uomers) {
		this.uomers = uomers;
	}

	/**
	 * 
	 * @return The friends
	 */
	public List<ContactSyncPojo> getFriends() {
		return friends;
	}

	/**
	 * 
	 * @param friends
	 *            The friends
	 */
	public void setFriends(List<ContactSyncPojo> friends) {
		this.friends = friends;
	}

}