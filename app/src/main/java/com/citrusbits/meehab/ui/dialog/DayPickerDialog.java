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

public class DayPickerDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private SelectDayDialogListener selectDayDialogListener;

	private NumberPicker npSelectDay;

	private String daySelected = "";

	private String[] dayStatus;

	public DayPickerDialog(Context context) {

		// super(context,R.style.PauseDialog);
		super(context);
		this.context = context;
	}

	public DayPickerDialog setDayDialogListener(
			SelectDayDialogListener listener, String day) {

		this.selectDayDialogListener = listener;
		this.daySelected = day;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		setContentView(R.layout.dialog_select_day);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		npSelectDay = (NumberPicker) findViewById(R.id.npSelectDay);
		findViewById(R.id.ibDone).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);

		int position = 0;
		dayStatus = context.getResources().getStringArray(
				R.array.days_arr);

		for (int i = 0; i < dayStatus.length; i++) {
			String status = dayStatus[i];
			if (status.equals(daySelected)) {
				position = i;
				break;
			}
		}

		npSelectDay
				.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npSelectDay.setMinValue(0);
		npSelectDay.setMaxValue(dayStatus.length - 1);
		npSelectDay.setValue(position);
		npSelectDay.setDisplayedValues(dayStatus);
		npSelectDay.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibDone:
			if (selectDayDialogListener != null) {
				daySelected = dayStatus[npSelectDay.getValue()];
				selectDayDialogListener.onDoneClick(this, daySelected);
			}

			break;
		case R.id.ibCancel:
			if (selectDayDialogListener != null) {
				selectDayDialogListener.onCancelClick(this);
			}
			break;
		}

	}

	public interface SelectDayDialogListener {
		void onDoneClick(DayPickerDialog dialog,
				String maritalStatusSelected);

		void onCancelClick(DayPickerDialog dialog);
	}

}
