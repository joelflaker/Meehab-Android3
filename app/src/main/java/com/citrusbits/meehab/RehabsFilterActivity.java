package com.citrusbits.meehab;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.citrusbits.meehab.adapters.FilterExpandableRehabAdapter;
import com.citrusbits.meehab.dialog.DistancePickerDialog;
import com.citrusbits.meehab.dialog.DistancePickerDialog.DistancePickerDialogListener;
import com.citrusbits.meehab.fragments.FilterResultHolder;
import com.citrusbits.meehab.model.ExpCategory;
import com.citrusbits.meehab.model.ExpChild;
import com.citrusbits.meehab.model.FriendFilterResultHolder;
import com.citrusbits.meehab.model.MeetingFilterModel;
import com.citrusbits.meehab.model.RehaabFilterResultHolder;

public class RehabsFilterActivity extends SocketActivity implements
		OnClickListener {

	public static final String MEETING_FILTER = "meeting_filter";
	public static final int CLEAR_FILTER = 2342;
	private int ParentClickStatus = -1;
	private int ChildClickStatus = -1;
	private ExpandableListView expListFilter;
	private ArrayList<ExpCategory> categories;
	FilterExpandableRehabAdapter mAdapter;

	private Button btnZipCode;
	private EditText editZipCode;
	private Button btnDistance;
	private TextView txtDistance;

	public static RehaabFilterResultHolder filterModel = new RehaabFilterResultHolder();
	private String[] distanceValues;
	private CheckBox tglOpenNow;

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

		findViewById(R.id.btnOpenNow).setOnClickListener(this);
		tglOpenNow = (CheckBox)findViewById(R.id.tglOpenNow);
		
		btnZipCode = (Button) findViewById(R.id.btnZipCode);
		editZipCode = (EditText) findViewById(R.id.editZipCode);
		btnDistance = (Button) findViewById(R.id.btnDistance);
		txtDistance = (TextView) findViewById(R.id.txtDistance);

		expListFilter = (ExpandableListView) findViewById(R.id.expListFilter);
		
		findViewById(R.id.ibClear).setOnClickListener(this);

		btnZipCode.setOnClickListener(this);
		btnDistance.setOnClickListener(this);

		categories = buildDummyData();
		presetFilter();
		

		// Adding ArrayList data to ExpandableListView values
		mAdapter = new FilterExpandableRehabAdapter(this, expListFilter,
				categories,filterModel);

		// Set Adapter to ExpandableList Adapter
		expListFilter.setAdapter(mAdapter);

	}

	private void presetFilter() {
		
		//open now?
		if (filterModel.isOpenNow()) {
			tglOpenNow.setChecked(true);
		}else{
			tglOpenNow.setChecked(false);
		}
		
		//types
		List<ExpChild> types = categories.get(0)
				.getChildren();

//		boolean allChecked = true;
//		String name = types.get(childPosition).getName();

		if (filterModel.isSelectAllType()) {
			types.get(0).setName("Deselect All");
			for(ExpChild child: types){
				
					child.setChecked(true);
			}
		}else{
			ArrayList<String > typesArr = (ArrayList<String>) filterModel.getRehabType();
				for(ExpChild child: types){
					if(typesArr.contains(child.getName())){
						child.setChecked(true);
					}else{
						child.setChecked(false);
					}
			}
		}
//			filterModel.setanyType(false);
//			filterModel.addRehabType(name);
//		} else if (groupPosition == 1) {
//			filterModel.setanyInsuranceAccepted(false);
//			filterModel.addInsuranceAccepted(name);
//		}
		
		//insurance
		List<ExpChild> insurances = categories.get(1)
				.getChildren();
		if (filterModel.isSelectAllInsurance()) {
			insurances.get(0).setName("Deselect All");
			for(ExpChild child: insurances){
					child.setChecked(true);
			}
		}else{
			ArrayList<String > insArr = (ArrayList<String>) filterModel.getInsuranceAccepted();
				for(ExpChild child: insurances){
					if(insArr.contains(child.getName())){
						child.setChecked(true);
					}else{
						child.setChecked(false);
					}
			}
		}
		//ZIP
		editZipCode.setText(filterModel.getZipCode());
		
		//distance
		if(filterModel.is50Distance()){
			txtDistance.setText(R.string.more_than_50_miles);
		}else{
			String miles = filterModel.getDistance();
			txtDistance.setText(miles == null ? "5 Miles": miles);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnMyFavorite:
			tglOpenNow.setChecked(!tglOpenNow.isChecked());
			break;
		case R.id.ibApply:
			// getherUserFilters();

			filterModel = mAdapter.getFilterResultHolder();
			filterModel.apply(true);
			
			filterModel.setOpenNow(tglOpenNow.isChecked());
			String zipCode = editZipCode.getText().toString().trim();
			if (zipCode.isEmpty()) {
				filterModel.setanyZipCode(true);
			} else {
				filterModel.setanyZipCode(false);
				filterModel.setZipCode(zipCode);
			}

			String distance = txtDistance.getText().toString().trim();
			if (distance.equalsIgnoreCase(getString(R.string.more_than_50_miles))) {
				filterModel.setanyDistance(true);
			} else {
				filterModel.setanyDistance(false);
				filterModel.setDistance(distance);
			}

//			Toast.makeText(RehabsFilterActivity.this, "Apply!",
//					Toast.LENGTH_SHORT).show();

			getIntent().putExtra(MEETING_FILTER, filterModel);
			setResult(RESULT_OK, getIntent());
			finish();
			break;
		case R.id.ibClear:
			filterModel.apply(false);
			setResult(CLEAR_FILTER, new Intent());
			filterModel = new RehaabFilterResultHolder();
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
				R.array.rehab_facility_arr);
		
		final ExpChild topItem = new ExpChild();
		topItem.setName("Select All");
		parent.addChild(topItem);

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

		final ExpChild topItem1 = new ExpChild();
		topItem1.setName("Select All");
		parent2.addChild(topItem1);
		
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
