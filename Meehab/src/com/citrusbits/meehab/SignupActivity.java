package com.citrusbits.meehab;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.contacts.GetContactTask;
import com.citrusbits.meehab.contacts.GetContactTask.ContactsListener;
import com.citrusbits.meehab.model.SignupModel;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.services.SocketService;
import com.citrusbits.meehab.utils.FacebookUtil;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.Prefs;
import com.citrusbits.meehab.utils.UtilityClass;
import com.citrusbits.meehab.webservices.AddUserService;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.ImageDownloader;
import com.facebook.internal.ImageRequest;
import com.facebook.internal.ImageResponse;

public class SignupActivity extends SocketActivity implements
		OnSocketResponseListener, OnClickListener, Observer {

	private static final int ACTIVITY_REQUEST_CODE = 33;
	private EditText etUserName, etEmail, etPassword, etPhoneNumber;
	private ImageButton ibNext;
	ProgressDialog pd;

	AddUserService checkEmail;
	AddUserService checkUsername;
	// facebook user
	private JSONObject user;
	String fbNameString;
	String fbEmailString;
	private String fbIdString;
	private String sha1Pass;

	JSONArray contactsArray = new JSONArray();

	GetContactTask contactTask;

	SignupModel signup;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (contactTask != null
				&& contactTask.getStatus() == android.os.AsyncTask.Status.RUNNING) {
			contactTask.cancel(true);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		contactTask = new GetContactTask(this);
		contactTask.setContactsListener(new ContactsListener() {

			@Override
			public void onFetchContacts(JSONArray jsonArray) {
				// TODO Auto-generated method stub
				contactsArray = jsonArray;
				Log.e("Device contacts are ", jsonArray.toString());

			}
		});
		contactTask.execute();

		pd = UtilityClass.getProgressDialog(this);
		etUserName = (EditText) findViewById(R.id.etUserName);
		etEmail = (EditText) findViewById(R.id.etEmail);
		etPassword = (EditText) findViewById(R.id.etPassword);
		etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
		ibNext = (ImageButton) findViewById(R.id.ibNext);

		// top back button
		findViewById(R.id.ivBack).setOnClickListener(this);

		ibNext.setOnClickListener(this);

		// if facebook login user info
		Bundle extra = getIntent().getExtras();
		if (extra != null
				&& extra.getBoolean(LoginAndRegisterActivity.FACEBOOK)) {
			final AccessToken accessToken = AccessToken.getCurrentAccessToken();
			if (accessToken == null) {
				App.alert("Facebook access problem");
				return;
			}
			pd.show();

			GraphRequest request = GraphRequest.newMeRequest(accessToken,
					new GraphRequest.GraphJSONObjectCallback() {
						@Override
						public void onCompleted(JSONObject me,
								GraphResponse response) {
							pd.dismiss();
							// Application code
							Log.v("LoginActivity", response.toString());

							if (response.getError() != null) {
								return;
							}
							user = me;

							if (user != null) {
								fbNameString = user.optString("name");
								fbEmailString = user.optString("email");
								fbIdString = user.optString("id");

								// etUserName.setText(fbNameString.replace(" ",
								// ""));
								etUserName.setText(fbNameString
										.replace(" ", ""));
								// etUserName.setEnabled(false);

								etEmail.setText(fbEmailString);
							}

							ImageRequest request = getImageRequest();
							if (request != null) {
								ImageDownloader.downloadAsync(request);
							}
							;

							FacebookUtil.setfacebookInfo(
									getApplicationContext(),
									user.optString("id"),
									accessToken.getToken(),
									user.optString("name"),
									user.optString("email"));

						}
					});
			Bundle parameters = new Bundle();
			parameters.putString("fields", "id,name,email,gender, birthday");
			request.setParameters(parameters);
			request.executeAsync();

		}

		etUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				validateUsername(hasFocus);
			}

		});
		etUserName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				etUserName.setError(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
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

		etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				validatePassword(hasFocus);
			}

		});

		etPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				etPassword.setError(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		etPhoneNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				etPhoneNumber.setError(null);
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

	private void validateUsername(boolean hasFocus) {
		String usernameString = etUserName.getText().toString();
		if (TextUtils.isEmpty(usernameString) || usernameString.length() < 2
				|| usernameString.length() > 20) {
			if (!hasFocus)
				etUserName.setError("Username must be 2-20 characters");
		} else {
			etUserName.setError(null);
		}
	}

	private void validateEmail(boolean hasFocus) {
		String emailString = etEmail.getText().toString();
		if (emailString.trim().length() == 0) {
			if (!hasFocus)
				etEmail.setError("Email is required");
		} else if (!UtilityClass.isValidEmail(emailString)) {
			if (!hasFocus)
				etEmail.setError("Email not valid");
		} else {
			etEmail.setError(null);
			// check email
		}
	}

	private void validatePassword(boolean hasFocus) {
		String passwordString = etPassword.getText().toString();
		com.citrusbits.meehab.utils.Pair pair = UtilityClass
				.isPasswordValid(passwordString);
		if (passwordString.trim().length() == 0) {
			if (!hasFocus)
				etPassword.setError("Password is required!");
		} else if (!pair.valid) {
			if (!hasFocus)
				etPassword.setError(pair.message);
		} else {
			etPassword.setError(null);
		}
	}

	private boolean validatePhoneNumber(String phoneNumber) {

		return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);

	}

	@Override
	public void update(Observable observable, Object data) {

	}

	@Override
	public void onClick(View v) {

		Intent intent = null;
		switch (v.getId()) {
		case R.id.ibNext:
			attemptSignup();
			break;
		case R.id.ivBack:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	private void attemptSignup() {
		// All fields Contain values
		String usernameString = etUserName.getText().toString();
		String emailString = etEmail.getText().toString();
		String passwordString = etPassword.getText().toString();
		String phoneNumber = etPhoneNumber.getText().toString().trim();
		phoneNumber="+1"+phoneNumber;

		if (!validatePhoneNumber(phoneNumber)) {
			etPhoneNumber.setError(getString(R.string.phone_number_is_invalid));
			return;
		}

		sha1Pass = UtilityClass.encryptPassword(passwordString);

		// validating inputs
		validateUsername(false);
		validateEmail(false);
		validatePassword(false);

		if (etUserName.getError() != null) {
			etUserName.requestFocus();
			etUserName.selectAll();
			return;
		} else if (etEmail.getError() != null) {
			etEmail.requestFocus();
			etEmail.selectAll();
			return;
		} else if (etPassword.getError() != null) {
			etPassword.requestFocus();
			etPassword.selectAll();
			return;
		}

		signup = new SignupModel();
		signup.setUserName(usernameString);
		signup.setPassword(passwordString);
		signup.setEmil(emailString);
		signup.setFbId(fbIdString);
		signup.setPhoneNumber(phoneNumber);
		signup.setContact(contactsArray.toString());

		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
				.getWindowToken(), 0);

		if (NetworkUtil.getConnectivityStatus(this) == 0) {
			App.alert(getString(R.string.no_internet_connection));
			return;
		}

		// openActivity(phoneNumber, VerificationActivity.SMS,signup);

		checkInfo(usernameString, emailString, phoneNumber);

		// Signup code go here
	}

	public void checkInfo(String usernameString, String emailString,
			String phone) {
		
		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.alert(getString(R.string.no_internet_connection));
			return;
		}
		JSONObject checkInfoParams = new JSONObject();
		try {
			// Toast.makeText(this, itemName,
			// Toast.LENGTH_SHORT).show();
			checkInfoParams.put("email", usernameString);
			checkInfoParams.put("username", emailString);
			checkInfoParams.put("phone", phone);

			pd.show();
			socketService.checkUserInfo(checkInfoParams);
		} catch (JSONException e) {
			pd.dismiss();
			e.printStackTrace();
			checkInfoParams = null;
		}

	}

	public void signup(String usernameString, String emailString,
			String passwordString) {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.alert(getString(R.string.no_internet_connection));
			return;
		}
		JSONObject signupParams = new JSONObject();
		try {
			// Toast.makeText(this, itemName,
			// Toast.LENGTH_SHORT).show();
			signupParams.put(EventParams.SIGNUP_USERNAME, usernameString);
			signupParams.put(EventParams.SIGNUP_EMAIL, emailString);
			// jobj.put("phone", UtilityClass
			// .phoneNumberNormal(strPhoneNumber));
			if (fbIdString != null) {
				signupParams.put(EventParams.SIGNUP_SOCIAL_ID, fbIdString);
				signupParams.put(EventParams.SIGNUP_TYPE,
						EventParams.SIGNUP_TYPE_VALUE.facebook.toString());
			} else {
				signupParams.put(EventParams.SIGNUP_TYPE,
						EventParams.SIGNUP_TYPE_VALUE.account.toString());
			}
			signupParams.put(EventParams.SIGNUP_PASSWORD, passwordString);

			pd.show();
			socketService.registerAccount(signupParams);
		} catch (JSONException e) {
			pd.dismiss();
			e.printStackTrace();
			signupParams = null;
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

	private ImageRequest getImageRequest() {
		ImageRequest request = null;
		ImageRequest.Builder requestBuilder = new ImageRequest.Builder(
				App.getInstance(),
				ImageRequest.getProfilePictureUri(
						user.optString("id"),
						getResources()
								.getDimensionPixelSize(
										R.dimen.usersettings_fragment_profile_picture_width),
						getResources()
								.getDimensionPixelSize(
										R.dimen.usersettings_fragment_profile_picture_height)));

		request = requestBuilder.setCallerTag(App.getInstance())
				.setCallback(new ImageRequest.Callback() {
					@Override
					public void onCompleted(ImageResponse response) {

						App.getInstance().globleBitmap = Bitmap.createBitmap(
								response.getBitmap(), 0, 0, 400, 400);
					}
				}).build();
		return request;
	}

	@Override
	void onBackendConnected() {

	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		pd.dismiss();

		JSONObject object = (JSONObject) obj;

		Log.e("CheckInfo Response is ", object.toString());

		/*
		 * new Prefs(this).getPrefs().edit().putString(Prefs.PREF_PASS,
		 * sha1Pass) .commit();
		 * 
		 * Intent intent = new Intent(SignupActivity.this,
		 * ProfileSetupActivity.class);
		 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
		 * Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(intent);
		 * overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
		 * setResult(RESULT_OK); finish();
		 */
		if (event.equals(EventParams.METHOD_CHECK_INFO)) {
			openActivity(VerificationActivity.SMS, signup);
		}
	}

	@Override
	public void onSocketResponseFailure(String message) {
		pd.dismiss();
		App.alert(message);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(LoginAndRegisterActivity.destroyThis){
			finish();
		}
	}

	private void openActivity(String method, SignupModel signup) {
		Intent verification = new Intent(this, VerificationActivity.class);
		verification.putExtra(VerificationActivity.INTENT_PHONENUMBER,
				signup.getPhoneNumber());
		verification.putExtra(VerificationActivity.EXTRA_SIGNUP,
				(Serializable) signup);
		verification.putExtra(VerificationActivity.INTENT_METHOD, method);

		verification.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(verification);
		
		LoginAndRegisterActivity.destroyThis=true;
	}

}
