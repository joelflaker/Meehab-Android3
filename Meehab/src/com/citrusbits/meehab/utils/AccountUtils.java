package com.citrusbits.meehab.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormatSymbols;
import java.util.Formatter;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Location;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Spinner;

import com.citrusbits.meehab.HomeActivity;
import com.citrusbits.meehab.LoginAndRegisterActivity;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.google.android.gms.maps.model.LatLng;

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
