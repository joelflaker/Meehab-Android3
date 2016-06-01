package com.citrusbits.meehab.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.ls.LSInput;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.citrusbits.meehab.HomeActivity;
import com.citrusbits.meehab.MeetingDetailsActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.UserProfileActivity;
import com.citrusbits.meehab.adapters.FavFriendsGridAdapter;
import com.citrusbits.meehab.adapters.FriendsGridAdapter;
import com.citrusbits.meehab.adapters.FavMeetingListAdapter;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.customviews.StillViewPager;
import com.citrusbits.meehab.map.LocationService;
import com.citrusbits.meehab.map.LocationService.LocationListener;
import com.citrusbits.meehab.map.LocationService.MyLocalBinder;
import com.citrusbits.meehab.map.LocationUtils;
import com.citrusbits.meehab.model.GetFriendsResponse;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.model.MessageModel;
import com.citrusbits.meehab.model.MeetingModel.MarkerColorType;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.pojo.MeetingResponse;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.MeetingUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.gson.Gson;

public class MyFavoritesFragment extends Fragment implements
		View.OnClickListener, OnSocketResponseListener {

	private ChildFragmentAdapter mPagerAdapter;
	private ViewPager mPager;
	static GridView grid;
	static ListView list;
	private static boolean listIsEmpty = true;
	private static boolean gridIsEmpty = true;

	FavMeetingListAdapter meetingsAdapter;

	ArrayList<MeetingModel> meetings = new ArrayList<MeetingModel>();

	FavFriendsGridAdapter friendsGridAdapter;
	private Button btnMeetings, btnFriends;
	private boolean isMeetingsEdit = false;
	private boolean isFriendsEdit = false;
	private ViewGroup containerRemove;
	private ImageButton ibRemove;
	private int mCurrentFragmentPosition = 0;
	private HomeActivity homeActivity;
	private ImageButton ibEdit;
	private EditText etFavouriteSearch;
	private View focus_thief;

	private ImageView ivMeetingBar;
	private ImageView ivFriendsBar;

	private Dialog pd;

	private boolean meetingUpdated = false;

	private Location myLocation;

	LocationService locationService;

	EditText editTopCenter;

	MeetingProcessingTask meetingProcessinTask;

	private Context mContext;

	List<UserAccount> userAccounts = new ArrayList<UserAccount>();

	List<UserAccount> userAccountsCache = new ArrayList<UserAccount>();

	private int currentTabPosition;
	
	protected int mReqPosition;
	protected int mAccountPosition;

	public MyFavoritesFragment() {
	}

	ServiceConnection locServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			locationService = ((MyLocalBinder) service).getService();
			locationService.addListener(locationListener);
		}
	};

	LocationListener locationListener = new LocationListener() {

		@Override
		public void onChangeLocation(Location location) {
			myLocation = location;
		}
	};
	
	public void onDestroy() {
		grid = null;
		list = null;
		super.onDestroy();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		this.homeActivity = (HomeActivity) getActivity();
		pd = UtilityClass.getProgressDialog(homeActivity);

		myLocation = LocationUtils.getLastLocation(getActivity());
		Intent i = new Intent(mContext, LocationService.class);
		mContext.bindService(i, locServiceConnection, Context.BIND_AUTO_CREATE);
		mContext.startService(i);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_favorites, container,
				false);

		view.findViewById(R.id.ivMenu).setOnClickListener(this);
		ivMeetingBar = (ImageView) view.findViewById(R.id.ivMeetingBar);
		ivFriendsBar = (ImageView) view.findViewById(R.id.ivFriendsBar);

		ibEdit = (ImageButton) view.findViewById(R.id.ibEdit);
		editTopCenter = (EditText) view.findViewById(R.id.editTopCenter);

		editTopCenter.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String inputText = s.toString().trim().toLowerCase();

				if (currentTabPosition == 1) {
					userAccounts.clear();
					for (UserAccount account : userAccountsCache) {
						String name = account.getUsername();
						if (name.toLowerCase().contains(inputText)) {
							userAccounts.add(account);
						}
					}

					friendsGridAdapter.notifyDataSetChanged();
				} else {
					meetingsAdapter.filter(inputText);
				}

			}
		});

		mPager = (ViewPager) view.findViewById(R.id.pager);
		mPager.setOffscreenPageLimit(0);
		btnMeetings = (Button) view.findViewById(R.id.btnMeetings);
		btnFriends = (Button) view.findViewById(R.id.btnFriends);
		containerRemove = (ViewGroup) view.findViewById(R.id.containerRemove);
		ibRemove = (ImageButton) view.findViewById(R.id.ibRemove);

		ibEdit.setOnClickListener(this);
		ibRemove.setOnClickListener(this);
		btnMeetings.setOnClickListener(this);
		btnFriends.setOnClickListener(this);

		list = new ListView(getActivity());
		grid = new GridView(getActivity());
		list.setOnItemClickListener(listeItemClickListener);
		grid.setOnItemClickListener(gridItemClickListener);

		grid.setLayoutParams(new GridView.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		
		int width = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 100, getActivity().getResources()
						.getDisplayMetrics());

		int grid_spacing = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 10, getActivity().getResources()
						.getDisplayMetrics());
		
		grid.setPadding(grid_spacing, grid_spacing, grid_spacing, grid_spacing);
		grid.setHorizontalSpacing(grid_spacing);
		grid.setVerticalSpacing(grid_spacing);
		
//		grid.setBackgroundColor(Color.WHITE);
		grid.setNumColumns(3);
		grid.setColumnWidth(GridView.AUTO_FIT);
		grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

		// load fav meetings

		// init friends adapter
		friendsGridAdapter = new FavFriendsGridAdapter(getActivity(),
				R.layout.grid_item_friend, userAccounts);

		meetingsAdapter = new FavMeetingListAdapter(getActivity(),
				R.layout.list_item_meeting, meetings);
		list.setAdapter(meetingsAdapter);
		list.setDividerHeight(0);

		grid.setAdapter(friendsGridAdapter);
		friendsGridAdapter.notifyDataSetChanged();

		mPagerAdapter = new ChildFragmentAdapter(getChildFragmentManager());

		mPager.setAdapter(mPagerAdapter);

		mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// setUIButtonText(arg0);
				editTopCenter.setText("");
				changeTab(arg0);
				currentTabPosition = arg0;
				setRemoveResource();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

		});

		changeTab(0);

		refreshMeetingList();

		getFriends();

		return view;
	}

	OnItemClickListener listeItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long id) {
			if (!meetingsAdapter.getEdit()) {
				Intent intent = new Intent(getActivity(), MeetingDetailsActivity.class);
				Bundle bundle = new Bundle();

				bundle.putSerializable("meeting", meetings.get(position));

				intent.putExtras(bundle);

				// getActivity().startActivity(intent);
				mReqPosition = position;
				// Toast.makeText(mContext, "Request Position is "+mReqPosition,
				// Toast.LENGTH_SHORT).show();
				startActivityForResult(intent, MeetingsFragment.REQUEST_MEETING_DETAILS);
				getActivity().overridePendingTransition(R.anim.activity_in,
						R.anim.activity_out);
				return;
			}
			meetings.get(position).setChecked(!meetings.get(position).isChecked());
			meetingsAdapter.notifyDataSetChanged();
			setRemoveResource();

		}
	};

	OnItemClickListener gridItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View v, int position,
				long id) {
			if (!friendsGridAdapter.isEdit()) {
				mAccountPosition = position;
				UserAccount account = userAccounts.get(position);

				Intent intent = new Intent(getActivity(), UserProfileActivity.class);
				intent.putExtra(UserProfileActivity.EXTRA_USER_ACCOUNT, account);

				// put friend
				startActivityForResult(intent, FriendsFragment.DETAIL_REQUEST);
				getActivity().overridePendingTransition(R.anim.activity_in,
						R.anim.activity_out);
				return;
			}
			userAccounts.get(position).setChecked(
					!userAccounts.get(position).isChecked());

			friendsGridAdapter.notifyDataSetChanged();

			setRemoveResource();

		}
	};

	public void startDetailActivity(int position) {
		
	}
	
	public void setRemoveResource() {
		switch (currentTabPosition) {
		case 0:
			ibRemove.setImageResource(isMeetingChecked() ? R.drawable.remove_btn_selected
					: R.drawable.remove_btn);
			break;
		case 1:
			ibRemove.setImageResource(isUserChecked() ? R.drawable.remove_btn_selected
					: R.drawable.remove_btn);
			break;
		}
	}

	private String getMeetingIds() {
		String meetingIds = "";
		for (MeetingModel m : meetings) {
			if (m.isChecked()) {
				if (!meetingIds.isEmpty()) {
					meetingIds = meetingIds + ",";
				}
				meetingIds = meetingIds + m.getMeetingId();
			}
		}

		return meetingIds;
	}

	private String getUserIds() {
		String userIds = "";
		for (UserAccount user : userAccounts) {
			if (user.isChecked()) {
				if (!userIds.isEmpty()) {
					userIds = userIds + ",";
				}
				userIds = userIds + user.getId();
			}
		}

		return userIds;
	}

	private boolean isMeetingChecked() {
		for (MeetingModel m : meetings) {
			if (m.isChecked()) {
				return true;
			}
		}

		return false;
	}

	private boolean isUserChecked() {
		for (UserAccount user : userAccounts) {
			if (user.isChecked()) {
				return true;
			}
		}

		return false;
	}

	public void unfavoriteMeetings(String meeting_ids) {

		if (!NetworkUtils.isNetworkAvailable(mContext)) {
			Toast.makeText(mContext,
					mContext.getString(R.string.no_internet_connection),
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (homeActivity.socketService == null) {
			return;
		}
		try {
			pd.show();

			JSONObject params = new JSONObject();
			params.put("meeting_ids", meeting_ids);
			params.put("favorite", "0");
			// params.put("userid",AccountUtils.getUserId(mContext));
			homeActivity.socketService.addUserFavourite(params);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void unFavoriteUsers(String friendIds) {

		if (!NetworkUtils.isNetworkAvailable(mContext)) {
			Toast.makeText(mContext,
					mContext.getString(R.string.no_internet_connection),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (homeActivity.socketService != null) {
			try {
				pd.show();
				JSONObject object = new JSONObject();
				object.put("friend_ids", friendIds);
				object.put("favorite", 0);
				Log.i("data", object.toString());
				homeActivity.socketService.favourteUser(object);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	public void getFriends() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}
		if (homeActivity.socketService != null) {

			JSONObject object = new JSONObject();

			try {
				object.put("user_id", AccountUtils.getUserId(getActivity()));
				object.put("device_id", DeviceUtils.getDeviceId(homeActivity));
				object.put("type", "favorite");
				Log.e("json send ", object.toString());
				homeActivity.socketService.getAllFriends(object);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	void refreshMeetingList() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}
		if (homeActivity.socketService != null) {
			pd.show();
			JSONObject json = new JSONObject();
			try {
				json.put("type", "favorite");
				homeActivity.socketService.getMeeting(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	public void changeTab(int position) {

		switch (position) {
		case 0:
			btnMeetings.setEnabled(false);
			btnFriends.setEnabled(true);
			ivMeetingBar.setVisibility(View.VISIBLE);
			ivFriendsBar.setVisibility(View.INVISIBLE);
			break;
		case 1:
			btnMeetings.setEnabled(true);
			btnFriends.setEnabled(false);
			ivMeetingBar.setVisibility(View.INVISIBLE);
			ivFriendsBar.setVisibility(View.VISIBLE);
			break;
		}

		editTopCenter.setText("");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivMenu:
			if (homeActivity.isDrawerOpen()) {
				homeActivity.changeDrawerVisibility(false);
			} else {
				homeActivity.changeDrawerVisibility(true);
			}
			break;
		case R.id.btnMeetings:
			mPager.setCurrentItem(0);
			break;
		case R.id.btnFriends:
			mPager.setCurrentItem(1);
			break;

		case R.id.ibRemove:

			switch (currentTabPosition) {
			case 0:
				String meetingIds = getMeetingIds();
				if (meetingIds.isEmpty()) {
					return;
				}

				unfavoriteMeetings(meetingIds);
				break;
			case 1:
				String userIds = getUserIds();
				if (userIds.isEmpty()) {
					return;
				}

				unFavoriteUsers(userIds);

				break;
			}

			break;
		case R.id.ibEdit:

			meetingsAdapter.setEdit(!meetingsAdapter.getEdit());
			friendsGridAdapter.setEdit(!friendsGridAdapter.isEdit());

			if (!friendsGridAdapter.isEdit()) {
				unCheckAll();
			}
			meetingsAdapter.notifyDataSetChanged();
			friendsGridAdapter.notifyDataSetChanged();

			int editResId = meetingsAdapter.getEdit() ? R.drawable.cancel_btn_small
					: R.drawable.edit_btn;

			ibEdit.setImageResource(editResId);

			ibRemove.setVisibility(meetingsAdapter.getEdit() ? View.VISIBLE
					: View.GONE);

			break;

		default:
			break;
		}
	}

	public void unCheckAll() {
		for (MeetingModel m : meetings) {
			if (m.isChecked()) {
				m.setChecked(false);
			}
		}

		for (UserAccount accout : userAccounts) {
			if (accout.isChecked()) {
				accout.setChecked(false);
			}
		}
	}

	/**
	 * 
	 */

	/**
	 * @param string
	 */
	protected void toast(String string) {
		Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
	}

	public  class ChildFragmentAdapter extends FragmentStatePagerAdapter {
		public ChildFragmentAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return 2;
		}
		
		@Override
	    public int getItemPosition(Object object) {
	        return POSITION_NONE;
	    }

		@Override
		public CharSequence getPageTitle(int position) {
			return super.getPageTitle(position);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0: // Fragment # 0 - This will show image
				return new MeetingsListFragment();
			case 1: // Fragment # 1 - This will show image
				return new FriendsGridFragment();
			default:// Fragment # 2-9 - Will show list
				return new MeetingsListFragment();
			}
		}
	}

	public  static class FriendsGridFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_fav_friends, container,
					false);
			if(gridIsEmpty){
				return v;
			}else{
				return grid;
			}
		}
	}

	public static class MeetingsListFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_fav_meetings, container,
					false);

			if(listIsEmpty){
				return v;
			}else{
				return list;
			}
		}
	}

	public void updateEmptyViews() {
		boolean needUpdate = listIsEmpty != (meetingsAdapter.getCount() == 0)
				|| gridIsEmpty != (friendsGridAdapter.getCount() == 0);
		
		listIsEmpty = meetingsAdapter.getCount() == 0;
		gridIsEmpty = friendsGridAdapter.getCount() == 0;
		if(needUpdate)
		mPagerAdapter.notifyDataSetChanged();
		
		
	}
	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		pd.dismiss();
		// Location myLocation=map.getMyLocation();
		if (event.equals(EventParams.EVENT_MEETING_GET_ALL)) {

			meetingUpdated = true;
			try {
				JSONObject data = ((JSONObject) obj);

				Log.e("Meeting data", data.toString());

				Gson gson = new Gson();
				MeetingResponse response = gson.fromJson(data.toString(),
						MeetingResponse.class);
				meetings = (ArrayList<MeetingModel>) response
						.getGetAllMeetings();

				meetingProcessinTask = new MeetingProcessingTask();
				meetingProcessinTask.execute();

			} catch (Exception e) {

			}

			// refrshFavList();

		} else if (event.equals(EventParams.METHOD_GET_ALL_FRIENDS)) {
			userAccounts.clear();
			Gson gson = new Gson();
			JSONObject data = (JSONObject) obj;
			Log.e("All Friends data is ", obj.toString());
			GetFriendsResponse response = gson.fromJson(data.toString(),
					GetFriendsResponse.class);
			List<UserAccount> friends = response.getFriends();
			Log.e("Friends data is ", data.toString());
			for (int i = 0; i < friends.size(); i++) {

				Log.e("User name ", friends.get(i).getUsername());

				friends.get(i).setAge(
						MeetingUtils.calculateAge(friends.get(i)
								.getDateOfBirth()));
				UserAccount user = friends.get(i);

			}

			userAccounts.addAll(friends);
			userAccountsCache.addAll(friends);
			friendsGridAdapter.notifyDataSetChanged();
			updateEmptyViews();
			/*
			 * Toast.makeText(homeActivity, "Friends data is " +
			 * data.toString(), Toast.LENGTH_SHORT).show();
			 */
		} else if (event.equals(EventParams.EVENT_ADD_USER_FAVOURITE)) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
			List<MeetingModel> meetingArra = meetingsAdapter.getMeetingArray();
			for (int i = 0; i < meetings.size(); i++) {
				MeetingModel m = meetings.get(i);
				if (m.isChecked()) {

					meetings.remove(i);

					for (MeetingModel mm : meetingArra) {
						if (mm.getId() == m.getId()) {
							meetingArra.remove(mm);
							break;
						}
					}

					i--;
				}
			}

			meetingsAdapter.notifyDataSetChanged();
			updateEmptyViews();

		} else if (event.equals(EventParams.METHOD_FAVOURITE_USER)) {
			if (pd != null && pd.isShowing()) {
				pd.dismiss();
			}
			for (int i = 0; i < userAccounts.size(); i++) {
				UserAccount account = userAccounts.get(i);
				if (account.isChecked()) {
					userAccounts.remove(i);
					for (UserAccount acc : userAccountsCache) {
						if (acc.getId() == account.getId()) {
							userAccountsCache.remove(acc);
							break;
						}
					}
					i--;
				}
			}

			friendsGridAdapter.notifyDataSetChanged();
			updateEmptyViews();

		}

	}

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		pd.dismiss();
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
	}

	public class MeetingProcessingTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			if (meetings.size() > 0) {

				for (int i = 0; i < meetings.size(); i++) {
					MeetingModel meeting = meetings.get(i);
					meeting.setFavourite(meeting.getFavouriteMeeting() == 1);

					NearesDateTime nearDateTime = getOnDate(meeting.getOnDay(),
							meeting.getOnTime(), i);

					/*
					 * String onDate = getOnDate(meeting.getOnDay(),
					 * meeting.getOnTime());
					 */

					String onDate = nearDateTime.getDate();

					meeting.setOnDateOrigin(onDate);

					meeting.setNearestTime(nearDateTime.getTime());

					Location pinLocation = new Location("B");
					pinLocation.setLatitude(meeting.getLatitude());
					pinLocation.setLongitude(meeting.getLongitude());
					double distance = (double) (myLocation
							.distanceTo(pinLocation) * 0.000621371192f);

					distance = Math.floor(distance * 100) / 100f;
					meeting.setDistanceInMiles(distance);

					Date dateObje = MeetingUtils.getDateObject(onDate);
					meeting.setDateObj(dateObje);
					meeting.setOnDate(MeetingUtils.formateDate(dateObje));
					// }

					MeetingUtils.setStartInTime(meeting, meeting.getOnDateOrigion(),
							meeting.getNearestTime());
				}

				sortData();
				String prevDate = "";
				for (int i = 0; i < meetings.size(); i++) {
					MeetingModel m = meetings.get(i);
					if (m.getOnDateOrigion() == null) {
						continue;
					}
					if (!prevDate.equals(m.getOnDateOrigion())) {
						m.setDateHeaderVisibility(true);
						prevDate = m.getOnDateOrigion();
					}
				}
			}

			return null;
		}

//		public void setStartInTime(MeetingModel model, String date,
//				String onTime) {
//			SimpleDateFormat prevFormate = new SimpleDateFormat("dd/MM/yyyy");
//			try {
//				Date date2 = prevFormate.parse(date);
//				SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
//				final Date dateObj = _12HourSDF.parse(onTime);
//				Calendar calendar = Calendar.getInstance();
//
//				calendar.setTime(date2);
//
//				calendar.set(Calendar.HOUR_OF_DAY, dateObj.getHours());
//				calendar.set(Calendar.MINUTE, dateObj.getMinutes());
//				calendar.set(Calendar.SECOND, 0);
//
//				Calendar currentCalendar = Calendar.getInstance();
//
//				long difference = calendar.getTimeInMillis()
//						- currentCalendar.getTimeInMillis();
//
//				long x = difference / 1000;
//				Log.e("Difference ", x + "");
//
//				long days = x / (60 * 60 * 24);
//
//				long seconds = x % 60;
//				x /= 60;
//				long minutes = x % 60;
//				x /= 60;
//				long hours = x % 24;
//
//				Log.e("Hours Difference ", hours + ":" + minutes);
//				SimpleDateFormat mmmDate = new SimpleDateFormat(
//						"dd/MM/yyyy hh:mm a");
//				String mDate = mmmDate.format(calendar.getTime());
//				String mNow = mmmDate.format(currentCalendar.getTime());
//				Log.e("Meeting date time ", mDate);
//				Log.e("Nnow date time ", mNow);
//
//				if (days > 0) {
//					model.setMarkerTypeColor(MarkerColorType.GREEN);
//					model.setStartInTime("AFTER " + days + " "
//							+ (days == 1 ? "DAY" : "DAYS"));
//				} else if (hours > 1 || hours == 1 && minutes > 0) {
//					model.setMarkerTypeColor(MarkerColorType.GREEN);
//					model.setStartInTime("AFTER " + hours + " "
//							+ (hours == 1 ? "HOUR" : "HOURS"));
//				} else if (hours == 0 && minutes >= 0) {
//					model.setMarkerTypeColor(MarkerColorType.ORANGE);
//					model.setStartInTime("START IN HOUR");
//				} else if (hours == 0 && minutes <= 0 || hours < 0
//						&& hours > -2) {
//					model.setMarkerTypeColor(MarkerColorType.RED);
//					model.setStartInTime("ONGOING");
//				} else if (hours <= -1) {
//					model.setMarkerTypeColor(MarkerColorType.RED);
//					model.setStartInTime("COMPLETED");
//				}
//
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//		}

		public void sortData() {
			Collections.sort(meetings, new Comparator<MeetingModel>() {
				public int compare(MeetingModel o1, MeetingModel o2) {

					if (o1.getDateObj() == null || o2.getDateObj() == null)
						return 0;
					return o1.getDateObj().compareTo(o2.getDateObj());
				}
			});
		}

		String daysInWeek[] = { "monday", "tuesday", "wednesday", "thursday",
				"friday", "saturday", "sunday" };

		public NearesDateTime getOnDate(String days, String times, int position) {

			List<NearesDateTime> nearestDateTimes = new ArrayList<>();

			String dayArray[] = days.split(",");
			String timeArray[] = times.split(",");
			int nearPosition = 0;
			int minDayDiffer = Integer.MAX_VALUE;
			for (int j = 0; j < dayArray.length; j++) {
				String onDay = dayArray[j];
				String onTime = timeArray[j];

				Calendar sCalendar = Calendar.getInstance();
				String dayLongName = sCalendar.getDisplayName(
						Calendar.DAY_OF_WEEK, Calendar.LONG,
						Locale.getDefault());
				int onDayPositon = -1;
				int todayPosition = -1;

				for (int i = 0; i < daysInWeek.length; i++) {
					if (onDay.toLowerCase().equals(daysInWeek[i])) {
						onDayPositon = i;
					}

					if (dayLongName.toLowerCase().equals(daysInWeek[i])) {
						todayPosition = i;
					}
				}

				if (onDayPositon > todayPosition) {
					sCalendar.add(Calendar.DAY_OF_MONTH,
							(onDayPositon - todayPosition));
					int dayDiffer = onDayPositon - todayPosition;
					if (minDayDiffer > dayDiffer) {
						minDayDiffer = dayDiffer;
						nearPosition = j;
					}

				} else if (onDayPositon < todayPosition) {
					sCalendar.add(Calendar.DAY_OF_MONTH, ((daysInWeek.length)
							- todayPosition + onDayPositon));

					int dayDiffer = ((daysInWeek.length) - todayPosition + onDayPositon);
					if (minDayDiffer > dayDiffer) {
						minDayDiffer = dayDiffer;
						nearPosition = j;
					}

				} else if (onDayPositon == todayPosition) {
					SimpleDateFormat _12HourSDF = new SimpleDateFormat(
							"hh:mm a");
					try {
						final Date dateObj = _12HourSDF.parse(onTime);
						sCalendar.set(Calendar.HOUR_OF_DAY,
								dateObj.getHours() + 1);
						sCalendar.set(Calendar.MINUTE, dateObj.getMinutes());
						if (sCalendar.before(Calendar.getInstance())) {
							sCalendar.add(Calendar.DAY_OF_MONTH,
									(daysInWeek.length));

							int dayDiffer = daysInWeek.length;
							if (minDayDiffer > dayDiffer) {
								minDayDiffer = dayDiffer;
								nearPosition = j;
							}

						} else {
							meetings.get(position).setTodayMeeting(true);
							int dayDiffer = 0;
							if (minDayDiffer > dayDiffer) {
								minDayDiffer = dayDiffer;
								nearPosition = j;
							}
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				SimpleDateFormat dateFormate = new SimpleDateFormat(
						"dd/MM/yyyy");
				String dateMade = dateFormate.format(sCalendar.getTime());

				NearesDateTime nearDateTime = new NearesDateTime();
				nearDateTime.setDate(dateMade);
				nearDateTime.setTime(onTime);
				nearestDateTimes.add(nearDateTime);
			}

			return nearestDateTimes.get(nearPosition);

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			meetingsAdapter = new FavMeetingListAdapter(getActivity(),
					R.layout.list_item_meeting, meetings);
			list.setAdapter(meetingsAdapter);
			meetingsAdapter.notifyDataSetChanged();
			updateEmptyViews();

		}

	}

	public class NearesDateTime {
		String date;
		String time;

		public void setDate(String date) {
			this.date = date;
		}

		public String getDate() {
			return this.date;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getTime() {
			return this.time;
		}
	}
}