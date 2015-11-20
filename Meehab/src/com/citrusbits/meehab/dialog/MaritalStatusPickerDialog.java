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


public class MaritalStatusPickerDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private MaritalStatusDialogListener maritalStatusListener;

	private NumberPicker npMaritalStatus;

	private String maritalSelected = "";
	
	private String[] maritalStatuses;

	public MaritalStatusPickerDialog(Context context) {

		// super(context,R.style.PauseDialog);
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public MaritalStatusPickerDialog setMaritalStatusListenere(
			MaritalStatusDialogListener listener, String marital) {

		this.maritalStatusListener = listener;
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

		setContentView(R.layout.dialog_marital_status);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		npMaritalStatus = (NumberPicker) findViewById(R.id.npMaritalStatus);
		findViewById(R.id.ibDone).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);
		
		int position=0;
		maritalStatuses = context.getResources().getStringArray(
				R.array.maritalStatuses);
		
		for(int i=0;i<maritalStatuses.length;i++){
			String status=maritalStatuses[i];
			if(status.equals(maritalSelected)){
				position=i;
				break;
			}
		}
		
		npMaritalStatus.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npMaritalStatus.setMinValue(0);
		npMaritalStatus.setMaxValue(4);
		npMaritalStatus.setValue(position);
		npMaritalStatus.setDisplayedValues(maritalStatuses);
		npMaritalStatus.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);

		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibDone:
			if (maritalStatusListener != null) {
				maritalSelected=maritalStatuses[npMaritalStatus.getValue()];
				maritalStatusListener.onDoneClick(this, maritalSelected);
			}

			break;
		case R.id.ibCancel:
			if (maritalStatusListener != null) {
				maritalStatusListener.onCancelClick(this);
			}
			break;
		}

	}

	public interface MaritalStatusDialogListener {

		public void onDoneClick(MaritalStatusPickerDialog dialog,
				String maritalStatusSelected);

		public void onCancelClick(MaritalStatusPickerDialog dialog);
	}

}
