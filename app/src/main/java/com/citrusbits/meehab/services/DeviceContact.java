package com.citrusbits.meehab.services;

public class DeviceContact {

	private String contactName;
	private String phoneNumber;
	private int contactId;
	private int version;
	private String contactAction;
	private int syncFlag;

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getContactId() {
		return contactId;
	}

	public void setContactId(int contactId) {
		this.contactId = contactId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactAction() {
		return contactAction;
	}

	public void setContactAction(String contactAction) {
		this.contactAction = contactAction;
	}

	public int getSyncFlag() {
		return syncFlag;
	}

	public void setSyncFlag(int syncFlag) {
		this.syncFlag = syncFlag;
	}
	

	public enum ContactAction {
		ADD, DELETE
	}

}
