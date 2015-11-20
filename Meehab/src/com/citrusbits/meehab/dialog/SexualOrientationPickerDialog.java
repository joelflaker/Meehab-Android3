package com.citrusbits.meehab.dialog;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.NumberPicker;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.dialog.DobPickerDialog.DatePickerDialogListener;


public class SexualOrientationPickerDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private SexualOrientationDialogListener sexaulOrienationListener;

	private NumberPicker npSexualOrientation;

	private String maritalSelected = "";
	
	private String[] sexualOrienations;

	public SexualOrientationPickerDialog(Context context) {

		// super(context,R.style.PauseDialog);
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public SexualOrientationPickerDialog setSexualOrientationListener(
			SexualOrientationDialogListener listener, String marital) {

		this.sexaulOrienationListener = listener;
		this.maritalSelected = marital;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		setContentView(R.layout.dialog_sexual_orientation);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		npSexualOrientation = (NumberPicker) findViewById(R.id.npSexualOrientation);
		findViewById(R.id.ibDone).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);
		
		int position=0;
		sexualOrienations = context.getResources().getStringArray(
				R.array.sextualOrientations);
		
		for(int i=0;i<sexualOrienations.length;i++){
			String status=sexualOrienations[i];
			if(status.equals(maritalSelected)){
				position=i;
				break;
			}
		}
		
		npSexualOrientation.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npSexualOrientation.setMinValue(0);
		npSexualOrientation.setMaxValue(sexualOrienations.length-1);
		npSexualOrientation.setValue(position);
		npSexualOrientation.setDisplayedValues(sexualOrienations);
		npSexualOrientation.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);

		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibDone:
			if (sexaulOrienationListener != null) {
				maritalSelected=sexualOrienations[npSexualOrientation.getValue()];
				sexaulOrienationListener.onDoneClick(this, maritalSelected);
			}

			break;
		case R.id.ibCancel:
			if (sexaulOrienationListener != null) {
				sexaulOrienationListener.onCancelClick(this);
			}
			break;
		}

	}

	public interface SexualOrientationDialogListener {

		public void onDoneClick(SexualOrientationPickerDialog dialog,
				String sexualOrientation);

		public void onCancelClick(SexualOrientationPickerDialog dialog);
	}

}
