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

public class RatingPickerDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private RatingPickerDialogListener ratingPickerListener;

	private NumberPicker npRating;

	private String ratingSelected = "";


	
	
	final String[] starValues = new String[] { "< 1 Star", "< 2 Stars",
			"< 3 Stars", "< 4 Stars", "5 Stars" };

	public RatingPickerDialog(Context context) {

		// super(context,R.style.PauseDialog);
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public RatingPickerDialog setRatingPickerListener(
			RatingPickerDialogListener listener, String rating) {

		this.ratingPickerListener = listener;
		this.ratingSelected = rating;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		setContentView(R.layout.dialog_rating_picker);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		npRating = (NumberPicker) findViewById(R.id.npRating);
		findViewById(R.id.ibDone).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);

		int position = 0;

		for (int i = 0; i < starValues.length; i++) {
			String status = starValues[i];
			if (status.equals(ratingSelected)) {
				position = i;
				break;
			}
		}

		npRating
				.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npRating.setMinValue(0);
		npRating.setMaxValue(starValues.length - 1);
		npRating.setValue(position);
		npRating.setDisplayedValues(starValues);
		npRating.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibDone:
			if (ratingPickerListener != null) {
				ratingSelected = starValues[npRating.getValue()];
				ratingPickerListener.onDoneClick(this, ratingSelected);
			}

			break;
		case R.id.ibCancel:
			if (ratingPickerListener != null) {
				ratingPickerListener.onCancelClick(this);
			}
			break;
		}

	}

	public interface RatingPickerDialogListener {

		public void onDoneClick(RatingPickerDialog dialog,
				String distanceSelected);

		public void onCancelClick(RatingPickerDialog dialog);
	}

}
