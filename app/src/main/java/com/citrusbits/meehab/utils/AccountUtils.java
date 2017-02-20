package com.citrusbits.meehab.utils;

import android.content.Context;

import com.citrusbits.meehab.prefrences.AppPrefs;

public class AccountUtils {

	public static int getUserId(Context context) {
		AppPrefs prefs = AppPrefs.getAppPrefs(context);
		return prefs.getIntegerPrefs(AppPrefs.KEY_USER_ID,
				AppPrefs.DEFAULT.USER_ID);
	}

	public static void saveUserId(Context context, int userId) {
		AppPrefs prefs = AppPrefs.getAppPrefs(context);
		prefs.getIntegerPrefs(AppPrefs.KEY_USER_ID, AppPrefs.DEFAULT.USER_ID);
		prefs.saveIntegerPrefs(AppPrefs.KEY_USER_ID, userId);
	}
	
	public static int getChatFriendId(Context context) {
		AppPrefs prefs = AppPrefs.getAppPrefs(context);
		return prefs.getIntegerPrefs(AppPrefs.KEY_CHAT_FRIEND_ID,
				AppPrefs.DEFAULT.CHAT_FRIEND_ID);
	}

	public static void setPassword(Context context,String value) {
		AppPrefs prefs = AppPrefs.getAppPrefs(context);
		prefs.saveStringPrefs(AppPrefs.KEY_USER_PASS,value);
	}

	public static String getPassword(Context context) {
		AppPrefs prefs = AppPrefs.getAppPrefs(context);
		return prefs.getStringPrefs(AppPrefs.KEY_USER_PASS,
				"");
	}

	public static void setTime(Context context,long value) {
		AppPrefs prefs = AppPrefs.getAppPrefs(context);
		prefs.saveLongPrefs(AppPrefs.KEY_USER_INS_TIME,value);
	}

	public static long getTime(Context context) {
		AppPrefs prefs = AppPrefs.getAppPrefs(context);
		return prefs.getLongPrefs(AppPrefs.KEY_USER_INS_TIME,0);
	}



}
