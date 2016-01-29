package com.citrusbits.meehab.utils;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class DeviceUtils {

	public static String getDeviceId(Context context) {

		String deviceId = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);

		// return "6939E21C-5CF3-4AF7-B821-7C2D8D699E42";

		// return "E2C6C6FF-3556-46D8-B55D-32DDB6CF961E";

		return deviceId;
		// return "78f56c1d225d69bc";
	}

	public static int getDeviceWidth(Context context) {
		int width = ((Activity) context).getWindowManager().getDefaultDisplay()
				.getWidth();
		return width;
	}

	public static void hideSoftKeyboard(Context context) {
		View view = ((Activity) context).getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static void showSoftKeyboard(Context context) {
		View view = ((Activity) context).getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);

			imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
		}
	}

}
