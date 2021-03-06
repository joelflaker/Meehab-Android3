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


public class EthenticityPickerDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private EthenticityDialogListener ethenticityDialogListener;

	private NumberPicker npEthenticity;

	private String ethenticitySelected = "";
	
	private String[] ethenticity;

	public EthenticityPickerDialog(Context context) {

		// super(context,R.style.PauseDialog);
		super(context);
		this.context = context;
	}

	public EthenticityPickerDialog setEthenticityDialogListener(
			EthenticityDialogListener listener, String ethenticity) {

		this.ethenticityDialogListener = listener;
		this.ethenticitySelected = ethenticity;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		setContentView(R.layout.dialog_ethenticity);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		npEthenticity = (NumberPicker) findViewById(R.id.npEthenticity);
		findViewById(R.id.ibDone).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);
		
		int position=0;
		ethenticity = context.getResources().getStringArray(
				R.array.ethnicities);
		
		for(int i=0;i<ethenticity.length;i++){
			String status=ethenticity[i];
			if(status.equals(ethenticitySelected)){
				position=i;
				break;
			}
		}
		
		npEthenticity.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npEthenticity.setMinValue(0);
		npEthenticity.setMaxValue(ethenticity.length-1);
		npEthenticity.setValue(position);
		npEthenticity.setDisplayedValues(ethenticity);
		npEthenticity.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);

		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibDone:
			if (ethenticityDialogListener != null) {
				ethenticitySelected=ethenticity[npEthenticity.getValue()];
				ethenticityDialogListener.onDoneClick(this, ethenticitySelected);
			}

			break;
		case R.id.ibCancel:
			if (ethenticityDialogListener != null) {
				ethenticityDialogListener.onCancelClick(this);
			}
			break;
		}

	}

	public interface EthenticityDialogListener {

		public void onDoneClick(EthenticityPickerDialog dialog,
				String ethenticity);

		public void onCancelClick(EthenticityPickerDialog dialog);
	}

}
