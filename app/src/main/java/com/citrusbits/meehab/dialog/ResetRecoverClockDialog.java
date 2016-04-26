package com.citrusbits.meehab.dialog;

import java.util.Calendar;
import java.util.Date;

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

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.dialog.ResetRecoverClockDialog.ResetRecoveryClockDialogListener;
import com.citrusbits.meehab.utils.DateTimeUtils;

public class ResetRecoverClockDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private ResetRecoveryClockDialogListener recoveryClockDialogListener;

	private DatePicker dpDB;

	private Calendar calendar;

	private String dateSelected = "";

	

	public ResetRecoverClockDialog(Context context) {

		// super(context,R.style.PauseDialog);
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public ResetRecoverClockDialog setRecoveryClockDialogListener(
			ResetRecoveryClockDialogListener listener, Calendar cal) {

		this.recoveryClockDialogListener = listener;
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

		setContentView(R.layout.dialog_reset_recovery_clock);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		TextView tvHeading = (TextView) findViewById(R.id.tvHeading);
		if (calendar == null) {
			calendar = Calendar.getInstance();
		}

		

		dateSelected =DateTimeUtils.calendarToDate(calendar);

		dpDB = (DatePicker) findViewById(R.id.dpDB);
		dpDB.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH),
				new MyOnDateChangedListener());
		
		dpDB.setMaxDate(new Date().getTime());

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
			if (recoveryClockDialogListener != null) {
				recoveryClockDialogListener.onDoneClick(this, dateSelected);
			}

			break;
		case R.id.ibCancel:
			if (recoveryClockDialogListener != null) {
				recoveryClockDialogListener.onCancelClick(this);
			}
			break;
		}

	}

	public interface ResetRecoveryClockDialogListener {

		public void onDoneClick(ResetRecoverClockDialog dialog,
				String dateSelected);

		public void onCancelClick(ResetRecoverClockDialog dialog);
	}

}
