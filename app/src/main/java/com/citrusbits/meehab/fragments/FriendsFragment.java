package com.citrusbits.meehab.fragments;

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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.citrusbits.meehab.FriendsFilterActivity;
import com.citrusbits.meehab.HomeActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.UserProfileActivity;
import com.citrusbits.meehab.adapters.FriendsGridAdapter;
import com.citrusbits.meehab.adapters.FriendsListAdapter;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.DatabaseHandler;
import com.citrusbits.meehab.model.FriendFilterResultHolder;
import com.citrusbits.meehab.model.FriendMessageModel;
import com.citrusbits.meehab.model.GetFriendsResponse;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.gson.Gson;

public class FriendsFragment extends Fragment implements
		OnSocketResponseListener, OnBackendConnectListener,
		ListView.OnItemClickListener, View.OnClickListener {

	public static final String EXTRA_FILTER_RESULT = "filter_result";

	private static final int Filter_request = 200;
	public static final int DETAIL_REQUEST = 300;

	private ChildFragmentAdapter mAdapter;
	private ViewPager mPager;
	static GridView grid;
	static ListView list;

	FriendsListAdapter friendsListAdapter;

	FriendsGridAdapter friendsGridAdapter;
	private Button btnGrid, btnList;
	private ImageView ivGridBar, ivListBar;

	private HomeActivity homeActivity;
	private EditText editTopCenter;

	private Dialog pd;;

	List<UserAccount> userAccounts = new ArrayList<UserAccount>();

	List<UserAccount> userAccountsCache = new ArrayList<UserAccount>();

	private int mAccountPosition = -1;

	DatabaseHandler dbHandler;
	
	private boolean isFriendsFetch=false;

	private View emptyList;

	public static FriendFilterResultHolder fFilterResultHolder = new FriendFilterResultHolder();

	public FriendsFragment() {
	}
	
	public void onDestroy() {
		grid = null;
		list = null;
		super.onDestroy();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.homeActivity = (HomeActivity) getActivity();
		pd = UtilityClass.getProgressDialog(getActivity());
		dbHandler = DatabaseHandler.getInstance(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_friends, container, false);

		// init UI
		v.findViewById(R.id.topMenuBtn).setOnClickListener(this);
		v.findViewById(R.id.btnFilter).setOnClickListener(this);
		mPager = (ViewPager) v.findViewById(R.id.pager);
		btnGrid = (Button) v.findViewById(R.id.btnGrid);
		btnList = (Button) v.findViewById(R.id.btnList);
		emptyList = v.findViewById(R.id.emptyList);

		ivGridBar = (ImageView) v.findViewById(R.id.ivGridBar);
		ivListBar = (ImageView) v.findViewById(R.id.ivListBar);

		editTopCenter = (EditText) v.findViewById(R.id.editTopCenter);
		editTopCenter.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String text = editTopCenter.getText().toString();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String inputText = s.toString().trim().toLowerCase();
				userAccounts.clear();
				for (UserAccount account : userAccountsCache) {
					String name = account.getUsername();
					if (name.toLowerCase().contains(inputText)) {
						userAccounts.add(account);
					}
				}

				friendsGridAdapter.notifyDataSetChanged();
				friendsListAdapter.notifyDataSetChanged();
				updateEmptyViewVisibility();

			}
		});

		int width = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 100, getActivity().getResources()
						.getDisplayMetrics());

		int grid_spacing = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 10, getActivity().getResources()
						.getDisplayMetrics());

		list = new ListView(getActivity());
		list.setClickable(true);
		grid = new GridView(getActivity());
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
		friendsGridAdapter = new FriendsGridAdapter(getActivity(),
				R.layout.grid_item_friend, userAccounts);

		grid.setAdapter(friendsGridAdapter);

		friendsGridAdapter.notifyDataSetChanged();

		// init list adapter
		friendsListAdapter = new FriendsListAdapter(getActivity(),
				R.layout.list_item_friend, userAccounts);
		list.setAdapter(friendsListAdapter);
		friendsListAdapter.notifyDataSetChanged();

		btnGrid.setOnClickListener(this);
		btnList.setOnClickListener(this);
		grid.setOnItemClickListener(this);
		list.setOnItemClickListener(this);

		mAdapter = new ChildFragmentAdapter(getChildFragmentManager());
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

		return v;
	}

	private void updateEmptyViewVisibility() {
		if(friendsListAdapter.getCount() == 0){
			emptyList.setVisibility(View.VISIBLE);
		}else{
			emptyList.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == getActivity().RESULT_OK) {
			if (requestCode == Filter_request) {

				FriendFilterResultHolder fFilterResultHolder = (FriendFilterResultHolder) data
						.getSerializableExtra(EXTRA_FILTER_RESULT);
				this.fFilterResultHolder = fFilterResultHolder;
				boolean onlineNowFilter = fFilterResultHolder.isOnlineNow();
				boolean willingToSponsorFilter = fFilterResultHolder
						.isWillingToSponsor();
				boolean hasKidsFilter = fFilterResultHolder.isHasKids();
				userAccounts.clear();

				for (UserAccount user : userAccountsCache) {

					Log.e("willing to sponser", user.getWillingSponsor());
					boolean userOnline = user.getCheckinType().equals("online");
					boolean userWillingToSponser = !user.getWillingSponsor()
							.toLowerCase().equals("no");
					// boolean userHaskids =
					// !user.getHaveKids().equals("no")||!user.getHaveKids().isEmpty();

					boolean userHaskids = user.getHaveKids().toLowerCase()
							.equals("yes");

					boolean onlineSatisfy = satisfyFilter(onlineNowFilter,
							userOnline);
					boolean willingToSponserSatisfy = satisfyFilter(
							willingToSponsorFilter, userWillingToSponser);

					boolean hasKidsSatisfy = satisfyFilter(hasKidsFilter,
							userHaskids);

					boolean hasFavSatisfy = favoriteStatusfySatisfy(user,
							fFilterResultHolder);

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

					if (onlineSatisfy && willingToSponserSatisfy
							&& hasKidsSatisfy && isGenderSatisfy
							&& isSatisfyAge && isEthenticity && isMaritalStatus
							&& isInterestedIn && isTimeToSobar && isHeight
							&& isWeight && hasFavSatisfy) {
						userAccounts.add(user);
					}

				}
				
				friendsGridAdapter.notifyDataSetChanged();
				friendsListAdapter.notifyDataSetChanged();
				
				updateEmptyViewVisibility();

				// boolean friendTypeon

			} else if (requestCode == DETAIL_REQUEST) {
				if (mAccountPosition != -1) {
					UserAccount userAccount = (UserAccount) data
							.getSerializableExtra(UserProfileActivity.EXTRA_USER_ACCOUNT);
					// userAccountsCache.set(mAccountPosition, userAccount);
					if (userAccount.isBlocked() == 1) {
						userAccountsCache.remove(mAccountPosition);
						userAccounts.clear();
						userAccounts.addAll(userAccountsCache);
						friendsGridAdapter.notifyDataSetChanged();
						friendsListAdapter.notifyDataSetChanged();
					}

					else if (userAccount.isFavourite() != userAccountsCache
							.get(mAccountPosition).isFavourite()) {
						userAccountsCache.set(mAccountPosition, userAccount);
						userAccounts.clear();
						userAccounts.addAll(userAccountsCache);
						friendsGridAdapter.notifyDataSetChanged();
						friendsListAdapter.notifyDataSetChanged();
					}

				}
			}
		} else if (resultCode == FriendsFilterActivity.CLEAR_FRIEND_FILTER) {
			this.fFilterResultHolder = new FriendFilterResultHolder();
			userAccounts.clear();
			userAccounts.addAll(userAccountsCache);
			friendsGridAdapter.notifyDataSetChanged();
			friendsListAdapter.notifyDataSetChanged();
			updateEmptyViewVisibility();
		}
	}

	public void applyFilter(FriendFilterResultHolder fFilterResultHolder) {

		this.fFilterResultHolder = fFilterResultHolder;
		boolean onlineNowFilter = fFilterResultHolder.isOnlineNow();
		boolean willingToSponsorFilter = fFilterResultHolder
				.isWillingToSponsor();
		boolean hasKidsFilter = fFilterResultHolder.isHasKids();
		userAccounts.clear();

		for (UserAccount user : userAccountsCache) {

			Log.e("willing to sponser", user.getWillingSponsor());
			boolean userOnline = user.getCheckinType().equals("online");
			boolean userWillingToSponser = !user.getWillingSponsor()
					.toLowerCase().equals("no");
			// boolean userHaskids =
			// !user.getHaveKids().equals("no")||!user.getHaveKids().isEmpty();

			boolean userHaskids = user.getHaveKids().toLowerCase()
					.equals("yes");

			boolean onlineSatisfy = satisfyFilter(onlineNowFilter, userOnline);
			boolean willingToSponserSatisfy = satisfyFilter(
					willingToSponsorFilter, userWillingToSponser);

			boolean hasKidsSatisfy = satisfyFilter(hasKidsFilter, userHaskids);

			boolean hasFavSatisfy = favoriteStatusfySatisfy(user,
					fFilterResultHolder);

			boolean isGenderSatisfy = genderSatisfy(user, fFilterResultHolder);

			boolean isSatisfyAge = satisfyAge(user, fFilterResultHolder);

			boolean isEthenticity = ethenticitySatisfy(user,
					fFilterResultHolder);

			boolean isMaritalStatus = MaritalStatusSatisfy(user,
					fFilterResultHolder);

			boolean isInterestedIn = interestedInSatisfy(user,
					fFilterResultHolder);

			boolean isTimeToSobar = satisfyTimeSobar(user, fFilterResultHolder);

			boolean isHeight = satisfyHeight(user, fFilterResultHolder);

			boolean isWeight = satisfyWeight(user, fFilterResultHolder);

			if (onlineSatisfy && willingToSponserSatisfy && hasKidsSatisfy
					&& isGenderSatisfy && isSatisfyAge && isEthenticity
					&& isMaritalStatus && isInterestedIn && isTimeToSobar
					&& isHeight && isWeight && hasFavSatisfy) {
				userAccounts.add(user);
			}

		}

		// boolean friendTypeon

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
		if (weight.isEmpty()) {
			return false;
		}

		try {

		} catch (Exception e) {
		}

		weight = weight.toLowerCase().replace("lbs", "").trim();
		List<String> weights = filterResultHolder.getWeight();

		for (String weightRange : weights) {
			weightRange = weightRange.toLowerCase().replace("lbs", "").trim();
			String weightArr[] = weightRange.split("-");
			int startWeight = Integer.parseInt(weightArr[0]);
			int endWeight = Integer.parseInt(weightArr[1]);
			int userWeight = Integer.parseInt(weight);
			if (userWeight >= startWeight && userWeight <= endWeight) {
				return true;
			}
		}

		return false;

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

	private boolean favoriteStatusfySatisfy(UserAccount user,
			FriendFilterResultHolder fFilterResultHolder) {
		/*
		 * Toast.makeText(getActivity(), "is Any Age:" +
		 * fFilterResultHolder.isAnyAge(), Toast.LENGTH_SHORT).show();
		 */
		if (fFilterResultHolder.isAnyFriendType()) {
			return true;
		}

		int userFavorite = user.isFavourite();
		if (userFavorite == 0) {
			return false;
		}
		List<String> friendTypes = fFilterResultHolder.getFriendType();
		for (String friendType : friendTypes) {
			if ("favourite".equals(friendType.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	private boolean genderSatisfy(UserAccount user,
			FriendFilterResultHolder fFilterResultHolder) {
		/*
		 * Toast.makeText(getActivity(), "is Any Age:" +
		 * fFilterResultHolder.isAnyAge(), Toast.LENGTH_SHORT).show();
		 */
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

		return filterValue == false ? true : filterValue == userValue;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mAccountPosition = position;
		UserAccount account = userAccounts.get(position);

		Intent intent = new Intent(getActivity(), UserProfileActivity.class);
		intent.putExtra(UserProfileActivity.EXTRA_USER_ACCOUNT, account);

		// put friend
		startActivityForResult(intent, DETAIL_REQUEST);
		getActivity().overridePendingTransition(R.anim.activity_in,
				R.anim.activity_out);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topMenuBtn:
			if (homeActivity.isDrawerOpen()) {
				homeActivity.changeDrawerVisibility(false);
			} else {
				homeActivity.changeDrawerVisibility(true);
			}
			break;
		case R.id.btnFilter:
			Intent intent = new Intent(getActivity(),
					FriendsFilterActivity.class);

			intent.putExtra(EXTRA_FILTER_RESULT, fFilterResultHolder);

			this.startActivityForResult(intent, Filter_request);

			getActivity().overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);

			break;
		case R.id.btnGrid:
			if(friendsListAdapter.getCount() == 0){
				return;
			}
			mPager.setCurrentItem(0);
			break;
		case R.id.btnList:
			if(friendsListAdapter.getCount() == 0){
				return;
			}
			mPager.setCurrentItem(1);
			break;

		default:
			break;
		}
	}

	public class ChildFragmentAdapter extends FragmentPagerAdapter {
		public ChildFragmentAdapter(FragmentManager fragmentManager) {
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
		
		if(isFriendsFetch){
			return;
		}
		if (!NetworkUtils.isNetworkAvailable(homeActivity)) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}

		if (homeActivity.socketService != null) {
			pd.show();
			JSONObject object = new JSONObject();

			try {
				object.put("user_id", AccountUtils.getUserId(getActivity()));
				object.put("device_id", DeviceUtils.getDeviceId(homeActivity));
				object.put("type", "unblocked");
				Log.e("json send ", object.toString());
				homeActivity.socketService.getAllFriends(object);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onBackendConnected() {
		getFriends();
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		pd.dismiss();
		if (event.equals(EventParams.METHOD_GET_ALL_FRIENDS)) {
			isFriendsFetch = true;
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
						calculateAge(friends.get(i).getDateOfBirth()));
				UserAccount user = friends.get(i);
				if (!dbHandler.friendExists(user.getId())) {
					FriendMessageModel messageModel = new FriendMessageModel();
					messageModel.setFriendId(user.getId());
					messageModel.setName(user.getUsername());
					dbHandler.addFriend(messageModel);
				} else {
					Log.e("User id already exists "
							+ dbHandler.getFriend(user.getId()).getName(),
							String.valueOf(user.getId()));
				}

			}

			userAccounts.addAll(friends);
			userAccountsCache.addAll(friends);
			friendsGridAdapter.notifyDataSetChanged();

			applyFilter(fFilterResultHolder);

			updateEmptyViewVisibility();
			/*
			 * Toast.makeText(homeActivity, "Friends data is " +
			 * data.toString(), Toast.LENGTH_SHORT).show();
			 */
		} else if (event.equals(EventParams.METHOD_BLOCK_USER_NOTIFY)) {
			JSONObject data = ((JSONObject) obj);
			try {

				String message = data.getString("message");

				int userId = data.getInt("user_id");
				Log.e("First user account size", "Size " + userAccounts.size());
				for (int i = 0; i < userAccounts.size(); i++) {

					if (userId == userAccounts.get(i).getId()) {
						userAccounts.remove(i);
					}
				}

				for (int i = 0; i < userAccountsCache.size(); i++) {
					if (userId == userAccountsCache.get(i).getId()) {
						userAccountsCache.remove(i);
					}
				}

				friendsListAdapter.notifyDataSetChanged();
				friendsGridAdapter.notifyDataSetChanged();

				updateEmptyViewVisibility();
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else if (event.equals(EventParams.METHOD_CHECK_IN_USER)) {
			JSONObject data = ((JSONObject) obj);
			try {

				String message = data.getString("message");

				int friendId = data.getInt("friend_id");
				String checkInType = data.getString("checkin_type");
				Log.e("First user account size", "Size " + userAccounts.size());
				for (int i = 0; i < userAccounts.size(); i++) {

					if (friendId == userAccounts.get(i).getId()) {
						userAccounts.get(i).setCheckinType(checkInType);
					}
				}

				for (int i = 0; i < userAccountsCache.size(); i++) {
					if (friendId == userAccountsCache.get(i).getId()) {
						userAccountsCache.get(i).setCheckinType(checkInType);
					}
				}

				friendsListAdapter.notifyDataSetChanged();
				friendsGridAdapter.notifyDataSetChanged();

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		if (pd != null) {
			pd.dismiss();
		}
	}

}
