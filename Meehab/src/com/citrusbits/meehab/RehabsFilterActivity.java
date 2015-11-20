package com.citrusbits.meehab;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.adapters.FilterExpandableRehabAdapter;
import com.citrusbits.meehab.fragments.FilterResultHolder;
import com.citrusbits.meehab.model.ExpCategory;
import com.citrusbits.meehab.model.ExpChild;
import com.citrusbits.meehab.model.FriendFilterResultHolder;
import com.citrusbits.meehab.model.MeetingFilterModel;
import com.citrusbits.meehab.model.RehaabFilterResultHolder;

public class RehabsFilterActivity extends SocketActivity implements
		OnClickListener {

	public static final String MEETING_FILTER = "meeting_filter";
	private int ParentClickStatus = -1;
	private int ChildClickStatus = -1;
	private ExpandableListView expListFilter;
	private ArrayList<ExpCategory> categories;
	FilterExpandableRehabAdapter mAdapter;

	private Button btnZipCode;
	private EditText editZipCode;
	private Button btnDistance;
	private TextView txtDistance;

	private MeetingFilterModel filterModel = new MeetingFilterModel();
	private String[] distanceValues;

	// private ArrayList<Parent> parents;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_rehabs_filter);

		// top back button
		findViewById(R.id.ibCancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});

		findViewById(R.id.ibApply).setOnClickListener(this);

		btnZipCode = (Button) findViewById(R.id.btnZipCode);
		editZipCode = (EditText) findViewById(R.id.editZipCode);
		btnDistance = (Button) findViewById(R.id.btnDistance);
		txtDistance = (TextView) findViewById(R.id.txtDistance);

		expListFilter = (ExpandableListView) findViewById(R.id.expListFilter);

		btnZipCode.setOnClickListener(this);
		btnDistance.setOnClickListener(this);

		categories = buildDummyData();

		// Adding ArrayList data to ExpandableListView values
		mAdapter = new FilterExpandableRehabAdapter(this, expListFilter,
				categories);

		// Set Adapter to ExpandableList Adapter
		expListFilter.setAdapter(mAdapter);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibApply:
			// getherUserFilters();

			RehaabFilterResultHolder resultHolder = mAdapter
					.getFilterResultHolder();
			String zipCode = editZipCode.getText().toString().trim();
			if (zipCode.isEmpty()) {
				resultHolder.setanyZipCode(true);
			} else {
				resultHolder.setZipCode(zipCode);
			}

			String distance = txtDistance.getText().toString().trim();
			if (distance.equals("more than 50 miles")) {
				resultHolder.setanyDistance(true);
			} else {
				resultHolder.setDistance(distance);
			}

			Toast.makeText(RehabsFilterActivity.this, "Apply!",
					Toast.LENGTH_SHORT).show();

			getIntent().putExtra(MEETING_FILTER, resultHolder);
			setResult(RESULT_OK, getIntent());
			finish();
			break;

		case R.id.btnZipCode:
			editZipCode.requestFocus();
			break;
		case R.id.btnDistance:
			presentDistancePicker();
			break;

		default:
			break;
		}
	}

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
		parent.setName("Type");
		parent.setValue("Any");
		String[] daysValues = getResources().getStringArray(
				R.array.rehab_type_arr);

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
		parent2.setName("Insurance Accepted");
		parent2.setValue("Any");
		String[] timeRangeValues = getResources().getStringArray(
				R.array.insurance_arr);

		for (String value : timeRangeValues) {
			final ExpChild child = new ExpChild();
			child.setName(value);
			// parent2.getChildren().add(child);
			parent2.addChild(child);

		}
		// Adding Parent class object to ArrayList
		list.add(parent2);

		return list;
	}

}
