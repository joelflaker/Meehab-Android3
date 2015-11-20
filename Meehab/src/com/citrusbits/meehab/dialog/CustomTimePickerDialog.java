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
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.dialog.CustomTimePickerDialog.TimePickerDialogListener;

public class CustomTimePickerDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private TimePickerDialogListener timePickerDialogListener;

	private TimePicker timePicker;

	private Calendar calendar;

	private String timeSelected = "";

	

	public CustomTimePickerDialog(Context context) {

		// super(context,R.style.PauseDialog);
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public CustomTimePickerDialog setTimePickerDialog(
			TimePickerDialogListener listener, Calendar cal) {

		this.timePickerDialogListener = listener;
		this.calendar = cal;

		return this;
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		setContentView(R.layout.dialog_time_picker);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		
		if (calendar == null) {
			calendar = Calendar.getInstance();
		}

	

		timeSelected = getTime(calendar);

		timePicker = (TimePicker) findViewById(R.id.timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		
		
		findViewById(R.id.ibDone).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibDone:
			if (timePickerDialogListener != null) {
				calendar.set(Calendar.HOUR, timePicker.getCurrentHour());
				calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
				timeSelected=getTime(calendar);
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
