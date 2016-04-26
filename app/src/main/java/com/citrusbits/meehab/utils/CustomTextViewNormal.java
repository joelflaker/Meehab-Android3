package com.citrusbits.meehab.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

public class CustomTextViewNormal extends TextView{

	public CustomTextViewNormal(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomTextViewNormal(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomTextViewNormal(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "vagrlsb.ttf");
			setTypeface(tf);
		}
	}
}
