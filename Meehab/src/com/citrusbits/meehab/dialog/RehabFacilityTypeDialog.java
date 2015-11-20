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
import com.citrusbits.meehab.dialog.RehabFacilityTypeDialog.RehabFacilityTypeDialogListener;

public class RehabFacilityTypeDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private RehabFacilityTypeDialogListener facilityTypeListener;

	private NumberPicker npRehabType;

	private String facilityTypeSelected = "";

	private String[] rehabTypeStatus;

	public RehabFacilityTypeDialog(Context context) {

		// super(context,R.style.PauseDialog);
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public RehabFacilityTypeDialog setRehabFacilityTypeListener(
			RehabFacilityTypeDialogListener listener, String facilityType) {

		this.facilityTypeListener = listener;
		this.facilityTypeSelected = facilityType;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		setContentView(R.layout.dialog_rehab_facility);
		
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		npRehabType = (NumberPicker) findViewById(R.id.npRehabType);
		findViewById(R.id.ibDone).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);

		int position = 0;
		rehabTypeStatus = context.getResources().getStringArray(
				R.array.meetingsTypesValues);

		for (int i = 0; i < rehabTypeStatus.length; i++) {
			String status = rehabTypeStatus[i];
			if (status.equals(facilityTypeSelected)) {
				position = i;
				break;
			}
		}

		npRehabType
				.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npRehabType.setMinValue(0);
		npRehabType.setMaxValue(rehabTypeStatus.length - 1);
		npRehabType.setValue(position);
		npRehabType.setDisplayedValues(rehabTypeStatus);
		npRehabType.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibDone:
			if (facilityTypeListener != null) {
				facilityTypeSelected = rehabTypeStatus[npRehabType
						.getValue()];
				facilityTypeListener.onDoneClick(this, facilityTypeSelected);
			}

			break;
		case R.id.ibCancel:
			if (facilityTypeListener != null) {
				facilityTypeListener.onCancelClick(this);
			}
			break;
		}

	}

	public interface RehabFacilityTypeDialogListener {

		public void onDoneClick(RehabFacilityTypeDialog dialog,
				String rehabFacilityType);

		public void onCancelClick(RehabFacilityTypeDialog dialog);
	}

}
