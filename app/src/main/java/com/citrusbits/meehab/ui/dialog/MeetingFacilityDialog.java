package com.citrusbits.meehab.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.NumberPicker;

import com.citrusbits.meehab.R;

public class MeetingFacilityDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private MeetingFacilityDialogListener meetingFacilityListener;

	private NumberPicker npMeetingFacility;

	private String meetingFacilitySelected = "";

	private String[] meetingFacilityStatus;

	public MeetingFacilityDialog(Context context) {

		// super(context,R.style.PauseDialog);
		super(context);
		this.context = context;
	}

	public MeetingFacilityDialog setMeetingFacilityListener(
			MeetingFacilityDialogListener listener, String meetingAcility) {

		this.meetingFacilityListener = listener;
		this.meetingFacilitySelected = meetingAcility;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		setContentView(R.layout.dialog_type_of_meeting_facility);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		npMeetingFacility = (NumberPicker) findViewById(R.id.npMeetingFacility);
		findViewById(R.id.ibDone).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);

		int position = 0;
		meetingFacilityStatus = context.getResources().getStringArray(
				R.array.meeting_type_facility);

		for (int i = 0; i < meetingFacilityStatus.length; i++) {
			String status = meetingFacilityStatus[i];
			if (status.equals(meetingFacilitySelected)) {
				position = i;
				break;
			}
		}

		npMeetingFacility
				.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npMeetingFacility.setMinValue(0);
		npMeetingFacility.setMaxValue(meetingFacilityStatus.length - 1);
		npMeetingFacility.setValue(position);
		npMeetingFacility.setDisplayedValues(meetingFacilityStatus);
		npMeetingFacility.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibDone:
			if (meetingFacilityListener != null) {
				meetingFacilitySelected = meetingFacilityStatus[npMeetingFacility
						.getValue()];
				meetingFacilityListener.onDoneClick(this,
						meetingFacilitySelected);
			}

			break;
		case R.id.ibCancel:
			if (meetingFacilityListener != null) {
				meetingFacilityListener.onCancelClick(this);
			}
			break;
		}

	}

	public interface MeetingFacilityDialogListener {

		public void onDoneClick(MeetingFacilityDialog dialog,
				String meetingFacility);

		public void onCancelClick(MeetingFacilityDialog dialog);
	}

}
