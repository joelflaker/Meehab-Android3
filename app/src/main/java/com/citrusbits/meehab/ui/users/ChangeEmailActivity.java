package com.citrusbits.meehab.ui.users;

import org.json.JSONObject;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.ui.SocketActivity;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.UtilityClass;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangeEmailActivity extends SocketActivity implements
		OnSocketResponseListener, OnClickListener {

	private Dialog pd;
	private Button btnSave;
	private EditText etEmail;
	private TextView textResponse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_email);

		// top back button
		findViewById(R.id.topMenuBtn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});
		pd = UtilityClass.getProgressDialog(this);

		UserDatasource userDatasource = new UserDatasource(this);

		UserAccount mUser = userDatasource.findUser(AccountUtils
				.getUserId(this));

		etEmail = (EditText) findViewById(R.id.etEmail);
		etEmail.setText(mUser.getEmail());

		btnSave = (Button) findViewById(R.id.btnSave);
		textResponse = (TextView) findViewById(R.id.textResponse);

		btnSave.setOnClickListener(this);

		etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				validateEmail(hasFocus);
			}
		});

		etEmail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				etEmail.setError(null);
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
	public void onBackendConnected() {

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btnSave) {
			attempChangeEmail();
		}

	}

	/**
	 * 
	 */
	private void attempChangeEmail() {
		validateEmail(false);

		if (etEmail.getError() != null) {
			etEmail.requestFocus();
			etEmail.selectAll();
			return;
		}

		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
				.getWindowToken(), 0);

		if (NetworkUtil.getConnectivityStatus(this) == 0) {
			App.toast("It's seems to be network problem");
			return;
		}

		String emailString = etEmail.getText().toString();
		JSONObject params = new JSONObject();
		try {
			// Toast.makeText(this, itemName,
			// Toast.LENGTH_SHORT).show();
			params.put(EventParams.SIGNUP_EMAIL, emailString);
			pd.show();
			socketService.updateAccount(params);
		} catch (Exception e) {

		}
	}

	private void validateEmail(boolean hasFocus) {
		String emailString = etEmail.getText().toString();
		if (emailString.trim().length() == 0) {
			if (!hasFocus)
				etEmail.setError(getString(R.string.email_is_required));
		} else if (!UtilityClass.isValidEmail(emailString)) {
			if (!hasFocus)
				etEmail.setError(getString(R.string.email_not_valid));
		} else {
			etEmail.setError(null);
			// check email
		}
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		pd.dismiss();
		if(event.equals(EventParams.EVENT_USER_UPDATE)){
			textResponse.setText(R.string.email_has_been_changed);
			textResponse.setTextColor(Color.GREEN);
		}
//		onBackPressed();

	}

	@Override
	public void onSocketResponseFailure(String event,String message) {
		
		if(event.equals(EventParams.EVENT_USER_UPDATE)){
			pd.dismiss();
		textResponse.setText(message);
		textResponse.setTextColor(Color.RED);
		}

	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
	}

}
