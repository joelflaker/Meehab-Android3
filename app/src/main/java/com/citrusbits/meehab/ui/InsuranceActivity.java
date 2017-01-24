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

import java.util.Arrays;

public class InsuranceActivity extends SocketActivity implements
		OnClickListener, OnSocketResponseListener{
	NumberPicker npInsurance;
	String[] values;

	public static final String _mode = InsuranceActivity.class.getSimpleName();

	InsuranceMode mode;

	UserDatasource userDatasource;
	UserAccount user;
	
	private Dialog pd;
	private String insurance;
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
				if (status.equals(insurance)) {
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
		
		this.insurance = insurance;
		if (!NetworkUtils.isNetworkAvailable(this)) {
			Toast.makeText(this,
					getString(R.string.no_internet_connection),
					Toast.LENGTH_SHORT).show();

			return;
		}
		
		JSONObject obj=new JSONObject();
		try {
			obj.put("insurance", insurance);
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
			//update insurance list
			JSONObject data = (JSONObject)obj;
			JSONArray insurances = data.optJSONArray("insurances");
			if(insurances != null){
				String[] insurancesString = new String[insurances.length()];
				for (int i = 0; i < insurances.length(); i++) {
					insurancesString[i] = insurances.optJSONObject(i).optString("name");
				}
				values = insurancesString;
				updateInsurancesData();
			}
//			{"type":true,"message":"Insurance List","insurances":[{"id":3,"name":"Anthem"},{"id":4,"name":"APS Healthcare"},{"id":5,"name":"Assurant"},{"id":6,"name":"BJC Behavioral"},{"id":7,"name":"BlueCross BlueShield"},{"id":8,"name":"Cigna"},{"id":9,"name":"ComPsych"},{"id":10,"name":"Consociate-Dansig"},{"id":11,"name":"Core Source"},{"id":12,"name":"Geha Health Plans"},{"id":13,"name":"Greatwest"},{"id":14,"name":"HealthLink"},{"id":15,"name":"Health Alliance"},{"id":17,"name":"Humana\/LifeSync"},{"id":18,"name":"Kaiser"},{"id":19,"name":"Medicaid"},{"id":20,"name":"Medical Mutual"},{"id":21,"name":"MHN Managed Health Network"},{"id":22,"name":"MH Net\/Group Health Plan (GHP)"},{"id":23,"name":"MVP HealthCare"},{"id":24,"name":"Obamacare"},{"id":25,"name":"Oxfort"},{"id":26,"name":"Perspectives"},{"id":27,"name":"PHCS"},{"id":28,"name":"Primary Physicians Care"},{"id":29,"name":"St. John's Mercy Health Plan"},{"id":31,"name":"Unite Behavioral Healthcare"},{"id":32,"name":"ValueOptions"},{"id":33,"name":"Vista"},{"id":47,"name":"asdasdas"},{"id":49,"name":"test insurance"}]}
		}else if(event.equals(EventParams.EVENT_USER_UPDATE)){
			UserDatasource uds = new UserDatasource(this);
			UserAccount user = uds.findUser(AccountUtils.getUserId(this));
			user.setInsurance(insurance);
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
