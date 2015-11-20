package com.citrusbits.meehab;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.map.LocationService;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.ContactSyncService;
import com.citrusbits.meehab.services.SocketService;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

public class SplashActivity extends Activity {

	private final int SPLASH_DISPLAY_LENGHT =0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* this.startService(new Intent(this, LocationService.class)); */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		FacebookSdk.sdkInitialize(this.getApplicationContext());

		setContentView(R.layout.activity_splash);

		// start background service
		Intent intent = new Intent(this, SocketService.class);
		intent.setAction("ui");
		startService(intent);
		
		startService(new Intent(this,ContactSyncService.class));

		final int userId = AppPrefs.getAppPrefs(this).getIntegerPrefs(
				AppPrefs.KEY_USER_ID, AppPrefs.DEFAULT.USER_ID);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (userId == AppPrefs.DEFAULT.USER_ID) {

					AccessToken accessToken = AccessToken
							.getCurrentAccessToken();
					if (accessToken != null) {
						LoginManager.getInstance().logOut();
					}

					Intent mainIntent = new Intent(SplashActivity.this,
							LoginAndRegisterActivity.class);
					SplashActivity.this.startActivity(mainIntent);
					overridePendingTransition(R.anim.activity_in,
							R.anim.activity_out);
					SplashActivity.this.finish();
				} else {

					/*
					 * Intent intent = new Intent(SplashActivity.this,
					 * TwoOptionActivity.class);
					 */
					AppPrefs prefs = AppPrefs.getAppPrefs(SplashActivity.this);
					boolean profileSetup = prefs.getBooleanPrefs(
							AppPrefs.KEY_PROFILE_SETUP,
							AppPrefs.DEFAULT.PROFILE_SETUP);
					boolean profileSetupMore = prefs.getBooleanPrefs(
							AppPrefs.KEY_PROFILE_SETUP_MORE,
							AppPrefs.DEFAULT.PROFILE_SETUP_MORE);
					Intent intent;
					if (!profileSetup) {
						intent = new Intent(SplashActivity.this,
								ProfileSetupActivity.class);
					} else if (!profileSetupMore) {
						intent = new Intent(SplashActivity.this,
								ProfileSetupMoreActivity.class);
					} else {
						intent = new Intent(SplashActivity.this,
								TwoOptionActivity.class);
					}
					SplashActivity.this.finish();
					intent.putExtra("message", "");
					overridePendingTransition(R.anim.activity_in,
							R.anim.activity_out);
					startActivity(intent);
				}
			}
		}, SPLASH_DISPLAY_LENGHT);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}
