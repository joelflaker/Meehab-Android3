package com.citrusbits.meehab.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextViewBold extends TextView{

	public CustomTextViewBold(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomTextViewBold(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomTextViewBold(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "vagrounded-bold.ttf");
			setTypeface(tf);
		}
	}
}
