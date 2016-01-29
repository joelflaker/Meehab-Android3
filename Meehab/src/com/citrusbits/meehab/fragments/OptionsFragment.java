package com.citrusbits.meehab.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.citrusbits.meehab.BlockFriendsActivity;
import com.citrusbits.meehab.ChangeEmailActivity;
import com.citrusbits.meehab.ChangePasswordActivity;
import com.citrusbits.meehab.HomeActivity;
import com.citrusbits.meehab.InsuranceActivity;
import com.citrusbits.meehab.LoginAndRegisterActivity;
import com.citrusbits.meehab.ProvideAppFeedBackActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.RehabAdditionActivity;
import com.citrusbits.meehab.RequestMeetingAdditionActivity;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.dialog.LogoutDialog;
import com.citrusbits.meehab.dialog.LogoutDialog.LogoutDialogClickListener;
import com.citrusbits.meehab.dialog.UnlinkFacebookDialog;
import com.citrusbits.meehab.dialog.UnlinkFacebookDialog.UnlinkFacebookDialogListener;
import com.citrusbits.meehab.helpers.LogoutHelper;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.SocketService;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

public class OptionsFragment extends Fragment implements View.OnClickListener {

	UserDatasource userDatasource;
	private Button btnLinkFacebook;
	boolean isLinkedFacebook = false;

	HomeActivity homeActivity;

	Button btnChangeInsurance;

	CheckBox cbAppearOffline;
	CheckBox cbMessageNotification;

	AppPrefs prefs;

	public OptionsFragment() {
	}

	public OptionsFragment(HomeActivity homeActivity) {
		this.homeActivity = homeActivity;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDatasource = new UserDatasource(getActivity());

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_options, container, false);

		prefs = AppPrefs.getAppPrefs(getActivity());

		v.findViewById(R.id.topMenuBtn).setOnClickListener(this);
		v.findViewById(R.id.btnChangeEmail).setOnClickListener(this);
		v.findViewById(R.id.btnChangePassword).setOnClickListener(this);
		v.findViewById(R.id.btnChangeInsurance).setOnClickListener(this);
		btnLinkFacebook = (Button) v.findViewById(R.id.btnLinkFacebook);
		cbAppearOffline = (CheckBox) v.findViewById(R.id.cbAppearOffline);
		cbMessageNotification = (CheckBox) v
				.findViewById(R.id.cbMessageNotification);
		btnLinkFacebook.setOnClickListener(this);

		v.findViewById(R.id.requestMeetingAdditionBtn).setOnClickListener(this);
		v.findViewById(R.id.requestRehabAdditionBtn).setOnClickListener(this);
		v.findViewById(R.id.btnBlockFriends).setOnClickListener(this);
		v.findViewById(R.id.btnProvideFeedback).setOnClickListener(this);

		v.findViewById(R.id.rateUsBtn).setOnClickListener(this);
		v.findViewById(R.id.logout).setOnClickListener(this);

		cbAppearOffline.setOnCheckedChangeListener(onCheckChangeListener);
		cbMessageNotification.setOnCheckedChangeListener(onCheckChangeListener);

		boolean appearOffline = prefs.getBooleanPrefs(
				AppPrefs.KEY_APPEAR_OFFLINE, AppPrefs.DEFAULT.APPEAR_OFFLINE);

		boolean msgNotification = prefs.getBooleanPrefs(
				AppPrefs.KEY_MSG_NOTIFICATION,
				AppPrefs.DEFAULT.MESSAGE_NOTIFICATION);

		cbAppearOffline.setChecked(appearOffline);
		cbMessageNotification.setChecked(msgNotification);
		setLinkWithFacebook(getLinkWithFacebook());

		/*
		 * if (FacebookUtil.getfacebookId(getActivity()) != null &&
		 * !FacebookUtil.getfacebookId(getActivity()).isEmpty()) {
		 * isLinkedFacebook = true;
		 * btnLinkFacebook.setText(R.string.disconnect_my_facebook); }
		 */

		return v;
	}

	private boolean getLinkWithFacebook() {
		boolean linkWithFacebook = prefs.getBooleanPrefs(
				AppPrefs.KEY_LINKK_WITH_FACEBOOK,
				AppPrefs.DEFAULT.LINKK_WITH_FACEBOOK);
		return linkWithFacebook;
	}

	public void setLinkWithFacebook(boolean linkWithFacebook) {

		String linkStr = linkWithFacebook ? getString(R.string.disconnect_my_facebook)
				: getString(R.string.link_my_facebook_account);

		btnLinkFacebook.setText(linkStr);
	}

	OnCheckedChangeListener onCheckChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			switch (buttonView.getId()) {
			case R.id.cbAppearOffline:
				prefs.saveBooleanPrefs(AppPrefs.KEY_APPEAR_OFFLINE, isChecked);
				break;
			case R.id.cbMessageNotification:
				prefs.saveBooleanPrefs(AppPrefs.KEY_MSG_NOTIFICATION, isChecked);
				break;
			}

		}
	};

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
		case R.id.btnLinkFacebook:

			/*
			 * if (isLinkedFacebook) { AccessToken accessToken =
			 * AccessToken.getCurrentAccessToken(); if (accessToken != null) {
			 * LoginManager.getInstance().logOut(); } } else {
			 * 
			 * }
			 */

			final boolean linkWithFacebook = getLinkWithFacebook();
			if (!linkWithFacebook) {
				prefs.saveBooleanPrefs(AppPrefs.KEY_LINKK_WITH_FACEBOOK,
						!linkWithFacebook);
				setLinkWithFacebook(!linkWithFacebook);
			} else {

				new UnlinkFacebookDialog(getActivity())
						.setUnlinkFacebookDialogListener(
								new UnlinkFacebookDialogListener() {

									@Override
									public void onDisconnectClick(
											UnlinkFacebookDialog dialog) {
										// TODO Auto-generated method stub
										prefs.saveBooleanPrefs(
												AppPrefs.KEY_LINKK_WITH_FACEBOOK,
												!linkWithFacebook);
										setLinkWithFacebook(!linkWithFacebook);
										dialog.dismiss();
									}

									@Override
									public void onCancelClick(
											UnlinkFacebookDialog dialog) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								}).show();
			}

			break;
		case R.id.btnChangeEmail:
			intent = new Intent(getActivity(), ChangeEmailActivity.class);
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
					RequestMeetingAdditionActivity.class);
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
							// TODO Auto-generated method stub
							dialog.dismiss();
							// attemptLogout();

							LogoutHelper logoutHelper = new LogoutHelper(
									getActivity());
							logoutHelper.attemptLogout();
						}

						@Override
						public void onCancelClick(LogoutDialog dialog) {
							// TODO Auto-generated method stub
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

		HomeActivity homeActivity = (HomeActivity) getActivity();
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
