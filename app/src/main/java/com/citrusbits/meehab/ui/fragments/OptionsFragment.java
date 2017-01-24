package com.citrusbits.meehab.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.citrusbits.meehab.ui.users.BlockFriendsActivity;
import com.citrusbits.meehab.ui.users.ChangeEmailActivity;
import com.citrusbits.meehab.ui.users.ChangePasswordActivity;
import com.citrusbits.meehab.ui.HomeActivity;
import com.citrusbits.meehab.ui.InsuranceActivity;
import com.citrusbits.meehab.ui.users.LoginAndRegisterActivity;
import com.citrusbits.meehab.ui.ProvideAppFeedBackActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.ui.rehabs.RehabAdditionActivity;
import com.citrusbits.meehab.ui.meetings.MeetingAdditionActivity;
import com.citrusbits.meehab.ui.users.LoginAndRegisterActivity.PendingAction;
import com.citrusbits.meehab.ui.users.TermsAndConditionsActivity;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.ui.dialog.LogoutDialog;
import com.citrusbits.meehab.ui.dialog.LogoutDialog.LogoutDialogClickListener;
import com.citrusbits.meehab.ui.dialog.PasswordConfirmationDialog;
import com.citrusbits.meehab.ui.dialog.UnlinkFacebookDialog;
import com.citrusbits.meehab.ui.dialog.UnlinkFacebookDialog.UnlinkFacebookDialogListener;
import com.citrusbits.meehab.helpers.LogoutHelper;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.services.SocketService;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.UtilityClass;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

public class OptionsFragment extends Fragment implements OnSocketResponseListener, OnBackendConnectListener, View.OnClickListener {

	UserDatasource userDatasource;
	private CheckBox cbLinkFacebook;

	HomeActivity homeActivity;

	Button btnChangeInsurance;

	CheckBox cbAppearOffline;
	CheckBox cbMessageNotification;

	AppPrefs prefs;
	private Dialog pd;
	private boolean appearOnline;
	protected boolean isNotify;
	boolean isLinkedFacebook;
	private UserAccount user;
	private Button btnAppearTitle;
	private PendingAction pendingAction = PendingAction.NONE;
	private CallbackManager callbackManager;
	protected String fbId;
	
	ServiceCalls waitingFor = ServiceCalls.NONE;
	
	private enum ServiceCalls {
		NONE,USER_STATUS, USER_PUSH, LINK_FACEBOOK, UNLINK_FACEBOOK
	}

	public OptionsFragment() {
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDatasource = new UserDatasource(getActivity());
		this.homeActivity = (HomeActivity) getActivity();
		pd = UtilityClass.getProgressDialog(getContext());
		
		FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
		callbackManager = CallbackManager.Factory.create();
		
		LoginManager.getInstance().registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {

					@Override
					public void onSuccess(final LoginResult loginResult) {

						
						fbId = loginResult.getAccessToken().getUserId();
						setLinkWithFacebook();
						pd.show();
						updateFacebookFrineds(loginResult.getAccessToken());


					}

					@Override
					public void onCancel() {
						waitingFor = ServiceCalls.NONE;
						if (pendingAction != PendingAction.NONE) {
							showAlert();
							pendingAction = PendingAction.NONE;
						}
					}

					@Override
					public void onError(FacebookException exception) {
						waitingFor = ServiceCalls.NONE;
						if (pendingAction != PendingAction.NONE
								&& exception instanceof FacebookAuthorizationException) {
							showAlert();
							pendingAction = PendingAction.NONE;
						}
					}

					private void showAlert() {
						new AlertDialog.Builder(getContext())
								.setTitle("Cancel")
								.setMessage("Permision not granted")
								.setPositiveButton("OK", null).show();
					}
				});

		
	}

	protected void updateFacebookFrineds(AccessToken accessToken) {
		GraphRequest.newMyFriendsRequest(accessToken, new GraphRequest.GraphJSONArrayCallback() {
			
			@Override
			public void onCompleted(JSONArray friends, GraphResponse response) {
				 /* handle the result */
	        	if(response.getError() != null){
	        		return;
	        	}
	        	
	        	try {

	        		JSONObject params = new JSONObject();
	        		params.put(EventParams.SIGNUP_SOCIAL_ID, fbId);
					params.put("link", "facebook");
//					params.put(EventParams.USER_KEY_FB_F,friends);
					
					waitingFor = ServiceCalls.LINK_FACEBOOK;
					homeActivity.socketService.updateAccount(params);

				} catch (Exception e) {
					e.printStackTrace();
				}	        	
				
			}
    }).executeAsync();		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		homeActivity = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_options, container, false);

		prefs = AppPrefs.getAppPrefs(getActivity());
		user = userDatasource.findUser(AccountUtils.getUserId(getActivity()));

		if(user == null) {
			getActivity().finish();
			return v;
		}

		v.findViewById(R.id.topMenuBtn).setOnClickListener(this);
		v.findViewById(R.id.btnChangeEmail).setOnClickListener(this);
		v.findViewById(R.id.btnTermsCondition).setOnClickListener(this);
		v.findViewById(R.id.btnPrivacy).setOnClickListener(this);
		v.findViewById(R.id.btnChangePassword).setOnClickListener(this);
		v.findViewById(R.id.btnChangeInsurance).setOnClickListener(this);
		v.findViewById(R.id.btnDeleteAccount).setOnClickListener(this);
		btnAppearTitle = (Button)v.findViewById(R.id.btnAppearTitle);

		cbLinkFacebook = (CheckBox) v.findViewById(R.id.cbLinkFacebook);
		cbAppearOffline = (CheckBox) v.findViewById(R.id.cbAppearOffline);
		cbMessageNotification = (CheckBox) v.findViewById(R.id.cbMessageNotification);

		v.findViewById(R.id.requestMeetingAdditionBtn).setOnClickListener(this);
		v.findViewById(R.id.requestRehabAdditionBtn).setOnClickListener(this);
		v.findViewById(R.id.btnBlockFriends).setOnClickListener(this);
		v.findViewById(R.id.btnProvideFeedback).setOnClickListener(this);

		v.findViewById(R.id.rateUsBtn).setOnClickListener(this);
		v.findViewById(R.id.logout).setOnClickListener(this);


		//		user = userDatasource.findUser(AccountUtils.getUserId(getActivity()));
		//		if(TextUtils.isEmpty(user.getNotification()) && user.getNotification().equalsIgnoreCase("on")){
		//			msgNotification = true;
		//		}else{
		//			msgNotification = false;
		//		}

		
		fbId = user.getSocailId();
		appearOnline = resetAppearOfflineBool();
		isNotify = resetNotificationBool();

		prefs.saveBooleanPrefs(AppPrefs.KEY_APPEAR_OFFLINE, appearOnline);
		prefs.saveBooleanPrefs(AppPrefs.KEY_MSG_NOTIFICATION, isNotify);
		//				prefs.getBooleanPrefs(
		//				AppPrefs.KEY_APPEAR_OFFLINE, AppPrefs.DEFAULT.APPEAR_OFFLINE);

		//		boolean msgNotification = prefs.getBooleanPrefs(
		//				AppPrefs.KEY_MSG_NOTIFICATION,
		//				AppPrefs.DEFAULT.MESSAGE_NOTIFICATION);


		cbAppearOffline.setOnCheckedChangeListener(onCheckChangeListener);
		cbMessageNotification.setOnCheckedChangeListener(onCheckChangeListener);

		/*
		 * if (FacebookUtil.getfacebookId(getActivity()) != null &&
		 * !FacebookUtil.getfacebookId(getActivity()).isEmpty()) {
		 * isLinkedFacebook = true;
		 * btnLinkFacebook.setText(R.string.disconnect_my_facebook); }
		 */

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		setLinkWithFacebook();
	}
	
	private boolean resetNotificationBool() {
		cbMessageNotification.setOnCheckedChangeListener(null);
		boolean bool = false;
		if(TextUtils.isEmpty(user.getNotification()) || user.getNotification().equalsIgnoreCase("on")){
			cbMessageNotification.setChecked(true);
			bool = true;
		}else{
			cbMessageNotification.setChecked(false);
			bool = false;
		}
		
		cbMessageNotification.setOnCheckedChangeListener(onCheckChangeListener);
		return bool;
	}

	private boolean resetAppearOfflineBool() {
		cbAppearOffline.setOnCheckedChangeListener(null);
		boolean bool = false;
		if(user.getCheckinType().equalsIgnoreCase("offline")){
			//text change
			cbAppearOffline.setChecked(false);
//			btnAppearTitle.setText("Appear Online");			
			bool = true;
		}else{
			cbAppearOffline.setChecked(true);
			bool = false;
//			btnAppearTitle.setText("Appear Offline");
		}
		cbAppearOffline.setOnCheckedChangeListener(onCheckChangeListener);
		return bool;
	}

	@Override
	public void onBackendConnected() {

	}

	public void setLinkWithFacebook() {
		isLinkedFacebook = !TextUtils.isEmpty(fbId);
		String linkStr = isLinkedFacebook ? getString(R.string.disconnect_my_facebook)
				: getString(R.string.link_my_facebook_account);

		cbLinkFacebook.setOnCheckedChangeListener(null);
		cbLinkFacebook.setChecked(isLinkedFacebook);
		cbLinkFacebook.setOnCheckedChangeListener(onCheckChangeListener);
	}

	OnCheckedChangeListener onCheckChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.cbAppearOffline:
				appearOnline = isChecked;
				toggleAppearOffline();
				break;
			case R.id.cbLinkFacebook:
				if (!isLinkedFacebook) {
					if (NetworkUtil.getConnectivityStatus(getContext()) == 0) {
						App.toast(getString(R.string.no_internet_connection));
						return;
					}
//						loginAction = LoginAction.FACEBOOOK;

							List<String> permissions = new ArrayList<String>();
							permissions.add("email");
							permissions.add("user_friends");
							permissions.add("public_profile");
							permissions.add("user_birthday");
							LoginManager.getInstance().logInWithReadPermissions(OptionsFragment.this,permissions);
							
				} else {

					new UnlinkFacebookDialog(getActivity())
					.setUnlinkFacebookDialogListener(
							new UnlinkFacebookDialogListener() {

								@Override
								public void onDisconnectClick(
										UnlinkFacebookDialog dialog) {
									
									
									prefs.saveBooleanPrefs(
											AppPrefs.KEY_LINKK_WITH_FACEBOOK,
											!isLinkedFacebook);

									try{
										JSONObject params = new JSONObject();
										params.put("unlink", "facebook");
										pd.show();
										waitingFor = ServiceCalls.UNLINK_FACEBOOK;
										homeActivity.socketService.updateAccount(params);
										
										AccessToken accessToken = AccessToken.getCurrentAccessToken();
										if (accessToken != null) {
											LoginManager.getInstance().logOut();
										}
									}catch(Exception e){
										e.printStackTrace();
									}
									
									setLinkWithFacebook();
									dialog.dismiss();
								}

								@Override
								public void onCancelClick(
										UnlinkFacebookDialog dialog) {
									dialog.dismiss();
								}
							}).show();
				}
				break;
			case R.id.cbMessageNotification:
				isNotify = isChecked;
				toggleNotification();
				break;
			}

		}
		private void toggleAppearOffline() {

			if (NetworkUtil.getConnectivityStatus(getContext()) == 0) {
				cbAppearOffline.setChecked(!appearOnline);
				return;
			} else {

				JSONObject params = new JSONObject();
				try {

					if(appearOnline){
						params.put(EventParams.USER_CHECKIN_TYPE, "online");
					}else{
						params.put(EventParams.USER_CHECKIN_TYPE, "offline");
					}

				} catch (JSONException e) {
					e.printStackTrace();
					params = null;
				}

				if (params != null && params.length() > 0) {
					waitingFor = ServiceCalls.USER_STATUS;
					pd.show();
					homeActivity.socketService.updateAccount(params);
				}
			}
		}
		private void toggleNotification() {

			if (NetworkUtil.getConnectivityStatus(getContext()) == 0) {
				cbMessageNotification.setChecked(!isNotify);
				return;
			} else {

				JSONObject params = new JSONObject();
				try {

					if(isNotify){
						params.put(EventParams.USER_NOTIFICATION, "on");
					}else{
						params.put(EventParams.USER_NOTIFICATION, "off");
					}

				} catch (JSONException e) {
					e.printStackTrace();
					params = null;
				}

				if (params != null && params.length() > 0) {
					waitingFor = ServiceCalls.USER_PUSH;
					pd.show();
					homeActivity.socketService.updateAccount(params);
				}
			}
		}
	};

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {

		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}

		if (event.equals(EventParams.EVENT_USER_UPDATE)) {

			if(waitingFor == ServiceCalls.USER_STATUS){
				if(appearOnline){
					user.setCheckinType("online");					
				}else{
					user.setCheckinType("offline");
				}
			}else if(waitingFor == ServiceCalls.USER_PUSH){
				if(isNotify){
					user.setNotification("on");
				}else{
					user.setNotification("off");
				}
			}else if(waitingFor == ServiceCalls.LINK_FACEBOOK){
				user.setSocailId(fbId);
			}else if (waitingFor == ServiceCalls.UNLINK_FACEBOOK){
				fbId = "";
				user.setSocailId("");
			}

			waitingFor = ServiceCalls.NONE;
			userDatasource.update(user);

			setLinkWithFacebook();
			resetAppearOfflineBool();
			resetNotificationBool();
			prefs.saveBooleanPrefs(AppPrefs.KEY_APPEAR_OFFLINE, appearOnline);
			prefs.saveBooleanPrefs(AppPrefs.KEY_MSG_NOTIFICATION, isNotify);
		}else if (event.equals(EventParams.METHOD_USERS_DELETE)) {
			attemptLogout();
		}
	}

	@Override
	public void onSocketResponseFailure(String event, String message) {
		pd.dismiss();
		//		prefs.saveBooleanPrefs(AppPrefs.KEY_APPEAR_OFFLINE, appearOffline);

		if (event.equals(EventParams.EVENT_USER_UPDATE)) {
			isNotify = resetNotificationBool();
			appearOnline = resetAppearOfflineBool();
			
			if(waitingFor == ServiceCalls.LINK_FACEBOOK){
				fbId = "";
			}

			waitingFor = ServiceCalls.NONE;
		}else if (event.equals(EventParams.METHOD_USERS_DELETE)) {
			App.toast(""+message);
		}
	}
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.topMenuBtn:
			if (homeActivity.isDrawerOpen()) {
				homeActivity.changeDrawerVisibility(false);
			} else {
				homeActivity.changeDrawerVisibility(true);
			}
			break;
		case R.id.btnChangeEmail:
			intent = new Intent(getActivity(), ChangeEmailActivity.class);
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
			break;
		case R.id.btnTermsCondition:
			intent = new Intent(getActivity(), TermsAndConditionsActivity.class);
			intent.putExtra(TermsAndConditionsActivity.EXTRA_TERMS,1);
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
			break;
		case R.id.btnPrivacy:
			intent = new Intent(getActivity(), TermsAndConditionsActivity.class);
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
			break;
		case R.id.btnChangeInsurance:
			intent = new Intent(getActivity(), InsuranceActivity.class);

			intent.putExtra(InsuranceActivity._mode,
					InsuranceActivity.InsuranceMode.EDIT);

			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
			break;
		case R.id.btnChangePassword:
			intent = new Intent(getActivity(), ChangePasswordActivity.class);
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
			break;
		case R.id.requestMeetingAdditionBtn:
			intent = new Intent(getActivity(),
					MeetingAdditionActivity.class);
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
			break;
		case R.id.requestRehabAdditionBtn:
			intent = new Intent(getActivity(), RehabAdditionActivity.class);
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
			break;
		case R.id.btnBlockFriends:
			intent = new Intent(getActivity(), BlockFriendsActivity.class);
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
			break;
		case R.id.btnProvideFeedback:
			intent = new Intent(getActivity(), ProvideAppFeedBackActivity.class);
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
			break;
		case R.id.rateUsBtn:
			final String appPackageName = getActivity().getPackageName(); // getPackageName()
			// from
			// Context
			// or
			// Activity
			// object
			try {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id=" + appPackageName)));
			} catch (android.content.ActivityNotFoundException anfe) {
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("https://play.google.com/store/apps/details?id="
								+ appPackageName)));
			}
			break;
		case R.id.logout:
			// confirmation
			/*
			 * new AlertDialog.Builder(getActivity()).setTitle("Are you sure")
			 * .setPositiveButton("Logout", new
			 * DialogInterface.OnClickListener() {
			 * 
			 * @Override public void onClick(DialogInterface dialog, int which)
			 * { attemptLogout(); }
			 * }).setNegativeButton("Cancel",null).create().show();
			 */

			new LogoutDialog(getActivity()).setLogoutDialogListener(
					new LogoutDialogClickListener() {

						@Override
						public void onLogoutClick(LogoutDialog dialog) {
							dialog.dismiss();
							// attemptLogout();

							LogoutHelper logoutHelper = new LogoutHelper(
									getActivity());
							logoutHelper.attemptLogout();
							userDatasource.removeAllUsers();
							//							MeehabDBOpenHelper.deleteDB(getContext());
						}

						@Override
						public void onCancelClick(LogoutDialog dialog) {
							dialog.dismiss();
						}
					}).show();
			break;
		case R.id.btnDeleteAccount:

			new PasswordConfirmationDialog(getActivity(),getString(R.string.your_account_permanently_deleted)).setConfirmationListener(
					new PasswordConfirmationDialog.PasswordConfirmationDialogClickListener() {

						@Override
						public void onDeleteClick(String password, PasswordConfirmationDialog dialog) {
							dialog.dismiss();
							if(NetworkUtil.isConnected(homeActivity)) {
								pd.show();
								homeActivity.socketService.deleteAccount(password);
							}else {
								App.toast(getResources().getString(R.string.no_internet_connection));
							}
						}

						@Override
						public void onCancelClick(PasswordConfirmationDialog dialog) {
							dialog.dismiss();
						}
					}).show();
			break;
		default:
			break;
		}
	}

	private void attemptLogout() {
		// clear db and prefs

		Intent i = new Intent(getActivity(), SocketService.class);
		getActivity().stopService(i);

		App.getInstance()
		.getSharedPreferences(Consts.APP_PREFS_NAME,
				Context.MODE_PRIVATE).edit().clear().commit();

		AppPrefs prefs = AppPrefs.getAppPrefs(getActivity());
		prefs.saveIntegerPrefs(AppPrefs.KEY_USER_ID, AppPrefs.DEFAULT.USER_ID);
		/*
		 * homeActivity.socketService.emit(EventParams.EVENT_USER_LOGOUT, new
		 * JSONObject(), null);
		 */
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		if (accessToken != null) {
			LoginManager.getInstance().logOut();
		}
		Intent intent = new Intent(getActivity(),
				LoginAndRegisterActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("Exit me", true);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.activity_in,
				R.anim.activity_out);

		if (homeActivity.socketService != null) {
			//homeActivity.socketService.disconnectSocket();
		}

		getActivity().finish();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

}
