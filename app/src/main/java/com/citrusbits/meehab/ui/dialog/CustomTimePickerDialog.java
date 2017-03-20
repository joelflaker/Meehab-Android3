package com.citrusbits.meehab.ui.dialog;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.citrusbits.meehab.R;

public class CustomTimePickerDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private TimePickerDialogListener timePickerDialogListener;

	private TimePicker picker;

	private String timeSelected;

//	private String[] timeValues;

	private static final int TIME_PICKER_INTERVAL=15;
	private boolean mIgnoreEvent=false;
	private TimePicker.OnTimeChangedListener mTimePickerListener=new TimePicker.OnTimeChangedListener(){
		public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute){
			if (mIgnoreEvent)
				return;
			if (minute%TIME_PICKER_INTERVAL!=0){
				int minuteFloor=minute-(minute%TIME_PICKER_INTERVAL);
				minute=minuteFloor + (minute==minuteFloor+1 ? TIME_PICKER_INTERVAL : 0);
				if (minute==60)
					minute=0;
				mIgnoreEvent=true;
				timePicker.setCurrentMinute(minute);
				mIgnoreEvent=false;
			}

		}
	};
	private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");;

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


//		timeValues = context.getResources().getStringArray(
//				R.array.timeValues);
		
//		 int n = arr.length - 1;
//		 timeValues = new String[n];
//		System.arraycopy(arr,1,timeRangeValues,0,n);

//		Calendar now = Calendar.getInstance();
//		now.set(Calendar.HOUR_OF_DAY,6);
//		now.set(Calendar.MINUTE,0);

//		ArrayList<String> timeList = new ArrayList<>();
//        timeList.add(format.format(now.getTime()));
//		int date = now.get(Calendar.DAY_OF_MONTH) + 1;
//		do{
//            now.add(Calendar.MINUTE,15);
//            timeList.add(format.format(now.getTime()));
//		}while (now.get(Calendar.DAY_OF_MONTH) < date || now.get(Calendar.HOUR_OF_DAY) < 5 || now.get(Calendar.MINUTE) < 45);
//
//		timeValues = timeList.toArray(new String[timeList.size()]);
//		int position = 0;
//		for (int i = 0; i < timeValues.length; i++) {
//			String status = timeValues[i];
//			if (status.equals(timeSelected)) {
//				position = i;
//				break;
//			}
//		}
		picker = (TimePicker) findViewById(R.id.npTimeRange);
		picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
//		picker.setOnTimeChangedListener(mTimePickerListener);
//		picker.setMinValue(0);
//		picker.setMaxValue(timeValues.length - 1);
//		picker.setValue(position);
//		picker.setDisplayedValues(timeValues);
//		picker.setWrapSelectorWheel(false);

		setTimePickerInterval(picker);

		Date selectedDate;
		if(timeSelected != null){
			try {
				selectedDate = dateFormat.parse(timeSelected);
				int hour = selectedDate.getHours();
				if (Build.VERSION.SDK_INT >= 23)
					picker.setHour(hour);
				else{
					picker.setCurrentHour(hour);
				}
				int minute = selectedDate.getMinutes();
				if (Build.VERSION.SDK_INT >= 23)
					picker.setMinute(minute / TIME_PICKER_INTERVAL);
				else{
					picker.setCurrentMinute(minute / TIME_PICKER_INTERVAL);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else {
			int currentMint = Calendar.getInstance().get(Calendar.MINUTE);
			int minute = currentMint - currentMint % 5;
			if (Build.VERSION.SDK_INT >= 23)
				picker.setMinute(currentMint);
			else{
				picker.setCurrentMinute(currentMint);
			}
		}

		findViewById(R.id.ibDone).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);
	}

	@SuppressLint("NewApi")
	private void setTimePickerInterval(TimePicker timePicker) {
		try {
			Class<?> classForid = Class.forName("com.android.internal.R$id");
			// Field timePickerField = classForid.getField("timePicker");

			Field field = classForid.getField("minute");
			NumberPicker minutePicker = (NumberPicker) timePicker
					.findViewById(field.getInt(null));

			minutePicker.setMinValue(0);
			minutePicker.setMaxValue(3);
			ArrayList<String> displayedValues = new ArrayList<>();
			for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
				displayedValues.add(String.format("%2d", i));
			}
			//  for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
			//      displayedValues.add(String.format("%02d", i));
			//  }
			minutePicker.setDisplayedValues(displayedValues
					.toArray(new String[0]));
			minutePicker.setWrapSelectorWheel(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibDone:
			if (timePickerDialogListener != null) {
				int hour;
				if (Build.VERSION.SDK_INT >= 23)
					hour = picker.getHour();
				else{
					hour = picker.getCurrentHour();
				}
				int minute;
				if (Build.VERSION.SDK_INT >= 23)
					minute = picker.getMinute() * TIME_PICKER_INTERVAL;
				else{
					minute = picker.getCurrentMinute() * TIME_PICKER_INTERVAL;
				}
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				calendar.set(Calendar.MINUTE, minute);
				timeSelected = dateFormat.format(calendar.getTime());
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
