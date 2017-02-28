package com.citrusbits.meehab.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class InsuranceActivity extends SocketActivity implements
		OnClickListener, OnSocketResponseListener{
	NumberPicker npInsurance;
	String[] values;

	public static final String _mode = InsuranceActivity.class.getSimpleName();

	InsuranceMode mode;

	UserDatasource userDatasource;
	UserAccount user;
	
	private Dialog pd;
	private String mInsurance;
	private String[] fullValues;


	public enum InsuranceMode {
		ADD, EDIT;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insurance);
		pd = UtilityClass.getProgressDialog(this);
		mode = (InsuranceMode) getIntent().getSerializableExtra(_mode);
		if (mode == null) {
			mode = InsuranceMode.ADD;
		}

		userDatasource = new UserDatasource(this);
		user = userDatasource.findUser(AccountUtils.getUserId(this));

		
		findViewById(R.id.ibBack).setOnClickListener(this);
		findViewById(R.id.ibSkip).setOnClickListener(this);
		findViewById(R.id.ibSubmit).setOnClickListener(this);
		findViewById(R.id.ibCannotFindInsurance).setOnClickListener(this);
		npInsurance = (NumberPicker) findViewById(R.id.insurancePicker);

		values = getResources().getStringArray(R.array.insurance_arr);
		Arrays.sort(values);

//		updateInsurancesData();
		
//		if(getIntent().hasExtra("fromOption")){
//			findViewById(R.id.text).setVisibility(View.GONE);
//			findViewById(R.id.ibSkip).setVisibility(View.GONE);
//			}
		
		if (mode == InsuranceMode.EDIT) {
			findViewById(R.id.text).setVisibility(View.INVISIBLE);
			findViewById(R.id.ibSkip).setVisibility(View.GONE);
			
			int position = 0;
			String insurance_arr[] = this.getResources().getStringArray(
					R.array.insurance_arr);

			for (int i = 0; i < insurance_arr.length; i++) {
				String status = insurance_arr[i];
				if (status.equals(mInsurance)) {
					position = i;
					break;
				}
			}
			npInsurance.setValue(position);

		}
	}

	private void updateInsurancesData() {


		fullValues = Arrays.copyOf(values,values.length);

		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			if(value.length() > 26){
				values[i] = value.substring(0,22)+"...";
			}
		}

		npInsurance.setDisplayedValues(null);
		npInsurance
				.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npInsurance.setMinValue(0);
		npInsurance.setMaxValue(values.length - 1);
		npInsurance.setDisplayedValues(values);
		npInsurance.setWrapSelectorWheel(false);
		String insurance = user.getEthnicity();
	}

	@Override
	public void onBackendConnected() {
		pd.show();
		socketService.listOfInsurances();
	}

	@Override
	public void onClick(View v) {
		Intent i = null;
		switch (v.getId()) {
		case R.id.ibBack:
			InsuranceActivity.this.onBackPressed();
			break;
		case R.id.ibSkip:
			i = new Intent(InsuranceActivity.this, HomeActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
			break;
		case R.id.ibSubmit:
			String insurance = fullValues[npInsurance.getValue()];

//			if("No Insurance".equals(mInsurance)){
//				AccountUtils.setTime(this, Calendar.getInstance().getTimeInMillis());
//				i = new Intent(InsuranceActivity.this, HomeActivity.class);
//				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//						| Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(i);
//				overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
//				return;
//			}
			
			addInsurance(insurance);
			break;
		case R.id.ibCannotFindInsurance:
			startActivity(new Intent(InsuranceActivity.this,
					CannotFindInsuranceActivity.class));
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
			break;
		}
	}
	
	public void addInsurance(String insurance){
		
		this.mInsurance = insurance;
		if (!NetworkUtils.isNetworkAvailable(this)) {
			Toast.makeText(this,
					getString(R.string.no_internet_connection),
					Toast.LENGTH_SHORT).show();

			return;
		}

		if("No Insurance".equals(mInsurance)){
			AccountUtils.setTime(this, Calendar.getInstance().getTimeInMillis());
			this.mInsurance = "";
		}
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("mInsurance", mInsurance);
			socketService.updateAccount(obj);
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
	public void onSocketResponseSuccess(String event, Object obj) {
		if(pd != null && pd.isShowing()){
			pd.dismiss();
		}
		
		if(event.equals(EventParams.EVENT_INSURANCE_LIST)) {
			//update mInsurance list
			JSONObject data = (JSONObject)obj;
			JSONArray insurances = data.optJSONArray("insurances");
			if(insurances != null){
				ArrayList<String> insurancesList = new ArrayList<>();

				for (int i = 0; i < insurances.length(); i++) {
					insurancesList.add(insurances.optJSONObject(i).optString("name"));
				}

				Collections.sort(insurancesList, new Comparator<String>() {
					@Override
					public int compare(String s1, String s2) {
						return s1.compareToIgnoreCase(s2);
					}
				});
				insurancesList.add("No Insurance");
				values = new String[insurancesList.size()];
				values = insurancesList.toArray(values);
				updateInsurancesData();
				npInsurance.postDelayed(new Runnable() {
					@Override
					public void run() {
						updateInsurancesData();
					}
				},10);
			}
		}else if(event.equals(EventParams.EVENT_USER_UPDATE)){
			UserDatasource uds = new UserDatasource(this);
			UserAccount user = uds.findUser(AccountUtils.getUserId(this));
			user.setInsurance(mInsurance);
			uds.update(user);
			Toast.makeText(InsuranceActivity.this, "Insurance added successfully!", Toast.LENGTH_SHORT).show();
//			if(fromOptions){ finish(); return; }
			Intent i = new Intent(InsuranceActivity.this, HomeActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
		}
	}

	@Override
	public void onSocketResponseFailure(String event, String message) {
		if(EventParams.EVENT_USER_UPDATE.equals(event)){
			App.toast(""+message);
		}
		if(pd!=null && pd.isShowing()){
			pd.dismiss();
		}
	}

}
