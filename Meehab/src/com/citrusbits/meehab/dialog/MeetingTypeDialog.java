package com.citrusbits.meehab.dialog;

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

public class MeetingTypeDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private MeetingTypeDialogListener meetingTypeListener;

	private NumberPicker npMeetingType;

	private String meetingTypeSelected = "";

	private String[] meetingTypeStatus;

	public MeetingTypeDialog(Context context) {

		// super(context,R.style.PauseDialog);
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public MeetingTypeDialog setMeetingTypeListener(
			MeetingTypeDialogListener listener, String meetingType) {

		this.meetingTypeListener = listener;
		this.meetingTypeSelected = meetingType;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		setContentView(R.layout.dialog_meeting_type);
		
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		
		
		npMeetingType = (NumberPicker) findViewById(R.id.npMeetingType);
		findViewById(R.id.ibDone).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);

		int position = 0;
		meetingTypeStatus = context.getResources().getStringArray(
				R.array.meetingsTypesValues);

		for (int i = 0; i < meetingTypeStatus.length; i++) {
			String status = meetingTypeStatus[i];
			if (status.equals(meetingTypeSelected)) {
				position = i;
				break;
			}
		}

		npMeetingType
				.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npMeetingType.setMinValue(0);
		npMeetingType.setMaxValue(meetingTypeStatus.length - 1);
		npMeetingType.setValue(position);
		npMeetingType.setDisplayedValues(meetingTypeStatus);
		npMeetingType.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibDone:
			if (meetingTypeListener != null) {
				meetingTypeSelected = meetingTypeStatus[npMeetingType
						.getValue()];
				meetingTypeListener.onDoneClick(this, meetingTypeSelected);
			}

			break;
		case R.id.ibCancel:
			if (meetingTypeListener != null) {
				meetingTypeListener.onCancelClick(this);
			}
			break;
		}

	}

	public interface MeetingTypeDialogListener {

		public void onDoneClick(MeetingTypeDialog dialog,
				String maritalStatusSelected);

		public void onCancelClick(MeetingTypeDialog dialog);
	}

}
