package com.citrusbits.meehab.dialog;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.utils.DateTimeUtils;

public class DobPickerDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private DatePickerDialogListener datePickerDialogListener;

	private DatePicker dpDB;

	private Calendar calendar;

	private String dateSelected = "";
	
	boolean sober;

	public DobPickerDialog(Context context) {

		// super(context,R.style.PauseDialog);
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public DobPickerDialog setDobDialogListener(
			DatePickerDialogListener listener, Calendar cal) {

		this.datePickerDialogListener = listener;
		this.calendar = cal;

		return this;
	}
	
	public DobPickerDialog setSober(
			boolean sober) {

		this.sober=sober;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		
	
		
		setContentView(R.layout.dialog_date_picker);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		
		TextView tvHeading = (TextView) findViewById(R.id.tvHeading);
		if (calendar == null) {
			calendar = Calendar.getInstance();
		}
		
		if(sober){
			tvHeading.setText("Sober Date");
		}
		
		/*dateSelected = "" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/"
				+ calendar.get(Calendar.YEAR);*/
		
		dateSelected = DateTimeUtils.calendarToDate(calendar);

		dpDB = (DatePicker) findViewById(R.id.dpDB);
		dpDB.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				new MyOnDateChangedListener());

		findViewById(R.id.ibDone).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);
	}

	private class MyOnDateChangedListener implements OnDateChangedListener {
		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			
			Calendar calendar=Calendar.getInstance();
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, monthOfYear);
			calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			
			dateSelected = DateTimeUtils.calendarToDate(calendar);
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibDone:
			if (datePickerDialogListener != null) {
				datePickerDialogListener.onDoneClick(this, dateSelected);
			}

			break;
		case R.id.ibCancel:
			if (datePickerDialogListener != null) {
				datePickerDialogListener.onCancelClick(this);
			}
			break;
		}

	}

	public interface DatePickerDialogListener {

		public void onDoneClick(DobPickerDialog dialog, String dateSelected);

		public void onCancelClick(DobPickerDialog dialog);
	}

}
