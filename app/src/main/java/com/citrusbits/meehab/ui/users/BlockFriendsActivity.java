package com.citrusbits.meehab.ui.users;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.adapters.FriendsGridAdapter;
import com.citrusbits.meehab.adapters.FriendsListAdapter;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.model.FriendFilterResultHolder;
import com.citrusbits.meehab.model.GetFriendsResponse;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.ui.SocketActivity;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.gson.Gson;

public class BlockFriendsActivity extends SocketActivity implements
		OnSocketResponseListener, OnBackendConnectListener,
		ListView.OnItemClickListener, View.OnClickListener {

	public static final String EXTRA_FILTER_RESULT = "filter_result";

	private static final int Filter_request = 200;
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

	private Dialog pd;;

	List<UserAccount> userAccounts = new ArrayList<UserAccount>();

	List<UserAccount> userAccountsCache = new ArrayList<UserAccount>();

	private int mAccountPosition = -1;

	private TextView tvNumBlockedFriendsList;

	public BlockFriendsActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		pd = UtilityClass.getProgressDialog(this);
		setContentView(R.layout.activity_block_friends);
		tvNumBlockedFriendsList = (TextView) findViewById(R.id.tvNumBlockedFriendsList);
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

		getFriends();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == Filter_request) {

				FriendFilterResultHolder fFilterResultHolder = (FriendFilterResultHolder) data
						.getSerializableExtra(EXTRA_FILTER_RESULT);

				boolean onlineNowFilter = fFilterResultHolder.isOnlineNow();
				boolean willingToSponsorFilter = fFilterResultHolder
						.isWillingToSponsor();
				boolean hasKidsFilter = fFilterResultHolder.isHasKids();
				userAccounts.clear();

				for (UserAccount user : userAccountsCache) {

					Log.e("willing to sponser", user.getWillingSponsor());
					boolean userOnline = user.getCheckinType().equals("online");
					boolean userWillingToSponser = !user.getWillingSponsor()
							.equals("no");
					boolean userHaskids = !user.getHaveKids().equals("no");

					boolean onlineSatisfy = satisfyFilter(onlineNowFilter,
							userOnline);
					boolean willingToSponserSatisfy = satisfyFilter(
							willingToSponsorFilter, userWillingToSponser);

					boolean hasKidsSatisfy = satisfyFilter(hasKidsFilter,
							userHaskids);

					boolean isGenderSatisfy = genderSatisfy(user,
							fFilterResultHolder);

					boolean isSatisfyAge = satisfyAge(user, fFilterResultHolder);

					boolean isEthenticity = ethenticitySatisfy(user,
							fFilterResultHolder);

					boolean isMaritalStatus = MaritalStatusSatisfy(user,
							fFilterResultHolder);

					boolean isInterestedIn = interestedInSatisfy(user,
							fFilterResultHolder);

					boolean isTimeToSobar = satisfyTimeSobar(user,
							fFilterResultHolder);

					boolean isHeight = satisfyHeight(user, fFilterResultHolder);

					boolean isWeight = satisfyWeight(user, fFilterResultHolder);

					if (/*
						 * onlineSatisfy && willingToSponserSatisfy&&
						 * hasKidsSatisfy&&
						 * isGenderSatisfy&&isSatisfyAge&&isEthenticity
						 * &&isMaritalStatus&& isInterestedIn&&
						 * isTimeToSobar&&isHeight&&
						 */isWeight) {
						userAccounts.add(user);
					}

				}

				// boolean friendType

			} else if (requestCode == DETAIL_REQUEST) {
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

	private boolean satisfyHeight(UserAccount user,
			FriendFilterResultHolder fFilterResultHolder) {

		if (fFilterResultHolder.isAnyHeight()) {
			return true;
		}
		String height = user.getHeight();

		if (height.isEmpty()) {
			return false;
		}

		String[] footInch = height.split("'");

		if (footInch.length < 2) {
			return false;
		}

		Log.e("Foot Inch Length ", String.valueOf(footInch.length));

		Log.e("Index0:Index1 ", footInch[0] + ":" + footInch[1]);

		int foot = Integer.parseInt(footInch[0].trim());
		int inch = Integer.parseInt(footInch[1].trim());

		List<String> heights = fFilterResultHolder.getHeight();

		if (foot == 4 && inch >= 5 || foot == 5 && inch == 0) {
			return heights.contains("4'5-5'0");
		} else if (foot == 5 && inch >= 1 && inch <= 5) {
			return heights.contains("5'1-5'5");
		} else if (foot >= 5 && inch >= 6 || foot == 6 && inch == 0) {
			Log.e("Condition Meet", "yes");
			return heights.contains("5'6-6'0");
		} else if (foot == 6 && inch >= 1 && inch <= 5) {
			return heights.contains("6'1-6'5");
		} else if (foot == 6 && inch >= 6 || foot == 7 && inch == 0) {
			return heights.contains("6'6-7'0");
		} else {
			return false;
		}

	}

	private boolean satisfyWeight(UserAccount user,
			FriendFilterResultHolder filterResultHolder) {

		if (filterResultHolder.isAnyWeight()) {
			return true;
		}

		String weight = user.getWeight();
		List<String> weights = filterResultHolder.getWeight();
		return weights.contains(weight);
	}

	private boolean satisfyTimeSobar(UserAccount user,
			FriendFilterResultHolder fFilterResultHolder) {

		if (fFilterResultHolder.isAnyTimeSober()) {
			return true;
		}

		int timeToSobar = calculateAge(user.getSoberSence());

		Log.e("sobarTime  is ", String.valueOf(timeToSobar));
		List<String> sobers = fFilterResultHolder.getSober();
		if (timeToSobar >= 0 && timeToSobar < 1) {

			return sobers.contains("Less than 1 year");
		} else if (timeToSobar >= 1 && timeToSobar <= 5) {
			return sobers.contains("1-5 years");
		} else if (timeToSobar >= 6 && timeToSobar <= 10) {
			return sobers.contains("6-10 years");
		} else if (timeToSobar >= 11 && timeToSobar <= 15) {
			return sobers.contains("11-15 years");
		} else if (timeToSobar >= 16 && timeToSobar <= 20) {
			return sobers.contains("16-20 years");
		} else if (timeToSobar >= 21 && timeToSobar <= 30) {
			return sobers.contains("21-30 years");
		} else if (timeToSobar >= 31 && timeToSobar <= 40) {
			return sobers.contains("31-40");
		} else if (timeToSobar >= 41) {
			return sobers.contains("41+ years");

		} else {
			return false;
		}

	}

	public boolean satisfyAge(UserAccount user,
			FriendFilterResultHolder fFilterResultHolder) {

		if (fFilterResultHolder.isAnyAge()) {
			return true;
		}

		int age = calculateAge(user.getDateOfBirth());

		Log.e("User age is ", String.valueOf(age));
		List<String> ages = fFilterResultHolder.getAge();
		if (age >= 18 && age <= 25) {
			return ages.contains("18-25 years");
		} else if (age >= 26 && age <= 30) {
			return ages.contains("26-30 years");
		} else if (age >= 31 && age <= 35) {
			return ages.contains("31-35 years");
		} else if (age >= 36 && age <= 40) {
			return ages.contains("36-40 years");
		} else if (age >= 41 && age <= 45) {
			return ages.contains("41-45 years");
		} else if (age >= 46 && age <= 50) {
			return ages.contains("46-50 years");
		} else if (age >= 51 && age <= 55) {
			return ages.contains("51-55 years");
		} else if (age >= 56 && age <= 60) {
			return ages.contains("56-60 years");
		} else if (age > 60) {
			return ages.contains("60+ years");
		} else {
			return false;
		}

	}

	public int calculateAge(String dateOfBirth) {

		// dateOfBirth = "02/28/1990";
		SimpleDateFormat dateFormate = new SimpleDateFormat("MM/dd/yy");

		try {
			Date dateOfBirthDate = dateFormate.parse(dateOfBirth);
			Date currentDate = Calendar.getInstance().getTime();
			/*
			 * Calendar calDateOfBirth = Calendar.getInstance();
			 * calDateOfBirth.setTime(dateOfBirthDate);
			 * 
			 * calDateOfBirth.set(Calendar.HOUR_OF_DAY, 0); // set hour to
			 * midnight calDateOfBirth.set(Calendar.MINUTE, 0); // set minute in
			 * hour calDateOfBirth.set(Calendar.SECOND, 0); // set second in
			 * minute calDateOfBirth.set(Calendar.MILLISECOND, 0);
			 */
			return getDiffYears(dateOfBirthDate, currentDate);

		} catch (Exception e) {
			// TODO: handle exception
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

	private boolean MaritalStatusSatisfy(UserAccount user,
			FriendFilterResultHolder fFilterResultHolder) {
		if (fFilterResultHolder.isAnyMaterialStatus()) {
			return true;
		}

		String userMaterialStatus = user.getMaritalStatus().toLowerCase();
		List<String> maritalStatuses = fFilterResultHolder.getMaterialStatus();
		for (String maritalStatus : maritalStatuses) {
			if (userMaterialStatus.equals(maritalStatus.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	private boolean interestedInSatisfy(UserAccount user,
			FriendFilterResultHolder fFilterResultHolder) {
		if (fFilterResultHolder.isAnyinterestedIn()) {
			return true;
		}

		String userInterestedIn = user.getIntrestedIn().toLowerCase();
		List<String> interestedInList = fFilterResultHolder.getInterestedIn();
		for (String interestedIn : interestedInList) {
			if (userInterestedIn.equals(interestedIn.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	private boolean ethenticitySatisfy(UserAccount user,
			FriendFilterResultHolder fFilterResultHolder) {
		if (fFilterResultHolder.isAnyEthenticity()) {
			return true;
		}

		String userEthenticity = user.getEthnicity().toLowerCase();
		List<String> ethenticities = fFilterResultHolder.getEthenticity();
		for (String ethencity : ethenticities) {
			if (userEthenticity.equals(ethencity.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	private boolean genderSatisfy(UserAccount user,
			FriendFilterResultHolder fFilterResultHolder) {
		Toast.makeText(this, "is Any Age:" + fFilterResultHolder.isAnyAge(),
				Toast.LENGTH_SHORT).show();
		if (fFilterResultHolder.isAnyGender()) {
			return true;
		}

		String userGender = user.getGender().toLowerCase();
		List<String> genders = fFilterResultHolder.getGender();
		for (String gender : genders) {
			if (userGender.equals(gender.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	private boolean satisfyFilter(boolean filterValue, boolean userValue) {

		return filterValue == userValue;

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

	public void getFriends() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}

		if (socketService != null) {
			pd.show();
			JSONObject object = new JSONObject();

			try {
				object.put("user_id", AccountUtils.getUserId(this));
				object.put("device_id", DeviceUtils.getDeviceId(this));
				object.put("type", "blocked");
				Log.e("json send ", object.toString());
				socketService.getAllFriends(object);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onBackendConnected() {
		// TODO Auto-generated method stub
		getFriends();
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		// TODO Auto-generated method stub
		pd.dismiss();
		if (event.equals(EventParams.METHOD_GET_ALL_FRIENDS)) {
			userAccounts.clear();
			Gson gson = new Gson();
			JSONObject data = (JSONObject) obj;
			GetFriendsResponse response = gson.fromJson(data.toString(),
					GetFriendsResponse.class);
			List<UserAccount> friends = response.getFriends();
			Log.e("Friends data is ", data.toString());
			for (int i = 0; i < friends.size(); i++) {

				Log.e("User name ", friends.get(i).getUsername());

				friends.get(i).setAge(
						calculateAge(friends.get(i).getDateOfBirth()));
				if (friends.get(i).isBlocked() == 0) {
					friends.remove(i);
					i--;
				}

			}

			for (int i = 0; i < friends.size(); i++) {
				friends.get(i).setAge(
						calculateAge(friends.get(i).getDateOfBirth()));
			}

			userAccounts.addAll(friends);
			userAccountsCache.addAll(friends);
			if (friends.size() > 0) {
				if (friends.size() == 1) {
					tvNumBlockedFriendsList.setText(String.format(
							getString(R.string.num_blocked_friend_list),
							friends.size()));
				} else {
					tvNumBlockedFriendsList.setText(String.format(
							getString(R.string.num_blocked_friends_list),
							friends.size()));
				}

			} else {
				tvNumBlockedFriendsList
						.setText(getString(R.string.no_blocked_friend_list));
			}

			friendsGridAdapter.notifyDataSetChanged();

			/*
			 * Toast.makeText(homeActivity, "Friends data is " +
			 * data.toString(), Toast.LENGTH_SHORT).show();
			 */
		}
	}

	@Override
	public void onSocketResponseFailure(String onEvent,String message) {
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
