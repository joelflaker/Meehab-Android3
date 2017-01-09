package com.citrusbits.meehab;

import org.json.JSONException;
import org.json.JSONObject;

import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.pojo.AddUserResponse;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class ForgetPasswordActivity extends SocketActivity implements OnSocketResponseListener, View.OnClickListener {

	private EditText emailEdit;
	private Button nextBtn;
	private Dialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);

//		if(App.getInstance().socketIO == null){
//			App.getInstance().initConnectNodeJS();
//		}
		//top back button
		findViewById(R.id.topMenuBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ForgetPasswordActivity.this.finish();
				overridePendingTransition(R.anim.activity_back_in,
						R.anim.activity_back_out);
			}});
		
		pd = UtilityClass.getProgressDialog(this);
		emailEdit = (EditText) findViewById(R.id.emailEdit);
		nextBtn = (Button)findViewById(R.id.nextBtn);

		nextBtn.setOnClickListener(this);

		emailEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String emailString  = emailEdit.getText().toString();
				if (UtilityClass.isValidEmail(emailString)){
					nextBtn.setEnabled(true);
				}else{
					nextBtn.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nextBtn:
			requestPassword();
			break;

		default:
			break;
		}
	}
	
	private void requestPassword() {
		// All fields Contain values
		String emailString = emailEdit.getText().toString();
		
		if(emailEdit.getError() != null){
			emailEdit.requestFocus();
			emailEdit.selectAll();
			return;
		}
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		
		if(NetworkUtil.getConnectivityStatus(this) == 0){
			App.toast(getString(R.string.network_problem));
			return;
		}else{

		JSONObject params = new JSONObject();
		try {
			// Toast.makeText(this, itemName,
			// Toast.LENGTH_SHORT).show();
			params.put(EventParams.SIGNUP_EMAIL, emailString);
			pd.show();
			socketService.forgotPassword(params);
		} catch (JSONException e) {
			e.printStackTrace();
			params = null;
		}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,R.anim.activity_back_out);
	}
	
	@Override
	void onBackendConnected() {
		
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		pd.dismiss();
		App.toast(getString(R.string.email_has_been_send));
	}

	@Override
	public void onSocketResponseFailure(String onEvent,String message) {
		pd.dismiss();
		App.toast("Cannot find this email address.");
	}
}
