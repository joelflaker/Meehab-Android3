package com.citrusbits.meehab.fragments;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.citrusbits.meehab.BlockFriendsActivity;
import com.citrusbits.meehab.ChangeEmailActivity;
import com.citrusbits.meehab.ChangePasswordActivity;
import com.citrusbits.meehab.HomeActivity;
import com.citrusbits.meehab.InsuranceActivity;
import com.citrusbits.meehab.LoginAndRegisterActivity;
import com.citrusbits.meehab.RequestMeetingAdditionActivity;
import com.citrusbits.meehab.ProvideAppFeedBackActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.RehabAdditionActivity;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.dialog.LogoutDialog;
import com.citrusbits.meehab.dialog.LogoutDialog.LogoutDialogClickListener;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.utils.FacebookUtil;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

public class OptionsFragment extends Fragment implements View.OnClickListener {

	UserDatasource userDatasource;
	private Button btnLinkFacebook;
	boolean isLinkedFacebook = false;

	HomeActivity homeActivity;

	Button btnChangeInsurance;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_options, container, false);

		v.findViewById(R.id.topMenuBtn).setOnClickListener(this);
		v.findViewById(R.id.btnChangeEmail).setOnClickListener(this);
		v.findViewById(R.id.btnChangePassword).setOnClickListener(this);
		v.findViewById(R.id.btnChangeInsurance).setOnClickListener(this);
		btnLinkFacebook = (Button) v.findViewById(R.id.btnLinkFacebook);
		btnLinkFacebook.setOnClickListener(this);

		v.findViewById(R.id.requestMeetingAdditionBtn).setOnClickListener(this);
		v.findViewById(R.id.requestRehabAdditionBtn).setOnClickListener(this);
		v.findViewById(R.id.btnBlockFriends).setOnClickListener(this);
		v.findViewById(R.id.btnProvideFeedback).setOnClickListener(this);

		v.findViewById(R.id.rateUsBtn).setOnClickListener(this);
		v.findViewById(R.id.logout).setOnClickListener(this);

		if (FacebookUtil.getfacebookId(getActivity()) != null
				&& !FacebookUtil.getfacebookId(getActivity()).isEmpty()) {
			isLinkedFacebook = true;
			btnLinkFacebook.setText(R.string.disconnect_my_facebook);
		}

		return v;
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
		case R.id.btnLinkFacebook:

			if (isLinkedFacebook) {
				AccessToken accessToken = AccessToken.getCurrentAccessToken();
				if (accessToken != null) {
					LoginManager.getInstance().logOut();
				}
			} else {

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
							attemptLogout();
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
		App.getInstance()
				.getSharedPreferences(Consts.APP_PREFS_NAME,
						Context.MODE_PRIVATE).edit().clear().commit();
		userDatasource.removeAllUsers();
		AppPrefs prefs = AppPrefs.getAppPrefs(getActivity());
		prefs.saveIntegerPrefs(AppPrefs.KEY_USER_ID, AppPrefs.DEFAULT.USER_ID);
		homeActivity.socketService.emit(EventParams.EVENT_USER_LOGOUT,
				new JSONObject(), null);
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
		getActivity().finish();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

}
