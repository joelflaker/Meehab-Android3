package com.citrusbits.meehab;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.pojo.AddUserResponse;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends SocketActivity implements
		OnSocketResponseListener, View.OnClickListener {

	public static final String TAG = LoginActivity.class.getSimpleName();

	private ImageButton ibLogin;
	private EditText passwordEdit, usernameEdit;
	private UserDatasource userDatasource;
	AddUserResponse userModel;
	ProgressDialog pd;

	AppPrefs prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		prefs = AppPrefs.getAppPrefs(LoginActivity.this);

		// top back btn
		findViewById(R.id.ivBack).setOnClickListener(this);

		pd = new ProgressDialog(this);
		pd.setCancelable(false);
		pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
		usernameEdit = (EditText) findViewById(R.id.usernameEdit);
		passwordEdit = (EditText) findViewById(R.id.passwordEdit);
		ibLogin = (ImageButton) findViewById(R.id.ibLogin);
		findViewById(R.id.tvLoginProblem).setOnClickListener(this);

		ibLogin.setOnClickListener(this);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibLogin:
			attemptLogin();
			break;
		case R.id.tvLoginProblem:
			Intent i2 = new Intent(this, ForgetPasswordActivity.class);
			startActivity(i2);
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
			break;
		case R.id.ivBack:
			onBackPressed();
			break;

		default:
			break;
		}
	}

	private void attemptLogin() {
		// All fields Contain values
		String strUsername = usernameEdit.getText().toString();
		String strPassword = passwordEdit.getText().toString();

		// validate username and email

		if (NetworkUtil.getConnectivityStatus(this) == 0) {
			App.alert(getString(R.string.no_internet_connection));
			return;
		} else {

			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), 0);

			JSONObject params = new JSONObject();
			try {
				// Toast.makeText(this, itemName,
				// Toast.LENGTH_SHORT).show();
				params.put(EventParams.SIGNUP_USERNAME, strUsername);
				params.put(EventParams.SIGNUP_PASSWORD, strPassword);
				params.put("device_id", DeviceUtils.getDeviceId(LoginActivity.this));

				pd.show();
				socketService.login(params);
			} catch (JSONException e) {
				e.printStackTrace();
				params = null;
			}

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

	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {

		Log.e(TAG, obj.toString());

		pd.dismiss();
		Intent intent = new Intent(LoginActivity.this, TwoOptionActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
		setResult(RESULT_OK);
		
		AppPrefs prefs = AppPrefs.getAppPrefs(LoginActivity.this);
		prefs.saveBooleanPrefs(AppPrefs.KEY_PROFILE_SETUP, true);
		prefs.saveBooleanPrefs(AppPrefs.KEY_PROFILE_SETUP_MORE, true);
		
		
		LoginActivity.this.finish();
	}

	@Override
	public void onSocketResponseFailure(String message) {
		pd.dismiss();
		App.alert(message);
	}
}
