package com.citrusbits.meehab;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.adapters.NavDrawerListAdapter;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.dialog.InsuranceDialog;
import com.citrusbits.meehab.dialog.InsuranceDialog.InsuranceDialogClickListener;
import com.citrusbits.meehab.dialog.SocialShareDialog;
import com.citrusbits.meehab.dialog.SocialShareDialog.SocialShareDialogClickListener;
import com.citrusbits.meehab.fragments.FilterResultHolder;
import com.citrusbits.meehab.fragments.FriendsFragment;
import com.citrusbits.meehab.fragments.MeetingsFragment;
import com.citrusbits.meehab.fragments.MessagesFragment;
import com.citrusbits.meehab.fragments.MyFavoritesFragment;
import com.citrusbits.meehab.fragments.MyProfileFragment;
import com.citrusbits.meehab.fragments.OptionsFragment;
import com.citrusbits.meehab.fragments.RecoveryClockFragment;
import com.citrusbits.meehab.fragments.RehabsFragment;
import com.citrusbits.meehab.helpers.LogoutHelper;
import com.citrusbits.meehab.images.PicassoBlurTransform;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.FriendFilterResultHolder;
import com.citrusbits.meehab.model.MeehabShare;
import com.citrusbits.meehab.model.MeetingFilterModel;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.facebook.FacebookSdk;
import com.squareup.picasso.Picasso;

public class HomeActivity extends SocketActivity implements
		OnSocketResponseListener {
	public static final String ACTION_MESSAGE_COUNT_UPDATE = "com.citrusbits.meehab.message.count";
	public static final String ACTION_LOGOUT = "com.citrusbits.meehab.logout";
	public static final String ACTION_PROFILE_UPDATE = "com.citrusbits.meehab.profile_updated";
	DrawerLayout drawer;
	ListView navList;

	private boolean isDrawerOpen = false;
	RelativeLayout rl;

	ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private Fragment mCurrentFragment;
	private UserDatasource userDatasource;
	private UserAccount user;

	ImageView ivUserIcon;
	ImageView ivPictureBig;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_LOGOUT);
		filter.addAction(ACTION_MESSAGE_COUNT_UPDATE);
		filter.addAction(ACTION_PROFILE_UPDATE);
		this.registerReceiver(receiver, filter);
		userDatasource = new UserDatasource(HomeActivity.this);
		userDatasource.open();
		user = userDatasource.findUser(AccountUtils.getUserId(this));

		FacebookSdk.sdkInitialize(this.getApplicationContext());
		setContentView(R.layout.activity_home);
		ivUserIcon = (ImageView) findViewById(R.id.ivUserIcon);
		ivPictureBig = (ImageView) findViewById(R.id.ivPictureBig);
		// drawer
		rl = (RelativeLayout) findViewById(R.id.topDrawer);
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		navList = (ListView) findViewById(R.id.navList);
		navList.setOnItemClickListener(new DrawerItemClickListener());
		String[] navMenuTitles = getResources().getStringArray(
				R.array.drawer_items_titles);
		TypedArray navMenuIcons = getResources().obtainTypedArray(
				R.array.drawer_items_icons);
		navDrawerItems = new ArrayList<NavDrawerItem>();
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
				.getResourceId(0, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
				.getResourceId(1, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
				.getResourceId(2, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons
				.getResourceId(3, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons
				.getResourceId(4, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons
				.getResourceId(5, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons
				.getResourceId(6, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons
				.getResourceId(7, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons
				.getResourceId(8, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons
				.getResourceId(9, -1)));
		navMenuIcons.recycle();
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		navList.setAdapter(adapter);

		// default fragment
		Bundle extra = getIntent().getExtras();
		if (extra != null) {
			if (extra.getString(TwoOptionActivity.M_DEFAULT_FRAGMENT).equals(
					"friends")) {
				// mCurrentFragment = new FriendsFragment();
			//	displayFragment(4);
				mCurrentFragment = new FriendsFragment(this);
			} else {
				mCurrentFragment = new MeetingsFragment(this);
				//displayFragment(3);
			}
		} else {
			mCurrentFragment = new MyProfileFragment(this);
		}
		
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, mCurrentFragment).commit();
		initUser();

		registerReceiver(receiver,
				new IntentFilter(ACTION_MESSAGE_COUNT_UPDATE));

	}

	String userImage;

	public void initUser() {

		userImage = user.getImage();

		TextView tvUserName = (TextView) findViewById(R.id.tvUserName);

		tvUserName.setText(user.getUsername());
		if (userImage==null||userImage.isEmpty()) {

			return;
		}
		userImage = getString(R.string.url) + userImage;

		Picasso.with(this)
				.load(userImage)
				.placeholder(R.drawable.profile_pic_big)
				// .resize(300, 200)
				.error(R.drawable.profile_pic_big)
				.transform(new PicassoBlurTransform(HomeActivity.this, 20))
				.into(ivPictureBig);
		ivUserIcon.setImageResource(R.drawable.profile_pic);
		ivUserIcon.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Picasso.with(HomeActivity.this).load(userImage)
						.placeholder(R.drawable.profile_pic).resize(100, 100)
						.error(R.drawable.profile_pic)
						.transform(new PicassoCircularTransform())
						.into(ivUserIcon);
			}
		}, 1000);

	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			displayFragment(position);
		}
	}

	public void changeDrawerVisibility(boolean open) {
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		isDrawerOpen = open;
		if (open) {
			drawer.openDrawer(rl);
		} else {
			drawer.closeDrawer(rl);
		}
	}

	public boolean isDrawerOpen() {
		return drawer.isDrawerOpen(rl);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		changeDrawerVisibility(false);
		mCurrentFragment=new RehabsFragment(HomeActivity.this);
		switchFragment(mCurrentFragment);
	}

	/**
	 * @param position
	 */

	public boolean isDrawderSkipPosition(int position) {
		return position == 5 || position == 7;
	}

	public void displayFragment(final int position) {

		if (!isDrawderSkipPosition(position)) {
			changeDrawerVisibility(false);
		}

		Fragment fragment = null;
		final android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
		switch (position) {
		case 0:
			fragment = new MyProfileFragment(this);
			setHomeTitle(position);
			break;
		case 1:
			fragment = new MyFavoritesFragment(this);
			setHomeTitle(position);
			// editTopCenter.setHint(R.string.search_for_favorites);
			break;
		case 2:
			fragment = new MessagesFragment(this);
			setHomeTitle(position);
			break;
		case 3:

			fragment = new MeetingsFragment(this);
			setHomeTitle(position);
			// editTopCenter.setHint(R.string.search_for_meetings);
			break;
		case 4:
			fragment = new FriendsFragment(this);
			setHomeTitle(position);
			// editTopCenter.setHint(R.string.search_for_meetings);
			break;

		case 5:
			// fragment = new RehabsFragment(this);
			// setHomeTitle(position);
			// editTopCenter.setHint(R.string.search_for_rehabs);
			new InsuranceDialog(HomeActivity.this).setInsuranceDialogListener(
					new InsuranceDialogClickListener() {

						@Override
						public void onSkipClick(InsuranceDialog dialog) {
							// TODO Auto-generated method stub
							changeDrawerVisibility(false);
							setHomeTitle(position);
							mCurrentFragment=new RehabsFragment(HomeActivity.this);
							fragmentManager
									.beginTransaction()
									.replace(R.id.container,mCurrentFragment)
									.commit();
							dialog.dismiss();
						}

						@Override
						public void onInsuranceClick(InsuranceDialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();

							HomeActivity.this
									.startActivity(new Intent(
											HomeActivity.this,
											InsuranceActivity.class));
							overridePendingTransition(R.anim.activity_in,
									R.anim.activity_out);
							changeDrawerVisibility(false);
						}
					}).show();

			return;

			// break;
		case 6:
			fragment = new RecoveryClockFragment(this);
			setHomeTitle(position);
			break;
		case 7: // Share
			// presentShareList();
			new SocialShareDialog(this).setSocialShareDialogListener(
					new SocialShareDialogClickListener() {

						@Override
						public void onTwitterClick(SocialShareDialog dialog) {
							// TODO Auto-generated method stub

							dialog.dismiss();
							MeehabShare.shareByTwitter(HomeActivity.this);

						}

						@Override
						public void onSMSClick(SocialShareDialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							MeehabShare.shareBySms(HomeActivity.this);
						}

						@Override
						public void onInstagramClick(SocialShareDialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							MeehabShare.shareByInstragram(HomeActivity.this);
						}

						@Override
						public void onFacebookClick(SocialShareDialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							MeehabShare.shareByFacebook(HomeActivity.this);
						}

						@Override
						public void onEmailClick(SocialShareDialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							MeehabShare.shareByEmail(HomeActivity.this);
						}

						@Override
						public void onCancelClick(SocialShareDialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();

						}
					}).show();
			break;
		case 8: // Big Book
			Intent i = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://www.google.com"));
			startActivity(i);
			break;
		case 9:
			fragment = new OptionsFragment(this);
			setHomeTitle(position);
			break;

		default:
			changeDrawerVisibility(false);
			break;
		}

		// current fragment
		if (fragment != null) {
			mCurrentFragment = fragment;
		} else {
			fragment = mCurrentFragment;
		}
		fragmentManager.beginTransaction().replace(R.id.container, fragment)
				.commit();
	}

	public void switchFragment(Fragment fragment) {
		android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, fragment)
				.commit();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		// mCurrentFragment.onActivityResult(arg0, arg1, arg2);
	}

	/**
	 * present user with sharing menu
	 */
	private void presentShareList() {
		final String[] options = { "By SMS", "By Email", "By Facebook",
				"By Twitter", "By Intagram", "Cancel" };

		ArrayAdapter<String> cuteAdapter = new ArrayAdapter<String>(
				getApplicationContext(), android.R.layout.simple_list_item_1,
				options) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = ((LayoutInflater) HomeActivity.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
							.inflate(android.R.layout.simple_list_item_1, null);
				}
				Log.d("", "" + position);
				TextView tv = (TextView) convertView
						.findViewById(android.R.id.text1);
				tv.setText(options[position]);
				tv.setGravity(Gravity.CENTER_VERTICAL
						| Gravity.CENTER_HORIZONTAL);
				return convertView;
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setAdapter(cuteAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();
				switch (item) {
				case 0:
					MeehabShare.shareBySms(HomeActivity.this);
					break;

				case 1:
					MeehabShare.shareByEmail(HomeActivity.this);
					break;
				case 2:
					MeehabShare.shareByFacebook(HomeActivity.this);
					break;
				case 3:
					MeehabShare.shareByTwitter(HomeActivity.this);
					break;
				case 4:
					MeehabShare.shareByInstragram(HomeActivity.this);
					break;
				case 5:

					break;

				default:
					break;
				}
			}
		});

		builder.show();
	}

	/**
	 * @param string
	 */
	protected void toast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		MeetingsFragment.resultHolder = new FilterResultHolder();
		FriendsFragment.fFilterResultHolder=new FriendFilterResultHolder();
		MeetingsFilterActivity.applyClear();
		FriendsFilterActivity.applyClear();

		this.unregisterReceiver(receiver);
	}

	/**
	 * @param position
	 */
	private void setHomeTitle(int position) {
		setTitle(navDrawerItems.get(position).getTitle());
		navList.setItemChecked(position, true);
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		synchronized (mCurrentFragment) {
			if (mCurrentFragment instanceof OnSocketResponseListener) {
				((OnSocketResponseListener) mCurrentFragment)
						.onSocketResponseSuccess(event, obj);
			}
		}
	}

	@Override
	void onBackendConnected() {
		synchronized (mCurrentFragment) {
			if (mCurrentFragment instanceof OnBackendConnectListener) {
				((OnBackendConnectListener) mCurrentFragment)
						.onBackendConnected();
			}
		}
	};

	@Override
	public void onSocketResponseFailure(String message) {
		synchronized (mCurrentFragment) {
			if (mCurrentFragment instanceof OnSocketResponseListener) {
				((OnSocketResponseListener) mCurrentFragment)
						.onSocketResponseFailure(message);
			}
		}
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(ACTION_LOGOUT)) {
				LogoutHelper logoutHelper = new LogoutHelper(HomeActivity.this);
				logoutHelper.attemptLogout();
			} else if (intent.getAction().equals(ACTION_MESSAGE_COUNT_UPDATE)) {
				if (adapter != null) {
					adapter.updateUnreadMessageCount();
				}
			} else if (intent.getAction().equals(ACTION_PROFILE_UPDATE)) {
				user = userDatasource.findUser(AccountUtils
						.getUserId(HomeActivity.this));
				initUser();
			}

		}
	};

}
