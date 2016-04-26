package com.citrusbits.meehab.dialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.dialog.CustomTimePickerDialog.TimePickerDialogListener;

public class CustomTimePickerDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private TimePickerDialogListener timePickerDialogListener;

	private NumberPicker picker;

	private String timeSelected;

	private String[] timeRangeValues;


	public CustomTimePickerDialog(Context context) {

		// super(context,R.style.PauseDialog);
		super(context);
		this.context = context;
	}

	public CustomTimePickerDialog setTimePickerDialog(
			TimePickerDialogListener listener, String cal) {

		this.timePickerDialogListener = listener;
		this.timeSelected = cal;

		return this;
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		setContentView(R.layout.dialog_time_picker);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		

		 String[] arr = context.getResources().getStringArray(
				R.array.timeRangeValues);
		
		 int n = arr.length - 1;
		 timeRangeValues = new String[n];
		System.arraycopy(arr,1,timeRangeValues,0,n);

		int position = 0;
		for (int i = 0; i < timeRangeValues.length; i++) {
			String status = timeRangeValues[i];
			if (status.equals(timeSelected)) {
				position = i;
				break;
			}
		}

		picker = (NumberPicker) findViewById(R.id.npTimeRange);
		picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		picker.setMinValue(0);
		picker.setMaxValue(timeRangeValues.length - 1);
		picker.setValue(position);
		picker.setDisplayedValues(timeRangeValues);
		picker.setWrapSelectorWheel(false);
		
		findViewById(R.id.ibDone).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibDone:
			if (timePickerDialogListener != null) {
				timeSelected = timeRangeValues[picker
				         						.getValue()];
				timePickerDialogListener.onDoneClick(this, timeSelected);
			}

			break;
		case R.id.ibCancel:
			if (timePickerDialogListener != null) {
				timePickerDialogListener.onCancelClick(this);
			}
			break;
		}

	}

	public interface TimePickerDialogListener {

		public void onDoneClick(CustomTimePickerDialog dialog, String timeSelected);

		public void onCancelClick(CustomTimePickerDialog dialog);
	}
	
	
	public String getTime(Calendar calendar){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(calendar.getTime());
	}

}
