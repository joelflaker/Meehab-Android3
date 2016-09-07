package com.citrusbits.meehab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.app.GsonRequest;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.contacts.PhoneContacts;
import com.citrusbits.meehab.db.DatabaseHandler;
import com.citrusbits.meehab.model.SignupModel;
import com.citrusbits.meehab.pojo.AddUserResponse;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.DeviceContact;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.CustomRequest;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.Prefs;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.gson.JsonObject;
import com.sinch.verification.Config;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;
import com.sinch.verification.internal.VerificationCodeListener;

public class VerificationActivity extends SocketActivity implements
		OnSocketResponseListener, OnClickListener {

	private static final String TAG = Verification.class.getSimpleName();
	// private final String APPLICATION_KEY =
	// "4e30c027-3577-4323-a772-da4b9dc26115";

	//private final String APPLICATION_KEY = "a152579d-4410-43e4-a374-e1602ab1272c";
	
    private final String APPLICATION_KEY = "f990687e-929e-4304-9571-99a3622767c3";



	public static final String SMS = "sms";

	public static final String INTENT_PHONENUMBER = "phonenumber";
	public static final String INTENT_METHOD = "method";
	public static final String EXTRA_SIGNUP = "signup";

	SignupModel signup;

	private Dialog pd;
	ImageButton ibNext;

	boolean verify;

	boolean getResponse = false;

	SmsReceiver smsReceiver;
	EditText etVerificationCode;
	Verification verification;

	boolean verifyRequest;

	ProgressBar progressBar;

	TextView btnDidnotGetACode;
	TextView tvCodeResent;

	TextView textTermsAndconditions;

	PhoneContacts phoneContacts;
	List<DeviceContact> contacts = new ArrayList<>();
	DatabaseHandler dbHandler;
    private String smsCode= "";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verification);

		dbHandler = DatabaseHandler.getInstance(VerificationActivity.this);

		phoneContacts = new PhoneContacts(VerificationActivity.this);

		contacts.clear();
		contacts.addAll(phoneContacts.getPhoneContacts());
		etVerificationCode = (EditText) findViewById(R.id.etVerificationCode);
		textTermsAndconditions = (TextView) findViewById(R.id.textTermsAndconditions);

		findViewById(R.id.ivBack).setOnClickListener(this);
		findViewById(R.id.txtTerms).setOnClickListener(this);
		textTermsAndconditions.setOnClickListener(this);

		btnDidnotGetACode = (TextView) findViewById(R.id.btnDidnotGetACode);
		tvCodeResent = (TextView) findViewById(R.id.tvCodeResent);

		btnDidnotGetACode.setOnClickListener(this);

		smsReceiver = new SmsReceiver();
		ibNext = (ImageButton) findViewById(R.id.ibNext);
		ibNext.setOnClickListener(this);
		pd = UtilityClass.getProgressDialog(this);
		signup = (SignupModel) getIntent().getSerializableExtra(EXTRA_SIGNUP);

		progressBar = (ProgressBar) findViewById(R.id.progressIndicator);

		Intent intent = getIntent();
		if (intent != null) {
			String phoneNumber = intent.getStringExtra(INTENT_PHONENUMBER);

			String method = intent.getStringExtra(INTENT_METHOD);

			createVerification(phoneNumber, method);

		}

		registerReceiver(smsReceiver, new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED"));

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

	void createVerification(String phoneNumber, String method) {



			/*<?xml version='1.0' encoding='UTF-8'?>
			<TwilioResponse>
			<SMSMessage>
			<Sid>SM838eba9ac1574e878025072b93bb123a</Sid>
			<DateCreated>Wed, 07 Sep 2016 05:19:32 +0000</DateCreated>
			<DateUpdated>Wed, 07 Sep 2016 05:19:32 +0000</DateUpdated>
			<DateSent/>
			<AccountSid>AC05f81aa73348b83219be2613c794871f</AccountSid>
			<To>+923215427693</To>
			<From>+12017205413</From>
			<Body>1234</Body>
			<Status>queued</Status>
			<Direction>outbound-api</Direction>
			<ApiVersion>2010-04-01</ApiVersion>
			<Price/>
			<PriceUnit>USD</PriceUnit>
			<Uri>/2010-04-01/Accounts/AC05f81aa73348b83219be2613c794871f/SMS/Messages/SM838eba9ac1574e878025072b93bb123a</Uri>
			<NumSegments>1</NumSegments>
			</SMSMessage>
			</TwilioResponse>*/
        final int randomNum = (int)(Math.random()*90000)+10000;
        smsCode= String.format("%05d",randomNum);
        Map<String, String> params = new HashMap<String, String>();
        params.put("From", Consts.FromNumber);
        params.put("To", phoneNumber);
        params.put("Body", smsCode);

        RequestQueue requestQueue = App.getInstance().getRequestQueue();

        CustomRequest request = new CustomRequest(Request.Method.POST, "https://" + Consts.SID + Consts.TwilioSecret +
                "@api.twilio.com/2010-04-01/Accounts/" + Consts.SID + "/SMS/Messages.json", params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
				try {
                    Log.i("response", response.toString());
					String status= response.getString("status");
					if(status.equals("queued")){

					}
                    else{
                        Toast.makeText(VerificationActivity.this, "Unable to Send Msg", Toast.LENGTH_SHORT).show();
                    }
				} catch (JSONException e) {
					e.printStackTrace();
                    Toast.makeText(VerificationActivity.this, "Unable to Send Msg", Toast.LENGTH_SHORT).show();

                }
				Log.i("response", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("response", error.toString());
                Toast.makeText(VerificationActivity.this, "Unable to Send Msg", Toast.LENGTH_SHORT).show();

            }
        });

        requestQueue.add(request);



	}

	public void verify(String code) {
		verifyRequest = true;

		progressBar.setVisibility(View.VISIBLE);

		verification.verify(code);

	}

	class MyVerificationListener implements VerificationListener {

		@Override
		public void onInitiated() {
			Log.d(TAG, "Initialized!");

		}

		@Override
		public void onInitiationFailed(Exception exception) {
			Log.e(TAG,
					"Verification initialization failed: "
							+ exception.getMessage());
			hideProgress(R.string.failed, false);
			getResponse = true;

		}

		@Override
		public void onVerified() {
			Log.d(TAG, "Verified!");
			hideProgress(R.string.verified, true);
			verify = true;
			getResponse = true;

			if (verifyRequest) {
				signup(signup);
			}

		}

		@Override
		public void onVerificationFailed(Exception exception) {
			Log.e(TAG, "Verification failed: " + exception.getMessage());
			getResponse = true;
			if (verifyRequest) {
				Toast.makeText(VerificationActivity.this, "Code not verified!",
						Toast.LENGTH_SHORT).show();
			}
			hideProgress(R.string.failed, false);

		}
	}

	public void signup(SignupModel model) {
		
		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}
		JSONObject signupParams = new JSONObject();
		try {
			// Toast.makeText(this, itemName,
			// Toast.LENGTH_SHORT).show();
			signupParams.put(EventParams.SIGNUP_USERNAME, model.getUserName());
			signupParams.put(EventParams.SIGNUP_EMAIL, model.getEmil());
			signupParams.put("phone", model.getPhoneNumber());

			signupParams.put("device_id",
					DeviceUtils.getDeviceId(VerificationActivity.this));
			String phoneContacts = model.getContact();
			// signupParams.put("phonebook", model.getContact());
			signupParams.put("phonebook", getPhoneBookArray());
			// jobj.put("phone", UtilityClass
			// .phoneNumberNormal(strPhoneNumber));
			if (model.getFbId() != null) {
				signupParams.put(EventParams.SIGNUP_SOCIAL_ID, model.getFbId());
				signupParams.put(EventParams.SIGNUP_TYPE,
						EventParams.SIGNUP_TYPE_VALUE.facebook.toString());
			} else {
				signupParams.put(EventParams.SIGNUP_TYPE,
						EventParams.SIGNUP_TYPE_VALUE.account.toString());
			}
			signupParams.put(EventParams.SIGNUP_PASSWORD, model.getPassword());

			pd.show();
			Log.e("Signup Params ", signupParams.toString());
			socketService.registerAccount(signupParams);
		} catch (JSONException e) {
			pd.dismiss();
			e.printStackTrace();
			signupParams = null;
		}

	}

	void hideProgress(int message, boolean success) {

		ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
		progressBar.setVisibility(View.INVISIBLE);

	}

	long preveTime = 0;

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		pd.dismiss();

		if (event.equals(EventParams.EVENT_USER_SIGNUP)) {

			for (int i = 0; i < contacts.size(); i++) {
				DeviceContact contact = contacts.get(i);
				if (!dbHandler.ContactExists(contact.getPhoneNumber())) {
					dbHandler.addContact(contact);
				}

			}
			
			
			AppPrefs prefs = AppPrefs.getAppPrefs(VerificationActivity.this);
			prefs.saveBooleanPrefs(AppPrefs.KEY_PROFILE_SETUP, false);
			prefs.saveBooleanPrefs(AppPrefs.KEY_PROFILE_SETUP_MORE, false);

			String sha1Pass = UtilityClass
					.encryptPassword(signup.getPassword());
			new Prefs(this).getPrefs().edit()
					.putString(Prefs.PREF_PASS, sha1Pass).commit();

			Intent intent = new Intent(VerificationActivity.this,
					ProfileSetupActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
			setResult(RESULT_OK);
			finish();
		}

	}

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		Toast.makeText(VerificationActivity.this, message, Toast.LENGTH_SHORT)
				.show();
		pd.dismiss();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnDidnotGetACode:

			tvCodeResent.setVisibility(View.VISIBLE);
			Intent intent = getIntent();
			if (intent != null) {
				String phoneNumber = intent.getStringExtra(INTENT_PHONENUMBER);

				String method = intent.getStringExtra(INTENT_METHOD);

				createVerification(phoneNumber, method);

			}

			break;

		case R.id.ibNext:
			String code = etVerificationCode.getText().toString().trim();
			if (code.isEmpty()) {
				Toast.makeText(VerificationActivity.this, "Please enter code",
						Toast.LENGTH_SHORT).show();
				return;
			}
			/*if (!verify && !verifyRequest) {
				verify(code);
			} else if (verify) {
				signup(signup);*/
			if (code.equals(smsCode)) {
				signup(signup);
			} else {
				Toast.makeText(VerificationActivity.this, "Code not verified!",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.txtTerms:
			startActivity(new Intent(this, TermsAndConditionsActivity.class));
			break;
		case R.id.textTermsAndconditions:
			startActivity(new Intent(this, TermsAndConditionsActivity.class));
			break;
		case R.id.ivBack:
			onBackPressed();
			break;
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(smsReceiver);
	}

	public class SmsReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// ---get the SMS message passed in---
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage shortMessage = SmsMessage
						.createFromPdu((byte[]) pdus[0]);

				Log.d("SMSReceiver",
						"SMS message sender: "
								+ shortMessage.getOriginatingAddress());
				Log.d("SMSReceiver",
						"SMS message text: "
								+ shortMessage.getDisplayMessageBody());
				String code = shortMessage.getDisplayMessageBody();
				if (code.toLowerCase().trim()
						.contains("your verification code is")) {
					code = code.toLowerCase()
							.replace("your verification code is", "").trim()
							.replace(".", "");
					etVerificationCode.setText(code);
				}

			}
		}
	}

}
