package com.citrusbits.meehab.model;

public class Contact {
	private String number;
	private String name;
	private String email;
	private String uomeUser;
	private String uomeFriend;
	private String uome_invite_add;
	private String is_name_exist;

	public Contact() {
		// default constructor
	}

	public Contact(String name, String number) {
		super();
		this.name = name;
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUomeUser() {
		return uomeUser;
	}

	public void setUomeUser(String uomeUser) {
		this.uomeUser = uomeUser;
	}

	public String getUome_invite_add() {
		return uome_invite_add;
	}

	public void setUome_invite_add(String uome_invite_add) {
		this.uome_invite_add = uome_invite_add;
	}

	public String getUomeFriend() {
		return uomeFriend;
	}

	public void setUomeFriend(String uomeFriend) {
		this.uomeFriend = uomeFriend;
	}

	public String getIs_name_exist() {
		return is_name_exist;
	}

	public void setIs_name_exist(String is_name_exist) {
		this.is_name_exist = is_name_exist;
	}

}
