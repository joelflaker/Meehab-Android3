package com.citrusbits.meehab.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.app.MeehabApp;
import com.citrusbits.meehab.model.NearestDateTime;
import com.citrusbits.meehab.ui.HomeActivity;
import com.citrusbits.meehab.ui.meetings.MeetingDetailsActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.ui.users.UserProfileActivity;
import com.citrusbits.meehab.adapters.FavFriendsGridAdapter;
import com.citrusbits.meehab.adapters.FavMeetingListAdapter;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.map.LocationService;
import com.citrusbits.meehab.map.LocationService.LocationListener;
import com.citrusbits.meehab.map.LocationService.MyLocalBinder;
import com.citrusbits.meehab.map.LocationUtils;
import com.citrusbits.meehab.model.GetFriendsResponse;
import com.citrusbits.meehab.model.MeetingModel;
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

	List<UserAccount> userAccounts = new ArrayList<>();

	List<UserAccount> userAccountsCache = new ArrayList<>();

	private int currentTabPosition;
	
	protected int mReqPosition;
	protected int mAccountPosition;
    private int preIbEditImage;
//	private View textViewCross;

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
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		this.homeActivity = (HomeActivity) getActivity();
		pd = UtilityClass.getProgressDialog(homeActivity);

		myLocation = LocationUtils.getLastLocation(getActivity());
		Intent i = new Intent(mContext.getApplicationContext(), LocationService.class);
		mContext.bindService(i, locServiceConnection, Context.BIND_AUTO_CREATE);
		mContext.startService(i);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		homeActivity = null;
		mContext = null;
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
//		textViewCross = view.findViewById(R.id.textViewCross);
//		textViewCross.setOnClickListener(this);

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
				if(inputText.length() == 0){
//					textViewCross.setVisibility(View.INVISIBLE);
                    ibEdit.setImageResource(preIbEditImage == 0 ? R.drawable.edit_btn : preIbEditImage);
                }else{
//					textViewCross.setVisibility(View.VISIBLE);
                    ibEdit.setImageResource(R.drawable.cancel_btn_small);
                }
				if (currentTabPosition == 1) {
					userAccounts.clear();
					for (UserAccount account : userAccountsCache) {
						String name = account.getUsername();
						String number = account.getPhone();
						String email = account.getEmail();
						if (name.toLowerCase().contains(inputText)
								|| email.contains(inputText)
								|| number.contains(inputText)) {
							userAccounts.add(account);
						}
					}

					friendsGridAdapter.notifyDataSetChanged();
				} else {
					meetingsAdapter.filter(inputText);
				}
				updateEmptyViews();
			}
		});

        editTopCenter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    UtilityClass.hideSoftKeyboard(getContext(),getActivity().getCurrentFocus());
                }
                return false;
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
			if (meetingsAdapter.getEdit()) {
				meetings.get(position).setChecked(!meetings.get(position).isChecked());
				meetingsAdapter.notifyDataSetChanged();
				setRemoveResource();
				return;
			}

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

		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MeetingsFragment.REQUEST_MEETING_DETAILS && resultCode == Activity.RESULT_OK){
			//remove un-fav
			editTopCenter.setText("");
			refreshMeetingList();
		}
		if(requestCode == FriendsFragment.DETAIL_REQUEST && resultCode == Activity.RESULT_OK){
			//remove un-fav
			getFriends();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	OnItemClickListener gridItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View v, int position,
				long id) {
			if (friendsGridAdapter.isEdit()) {

				userAccounts.get(position).setChecked(
						!userAccounts.get(position).isChecked());

				friendsGridAdapter.notifyDataSetChanged();

				setRemoveResource();
				return;
			}

			mAccountPosition = position;
			UserAccount account = userAccounts.get(position);

			Intent intent = new Intent(getActivity(), UserProfileActivity.class);
			intent.putExtra(UserProfileActivity.EXTRA_USER_ACCOUNT, account);

			// put friend
			startActivityForResult(intent, FriendsFragment.DETAIL_REQUEST);
			getActivity().overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);

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
			MeehabApp.toast(getString(R.string.no_internet_connection));
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
			MeehabApp.toast(getString(R.string.no_internet_connection));
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

		if(meetings.size() > 0 && position == 0){
			ibRemove.setVisibility(meetingsAdapter.getEdit() ? View.VISIBLE
					: View.GONE);
		}else if(userAccounts.size() > 0 && position == 1){
			ibRemove.setVisibility(meetingsAdapter.getEdit() ? View.VISIBLE
					: View.GONE);
		}else {
			ibRemove.setVisibility(View.GONE);
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

//			if(userAccounts.size() == 0 && meetings.size() == 0){
//				MeehabApp.toast("Nothing to edit!");
//			}

            if(editTopCenter.getText().length() > 0){
                editTopCenter.setText("");
                UtilityClass.hideSoftKeyboard(getContext(), editTopCenter);
                return;
            }

			meetingsAdapter.setEdit(!meetingsAdapter.getEdit());
			friendsGridAdapter.setEdit(!friendsGridAdapter.isEdit());

			if (!friendsGridAdapter.isEdit()) {
				unCheckAll();
			}
			meetingsAdapter.notifyDataSetChanged();
			friendsGridAdapter.notifyDataSetChanged();

            preIbEditImage = meetingsAdapter.getEdit() ? R.drawable.cancel_btn_small
					: R.drawable.edit_btn;

			ibEdit.setImageResource(preIbEditImage);

			if(meetings.size() > 0 && currentTabPosition == 0){
				ibRemove.setVisibility(meetingsAdapter.getEdit() ? View.VISIBLE
						: View.GONE);
			}else if(userAccounts.size() > 0 && currentTabPosition == 1){
				ibRemove.setVisibility(meetingsAdapter.getEdit() ? View.VISIBLE
						: View.GONE);
			}else {
				ibRemove.setVisibility(View.GONE);
			}

			break;
			case R.id.textViewCross:
				editTopCenter.setText("");
				UtilityClass.hideSoftKeyboard(getContext(), editTopCenter);
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
			userAccountsCache.clear();
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
//				UserAccount user = friends.get(i);
			}

			userAccounts.addAll(friends);
			userAccountsCache.addAll(friends);

			//avoid crashing
			if(!isAdded()) return;

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
			//avoid crashing
			if(!isAdded()) return;

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

			//avoid crashing
			if(!isAdded()) return;

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

					/*
					 * String onDate = getOnDate(meeting.getOnDay(),
					 * meeting.getOnTime());
					 */


					Location pinLocation = new Location("B");
					pinLocation.setLatitude(meeting.getLatitude());
					pinLocation.setLongitude(meeting.getLongitude());
					double distance = (double) (myLocation
							.distanceTo(pinLocation) * 0.000621371192f);

					distance = Math.floor(distance * 100) / 100f;
					meeting.setDistanceInMiles(distance);

					NearestDateTime nearDateTime = MeetingUtils.getNearestDate(meeting.getOnDay(),
							meeting.getOnTime());

					meeting.setNearestDateTime(nearDateTime.getDateTime());
					meeting.setOnDateOrigin(nearDateTime.getDate());
					meeting.setNearestTime(nearDateTime.getTime());
					meeting.setNearestDateTime(nearDateTime.getDateTime());
					meeting.setOnDate(MeetingUtils.formateDate(nearDateTime.getDateTime()));
					// }

					MeetingUtils.setMeetingTimingStatus(meeting, meeting.getNearestDateTime());
				}

				MeetingUtils.sortByDistance(meetings);
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

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			meetingsAdapter = new FavMeetingListAdapter(getActivity(),
					R.layout.list_item_meeting, meetings);
			if(!isAdded() || list == null) return;
			list.setAdapter(meetingsAdapter);
			meetingsAdapter.notifyDataSetChanged();
			updateEmptyViews();

		}

	}
}