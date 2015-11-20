package com.citrusbits.meehab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.NumberPicker;
import android.widget.Toast;

public class InsuranceActivity extends SocketActivity implements
		OnClickListener {
	NumberPicker npInsurance;
	String[] values;
	
	public static final String _mode=InsuranceActivity.class.getSimpleName();
	
	InsuranceMode mode;

	public enum InsuranceMode {
		ADD, EDIT;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insurance);
		mode=(InsuranceMode) getIntent().getSerializableExtra(_mode);
		if(mode==null){
			mode=InsuranceMode.ADD;
		}
		findViewById(R.id.ibBack).setOnClickListener(this);
		findViewById(R.id.ibSkip).setOnClickListener(this);
		findViewById(R.id.ibSubmit).setOnClickListener(this);
		findViewById(R.id.ibCannotFindInsurance).setOnClickListener(this);
		npInsurance = (NumberPicker) findViewById(R.id.insurancePicker);

		values = getResources().getStringArray(R.array.insurance_arr);

		npInsurance
				.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npInsurance.setMinValue(0);
		npInsurance.setMaxValue(values.length - 1);
		npInsurance.setDisplayedValues(values);
		npInsurance.setWrapSelectorWheel(false);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibBack:
			InsuranceActivity.this.onBackPressed();
			break;
		case R.id.ibSkip:
			startActivity(new Intent(InsuranceActivity.this, HomeActivity.class));
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
			break;
		case R.id.ibSubmit:
			startActivity(new Intent(InsuranceActivity.this, HomeActivity.class));
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
			break;
		case R.id.ibCannotFindInsurance:
			startActivity(new Intent(InsuranceActivity.this,
					CannotFindInsuranceActivity.class));
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

}
