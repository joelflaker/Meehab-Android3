/**
 * 
 */
package com.citrusbits.meehab.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author Qamar
 *
 */
public class TMeeting {
	boolean isChecked;
	boolean isCheckBoxVisible;
	private String name;
    private LatLng position;

    public TMeeting(String name, LatLng position) {
        this.name = name;
        this.position = position;
    }
    public boolean isChecked() {
    	return isChecked;
    }
    
	public boolean isCheckBoxVisible() {
		return isCheckBoxVisible;
	}
	public void setCheckBoxVisible(boolean isCheckBoxVisible) {
		this.isCheckBoxVisible = isCheckBoxVisible;
	}
	public void setChecked(boolean isChecked) {
    	this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public LatLng getPosition() {
        return position;
    }
	
}
