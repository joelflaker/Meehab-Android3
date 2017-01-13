package com.citrusbits.meehab;

import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.dialog.MessageDialog;
import com.citrusbits.meehab.dialog.RehabFacilityTypeDialog;
import com.citrusbits.meehab.dialog.RehabFacilityTypeDialog.RehabFacilityTypeDialogListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.citrusbits.meehab.utils.ValidationUtils;

@SuppressLint("NewApi")
public class RehabAdditionActivity extends SocketActivity implements
		OnClickListener, OnSocketResponseListener {

	EditText etName;
	EditText etPhoneNumber;
	EditText etEmail;
	EditText etNameOfRehaab;
	EditText etWebsite;
	TextView tvTypeOfRehabFacility;
	EditText etYourRelationshipToRehab;
	ImageButton ibSubmit;

	private Context mContext;

	private Dialog pd;
	
	RelativeLayout rlOtherRehabFacility;
	EditText etOtherRehabFacilities;
	ImageView ivOtherCross;
	private String[] rehabTypesStringArray;
	private int[] rehabTypesIds;
	private int typeOfRehabFacilityId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = this;
		pd = UtilityClass.getProgressDialog(this);
		setContentView(R.layout.activity_request_rehab_addition);
		
		rlOtherRehabFacility=(RelativeLayout) findViewById(R.id.rlOtherRehabFacility);
		etOtherRehabFacilities=(EditText) findViewById(R.id.etOtherRehabFacilities);
		
		findViewById(R.id.ivOtherCross).setOnClickListener(this);
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
		
		etPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher("US"));
		etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (editPhone.getText().toString().trim().length() > 13) {
//                    btnNext.setVisibility(View.VISIBLE);
//                } else {
//                    btnNext.setVisibility(View.INVISIBLE);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibSubmit:
			// onBackPressed();
			submitRehab();
			break;
		case R.id.ivOtherCross:
//			tvTypeOfRehabFacility.setText("");
			etOtherRehabFacilities.setText("");
//			rlOtherRehabFacility.setVisibility(View.GONE);
			break;
		case R.id.tvTypeOfRehabFacility:
			
			hideKeyboard();
			/*
			 * Toast.makeText(RehabAdditionActivity.this,
			 * "Type of Rehab Facility!", Toast.LENGTH_SHORT).show();
			 */
			String facilityType = tvTypeOfRehabFacility.getText().toString()
					.trim();

			new RehabFacilityTypeDialog(RehabAdditionActivity.this)
					.setRehabFacilityTypeListener(
							new RehabFacilityTypeDialogListener() {

								@Override
								public void onDoneClick(
										RehabFacilityTypeDialog dialog,
										String rehabFacilityType,int position) {
									dialog.dismiss();
									if(rehabFacilityType.toLowerCase().equals("other")){
										rlOtherRehabFacility.setVisibility(View.VISIBLE);
										etOtherRehabFacilities.requestFocus();
										typeOfRehabFacilityId = -1;
									}else{
										rlOtherRehabFacility.setVisibility(View.GONE);
										etYourRelationshipToRehab.requestFocus();
									}
									tvTypeOfRehabFacility.setText(rehabFacilityType);
									if(rehabTypesIds != null && rehabTypesIds.length > 0)
									typeOfRehabFacilityId = rehabTypesIds[position];
								}

								@Override
								public void onCancelClick(
										RehabFacilityTypeDialog dialog) {
									dialog.dismiss();
								}
							}, facilityType)
					.setData(rehabTypesStringArray).show();
			break;
		}

	}

	public void submitRehab() {
		String name = etName.getText().toString().trim();
		String phone = UtilityClass.phoneNumberNormal(etPhoneNumber.getText().toString().trim());
		String email = etEmail.getText().toString().trim();

		String rehabName = etNameOfRehaab.getText().toString().trim();
		String website = etWebsite.getText().toString().trim();
		String typeOfRehabFacility = tvTypeOfRehabFacility.getText().toString()
				.trim();
		if(typeOfRehabFacility.toLowerCase().equals("other")){
			typeOfRehabFacility = etOtherRehabFacilities.getText().toString().trim();
		}
		String relationToRehab = etYourRelationshipToRehab.getText().toString()
				.trim();

		if (name.isEmpty() || ValidationUtils.isSpecialChar(name)) {
			Toast.makeText(mContext, "Enter valid name!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (phone.isEmpty() || phone.length() != 10 || !ValidationUtils.validatePhoneNumber(phone)) {
			Toast.makeText(mContext, "Enter valid phone number!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (email.isEmpty() || !ValidationUtils.isValidEmail(email)) {
			Toast.makeText(mContext, "Enter valid email!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (rehabName.isEmpty()  || ValidationUtils.isSpecialChar(rehabName)) {
			Toast.makeText(mContext, "Enter valid Rehab Name!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (website.isEmpty() || !ValidationUtils.isValidUrl(website)) {
			Toast.makeText(mContext, "Enter valid website url! e.g. www.abc.com",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (typeOfRehabFacility.isEmpty()/* || ValidationUtils.isSpecialChar(typeOfRehabFacility)*/) {
			Toast.makeText(mContext,
					"Select valid Rehab Facility!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (relationToRehab.isEmpty()  || ValidationUtils.isSpecialChar(relationToRehab)) {
			Toast.makeText(mContext, "Enter valid relationship to rehab!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (!NetworkUtils.isNetworkAvailable(RehabAdditionActivity.this)) {
			Toast.makeText(mContext,
					getString(R.string.no_internet_connection),
					Toast.LENGTH_SHORT).show();

			return;
		}

		JSONObject json = new JSONObject();
		try {
			json.put("user_name", name);
			json.put("user_phone", phone);
			json.put("user_email", email);

			json.put("name", rehabName);
			json.put("website", website);
			json.put("relation", relationToRehab);
			json.put("type", typeOfRehabFacility);
//			json.put("codes", typeOfRehabFacilityId);

			socketService.addRehab(json);

			Log.e("json", json.toString());
			pd.show();

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

	@Override
	void onBackendConnected() {
//		socketService.listOfRehabTypes();
	}


	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		if (pd != null) {
			pd.dismiss();
		}

//		if (event.equals(EventParams.EVENT_REHAB_TYPES_LIST)) {
//			JSONObject data = (JSONObject)obj;
//			JSONArray rehabTypes = data.optJSONArray("types");
//			if(rehabTypes != null){
//				String[] rehabTypesString = new String[rehabTypes.length()];
//				int[] rehabTypesIds = new int[rehabTypes.length()];
//				for (int i = 0; i < rehabTypes.length(); i++) {
//					rehabTypesString[i] = rehabTypes.optJSONObject(i).optString("name");
//					rehabTypesIds[i] = rehabTypes.optJSONObject(i).optInt("id");
//				}
//				this.rehabTypesIds = rehabTypesIds;
//				this.rehabTypesStringArray = rehabTypesString;
//			}
//		}else
		if (event.equals(EventParams.EVENT_ADD_REHAB)) {
			new MessageDialog(this,R.string.thanks_on_rehab_addition)
					.setDialogClickListener(new MessageDialog.MessageDialogClickListener() {
						@Override
						public void onOkClick(MessageDialog dialog) {
							dialog.dismiss();
							onBackPressed();
						}
					}).show();
		}

	}

	
	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		if (pd != null) {
			pd.dismiss();
		}

		Toast.makeText(RehabAdditionActivity.this, message, Toast.LENGTH_SHORT)
				.show();
	}

}
