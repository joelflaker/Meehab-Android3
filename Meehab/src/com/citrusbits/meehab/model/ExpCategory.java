package com.citrusbits.meehab.model;

import java.util.ArrayList;

public class ExpCategory {

	private String name;
	private String value;
	private ArrayList<ExpChild> children = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ArrayList<ExpChild> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<ExpChild> children) {
		this.children = children;
	}

	public void addChild(ExpChild child) {
		children.add(child);

	}
}
