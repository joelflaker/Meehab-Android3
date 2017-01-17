package com.citrusbits.meehab.ui.users;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.ui.SocketActivity;
import com.citrusbits.meehab.ui.TwoOptionActivity;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.app.GCMManager;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.contacts.PhoneContacts;
import com.citrusbits.meehab.db.DatabaseHandler;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.pojo.AddUserResponse;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.DeviceContact;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.UtilityClass;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

public class LoginActivity extends SocketActivity implements
		OnSocketResponseListener, View.OnClickListener {

	public static final String TAG = LoginActivity.class.getSimpleName();

	private ImageButton ibLogin;
	private EditText passwordEdit, usernameEdit;
	private UserDatasource userDatasource;
	AddUserResponse userModel;
	Dialog pd;

	AppPrefs prefs;

	PhoneContacts phoneContacts;
	List<DeviceContact> contacts = new ArrayList<DeviceContact>();
	DatabaseHandler dbHandler;

	//Henry
	//abc123
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		if (GCMManager.getCMId(this).isEmpty()) {
			GCMManager gcmManager = new GCMManager(this);
			gcmManager.getRegId();
		}
		prefs = AppPrefs.getAppPrefs(LoginActivity.this);
		dbHandler = DatabaseHandler.getInstance(LoginActivity.this);

		phoneContacts = new PhoneContacts(LoginActivity.this);

		contacts.clear();
		contacts.addAll(phoneContacts.getPhoneContacts());

		// top back btn
		findViewById(R.id.ivBack).setOnClickListener(this);

		pd = UtilityClass.getProgressDialog(this);
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

	private JSONArray getPhoneBookArray() {

		JSONArray phoneBook = new JSONArray();
		for (DeviceContact contac : contacts) {
			JSONObject phone = new JSONObject();
			try {

				phone.put("phone", contac.getPhoneNumber());
				phone.put("name", contac.getContactName());

				phoneBook.put(phone);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return phoneBook;

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
			App.toast(getString(R.string.no_internet_connection));
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
				params.put("device_id",
						DeviceUtils.getDeviceId(LoginActivity.this));
				
				params.put("phonebook", getPhoneBookArray());

				params.put("device_token",
						GCMManager.getCMId(LoginActivity.this));

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
	public void onBackendConnected() {

	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {

		if (event.equals(EventParams.METHOD_USER_LOGIN)) {
			Log.e(TAG, obj.toString());
			pd.dismiss();

			for (int i = 0; i < contacts.size(); i++) {
				DeviceContact contact = contacts.get(i);
				if (!dbHandler.ContactExists(contact.getPhoneNumber())) {
					dbHandler.addContact(contact);
				}

			}
			LoginAndRegisterActivity.destroyThis=true;
			LoginActivity.this.finish();
			Intent intent = new Intent(LoginActivity.this,
					TwoOptionActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
			setResult(RESULT_OK);

			AppPrefs prefs = AppPrefs.getAppPrefs(LoginActivity.this);
			prefs.saveBooleanPrefs(AppPrefs.KEY_PROFILE_SETUP, true);
			prefs.saveBooleanPrefs(AppPrefs.KEY_PROFILE_SETUP_MORE, true);

			
		}

	}

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		pd.dismiss();
		App.toast(message);
	}
}
