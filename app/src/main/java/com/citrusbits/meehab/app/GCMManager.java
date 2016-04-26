package com.citrusbits.meehab.app;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.citrusbits.meehab.prefrences.AppPrefs;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMManager {

	GoogleCloudMessaging gcm;
	String regid;
	String PROJECT_NUMBER = "177182026110";

	private Context mContext;

	public GCMManager(Context context) {
		this.mContext = context;

	}

	public void getRegId() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(mContext
								.getApplicationContext());
					}

					regid = gcm.register(PROJECT_NUMBER);

					AppPrefs.getAppPrefs(mContext).saveStringPrefs(
							AppPrefs.KEY_GCM_REG_ID, regid);

					msg = "Device registered, registration ID=" + regid;
					Log.i("GCM", msg);

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();

				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// etRegId.setText(msg + "\n");
				Log.e("Result", msg);
			}
		}.execute(null, null, null);
	}
	
	public static String getCMId(Context context){
		return AppPrefs.getAppPrefs(context).getStringPrefs(AppPrefs.KEY_GCM_REG_ID, "");
	}

}
