package com.citrusbits.meehab.prefrences;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPrefs {
	public static final String KEY_PREFS_NAME = AppPrefs.class.getSimpleName();

	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";

	// User Inforamtions
	public static final String KEY_USER_NAME = "user_name";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_USER_IMAGE = "user_image";

	public static final String KEY_PROFILE_SETUP = "profile_set_up";
	public static final String KEY_PROFILE_SETUP_MORE = "profile_setup_more";

	public static final String KEY_CHAT_ACTIVITY_OPEN = "chat_activity_open";
	public static final String KEY_CHAT_FRIEND_ID = "chat_friend_id";

	Context mContext;
	SharedPreferences prefs;
	Editor editor;

	private static AppPrefs appPrefs;

	public static AppPrefs getAppPrefs(Context context) {
		if (appPrefs == null) {
			appPrefs = new AppPrefs(context);
		}

		return appPrefs;
	}

	private AppPrefs(Context context) {
		this.mContext = context;
		prefs = context.getSharedPreferences(KEY_PREFS_NAME,
				Context.MODE_PRIVATE);
		editor = prefs.edit();
	}

	// Boolean

	public void saveBooleanPrefs(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getBooleanPrefs(String key, boolean defValue) {
		return prefs.getBoolean(key, defValue);
	}

	// Integer
	public void saveIntegerPrefs(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public int getIntegerPrefs(String key, int defValue) {
		return prefs.getInt(key, defValue);
	}

	// Float
	public void saveFloatPrefs(String key, float value) {
		editor.putFloat(key, value);
		editor.commit();
	}

	public float getFloattPrefs(String key, float latitude) {
		return prefs.getFloat(key, latitude);
	}

	public void saveDoublePrefs(String key, double value) {
		editor.putLong(key, Double.doubleToLongBits(value));
		editor.commit();
	}

	public double getDoubletPrefs(String key, double defValue) {
		double value = Double.longBitsToDouble(prefs.getLong(key, 0));
		value = value == 0 ? defValue : value;
		return value;

	}

	// String
	public void saveStringPrefs(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public String getStringPrefs(String key, String defValue) {
		return prefs.getString(key, defValue);
	}

	public static final class DEFAULT {
		public static final float LATITUDE = 33.671447f;
		public static final float LONGITUDE = 73.069612f;

		public static final String USER_NAME = "";
		public static final int USER_ID = -1;
		public static final String USER_IMAGE = "";

		public static final boolean PROFILE_SETUP = false;;
		public static final boolean PROFILE_SETUP_MORE = false;

		public static final boolean CHAT_ACTIVITY_OPEN = false;
		
		public static final int CHAT_FRIEND_ID = -1;
	}

}
