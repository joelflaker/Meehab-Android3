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

public class HeightPickerDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private HeightPickerDialogListener heightPickerListener;

	private NumberPicker npFeet;
	private NumberPicker npInches;

	private String heightSelected = "";

	private String[] maritalStatuses;
	
	final String[] valuesFeet = new String[] {"4'", "5'", "6'", "7'", "8'", "9'", "10'" };
	final String[] valuesInches = new String[] { "0''", "1''", "2''",
			"3''", "4''", "5''", "6''", "7''", "8''", "9''", "10''", "11''" };

	public HeightPickerDialog(Context context) {

		// super(context,R.style.PauseDialog);
		super(context);
		this.context = context;
	}

	public HeightPickerDialog setHeightPickerDialogListenere(
			HeightPickerDialogListener listener, String height) {

		this.heightPickerListener = listener;
		this.heightSelected = height;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		setContentView(R.layout.dialog_height_picker);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		npFeet = (NumberPicker) findViewById(R.id.npFeet);
		npInches = (NumberPicker) findViewById(R.id.npInches);
		findViewById(R.id.ibDone).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);

		int feetPostion = 0;
		int inchPosition = 0;
		

		npFeet.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npFeet.setMaxValue(valuesFeet.length-1);
		npFeet.setMinValue(0);
		npFeet.setDisplayedValues(valuesFeet);
		npFeet.setWrapSelectorWheel(false);

		npInches.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npInches.setMaxValue(valuesInches.length-1);
		npInches.setMinValue(0);
		npInches.setDisplayedValues(valuesInches);
		npInches.setWrapSelectorWheel(false);

		if (!heightSelected.isEmpty()) {
			String feetInched[] = heightSelected.split(" ");
			if (feetInched.length == 2) {
				String feetSelected = feetInched[0];
				String inchSelected = feetInched[1];
				for (int i = 0; i < valuesFeet.length; i++) {
					String feet = valuesFeet[i];
					if (feet.equals(feetSelected)) {
						feetPostion = i;
						break;
					}
				}

				for (int i = 0; i < valuesInches.length; i++) {
					String intch = valuesInches[i];
					if (intch.equals(inchSelected)) {
						inchPosition = i;
						break;
					}
				}
			}

			npFeet.setValue(feetPostion);
			npInches.setValue(inchPosition);

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibDone:
			if (heightPickerListener != null) {
				heightSelected = valuesFeet[npFeet.getValue()] + " " + valuesInches[npInches.getValue()];
				heightPickerListener.onDoneClick(this, heightSelected);
			}

			break;
		case R.id.ibCancel:
			if (heightPickerListener != null) {
				heightPickerListener.onCancelClick(this);
			}
			break;
		}

	}

	public interface HeightPickerDialogListener {

		public void onDoneClick(HeightPickerDialog dialog, String heightSelected);

		public void onCancelClick(HeightPickerDialog dialog);
	}

}
