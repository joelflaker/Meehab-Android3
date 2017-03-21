package com.citrusbits.meehab.ui;

import java.util.ArrayList;
import java.util.Calendar;

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
import android.text.TextUtils;
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

import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.ui.users.ChatActivity;
import com.citrusbits.meehab.ui.users.FriendsFilterActivity;
import com.citrusbits.meehab.ui.meetings.MeetingsFilterActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.adapters.NavDrawerListAdapter;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.ui.dialog.InsuranceDialog;
import com.citrusbits.meehab.ui.dialog.InsuranceDialog.InsuranceDialogClickListener;
import com.citrusbits.meehab.ui.dialog.SocialShareDialog;
import com.citrusbits.meehab.ui.dialog.SocialShareDialog.SocialShareDialogClickListener;
import com.citrusbits.meehab.ui.fragments.FilterResultHolder;
import com.citrusbits.meehab.ui.fragments.FriendsFragment;
import com.citrusbits.meehab.ui.fragments.MeetingsFragment;
import com.citrusbits.meehab.ui.fragments.MessagesFragment;
import com.citrusbits.meehab.ui.fragments.MyFavoritesFragment;
import com.citrusbits.meehab.ui.fragments.MyProfileFragment;
import com.citrusbits.meehab.ui.fragments.OptionsFragment;
import com.citrusbits.meehab.ui.fragments.RecoveryClockFragment;
import com.citrusbits.meehab.ui.fragments.RehabsFragment;
import com.citrusbits.meehab.helpers.LogoutHelper;
import com.citrusbits.meehab.images.PicassoBlurTransform;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.FriendFilterResultHolder;
import com.citrusbits.meehab.model.MeehabShare;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.facebook.FacebookSdk;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

@SuppressWarnings("ResourceType")
public class HomeActivity extends SocketActivity implements
		OnSocketResponseListener {
	public static final String ACTION_MESSAGE_COUNT_UPDATE = "com.citrusbits.meehab.message.count";
	public static final String ACTION_LOGOUT = "com.citrusbits.meehab.logout";
	public static final String ACTION_PROFILE_UPDATE = "com.citrusbits.meehab.profile_updated";
	private static final String CURRENT_FRAGMENT_TAG = "currentFragment";

	DrawerLayout drawer;
	ListView navList;

	private boolean isDrawerOpen = false;
	RelativeLayout rl;

	ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private Fragment mCurrentFragment;
	private UserDatasource userDatasource;
	private UserAccount mUser;

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
		mUser = userDatasource.findUser(AccountUtils.getUserId(this));

		FacebookSdk.sdkInitialize(this.getApplicationContext());
		setContentView(R.layout.activity_home);
		ivUserIcon = (ImageView) findViewById(R.id.ivUserIcon);
		ivPictureBig = (ImageView) findViewById(R.id.ivPictureBig);
		// drawer
		rl = (RelativeLayout) findViewById(R.id.topDrawer);
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		navList = (ListView) findViewById(R.id.navList);

		findViewById(R.id.topMenuProfile).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				displayFragment(0);
			}
		});
		navList.setOnItemClickListener(new DrawerItemClickListener());
		String[] navMenuTitles = getResources().getStringArray(
				R.array.drawer_items_titles);
		TypedArray navMenuIcons = getResources().obtainTypedArray(
				R.array.drawer_items_icons);
		navDrawerItems = new ArrayList<>();
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(8, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1)));
		navMenuIcons.recycle();
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		navList.setAdapter(adapter);

		// default fragment
		Bundle extra = getIntent().getExtras();
		if (extra != null) {
			if (extra.containsKey(TwoOptionActivity.M_DEFAULT_FRAGMENT) && extra.getString(TwoOptionActivity.M_DEFAULT_FRAGMENT).equals(
					"friends")) {
				// mCurrentFragment = new FriendsFragment();
			//	displayFragment(4);
				mCurrentFragment = new FriendsFragment();
			} else if (extra.containsKey(ChatActivity.KEY_PUSH_CHAT)){
				
				mCurrentFragment = new MessagesFragment();
//				((MessagesFragment)mCurrentFragment).setPushExtra(extra);
			}else{
				mCurrentFragment = new MeetingsFragment();
//				mCurrentFragment = new RehabsFragment();
//				mCurrentFragment = new MyFavoritesFragment(this);
				//displayFragment(3);
			}
		} else {
			mCurrentFragment = new MyProfileFragment();
		}
		
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, mCurrentFragment).commit();
		initUser();

		registerReceiver(receiver,
				new IntentFilter(ACTION_MESSAGE_COUNT_UPDATE));

	}


	public void initUser() {
		
		if(mUser == null) return;

		final String userImage = mUser.getImage();

		TextView tvUserName = (TextView) findViewById(R.id.tvUserName);

		tvUserName.setText(mUser.getUsername());

		Picasso.with(this)
				.load(R.drawable.img_place_holder)
				.transform(new PicassoBlurTransform(HomeActivity.this, Consts.IMAGE_BLURR_RADIUS))
				.into(ivPictureBig);

		ivUserIcon.setImageResource(R.drawable.profile_pic_border);
		if(!TextUtils.isEmpty(userImage)) {

			Picasso.with(this)
					.load(userImage)
					.placeholder(R.drawable.img_place_holder)
					// .resize(300, 200)
					.error(R.drawable.img_place_holder)
					.transform(new PicassoBlurTransform(HomeActivity.this, Consts.IMAGE_BLURR_RADIUS))
					.into(ivPictureBig);

			ivUserIcon.postDelayed(new Runnable() {

				@Override
				public void run() {
					Picasso.with(HomeActivity.this).load(userImage)
							.placeholder(R.drawable.profile_pic_border)
							.error(R.drawable.profile_pic_border)
							.transform(new PicassoCircularTransform())
							.into(ivUserIcon);
				}
			}, 1000);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (adapter != null) {
			adapter.updateUnreadMessageCount();
		}
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
					.getSystemService(INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}

		if(mUser == null) {
			new LogoutHelper(HomeActivity.this)
					.attemptLogout();
			return;
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
		super.onNewIntent(intent);
		changeDrawerVisibility(false);
		mCurrentFragment = new RehabsFragment();
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
			fragment = new MyProfileFragment();
			setHomeTitle(position);
			break;
		case 1:
			fragment = new MyFavoritesFragment();
			setHomeTitle(position);
			// editTopCenter.setHint(R.string.search_for_favorites);
			break;
		case 2:
			fragment = new MessagesFragment();
			setHomeTitle(position);
			break;
		case 3:

			fragment = new MeetingsFragment();
			setHomeTitle(position);
			// editTopCenter.setHint(R.string.search_for_meetings);
			break;
		case 4:
			fragment = new FriendsFragment();
			setHomeTitle(position);
			// editTopCenter.setHint(R.string.search_for_meetings);
			break;

		case 5:
			// fragment = new RehabsFragment(this);
			// setHomeTitle(position);
			// editTopCenter.setHint(R.string.search_for_rehabs);
			mUser = userDatasource.findUser(AccountUtils.getUserId(this));


			if(TextUtils.isEmpty(mUser.getInsurance()) /*|| "No Insurance".equals(mUser.getInsurance())*/){
				long oldCheck = AccountUtils.getTime(this);
				boolean isNoInsuranceSelected = oldCheck != 0;
				long diff = Calendar.getInstance().getTimeInMillis() - oldCheck;

				boolean shouldShowAlert = true;
				if(isNoInsuranceSelected && diff >= 1.21e+9){
					//reset time
					AccountUtils.setTime(this,Calendar.getInstance().getTimeInMillis());
				}else if(isNoInsuranceSelected){
					shouldShowAlert = false;
				}
				if(shouldShowAlert) {
					new InsuranceDialog(HomeActivity.this).setInsuranceDialogListener(
							new InsuranceDialogClickListener() {

								@Override
								public void onSkipClick(InsuranceDialog dialog) {
									changeDrawerVisibility(false);
									setHomeTitle(position);
									mCurrentFragment = new RehabsFragment();
									fragmentManager
											.beginTransaction()
											.replace(R.id.container, mCurrentFragment)
											.commit();
									dialog.dismiss();
								}

								@Override
								public void onInsuranceClick(InsuranceDialog dialog) {
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
				}
			}
			changeDrawerVisibility(false);
			mCurrentFragment = new RehabsFragment();
			fragmentManager
					.beginTransaction()
					.replace(R.id.container, mCurrentFragment)
					.commit();
			return;
		case 6:
			fragment = new RecoveryClockFragment();
			setHomeTitle(position);
			break;
		case 7: // Share
			// presentShareList();
			new SocialShareDialog(this).setSocialShareDialogListener(
					new SocialShareDialogClickListener() {

						@Override
						public void onTwitterClick(SocialShareDialog dialog) {
							dialog.dismiss();
							MeehabShare.shareByTwitter(HomeActivity.this);

						}

						@Override
						public void onSMSClick(SocialShareDialog dialog) {
							dialog.dismiss();
							MeehabShare.shareBySms(HomeActivity.this);
						}

						@Override
						public void onInstagramClick(SocialShareDialog dialog) {
							dialog.dismiss();
							MeehabShare.shareByInstragram(HomeActivity.this);
						}

						@Override
						public void onFacebookClick(SocialShareDialog dialog) {
							dialog.dismiss();
							MeehabShare.shareByFacebook(HomeActivity.this);
						}

						@Override
						public void onEmailClick(SocialShareDialog dialog) {
							dialog.dismiss();
							MeehabShare.shareByEmail(HomeActivity.this);
						}

						@Override
						public void onCancelClick(SocialShareDialog dialog) {
							dialog.dismiss();

						}
					}).show();
			break;
		case 8: // Big Book
			if (NetworkUtils.isNetworkAvailable(this)) {
				socketService.getBigBookLink();
			}else {
				Toast.makeText(this,
						getString(R.string.no_internet_connection),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 9: // Settings
			fragment = new OptionsFragment();
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
		fragmentManager.beginTransaction().replace(R.id.container, fragment, CURRENT_FRAGMENT_TAG)
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
	 * present mUser with sharing menu
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
							.getSystemService(LAYOUT_INFLATER_SERVICE))
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

		MeetingsFragment.resultHolder = new FilterResultHolder();
		FriendsFragment.fFilterResultHolder=new FriendFilterResultHolder();
		MeetingsFilterActivity.applyClear();
		FriendsFilterActivity.applyClear();

		this.unregisterReceiver(receiver);
		super.onDestroy();
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
		if (EventParams.METHOD_BIG_BOOK.equals(event)) {
			//open link

			JSONObject data = ((JSONObject) obj);

			String url = data.optString("bigBook");

			if(!TextUtils.isEmpty(url)){
				Intent i = new Intent(Intent.ACTION_VIEW,
						Uri.parse(url));
				startActivity(i);
			}
		}else {
			synchronized (mCurrentFragment) {
				if (mCurrentFragment instanceof OnSocketResponseListener) {
					((OnSocketResponseListener) mCurrentFragment)
							.onSocketResponseSuccess(event, obj);
				}
			}
		}
	}

	@Override
	public void onSocketResponseFailure(String event,String message) {
		if (EventParams.METHOD_BIG_BOOK.equals(event)) {

		}else {
			synchronized (mCurrentFragment) {
				if (mCurrentFragment instanceof OnSocketResponseListener) {
					((OnSocketResponseListener) mCurrentFragment)
							.onSocketResponseFailure(event, message);
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		if(isDrawerOpen){
			changeDrawerVisibility(false);
			return;
		}
		
		super.onBackPressed();
	}
	@Override
	public void onBackendConnected() {
		synchronized (mCurrentFragment) {
			if (mCurrentFragment instanceof OnBackendConnectListener) {
				((OnBackendConnectListener) mCurrentFragment)
						.onBackendConnected();
			}
		}
	};

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_LOGOUT)) {
				LogoutHelper logoutHelper = new LogoutHelper(HomeActivity.this);
				logoutHelper.attemptLogout();
			} else if (intent.getAction().equals(ACTION_MESSAGE_COUNT_UPDATE)) {
				if (adapter != null) {
					adapter.updateUnreadMessageCount();
				}
			} else if (intent.getAction().equals(ACTION_PROFILE_UPDATE)) {
				mUser = userDatasource.findUser(AccountUtils
						.getUserId(HomeActivity.this));
				initUser();
			}

		}
	};

}
