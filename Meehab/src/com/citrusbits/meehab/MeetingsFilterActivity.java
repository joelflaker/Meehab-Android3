package com.citrusbits.meehab;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.citrusbits.meehab.adapters.FilterExpondableAdapter;
import com.citrusbits.meehab.dialog.DistancePickerDialog;
import com.citrusbits.meehab.dialog.DistancePickerDialog.DistancePickerDialogListener;
import com.citrusbits.meehab.dialog.RatingPickerDialog;
import com.citrusbits.meehab.dialog.RatingPickerDialog.RatingPickerDialogListener;
import com.citrusbits.meehab.fragments.FilterResultHolder;
import com.citrusbits.meehab.model.ExpCategory;
import com.citrusbits.meehab.model.ExpChild;
import com.citrusbits.meehab.model.MeetingFilterModel;

public class MeetingsFilterActivity extends SocketActivity implements
		OnClickListener {

	public static final String MEETING_FILTER = "meeting_filter";
	private int ParentClickStatus = -1;
	private int ChildClickStatus = -1;
	private ExpandableListView expListFilter;
	private ArrayList<ExpCategory> categories;
	FilterExpondableAdapter mAdapter;
	private Button btnMyFavorite;
	private CheckBox tglMyFavorite;
	private Button btnZipCode;
	private EditText editZipCode;
	private Button btnDistance;
	private TextView txtDistance;
	private TextView txtRating;
	private Button btnRating;

	private MeetingFilterModel filterModel = new MeetingFilterModel();
	private String[] distanceValues;

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
						onBackPressed();
					}
				});

		findViewById(R.id.topRightBtn).setOnClickListener(this);

		btnMyFavorite = (Button) findViewById(R.id.btnMyFavorite);
		tglMyFavorite = (CheckBox) findViewById(R.id.tglMyFavorite);
		btnZipCode = (Button) findViewById(R.id.btnZipCode);
		editZipCode = (EditText) findViewById(R.id.editZipCode);
		btnDistance = (Button) findViewById(R.id.btnDistance);
		txtDistance = (TextView) findViewById(R.id.txtDistance);
		btnRating = (Button) findViewById(R.id.btnRating);
		txtRating = (TextView) findViewById(R.id.txtRating);
		expListFilter = (ExpandableListView) findViewById(R.id.expListFilter);

		btnMyFavorite.setOnClickListener(this);
		btnZipCode.setOnClickListener(this);
		btnDistance.setOnClickListener(this);
		btnRating.setOnClickListener(this);

		categories = buildDummyData();

		// Adding ArrayList data to ExpandableListView values
		mAdapter = new FilterExpondableAdapter(this, expListFilter, categories);

		// Set Adapter to ExpandableList Adapter
		expListFilter.setAdapter(mAdapter);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topRightBtn:
			// getherUserFilters();

			FilterResultHolder resultHolder = mAdapter.getFilterResultHolder();

			resultHolder = appendFilter(resultHolder);

			getIntent().putExtra(MEETING_FILTER, resultHolder);
			setResult(RESULT_OK, getIntent());
			finish();
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
							// TODO Auto-generated method stub
							dialog.dismiss();
							txtDistance.setText(distanceSelected);
						}

						@Override
						public void onCancelClick(DistancePickerDialog dialog) {
							// TODO Auto-generated method stub
                              dialog.dismiss();
						}
					}, distance).show();
			break;
		case R.id.btnRating:
			// presentRatingPicker();
			String rating = txtRating.getText().toString().trim();
			new RatingPickerDialog(this).setRatingPickerListener(
					new RatingPickerDialogListener() {

						@Override
						public void onDoneClick(RatingPickerDialog dialog,
								String distanceSelected) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							txtRating.setText(distanceSelected);
						}

						@Override
						public void onCancelClick(RatingPickerDialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					}, rating).show();
			break;

		default:
			break;
		}
	}

	public FilterResultHolder appendFilter(FilterResultHolder filter) {

		filter.setFavourite(tglMyFavorite.isChecked());
		String zipcode = editZipCode.getText().toString().trim();
		filter.setAnyCode(zipcode.isEmpty() ? true : false);
		filter.setZipCode(zipcode);
		String distanceString = txtDistance.getText().toString().trim();
		filter.setAnyDistance(distanceString.equals("more than 50 miles") ? true
				: false);

		filter.setDistance(distanceString);

		String ratingString = txtRating.getText().toString().trim();

		filter.setAnyStar(ratingString.trim().equals("5 Stars") ? true : false);

		filter.setRating(ratingString);

		return filter;

	}

	/**
	 * 
	 */
	private void presentRatingPicker() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.picker_age, null);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setTitle("Set Rating");

		final String[] starValues = new String[] { "< 1 Star", "< 2 Stars",
				"< 3 Stars", "< 4 Stars", "5 Stars" };

		final NumberPicker np = (NumberPicker) dialogView
				.findViewById(R.id.picker);
		np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np.setMinValue(0);
		np.setMaxValue(starValues.length - 1);
		np.setDisplayedValues(starValues);
		np.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);

		dialogBuilder.setPositiveButton("Set",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						txtRating.setText(starValues[np.getValue()]);

						// update model
					}
				});
		dialogBuilder.setNegativeButton("Cancel", null);

		dialogBuilder.show();
	}

	/**
	 * 
	 */
	private void presentDistancePicker() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.picker_age, null);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setTitle("Set Distance");

		distanceValues = new String[] { "5 miles", "10 miles", "15 miles",
				"20 miles", "30 miles", "40 miles", "more than 50 miles" };

		final NumberPicker np = (NumberPicker) dialogView
				.findViewById(R.id.picker);
		np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np.setMinValue(0);
		np.setMaxValue(distanceValues.length - 1);
		np.setDisplayedValues(distanceValues);
		np.setWrapSelectorWheel(false);

		dialogBuilder.setPositiveButton("Set",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						txtDistance.setText(distanceValues[np.getValue()]);
						// set to model
					}
				});

		dialogBuilder.setNegativeButton("Cancel", null);

		dialogBuilder.show();

	}

	private ArrayList<ExpCategory> buildDummyData() {

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
