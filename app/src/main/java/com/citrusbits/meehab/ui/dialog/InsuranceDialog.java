package com.citrusbits.meehab.ui.dialog;

import com.citrusbits.meehab.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

public class InsuranceDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private ImageButton ibSkip;
	private ImageButton ibInsurance;

	private InsuranceDialogClickListener insuranceDialogClickListener;

	public InsuranceDialog(Context context) {
		super(context,android.R.style.Theme_Black_NoTitleBar);
		this.context = context;
	}

	public InsuranceDialog setInsuranceDialogListener(
			InsuranceDialogClickListener listener) {

		this.insuranceDialogClickListener = listener;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.dialog_insurance);
		findViewById(R.id.ibSkip).setOnClickListener(this);
		findViewById(R.id.ibInsurance).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibSkip:
			if (insuranceDialogClickListener != null) {
				insuranceDialogClickListener.onSkipClick(this);
			}

			break;
		case R.id.ibInsurance:
			if (insuranceDialogClickListener != null) {
				insuranceDialogClickListener.onInsuranceClick(this);
			}
			break;
		}

	}

	public interface InsuranceDialogClickListener {
		
		public void onSkipClick(InsuranceDialog dialog);
		public void onInsuranceClick(InsuranceDialog dialog);
	}

}
