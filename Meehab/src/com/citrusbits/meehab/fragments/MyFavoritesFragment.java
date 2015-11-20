package com.citrusbits.meehab.fragments;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.citrusbits.meehab.HomeActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.adapters.FriendsAdapter;
import com.citrusbits.meehab.adapters.MeetingsListAdapter;
import com.citrusbits.meehab.model.FriendModel;
import com.citrusbits.meehab.model.TMeeting;

public class MyFavoritesFragment extends Fragment implements
		View.OnClickListener {

	private ChildFragmentAdapter mAdapter;
	private ViewPager mPager;
	static GridView grid;
	static ListView list;

	ArrayList<TMeeting> meetings;
	MeetingsListAdapter meetingsAdapter;
	ArrayList<FriendModel> friends;
	FriendsAdapter friendsAdapter;
	private Button btnMeetings, btnFriends;
	private boolean isMeetingsEdit = false;
	private boolean isFriendsEdit = false;
	private ViewGroup containerRemove;
	private Button btnRemove;
	private int mCurrentFragmentPosition = 0;
	private HomeActivity homeActivity;
	private Button topRightBtn;
	private EditText etFavouriteSearch;
	private View focus_thief;

	private ImageView ivMeetingBar;
	private ImageView ivFriendsBar;

	public MyFavoritesFragment() {
	}

	public MyFavoritesFragment(HomeActivity homeActivity) {
		this.homeActivity = homeActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_favorites, container,
				false);

		view.findViewById(R.id.ivMenu).setOnClickListener(this);
		ivMeetingBar = (ImageView) view.findViewById(R.id.ivMeetingBar);
		ivFriendsBar = (ImageView) view.findViewById(R.id.ivFriendsBar);

		topRightBtn = (Button) view.findViewById(R.id.topRightBtn);
		etFavouriteSearch = (EditText) view
				.findViewById(R.id.etFavouriteSearch);
		focus_thief = view.findViewById(R.id.focus_thief);
		etFavouriteSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String text = etFavouriteSearch.getText().toString();
				if (TextUtils.isEmpty(text)) {
					etFavouriteSearch.setCompoundDrawablesWithIntrinsicBounds(
							android.R.drawable.ic_menu_search, 0, 0, 0);
				} else {
					etFavouriteSearch.setCompoundDrawables(null, null, null,
							null);
				}
				// searchMeetings(text);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		mPager = (ViewPager) view.findViewById(R.id.pager);
		btnMeetings = (Button) view.findViewById(R.id.btnMeetings);
		btnFriends = (Button) view.findViewById(R.id.btnFriends);
		containerRemove = (ViewGroup) view.findViewById(R.id.containerRemove);
		btnRemove = (Button) view.findViewById(R.id.btnRemove);

		topRightBtn.setOnClickListener(this);
		btnRemove.setOnClickListener(this);
		btnMeetings.setOnClickListener(this);
		btnFriends.setOnClickListener(this);

		list = new ListView(getActivity());
		grid = new GridView(getActivity());
		grid.setLayoutParams(new GridView.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		grid.setBackgroundColor(Color.WHITE);
		grid.setNumColumns(3);
		grid.setColumnWidth(GridView.AUTO_FIT);
		grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

		// load fav meetings
		meetings = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			meetings.add(new TMeeting("Meeting " + i, null));
		}

		friends = new ArrayList<>();
		for (int i = 0; i < 200; i++) {
			friends.add(new FriendModel("Friend " + i, null));
		}
		// init friends adapter
		friendsAdapter = new FriendsAdapter(getActivity(),
				R.layout.grid_item_friend, friends);
		grid.setAdapter(friendsAdapter);
		friendsAdapter.notifyDataSetChanged();

		mAdapter = new ChildFragmentAdapter(getChildFragmentManager());
		
		mPager.setAdapter(mAdapter);
		
		mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// setUIButtonText(arg0);
				changeTab(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
			
		});

		changeTab(0);
		return view;
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

		case R.id.btnRemove:
			removeSelectedItems();
			break;
		case R.id.topRightBtn:
			// edit
		//	toggleEditMode();
			break;

		default:
			break;
		}
	}

	/**
	 * 
	 */
	private void removeSelectedItems() {
		toggleEditMode();
	}

	private void toggleEditMode() {
		if (mCurrentFragmentPosition == 0) {
			if (isMeetingsEdit) {
				isMeetingsEdit = false;
				topRightBtn.setText("Edit");
				for (TMeeting model : meetings) {
					model.setCheckBoxVisible(false);
				}
				// meetingsAdapter.clear();
				// meetingsAdapter.addAll(meetings);
				meetingsAdapter.notifyDataSetChanged();

				containerRemove.setVisibility(View.GONE);
			} else {
				isMeetingsEdit = true;
				topRightBtn.setText("Cancel");
				for (TMeeting model : meetings) {
					model.setCheckBoxVisible(true);
				}
				// meetingsAdapter.clear();
				// meetingsAdapter.addAll(meetings);
				meetingsAdapter.notifyDataSetChanged();

				containerRemove.setVisibility(View.VISIBLE);
			}
		} else if (mCurrentFragmentPosition == 1) {
			if (isFriendsEdit) {
				isFriendsEdit = false;
				topRightBtn.setText("Edit");
				for (FriendModel model : friends) {
					model.setCheckBoxVisible(false);
				}
				// meetingsAdapter.clear();
				// meetingsAdapter.addAll(meetings);
				meetingsAdapter.notifyDataSetChanged();

				containerRemove.setVisibility(View.GONE);
			} else {
				isFriendsEdit = true;
				topRightBtn.setText("Cancel");
				for (FriendModel model : friends) {
					model.setCheckBoxVisible(true);
				}
				// meetingsAdapter.clear();
				// meetingsAdapter.addAll(meetings);
				meetingsAdapter.notifyDataSetChanged();

				containerRemove.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * @param string
	 */
	protected void toast(String string) {
		Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
	}

	public static class ChildFragmentAdapter extends FragmentPagerAdapter {
		public ChildFragmentAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return 2;
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

	public static class FriendsGridFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			return grid;
		}
	}

	public static class MeetingsListFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			return list;
		}
	}
	/**
	 * @param position
	 */
	/*
	 * public void setUIButtonText(int position) { mCurrentFragmentPosition =
	 * position; if(position == 0){ if(isMeetingsEdit){
	 * containerRemove.setVisibility(View.VISIBLE);
	 * topRightBtn.setText("Cancel"); }else{
	 * containerRemove.setVisibility(View.GONE); topRightBtn.setText("Edit"); }
	 * friendBtn.setBackgroundColor(Color.GRAY);
	 * meetingBtn.setBackgroundColor(Color.WHITE); }else if (position == 1){
	 * if(isFriendsEdit){ containerRemove.setVisibility(View.VISIBLE);
	 * topRightBtn.setText("Cancel"); }else{
	 * containerRemove.setVisibility(View.GONE); topRightBtn.setText("Edit"); }
	 * meetingBtn.setBackgroundColor(Color.GRAY);
	 * friendBtn.setBackgroundColor(Color.WHITE); } }
	 */
}