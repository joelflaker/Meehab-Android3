package com.citrusbits.meehab.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.dialog.AddReviewToCalendarDialog.AddReviewToCalendarDialogClickListener;

public class AddReviewToCalendarDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private AddReviewToCalendarDialogClickListener AddReviewToCalendarDialogClickListener;

	public AddReviewToCalendarDialog(Context context) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public AddReviewToCalendarDialog setAddReviewToCalendarDialogListener(
			AddReviewToCalendarDialogClickListener listener) {

		this.AddReviewToCalendarDialogClickListener = listener;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.dialog_add_to_calendar);
		findViewById(R.id.ibNo).setOnClickListener(this);
		findViewById(R.id.ibYes).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibNo:
			if (AddReviewToCalendarDialogClickListener != null) {
				AddReviewToCalendarDialogClickListener.onNoClick(this);
			}

			break;
		case R.id.ibYes:
			if (AddReviewToCalendarDialogClickListener != null) {
				AddReviewToCalendarDialogClickListener.onYesClick(this);
			}
			break;
		}

	}

	public interface AddReviewToCalendarDialogClickListener {

		public void onNoClick(AddReviewToCalendarDialog dialog);

		public void onYesClick(AddReviewToCalendarDialog dialog);
	}

}
