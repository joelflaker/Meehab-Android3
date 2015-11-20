package com.citrusbits.meehab;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.citrusbits.meehab.dialog.RehabFacilityTypeDialog;
import com.citrusbits.meehab.dialog.RehabFacilityTypeDialog.RehabFacilityTypeDialogListener;

public class RehabAdditionActivity extends Activity implements OnClickListener {

	EditText etName;
	EditText etPhoneNumber;
	EditText etEmail;
	EditText etNameOfRehaab;
	EditText etWebsite;
	TextView tvTypeOfRehabFacility;
	EditText etYourRelationshipToRehab;
	ImageButton ibSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_rehab_addition);
		findViewById(R.id.ibSubmit).setOnClickListener(this);
		etName = (EditText) findViewById(R.id.etName);
		etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
		etEmail = (EditText) findViewById(R.id.etEmail);
		etNameOfRehaab = (EditText) findViewById(R.id.etNameOfRehaab);
		etWebsite = (EditText) findViewById(R.id.etWebsite);
		tvTypeOfRehabFacility = (TextView) findViewById(R.id.tvTypeOfRehabFacility);
		tvTypeOfRehabFacility.setOnClickListener(this);
		etYourRelationshipToRehab = (EditText) findViewById(R.id.etYourRelationshipToRehab);

		// top back btn
		findViewById(R.id.topMenuBtn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibSubmit:
			onBackPressed();
			break;
		case R.id.tvTypeOfRehabFacility:
			/*Toast.makeText(RehabAdditionActivity.this,
					"Type of Rehab Facility!", Toast.LENGTH_SHORT).show();*/
			String facilityType=tvTypeOfRehabFacility.getText().toString().trim();
			
			new RehabFacilityTypeDialog(RehabAdditionActivity.this).setRehabFacilityTypeListener(new RehabFacilityTypeDialogListener() {
				
				@Override
				public void onDoneClick(RehabFacilityTypeDialog dialog,
						String rehabFacilityType) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					tvTypeOfRehabFacility.setText(rehabFacilityType);
				}
				
				@Override
				public void onCancelClick(RehabFacilityTypeDialog dialog) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			}, facilityType).show();
			break;
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

}
