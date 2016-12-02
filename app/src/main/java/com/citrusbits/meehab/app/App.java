package com.citrusbits.meehab.app;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.citrusbits.meehab.constants.Consts;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.model.LatLng;
import com.instabug.library.Instabug;

public class App extends Application {
	
	//Google analytics variables
    private static final String PROPERTY_ID = "UA-66671940-1";
    public static int GENERAL_TRACKER = 0;
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }
    
	/**
	 * Log or request TAG
	 */
	static Context context;
	public static final String TAG = "VolleyPatterns";
	public static boolean isPlayServiceOk = false;

	/**
	 * Global request queue for Volley
	 */
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	//	public static String APID;

	String SENDER_ID = "830535726535";

	//	public static final String EXTRA_MESSAGE = "message";
	//	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";

	GoogleCloudMessaging gcm;
	SharedPreferences prefs;
//	public Socket socketIO;
	public Bitmap globleBitmap;

	public static String regid;
	
	//public static int accountId;

	/**
	 * A singleton instance of the application class for easy access in other
	 * places
	 */
	private static App sInstance;
	private static LatLng myPosition = new LatLng(33.671447, 73.069612);;

	@Override
	public void onCreate() {
		super.onCreate();

		sInstance = this;
		context = this;

		//Instabug updated to new 1.7.4
//		Instabug.initialize((Application) context, "2dfe3b005171f96ba7b6151aa82be713")
//                    .setInvocationEvent(Instabug.IBGInvocationEvent.IBGInvocationEventShake);

	//	UserAccount user = new UserDatasource(this).findUser();
	//	accountId = (user != null )? user.getId(): -1;
		// initialize the singleton

		if (Build.VERSION.SDK_INT >= 14) {
			registerActivityLifecycleCallbacks(new MyLifecycleHandler());
		}

//		settings = getSharedPreferences(Consts.APPLICATION_PREFERENCE_NAME, 0);
		if (servicesOK()) {
			isPlayServiceOk = true;
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(this);

			if (regid.isEmpty()) {
				registerInBackground();
			}
		}else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
	}


	//	private void onstop() {
	//		new UtilityClass().logout(this);
	//	}
	
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		// String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		String registrationId = prefs.getString(Consts.GCM_TOKEN, "");
		if (registrationId.isEmpty()) {
			return "";
		}
		// Check if com.citrusbits.uome.app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// com.citrusbits.uome.app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {
		// This sample com.citrusbits.uome.app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your com.citrusbits.uome.app is up to you.
		return getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	private class GetTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			String msg = "";
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(context);
				}
				regid = gcm.register(SENDER_ID);


				Intent intent = new Intent("com.google.android.c2dm.intent.REGISTRATION");
				if(regid.compareTo("") == 0){
					regid = intent.getStringExtra("registration_id");
				}

				msg = "Device registered, registration ID=" + regid;

			} catch (IOException ex) {
				msg = "Error :" + ex.getMessage();
				// If there is an error, don't just keep trying to register.
				// Require the user to click a button again, or perform
				// exponential back-off.
			}

			Log.i("Message: ", "*************Message*************");
			Log.i("Message: ",  "" + msg);
			return msg;
		}

		@Override
		protected void onPostExecute(String msg) {
			int appVersion = getAppVersion(context);
			SharedPreferences.Editor editor = getGCMPreferences(context).edit();
			if(regid.compareTo("") == 0) {
				//				editor.putString(Consts.PREFS_GCM_TOKEN, "");
				registerInBackground();
			} else {
				editor.putString(Consts.GCM_TOKEN, regid);
			}

			editor.putInt(PROPERTY_APP_VERSION, appVersion);
			editor.commit();
		}

	}

	private void registerInBackground() {
		new GetTask().execute(null, null, null);
	}

	public boolean servicesOK() {
		switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ){
		case ConnectionResult.SUCCESS:
			return true;
		case ConnectionResult.SERVICE_MISSING: 
			Toast.makeText(this, "SERVICE MISSING", Toast.LENGTH_SHORT).show();
			break;
		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED: 
			Toast.makeText(this, "UPDATE REQUIRED", Toast.LENGTH_SHORT).show();
			break;
		default: Toast.makeText(this, GooglePlayServicesUtil.isGooglePlayServicesAvailable(this), Toast.LENGTH_SHORT).show();
		}

		return false;
	}

	public static Context getContext() {
		return context;
	}

	/**
	 * @return ApplicationController singleton instance
	 */
	public static synchronized App getInstance() {
		return sInstance;
	}

	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(this.mRequestQueue,
					new LruBitmapCache());
		}
		return this.mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
	
	public static void toast(String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}


	/**
	 * @return
	 */
	public static LatLng getMyPosition() {
		return myPosition;
	}


	public static void log(String string) {
		Log.d("Log", string);
	}

//	public synchronized Tracker getTracker(TrackerName trackerId) {
//        if (!mTrackers.containsKey(trackerId)) {
//
//            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
//            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
//                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(
//                            R.xml.global_tracker)
//                            : analytics.newTracker(R.xml.ecommerce_tracker);
//            t.enableAdvertisingIdCollection(true);
//            mTrackers.put(trackerId, t);
//        }
//        return mTrackers.get(trackerId);
//    }
	
	
	
	
}
