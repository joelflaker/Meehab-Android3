package com.citrusbits.meehab.map;

import com.citrusbits.meehab.prefrences.AppPrefs;

import android.content.Context;
import android.location.Location;

public class LocationUtils {

	public static Location getLastLocation(Context context) {
		AppPrefs prefs = AppPrefs.getAppPrefs(context);
		double latitude = prefs.getDoubletPrefs(AppPrefs.KEY_LATITUDE,
				AppPrefs.DEFAULT.LATITUDE);
		double longitude = prefs.getDoubletPrefs(AppPrefs.KEY_LONGITUDE,
				AppPrefs.DEFAULT.LONGITUDE);
		Location location = new Location("A");
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		return location;
	}

}
