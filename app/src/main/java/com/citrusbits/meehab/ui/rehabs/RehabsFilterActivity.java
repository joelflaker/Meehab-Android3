package com.citrusbits.meehab.ui.rehabs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.adapters.FilterExpandableRehabAdapter;
import com.citrusbits.meehab.app.MeehabApp;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.ui.dialog.DistancePickerDialog;
import com.citrusbits.meehab.ui.dialog.DistancePickerDialog.DistancePickerDialogListener;
import com.citrusbits.meehab.model.ExpCategory;
import com.citrusbits.meehab.model.ExpChild;
import com.citrusbits.meehab.model.RehaabFilterResultHolder;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.ui.SocketActivity;
import com.citrusbits.meehab.utils.KeyboardVisibilityListener;
import com.citrusbits.meehab.utils.UtilityClass;

import org.json.JSONArray;
import org.json.JSONObject;

public class RehabsFilterActivity extends SocketActivity implements
		OnClickListener, OnSocketResponseListener {

	public static final String MEETING_FILTER = "meeting_filter";
	public static final int CLEAR_FILTER = 2342;
	private ExpandableListView expListFilter;
	private ArrayList<ExpCategory> categories;
	FilterExpandableRehabAdapter mAdapter;

	private Button btnZipCode;
	private EditText editZipCode;
	private Button btnDistance;
	private TextView txtDistance;

	public static RehaabFilterResultHolder filterModel = new RehaabFilterResultHolder();
	private CheckBox tglOpenNow;
	private boolean isFilterCleared;
	private RehaabFilterResultHolder preFilterModel;
	private View inputAccessoryView;
    private View btnApply;

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
						filterModel = preFilterModel;
						finish();
					}
				});

        btnApply = findViewById(R.id.ibApply);
        btnApply.setOnClickListener(this);

		findViewById(R.id.btnOpenNow).setOnClickListener(this);
		tglOpenNow = (CheckBox)findViewById(R.id.tglOpenNow);

		btnZipCode = (Button) findViewById(R.id.btnZipCode);
		editZipCode = (EditText) findViewById(R.id.editZipCode);
		btnDistance = (Button) findViewById(R.id.btnDistance);
		txtDistance = (TextView) findViewById(R.id.txtDistance);

		expListFilter = (ExpandableListView) findViewById(R.id.expListFilter);

		findViewById(R.id.ibClear).setOnClickListener(this);

		findViewById(R.id.btnDone).setOnClickListener(this);
		findViewById(R.id.btnCancel).setOnClickListener(this);
		inputAccessoryView =  findViewById(R.id.inputAccessoryView);
		UtilityClass.setKeyboardVisibilityListener(this, new KeyboardVisibilityListener() {
			@Override
			public void onKeyboardVisibilityChanged(boolean keyboardVisible) {
				if(keyboardVisible){
                    btnApply.setVisibility(View.GONE);
                    inputAccessoryView.setVisibility(View.VISIBLE);
                    inputAccessoryView.animate().alpha(1).setDuration(100).start();
                }else {
                    btnApply.setVisibility(View.VISIBLE);
                    inputAccessoryView.setVisibility(View.GONE);
                    inputAccessoryView.setAlpha(0);
                }
			}
		});

		btnZipCode.setOnClickListener(this);
		btnDistance.setOnClickListener(this);

		categories = buildDefaultFilter();
		presetFilter();
		//record current filter
		preFilterModel = new RehaabFilterResultHolder(filterModel);
	}

	@Override
	public void onBackendConnected() {
		socketService.listOfInsurances();
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		if(event.equals(EventParams.EVENT_INSURANCE_LIST)) {
			//update insurance list
			JSONObject data = (JSONObject)obj;
			JSONArray insurances = data.optJSONArray("insurances");
			if(insurances != null){
				String[] insurancesString = new String[insurances.length()];
				for (int i = 0; i < insurances.length(); i++) {
					insurancesString[i] = insurances.optJSONObject(i).optString("name");
				}
				Arrays.sort(insurancesString);
				final ExpCategory parent2 = new ExpCategory();
				parent2.setChildren(new ArrayList<ExpChild>());
				parent2.setName("Insurance Accepted");
				parent2.setValue("Any");

				final ExpChild topItem1 = new ExpChild();
				topItem1.setName("Select All/Deselect All");
				parent2.addChild(topItem1);

				for (String value : insurancesString) {
					final ExpChild child = new ExpChild();
					child.setName(value);
					// parent2.getChildren().add(child);
					parent2.addChild(child);

				}
				// Adding Parent class object to ArrayList
				categories.set(1,parent2);
				presetFilter();
			}
		}
	}

	@Override
	public void onSocketResponseFailure(String event, String message) {
		if(EventParams.EVENT_INSURANCE_LIST.equals(event)){
			MeehabApp.toast(""+message);
		}
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
		if(filterModel.isAnyDistance()){
			txtDistance.setText("Any");
		}else {
			txtDistance.setText(filterModel.getDistance());
		}

		// Adding ArrayList data to ExpandableListView values
		mAdapter = new FilterExpandableRehabAdapter(this, expListFilter,
				categories,filterModel);

		// Set Adapter to ExpandableList Adapter
		expListFilter.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnCancel:
				editZipCode.setText("");
				//break is intentional ignored
			case R.id.btnDone:
				UtilityClass.hideSoftKeyboard(getCurrentFocus());
				break;
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
				filterModel.setAnyZipCode(true);
			} else {
				filterModel.setAnyZipCode(false);
				filterModel.setZipCode(zipCode);
			}

			String distance = txtDistance.getText().toString();
			if (distance.equalsIgnoreCase("any")) {
				filterModel.setAnyDistance(true);
			} else {
				filterModel.setAnyDistance(false);
				filterModel.setDistance(distance);
			}

			//this will clear filter
			if(/*isFilterCleared || */!isThereAnyFilter(filterModel)){
				onBackPressed();
				return;
			}

			getIntent().putExtra(MEETING_FILTER, filterModel);
			setResult(RESULT_OK, getIntent());
			finish();
			break;
		case R.id.ibClear:
			filterModel = new RehaabFilterResultHolder();
			presetFilter();
			break;
		case R.id.btnZipCode:
			editZipCode.requestFocus();
            UtilityClass.showSoftKeyboard(getCurrentFocus());
            break;
		case R.id.btnDistance:
			presentDistancePicker();
			break;

		default:
			break;
		}
	}

	private boolean isThereAnyFilter(RehaabFilterResultHolder filter) {
		return filter.isOpenNow() || filter.isAnyType()
				|| filter.isAnyInsuranceAccepted() || filter.isAnyZipCode()
				|| filter.isAnyDistance();
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

	private ArrayList<ExpCategory> buildDefaultFilter() {

		// Creating ArrayList of type parent class to store parent class objects
		final ArrayList<ExpCategory> list = new ArrayList<ExpCategory>();
		// Create parent class object

		final ExpCategory parent = new ExpCategory();
		parent.setChildren(new ArrayList<ExpChild>());
		parent.setName("Type");
		parent.setValue("Any");
		String[] rehabFacilityArr = getResources().getStringArray(
				R.array.rehab_facility_arr);

//		final ExpChild topItem = new ExpChild();
//		topItem.setName("Select All/Deselect All");
//		parent.addChild(topItem);

		for (String value : rehabFacilityArr) {
			final ExpChild child = new ExpChild();
			child.setName(value);
			parent.addChild(child);

		}
		// Adding Parent class object to ArrayList
		list.add(parent);

		final ExpCategory parent2 = new ExpCategory();
		parent2.setChildren(new ArrayList<ExpChild>());
		parent2.setName("Insurance Accepted");
		parent2.setValue("Any");
		String[] insuranceValues = getResources().getStringArray(
				R.array.insurance_arr);

		final ExpChild topItem1 = new ExpChild();
		topItem1.setName("Select All/Deselect All");
		parent2.addChild(topItem1);

		for (String value : insuranceValues) {
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
