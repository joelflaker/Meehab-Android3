package com.citrusbits.meehab;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.citrusbits.meehab.adapters.FilterExpandableFriendAdapter;
import com.citrusbits.meehab.fragments.FriendsFragment;
import com.citrusbits.meehab.model.ExpCategory;
import com.citrusbits.meehab.model.ExpChild;
import com.citrusbits.meehab.model.FriendFilterResultHolder;

public class FriendsFilterActivity extends Activity implements OnClickListener {

	private ExpandableListView expFriendsFilter;
	private FilterExpandableFriendAdapter mAdapter;
	private ArrayList<ExpCategory> categories;

	CheckBox cbOnlineNow;
	CheckBox cbWillingToSponsor;
	CheckBox cbHasKids;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_filter);

		// top back button
		findViewById(R.id.ibCancel).setOnClickListener(this);
		findViewById(R.id.ibApply).setOnClickListener(this);

		expFriendsFilter = (ExpandableListView) findViewById(R.id.expFriendsFilter);
		categories = buildDummyData();

		// Adding ArrayList data to ExpandableListView values
		mAdapter = new FilterExpandableFriendAdapter(this, expFriendsFilter,
				categories);

		// Set Adapter to ExpandableList Adapter
		expFriendsFilter.setAdapter(mAdapter);
	}

	private ArrayList<ExpCategory> buildDummyData() {

		// Creating ArrayList of type parent class to store parent class objects
		final ArrayList<ExpCategory> list = new ArrayList<ExpCategory>();
		// Create parent class object
		final ExpCategory parentHeader = new ExpCategory();
		parentHeader.setName("header");
		parentHeader.setValue("any");
		list.add(parentHeader);

		final ExpCategory parentFriendType = new ExpCategory();
		parentFriendType.setChildren(new ArrayList<ExpChild>());
		parentFriendType.setName("Friend Type");
		parentFriendType.setValue("Any");
		String[] friendTypeValues = getResources().getStringArray(
				R.array.friend_type_arr);

		for (String value : friendTypeValues) {
			final ExpChild child = new ExpChild();
			child.setName(value);
			parentFriendType.addChild(child);

		}
		// Adding Parent class object to ArrayList
		list.add(parentFriendType);

		final ExpCategory parentGender = new ExpCategory();
		parentGender.setChildren(new ArrayList<ExpChild>());
		parentGender.setName("Gender");
		parentGender.setValue("Any");
		String[] genderValues = getResources().getStringArray(
				R.array.gender_arr);

		for (String value : genderValues) {
			final ExpChild child = new ExpChild();
			child.setName(value);
			parentGender.addChild(child);

		}
		// Adding Parent class object to ArrayList
		list.add(parentGender);

		final ExpCategory parentAge = new ExpCategory();
		parentAge.setChildren(new ArrayList<ExpChild>());
		parentAge.setName("Age");
		parentAge.setValue("Any");
		String[] ageValues = getResources().getStringArray(R.array.age_arr);

		for (String value : ageValues) {
			final ExpChild child = new ExpChild();
			child.setName(value);
			parentAge.addChild(child);

		}
		// Adding Parent class object to ArrayList
		list.add(parentAge);

		final ExpCategory parentEthenticity = new ExpCategory();
		parentEthenticity.setChildren(new ArrayList<ExpChild>());
		parentEthenticity.setName("Ethenicity");
		parentEthenticity.setValue("Any");
		String[] athenticityValues = getResources().getStringArray(
				R.array.Ethenticity_arr);

		for (String value : athenticityValues) {
			final ExpChild child = new ExpChild();
			child.setName(value);
			parentEthenticity.addChild(child);

		}
		list.add(parentEthenticity);

		final ExpCategory parentMaterial = new ExpCategory();
		parentMaterial.setChildren(new ArrayList<ExpChild>());
		parentMaterial.setName("Material status");
		parentMaterial.setValue("Any");
		String[] materialValues = getResources().getStringArray(
				R.array.material_arr);

		for (String value : materialValues) {
			final ExpChild child = new ExpChild();
			child.setName(value);
			parentMaterial.addChild(child);

		}
		list.add(parentMaterial);

		final ExpCategory parentInterestedIn = new ExpCategory();
		parentInterestedIn.setChildren(new ArrayList<ExpChild>());
		parentInterestedIn.setName("Interested in");
		parentInterestedIn.setValue("Any");
		String[] interesteInValues = getResources().getStringArray(
				R.array.Interested_in_arr);

		for (String value : interesteInValues) {
			final ExpChild child = new ExpChild();
			child.setName(value);
			parentInterestedIn.addChild(child);

		}
		list.add(parentInterestedIn);

		final ExpCategory parentTimeSobar = new ExpCategory();
		parentTimeSobar.setChildren(new ArrayList<ExpChild>());
		parentTimeSobar.setName("Time sober");
		parentTimeSobar.setValue("Any");
		String[] timeSobarValues = getResources().getStringArray(
				R.array.time_sobar_arr);

		for (String value : timeSobarValues) {
			final ExpChild child = new ExpChild();
			child.setName(value);
			parentTimeSobar.addChild(child);

		}
		list.add(parentTimeSobar);

		final ExpCategory parentHeight = new ExpCategory();
		parentHeight.setChildren(new ArrayList<ExpChild>());
		parentHeight.setName("Height");
		parentHeight.setValue("Any");
		String[] heightValues = getResources().getStringArray(
				R.array.height_arr);

		for (String value : heightValues) {
			final ExpChild child = new ExpChild();
			child.setName(value);
			parentHeight.addChild(child);

		}
		list.add(parentHeight);

		final ExpCategory parentWeight = new ExpCategory();
		parentWeight.setChildren(new ArrayList<ExpChild>());
		parentWeight.setName("Weight");
		parentWeight.setValue("Any");

		final ExpChild selectAllChild = new ExpChild();
		selectAllChild.setName("Select All");
		parentWeight.addChild(selectAllChild);

		for (int i = 80; i <= 250; i = i + 5) {
			final ExpChild child = new ExpChild();
			child.setName(i + " lbs");
			parentWeight.addChild(child);

		}
		list.add(parentWeight);

		return list;
	}
	
	
	
	
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibCancel:
			onBackPressed();
			break;
		case R.id.ibApply:

			// View view = mAdapter.getHeaderView();
			

			FriendFilterResultHolder fFilterResultHolder = mAdapter
					.getFilterResultHolder();
			fFilterResultHolder.setOnlineNow(mAdapter.isOnline());
			Toast.makeText(FriendsFilterActivity.this, "Online: "+mAdapter.isOnline(), Toast.LENGTH_SHORT).show();
			
			fFilterResultHolder.setWillingToSponsor(mAdapter.isWillingtoSponosr());
			fFilterResultHolder.setHasKids(mAdapter.isHasKids());

			Intent returnIntent = new Intent();
			returnIntent.putExtra(FriendsFragment.EXTRA_FILTER_RESULT,
					fFilterResultHolder);
			setResult(RESULT_OK, returnIntent);
			finish();

			break;

		}
	}

	public void heightPickerDialog() {

		final String[] values = new String[] { "3'", "4'", "5'", "6'", "7'" };
		final String[] valuesInches = new String[] { "0''", "1''", "2''",
				"3''", "4''", "5''", "6''", "7''", "8''", "9''", "10''", "11''" };

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.height_picker, null);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setTitle("Select Height");

		final NumberPicker np = (NumberPicker) dialogView
				.findViewById(R.id.heightPickerFeet);
		np.setMaxValue(4);
		np.setMinValue(0);
		np.setDisplayedValues(values);
		np.setWrapSelectorWheel(false);

		final NumberPicker npInches = (NumberPicker) dialogView
				.findViewById(R.id.heightPickerInches);
		npInches.setMaxValue(11);
		npInches.setMinValue(0);
		npInches.setDisplayedValues(valuesInches);
		npInches.setWrapSelectorWheel(false);

		dialogBuilder.setPositiveButton("Set",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// btnHeight.setTag(np.getValue());
						// tvHeight.setText(values[np.getValue()] + " "
						// + valuesInches[npInches.getValue()]);

					}
				});

		dialogBuilder.setNegativeButton("Cancel", null);

		dialogBuilder.show();
	}

	public void weightPickerDialog() {

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.picker_weight, null);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setTitle("Select Weight");

		final NumberPicker np = (NumberPicker) dialogView
				.findViewById(R.id.picker);
		np.setMaxValue(500);
		np.setMinValue(50);
		np.setWrapSelectorWheel(false);

		dialogBuilder.setPositiveButton("Set",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// tvWeight.setText(String.valueOf(np.getValue()) +
						// " lbs");

					}
				});

		dialogBuilder.setNegativeButton("Cancel", null);

		dialogBuilder.show();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

}
