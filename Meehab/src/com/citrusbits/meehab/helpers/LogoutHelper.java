package com.citrusbits.meehab.helpers;

import com.citrusbits.meehab.HomeActivity;
import com.citrusbits.meehab.LoginAndRegisterActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.SocketService;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class LogoutHelper {

	Context mContext;

	public LogoutHelper(Context context) {
		this.mContext = context;
	}

	public void attemptLogout() {
		// clear db and prefs

		Intent i = new Intent(mContext, SocketService.class);

		if (mContext instanceof Activity) {
			((Activity) mContext).stopService(i);
		}

		clearLoginCredentails();

		Intent intent = new Intent(mContext, LoginAndRegisterActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("Exit me", true);
		if (mContext instanceof Activity) {
			Activity activity = (Activity) mContext;
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
			activity.finish();
		}

	}

	public void clearLoginCredentails() {
		App.getInstance()
				.getSharedPreferences(Consts.APP_PREFS_NAME,
						Context.MODE_PRIVATE).edit().clear().commit();

		AppPrefs prefs = AppPrefs.getAppPrefs(mContext);
		prefs.saveIntegerPrefs(AppPrefs.KEY_USER_ID, AppPrefs.DEFAULT.USER_ID);
		/*
		 * homeActivity.socketService.emit(EventParams.EVENT_USER_LOGOUT, new
		 * JSONObject(), null);
		 */
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		if (accessToken != null) {
			LoginManager.getInstance().logOut();
		}
	}
}
