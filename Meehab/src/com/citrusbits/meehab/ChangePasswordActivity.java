package com.citrusbits.meehab;

import org.json.JSONObject;

import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.UtilityClass;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangePasswordActivity extends SocketActivity implements OnSocketResponseListener, View.OnClickListener {

	private ProgressDialog pd;
	private EditText editNewPassword;
	private EditText editOldPassword;
	private EditText editReNewPassword;
	private Button btnSave;
	private TextView textResponse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		//top back button
		findViewById(R.id.topMenuBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}});
		pd = UtilityClass.getProgressDialog(this);
		
		
		
		editOldPassword = (EditText)findViewById(R.id.editOldPassword);
		editNewPassword = (EditText)findViewById(R.id.editNewPassword);
		editReNewPassword = (EditText)findViewById(R.id.editReNewPassword);
		btnSave = (Button) findViewById(R.id.btnSave);
		textResponse = (TextView) findViewById(R.id.textResponse);
		
		btnSave.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.btnSave){
			attempChangePassword();
		}

	}
	/**
	 * 
	 */
	private void attempChangePassword() {
		//check
		String oldassword = editOldPassword.getText().toString();
		String newPassword = editNewPassword.getText().toString();
		String reNewPassword = editReNewPassword.getText().toString();
		
		if(TextUtils.isEmpty(oldassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(reNewPassword)){
			textResponse.setText("Enter password");
			return;
		}
		
		if(!newPassword.equals(reNewPassword)){
			textResponse.setText("Password Does't match");
			return;
		}
		
		
		
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

		if(NetworkUtil.getConnectivityStatus(this) == 0){
			App.alert("It's seems to be network problem");
			return;
		}
		
		JSONObject params = new JSONObject();
		try {
			// Toast.makeText(this, itemName,
			// Toast.LENGTH_SHORT).show();
			params.put(EventParams.SIGNUP_PASSWORD, newPassword);
			pd.show();
			socketService.updateAccount(params);
		}catch (Exception e){
			
		}
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		pd.dismiss();
		textResponse.setText("Successfuly updated!");
		
	}

	@Override
	public void onSocketResponseFailure(String message) {
		pd.dismiss();
		textResponse.setText(message);
		onBackPressed();
		
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		this.overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
	}
}
