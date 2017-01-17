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
	

}
