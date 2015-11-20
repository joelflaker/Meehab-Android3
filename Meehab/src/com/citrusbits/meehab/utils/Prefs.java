package com.citrusbits.meehab.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.citrusbits.meehab.constants.Consts;


public class Prefs {
	//user prefs
	public static final String PREFS_GCM_TOKEN = "gcm_token";
	public static final String PREF_USER_ID = "user_id";
	public static final String PREF_ACCESS_TOKEN = "user_access_token";
	public static final String PREF_IS_LOGGED_IN = "is_logged_in";
	public static final String PREF_PASS = "user_pass";

	SharedPreferences sharedPrefs;
	Context context;
	public Prefs(Context ctx) {
		context = ctx;
		sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	public SharedPreferences getPrefs(){
		return sharedPrefs;
	}
	
	public String getString(String key){
		return sharedPrefs.getString(key, null);
	}

	public String getfacebookId(){
		return sharedPrefs.getString(Consts.FACEBOOK_ID, null);
	}

	public void setfacebookInfo(String facebookId,String facebookToken, String facebookName, String facebookEmail){
		Editor e = sharedPrefs.edit();
		e.putString(Consts.FACEBOOK_ID, facebookId); 
		e.putString(Consts.FACEBOOK_TOKEN, facebookToken);
		e.putString(Consts.FACEBOOK_NAME, facebookName);
		e.putString(Consts.FACEBOOK_EMAIL, facebookEmail); 
		e.commit();
	}

	public String getfacebookName(){
		return sharedPrefs.getString(Consts.FACEBOOK_NAME, null);
	}
	public void logout(){
		//		if (Session.getActiveSession() != null) {
		//			Session.getActiveSession().closeAndClearTokenInformation();
		//			Utility.clearFacebookCookies(ctx);
		//		}
		//		Session.setActiveSession(null);
		Editor e = sharedPrefs.edit();
		e.clear();
		e.commit();
	}

	public String getfacebookEmail(){
		return sharedPrefs.getString(Consts.FACEBOOK_EMAIL, null);
	}
}
