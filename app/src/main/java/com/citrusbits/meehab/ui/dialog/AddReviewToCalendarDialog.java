package com.citrusbits.meehab.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.citrusbits.meehab.R;

public class AddReviewToCalendarDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;
	private AddReviewToCalendarDialogClickListener AddReviewToCalendarDialogClickListener;

	private RsvpAction rsvpAction;

	public AddReviewToCalendarDialog(Context context) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public AddReviewToCalendarDialog setAddReviewToCalendarDialogListener(
			AddReviewToCalendarDialogClickListener listener,
			RsvpAction rsvpAction) {

		this.AddReviewToCalendarDialogClickListener = listener;
		this.rsvpAction = rsvpAction;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.dialog_add_to_calendar);
		if (rsvpAction == null) {
			rsvpAction = RsvpAction.RSVP;
		}
		TextView tvConfirmationText = (TextView) findViewById(R.id.tvConfirmationText);

		Log.e("RSVP", rsvpAction.name());

		tvConfirmationText.setText(getCommentText());

		findViewById(R.id.ibNo).setOnClickListener(this);
		findViewById(R.id.ibYes).setOnClickListener(this);
	}

	public String getCommentText() {
		String comment = context
				.getString(R.string.add_meeting_to_calendar_confirmation_text);
		switch (rsvpAction) {
		case RSVP:
			comment = context
					.getString(R.string.add_meeting_to_calendar_confirmation_text);
			break;
		case UNRSVP:
			comment = context
					.getString(R.string.remove_meeting_from_calendar_confirmation_text);
			break;
		case CHECKIN:
			comment = context
					.getString(R.string.check_meeting_confirmation_text);
			break;
		case CHECKOUT:
			comment = context
					.getString(R.string.check_out_meeting_confirmation_text);
			break;
		}
		return comment;
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
				AddReviewToCalendarDialogClickListener.onYesClick(this,
						rsvpAction);
			}
			break;
		}

	}

	public interface AddReviewToCalendarDialogClickListener {

		public void onNoClick(AddReviewToCalendarDialog dialog);

		public void onYesClick(AddReviewToCalendarDialog dialog,
				RsvpAction rsvpAction);
	}

	public enum RsvpAction {
		RSVP, UNRSVP, CHECKIN, CHECKOUT;
	}

}
