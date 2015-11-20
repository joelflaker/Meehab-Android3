package com.citrusbits.meehab.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

public class ScreenUtils {
	public static int[] screenWidthHeighDP(Context context) {
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		float density = context.getResources().getDisplayMetrics().density;
		float dpHeight = outMetrics.heightPixels / density;
		float dpWidth = outMetrics.widthPixels / density;
		return new int[] { (int) dpWidth, (int) dpHeight };
	}
	
	public static int[] screenWidthHeigh(Context context) {
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		float density = context.getResources().getDisplayMetrics().density;
		float dpHeight = outMetrics.heightPixels;
		float dpWidth = outMetrics.widthPixels;
		return new int[] { (int) dpWidth, (int) dpHeight };
	}
}
