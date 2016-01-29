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

public class WeightPickerDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private WeightDialogListener weightListener;

	private NumberPicker npWeight;

	private int weightPostion; // In pounds
	
	String weights[]=new String[451];
	
	private String selectedWeight;

	public WeightPickerDialog(Context context) {

		// super(context,R.style.PauseDialog);
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public WeightPickerDialog setWeightDialogListener(
			WeightDialogListener listener, String weight) {

		this.weightListener = listener;
		this.selectedWeight = weight;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		setContentView(R.layout.dialog_weight);
		
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		
		npWeight = (NumberPicker) findViewById(R.id.npWeight);
		findViewById(R.id.ibDone).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);
		
		for(int i=0;i<weights.length;i++){
			weights[i]=(i+50)+" "+"Lbs";
		}

		npWeight.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npWeight.setDisplayedValues(weights);
		npWeight.setMaxValue(weights.length-1);
		npWeight.setMinValue(0);
		
		
		for (int i = 0; i < weights.length; i++) {
			String feet = weights[i];
			if (feet.toLowerCase().equals(selectedWeight.toLowerCase())) {
				weightPostion = i;
				break;
			}
		}
		npWeight.setValue(weightPostion);
		
		
		
		npWeight.setWrapSelectorWheel(false);

		

		npWeight.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);
		
		
		

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibDone:
			if (weightListener != null) {

				weightListener.onDoneClick(this, weights[npWeight.getValue()]);
			}

			break;
		case R.id.ibCancel:
			if (weightListener != null) {
				weightListener.onCancelClick(this);
			}
			break;
		}

	}

	public interface WeightDialogListener {

		public void onDoneClick(WeightPickerDialog dialog, String weightSelected);

		public void onCancelClick(WeightPickerDialog dialog);
	}

}
