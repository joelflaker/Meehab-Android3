package com.citrusbits.meehab.ui.meetings;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.adapters.FilterExpondableAdapter;
import com.citrusbits.meehab.ui.dialog.DistancePickerDialog;
import com.citrusbits.meehab.ui.dialog.DistancePickerDialog.DistancePickerDialogListener;
import com.citrusbits.meehab.ui.fragments.FilterResultHolder;
import com.citrusbits.meehab.model.ExpCategory;
import com.citrusbits.meehab.model.ExpChild;
import com.citrusbits.meehab.model.MeetingFilterModel;
import com.citrusbits.meehab.ui.SocketActivity;

public class MeetingsFilterActivity extends SocketActivity implements
		OnClickListener {
	public static final int CLEAR_FILTER = 11;
	public static final String MEETING_FILTER = "meeting_filter";
	private boolean isFilterCleared;
	private ExpandableListView expListFilter;
	private static ArrayList<ExpCategory> cacheCategories = new ArrayList<>();
	private ArrayList<ExpCategory> categories = new ArrayList<>();
	FilterExpondableAdapter mAdapter;
	private Button btnMyFavorite;
	private CheckBox tglMyFavorite;
	private Button btnZipCode;
	private EditText editZipCode;
	private Button btnDistance;
	private TextView txtDistance;
//	private TextView txtRating;
//	private Button btnRating;

	private String[] distanceValues;

	private static boolean mFavorite;
	private static String mZipcode = "";
	private static String mDistance = "";
	private static int mRating = 0;
	private RatingBar rating;

	// private ArrayList<Parent> parents;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_meeting_filter);

		// top back button
		findViewById(R.id.topMenuBtn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});

		findViewById(R.id.ibClear).setOnClickListener(this);

		findViewById(R.id.ibClear).setOnClickListener(this);

		findViewById(R.id.btnApply).setOnClickListener(this);

		btnMyFavorite = (Button) findViewById(R.id.btnMyFavorite);
		tglMyFavorite = (CheckBox) findViewById(R.id.tglMyFavorite);
		btnZipCode = (Button) findViewById(R.id.btnZipCode);
		editZipCode = (EditText) findViewById(R.id.editZipCode);
		btnDistance = (Button) findViewById(R.id.btnDistance);
		txtDistance = (TextView) findViewById(R.id.txtDistance);
//		btnRating = (Button) findViewById(R.id.btnRating);
//		txtRating = (TextView) findViewById(R.id.txtRating);
		rating = (RatingBar) findViewById(R.id.rating);
		expListFilter = (ExpandableListView) findViewById(R.id.expListFilter);

		btnMyFavorite.setOnClickListener(this);
		btnZipCode.setOnClickListener(this);
		btnDistance.setOnClickListener(this);
//		btnRating.setOnClickListener(this);

		if (cacheCategories.isEmpty()) {
			categories = buildDefaultFilter();
		} else {
			categories.addAll(cacheCategories);
		}

		updateUI();

	}

	private void updateUI() {
		if (!mDistance.isEmpty()) {
			txtDistance.setText(mDistance);
		}
		rating.setRating(mRating);
		editZipCode.setText(mZipcode);
		tglMyFavorite.setChecked(mFavorite);

		// Adding ArrayList data to ExpandableListView values
		mAdapter = new FilterExpondableAdapter(this, expListFilter, categories);
		mAdapter.setFilterResultHolder((FilterResultHolder) getIntent()
				.getSerializableExtra(MEETING_FILTER));

		// Set Adapter to ExpandableList Adapter
		expListFilter.setAdapter(mAdapter);
	}

	public static void applyClear() {
		cacheCategories.clear();
		mDistance = "";
		mRating = 0;
		mZipcode = "";
		mFavorite = false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnApply:

			FilterResultHolder resultHolder = mAdapter.getFilterResultHolder();
			cacheCategories.clear();
			cacheCategories.addAll(categories);
			resultHolder = appendFilter(resultHolder);

			//this will clear filter
			if(/*isFilterCleared || */!isThereAnyFilter(resultHolder)){
				onBackPressed();
				return;
			}

			getIntent().putExtra(MEETING_FILTER, resultHolder);
			setResult(RESULT_OK, getIntent());
			finish();
			break;
		case R.id.ibClear:
			applyClear();
			categories = buildDefaultFilter();
			FilterResultHolder defaultFilter = new FilterResultHolder();
			getIntent().putExtra(MEETING_FILTER, defaultFilter);
			isFilterCleared = true;
			mAdapter.setFilterResultHolder(defaultFilter);
			updateUI();
			mAdapter.notifyDataSetChanged();
			break;
		case R.id.btnMyFavorite:
			tglMyFavorite.setChecked(!tglMyFavorite.isChecked());
			break;
		case R.id.btnZipCode:
			editZipCode.requestFocus();
			break;
		case R.id.btnDistance:
			// presentDistancePicker();
			String distance = txtDistance.getText().toString().trim();
			new DistancePickerDialog(this).setDistancePickerListener(
					new DistancePickerDialogListener() {

						@Override
						public void onDoneClick(DistancePickerDialog dialog,
								String distanceSelected) {
							dialog.dismiss();
							txtDistance.setText(distanceSelected);
						}

						@Override
						public void onCancelClick(DistancePickerDialog dialog) {
							dialog.dismiss();
						}
					}, distance).show();
			break;
//		case R.id.btnRating:
//			// presentRatingPicker();
//			String rating = txtRating.getText().toString().trim();
//			new RatingPickerDialog(this).setRatingPickerListener(
//					new RatingPickerDialogListener() {
//
//						@Override
//						public void onDoneClick(RatingPickerDialog dialog,
//								String distanceSelected) {
//							dialog.dismiss();
//							txtRating.setText(distanceSelected);
//						}
//
//						@Override
//						public void onCancelClick(RatingPickerDialog dialog) {
//							dialog.dismiss();
//						}
//					}, rating).show();
//			break;

		default:
			break;
		}
	}

	private boolean isThereAnyFilter(FilterResultHolder filter) {
		return filter.getAnyDay() || filter.getAnyTime()
				|| filter.getAnyType() || filter.getAnyCode()
				|| filter.isAnyDistance() || filter.getRating() != 0;
	}

	@Override
	public void onBackPressed() {
		if (isFilterCleared) {
			setResult(CLEAR_FILTER, new Intent());
			finish();
		}else {
			super.onBackPressed();
		}
	}

	public FilterResultHolder appendFilter(FilterResultHolder filter) {

		filter.setFavourite(tglMyFavorite.isChecked());
		String zipcode = editZipCode.getText().toString().trim();
		filter.setAnyCode(zipcode.isEmpty() ? true : false);
		filter.setZipCode(zipcode);
		String distanceString = txtDistance.getText().toString().trim();
		filter.setAnyDistance(distanceString.equalsIgnoreCase("any"));
		distanceString = distanceString.replace("Any","");
		filter.setDistance(distanceString);

//		String ratingString = txtRating.getText().toString().trim();
		int ratingInt = (int) rating.getRating();

//		filter.setAnyStar(ratingString.trim().equalsIgnoreCase("5 Stars") ? true : false);
		filter.setAnyStar(ratingInt != 0 ? true : false);

		filter.setRating(ratingInt);

		mDistance = distanceString;
		mRating = ratingInt;
		mZipcode = zipcode;
		mFavorite = tglMyFavorite.isChecked();

		return filter;

	}

	private ArrayList<ExpCategory> buildDefaultFilter() {

		// Creating ArrayList of type parent class to store parent class objects
		final ArrayList<ExpCategory> list = new ArrayList<ExpCategory>();
		// Create parent class object

		final ExpCategory parent = new ExpCategory();
		parent.setChildren(new ArrayList<ExpChild>());
		parent.setName("Days");
		parent.setValue("Any");
		String[] daysValues = getResources().getStringArray(R.array.daysValues);

		for (String value : daysValues) {
			final ExpChild child = new ExpChild();
			child.setName(value);
			// parent.getChildren().add(child);
			parent.addChild(child);

		}
		// Adding Parent class object to ArrayList
		list.add(parent);

		final ExpCategory parent2 = new ExpCategory();
		parent2.setChildren(new ArrayList<ExpChild>());
		parent2.setName("Time Range");
		parent2.setValue("Anytime");
		String[] timeRangeValues = getResources().getStringArray(
				R.array.timeRangeValues);

		for (String value : timeRangeValues) {
			final ExpChild child = new ExpChild();
			child.setName(value);
			// parent2.getChildren().add(child);
			parent2.addChild(child);

		}
		// Adding Parent class object to ArrayList
		list.add(parent2);

		final ExpCategory parent3 = new ExpCategory();
		parent3.setChildren(new ArrayList<ExpChild>());
		parent3.setName("Type");
		parent3.setValue("Any");
		String[] typesValues = getResources().getStringArray(
				R.array.meetingsTypesValues);

		for (String value : typesValues) {
			final ExpChild child = new ExpChild();
			child.setName(value);
			// parent3.getChildren().add(child);

			parent3.addChild(child);

		}
		// Adding Parent class object to ArrayList
		list.add(parent3);

		return list;
	}

}
