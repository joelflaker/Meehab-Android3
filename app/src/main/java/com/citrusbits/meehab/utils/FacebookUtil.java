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


public class FacebookUtil {
	
	public static boolean isConnected(Context ctx) {
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
		return sharedPrefs.getString(Consts.FACEBOOK_ID, null) != null;
	}
	
	public static String getfacebookId(Context ctx){
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
		return sharedPrefs.getString(Consts.FACEBOOK_ID, null);
	}
	
	public static void setfacebookInfo(Context ctx, String facebookId,String facebookToken, String facebookName, String facebookEmail){
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
        Editor e = sharedPrefs.edit();
        e.putString(Consts.FACEBOOK_ID, facebookId); 
        e.putString(Consts.FACEBOOK_TOKEN, facebookToken);
        e.putString(Consts.FACEBOOK_NAME, facebookName);
        e.putString(Consts.FACEBOOK_EMAIL, facebookEmail); 
        e.commit();
	}
	
//	public static void setfacebookId(Context ctx, String facebookId){
//		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
//        Editor e = sharedPrefs.edit();
//        e.putString(Consts.PREF_FACEBOOK_ID, facebookId); 
//        e.commit();
//	}
	
	public static String getfacebookName(Context ctx){
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
		return sharedPrefs.getString(Consts.FACEBOOK_NAME, null);
	}
//	
//	public static void setfacebookName(Context ctx, String facebookName){
//		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
//        Editor e = sharedPrefs.edit();
//        e.putString(Consts.PREF_FACEBOOK_NAME, facebookName); 
//        e.commit();
//	}
//	
	public static void logout(Context ctx){
//		if (Session.getActiveSession() != null) {
//			Session.getActiveSession().closeAndClearTokenInformation();
//			Utility.clearFacebookCookies(ctx);
//		}
//		Session.setActiveSession(null);
//		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
//		Editor e = sharedPrefs.edit();
//		e.clear();
//		e.commit();
	}
	
	public static String getfacebookEmail(Context ctx){
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
		return sharedPrefs.getString(Consts.FACEBOOK_EMAIL, null);
	}
//	
//	public static void setfacebookEmail(Context ctx, String facebookEmail){
//		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
//        Editor e = sharedPrefs.edit();
//        e.putString(Consts.PREF_FACEBOOK_EMAIL, facebookEmail); 
//        e.commit();
//	}
	
	public static String printKeyHash(Activity context) {
		PackageInfo packageInfo;
		String key = null;
		try {

			//getting application package name, as defined in manifest
			String packageName = context.getApplicationContext().getPackageName();

			//Retriving package info
			packageInfo = context.getPackageManager().getPackageInfo(packageName,
					PackageManager.GET_SIGNATURES);
			
			Log.e("Package Name=", context.getApplicationContext().getPackageName());
			
			for (Signature signature : packageInfo.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				key = new String(Base64.encode(md.digest(), 0));
			
				// String key = new String(Base64.encodeBytes(md.digest()));
				Log.e("Key Hash=", key);

			}
		} catch (NameNotFoundException e1) {
			Log.e("Name not found", e1.toString());
		}

		catch (NoSuchAlgorithmException e) {
			Log.e("No such an algorithm", e.toString());
		} catch (Exception e) {
			Log.e("Exception", e.toString());
		}

		return key;
	}
}
