package com.citrusbits.meehab;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.citrusbits.meehab.adapters.FriendsGridAdapter;
import com.citrusbits.meehab.adapters.FriendsListAdapter;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.model.FriendMessageModel;
import com.citrusbits.meehab.model.GetFriendsResponse;
import com.citrusbits.meehab.model.GetRsvpFriendsResponse;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.gson.Gson;

public class RsvpActivity extends SocketActivity implements
		OnSocketResponseListener, OnBackendConnectListener,
		ListView.OnItemClickListener, View.OnClickListener {

	public static final String EXTRA_MEETING_ID = "meeting_id";

	private static final int DETAIL_REQUEST = 300;

	private ChildFragmentAdapter mAdapter;
	private ViewPager mPager;
	static GridView grid;
	static ListView list;

	FriendsListAdapter friendsListAdapter;

	FriendsGridAdapter friendsGridAdapter;
	private Button btnGrid, btnList;
	private ImageView ivGridBar, ivListBar;

	private View focus_thief;

	private Dialog pd;

	List<UserAccount> userAccounts = new ArrayList<UserAccount>();

	List<UserAccount> userAccountsCache = new ArrayList<UserAccount>();

	private int mAccountPosition = -1;

	private int meetingId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		pd = UtilityClass.getProgressDialog(this);
		setContentView(R.layout.activity_rsvp_friends);
		meetingId = getIntent().getIntExtra(EXTRA_MEETING_ID, -1);
		findViewById(R.id.topMenuBtn).setOnClickListener(this);

		mPager = (ViewPager) findViewById(R.id.pager);
		btnGrid = (Button) findViewById(R.id.btnGrid);
		btnList = (Button) findViewById(R.id.btnList);

		ivGridBar = (ImageView) findViewById(R.id.ivGridBar);
		ivListBar = (ImageView) findViewById(R.id.ivListBar);

		focus_thief = findViewById(R.id.focus_thief);

		int width = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 100, getResources()
						.getDisplayMetrics());

		int grid_spacing = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 10, getResources()
						.getDisplayMetrics());

		list = new ListView(this);
		list.setClickable(true);
		grid = new GridView(this);
		grid.setClickable(true);
		grid.setLayoutParams(new GridView.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		grid.setBackgroundColor(Color.WHITE);
		grid.setPadding(grid_spacing, grid_spacing, grid_spacing, grid_spacing);
		grid.setHorizontalSpacing(grid_spacing);
		grid.setVerticalSpacing(grid_spacing);

		grid.setNumColumns(3);
		grid.setColumnWidth(GridView.AUTO_FIT);
		grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

		// init grid adapter
		friendsGridAdapter = new FriendsGridAdapter(this,
				R.layout.grid_item_friend, userAccounts);

		grid.setAdapter(friendsGridAdapter);

		friendsGridAdapter.notifyDataSetChanged();

		// init list adapter
		friendsListAdapter = new FriendsListAdapter(this,
				R.layout.list_item_friend, userAccounts);
		list.setAdapter(friendsListAdapter);
		friendsListAdapter.notifyDataSetChanged();

		btnGrid.setOnClickListener(this);
		btnList.setOnClickListener(this);
		grid.setOnItemClickListener(this);
		list.setOnItemClickListener(this);

		mAdapter = new ChildFragmentAdapter(getSupportFragmentManager());
		mPager.setAdapter(mAdapter);
		changeTab(0);
		mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// setUIButtonText(arg0);
				changeTab(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		getRSVPFriends();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == DETAIL_REQUEST) {
				if (mAccountPosition != -1) {
					UserAccount userAccount = (UserAccount) data
							.getSerializableExtra(UserProfileActivity.EXTRA_USER_ACCOUNT);
					// userAccountsCache.set(mAccountPosition, userAccount);
					userAccountsCache.remove(mAccountPosition);
					userAccounts.clear();
					userAccounts.addAll(userAccountsCache);
					friendsGridAdapter.notifyDataSetChanged();
					friendsListAdapter.notifyDataSetChanged();

				}
			}
		}
	}

	public int calculateAge(String dateOfBirth) {

		// dateOfBirth = "02/28/1990";

		SimpleDateFormat dateFormate = new SimpleDateFormat("MM/dd/yy");

		try {

			Log.i("Date Of birth", dateOfBirth);

			Date dateOfBirthDate = dateFormate.parse(dateOfBirth);
			Date currentDate = Calendar.getInstance().getTime();

			return getDiffYears(dateOfBirthDate, currentDate);

		} catch (Exception e) {
			// TODO: handle exception
			Log.i("Exception Date Of birth", dateOfBirth);
			return 0;
		}
	}

	public static int getDiffYears(Date first, Date last) {
		Calendar a = getCalendar(first);
		Calendar b = getCalendar(last);
		int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
		if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH)
				|| (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a
						.get(Calendar.DATE) > b.get(Calendar.DATE))) {
			diff--;
		}
		return diff;
	}

	public static Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mAccountPosition = position;
		UserAccount account = userAccounts.get(position);

		Intent intent = new Intent(this, UserProfileActivity.class);
		intent.putExtra(UserProfileActivity.EXTRA_USER_ACCOUNT, account);

		// put friend
		startActivityForResult(intent, DETAIL_REQUEST);
		this.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topMenuBtn:
			onBackPressed();
			break;

		case R.id.btnGrid:
			mPager.setCurrentItem(0);
			break;
		case R.id.btnList:
			mPager.setCurrentItem(1);
			break;

		default:
			break;
		}
	}

	public class ChildFragmentAdapter extends FragmentPagerAdapter {
		public ChildFragmentAdapter(
				android.support.v4.app.FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0: // Fragment # 0 - This will show image
				return new FriendsGridFragment();
			case 1: // Fragment # 1 - This will show image
				return new FriendsListFragment();
			default:// Fragment # 2-9 - Will show list
				return new FriendsGridFragment();
			}
		}
	}

	public static class FriendsGridFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			return grid;
		}

		@Override
		public void onResume() {
			super.onResume();
			((FriendsGridAdapter) grid.getAdapter()).notifyDataSetChanged();
		}
	}

	public static class FriendsListFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			return list;
		}

		@Override
		public void onResume() {
			super.onResume();
			((FriendsListAdapter) list.getAdapter()).notifyDataSetChanged();
		}
	}

	public void changeTab(int position) {
		switch (position) {
		case 0:
			btnGrid.setEnabled(false);
			btnList.setEnabled(true);
			ivGridBar.setVisibility(View.VISIBLE);
			ivListBar.setVisibility(View.INVISIBLE);
			break;
		case 1:
			btnGrid.setEnabled(true);
			btnList.setEnabled(false);
			ivGridBar.setVisibility(View.INVISIBLE);
			ivListBar.setVisibility(View.VISIBLE);
			break;
		}

	}

	public void getRSVPFriends() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}
		if (socketService != null) {
			pd.show();
			JSONObject object = new JSONObject();

			try {

				object.put("meeting_id", meetingId);
				Log.e("json send ", object.toString());

				socketService.getAllRSVPUsers(object);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onBackendConnected() {
		// TODO Auto-generated method stub
		getRSVPFriends();
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		// TODO Auto-generated method stub
		pd.dismiss();
		if (event.equals(EventParams.METHOD_RSVP_USERS)) {
			userAccounts.clear();
			Gson gson = new Gson();
			JSONObject data = (JSONObject) obj;
			GetRsvpFriendsResponse response = gson.fromJson(data.toString(),
					GetRsvpFriendsResponse.class);
			List<UserAccount> friends = response.getFriends();
			for (int i = 0; i < friends.size(); i++) {
				friends.get(i).setAge(
						calculateAge(friends.get(i).getDateOfBirth()));
			}
			userAccounts.addAll(friends);
			userAccountsCache.addAll(friends);
			friendsGridAdapter.notifyDataSetChanged();

		}
	}

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		// TODO Auto-generated method stub
		if (pd != null) {
			pd.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		this.overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

}
