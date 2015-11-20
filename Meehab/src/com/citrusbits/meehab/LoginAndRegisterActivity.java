package com.citrusbits.meehab;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.UtilityClass;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

public class LoginAndRegisterActivity extends SocketActivity implements
		OnSocketResponseListener, OnClickListener {

	private static final int ACTIVITY_REQUEST_CODE = 33;
	public static final String FACEBOOK = "fb_user";
	private CallbackManager callbackManager;
	private AccessTokenTracker mTokenTracker;
	private ProfileTracker mProfileTracker;
	private PendingAction pendingAction = PendingAction.NONE;
	private ImageButton ibLoginWithFacebook;
	AccessToken accessToken;

	private final String PENDING_ACTION_BUNDLE_KEY = "com.citrusbits.meehab:PendingAction";
	private ImageButton ibLogin;
	private ImageButton ibSignup;
	private JSONObject user;
	private ProgressDialog pd;
	public static Bitmap profilePicBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FacebookSdk.sdkInitialize(this.getApplicationContext());
		callbackManager = CallbackManager.Factory.create();
		pd = UtilityClass.getProgressDialog(this);
		LoginManager.getInstance().registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(final LoginResult loginResult) {

						accessToken = loginResult.getAccessToken();
						String fbId = accessToken.getUserId();
						try {
							JSONObject params = new JSONObject();
							params.put(EventParams.SIGNUP_SOCIAL_ID, fbId);
							pd.show();
							socketService.loginWithFacebook(params);

						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onCancel() {
						if (pendingAction != PendingAction.NONE) {
							showAlert();
							pendingAction = PendingAction.NONE;
						}
					}

					@Override
					public void onError(FacebookException exception) {
						if (pendingAction != PendingAction.NONE
								&& exception instanceof FacebookAuthorizationException) {
							showAlert();
							pendingAction = PendingAction.NONE;
						}
					}

					private void showAlert() {
						new AlertDialog.Builder(LoginAndRegisterActivity.this)
								.setTitle("Cancel")
								.setMessage("Permision not granted")
								.setPositiveButton("OK", null).show();
					}
				});

		if (savedInstanceState != null) {
			String name = savedInstanceState
					.getString(PENDING_ACTION_BUNDLE_KEY);
			pendingAction = PendingAction.valueOf(name);
		}

		setContentView(R.layout.activity_login_and_register);

		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.citrusbits.meehab", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("Key", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}
		// permissions.add("publish_actions");

		ibLoginWithFacebook = (ImageButton) findViewById(R.id.ibLoginWithFacebook);

		ibLogin = (ImageButton) findViewById(R.id.ibLogin);
		ibSignup = (ImageButton) findViewById(R.id.ibSignup);

		ibLoginWithFacebook.setOnClickListener(this);
		ibLogin.setOnClickListener(this);
		ibSignup.setOnClickListener(this);

	}

	/**
	 * @param name
	 */
	protected void alert(String name) {
		Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ACTIVITY_REQUEST_CODE:
				finish();
				break;

			default:
				break;
			}
		}

	}

	private enum PendingAction {
		NONE, POST_PHOTO, POST_STATUS_UPDATE
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.ibLoginWithFacebook:
			if (NetworkUtil.getConnectivityStatus(this) == 0) {
				App.alert(getString(R.string.no_internet_connection));
				return;
			} else {
				List<String> permissions = new ArrayList<String>();
				permissions.add("email");
				permissions.add("user_friends");
				permissions.add("public_profile");
				permissions.add("user_birthday");
				LoginManager.getInstance().logInWithReadPermissions(this,
						permissions);
			}
			break;
		case R.id.ibLogin:
			intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
			break;
		case R.id.ibSignup:
			intent = new Intent(this, SignupActivity.class);
			startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
			break;
		default:
			break;
		}
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		pd.dismiss();
		Intent i = new Intent(LoginAndRegisterActivity.this,
				TwoOptionActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
		overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
		finish();

	}

	@Override
	public void onSocketResponseFailure(String message) {
		pd.dismiss();
		Intent i = new Intent(LoginAndRegisterActivity.this,
				SignupActivity.class);
		i.putExtra(FACEBOOK, true);
		startActivityForResult(i, ACTIVITY_REQUEST_CODE);
		overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
	}

}
