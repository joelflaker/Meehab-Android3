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
import com.citrusbits.meehab.dialog.MaritalStatusPickerDialog.MaritalStatusDialogListener;

public class DistancePickerDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private DistancePickerDialogListener distancePickerListener;

	private NumberPicker npDistance;

	private String distanceSelected = "";

	
	
	
	 private String[] distanceValues = new String[] { "5 Miles", "10 Miles", "15 Miles",
			"20 Miles", "30 Miles", "40 Miles", "more than 50 Miles" };

	public DistancePickerDialog(Context context) {

		// super(context,R.style.PauseDialog);
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public DistancePickerDialog setDistancePickerListener(
			DistancePickerDialogListener listener, String distance) {

		this.distancePickerListener = listener;
		this.distanceSelected = distance;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		setContentView(R.layout.dialog_distance_picker);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		npDistance = (NumberPicker) findViewById(R.id.npDistance);
		findViewById(R.id.ibDone).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);

		int position = 0;
		
		for (int i = 0; i < distanceValues.length; i++) {
			String status = distanceValues[i];
			if (status.equals(distanceSelected)) {
				position = i;
				break;
			}
		}

		npDistance
				.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npDistance.setMinValue(0);
		npDistance.setMaxValue(distanceValues.length - 1);
		npDistance.setValue(position);
		npDistance.setDisplayedValues(distanceValues);
		npDistance.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibDone:
			if (distancePickerListener != null) {
				distanceSelected = distanceValues[npDistance.getValue()];
				distancePickerListener.onDoneClick(this, distanceSelected);
			}

			break;
		case R.id.ibCancel:
			if (distancePickerListener != null) {
				distancePickerListener.onCancelClick(this);
			}
			break;
		}

	}

	public interface DistancePickerDialogListener {

		public void onDoneClick(DistancePickerDialog dialog,
				String distanceSelected);

		public void onCancelClick(DistancePickerDialog dialog);
	}

}
