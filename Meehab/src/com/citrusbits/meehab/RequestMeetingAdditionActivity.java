package com.citrusbits.meehab;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.dialog.CustomTimePickerDialog;
import com.citrusbits.meehab.dialog.CustomTimePickerDialog.TimePickerDialogListener;
import com.citrusbits.meehab.dialog.DayPickerDialog;
import com.citrusbits.meehab.dialog.DayPickerDialog.SelectDayDialogListener;
import com.citrusbits.meehab.dialog.MeetingFacilityDialog;
import com.citrusbits.meehab.dialog.MeetingFacilityDialog.MeetingFacilityDialogListener;
import com.citrusbits.meehab.dialog.MeetingTypeDialog;
import com.citrusbits.meehab.dialog.MeetingTypeDialog.MeetingTypeDialogListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.citrusbits.meehab.utils.ValidationUtils;

public class RequestMeetingAdditionActivity extends SocketActivity implements
		OnClickListener, OnSocketResponseListener {

	EditText etPhoneNumber;
	EditText etMeetingName;
	TextView tvMeetingDay;
	TextView tvMeetingTime;
	EditText etAddress;
	EditText etCity;
	EditText etZipcode;
	TextView tvTypeOfMeeting;
	TextView tvTypeOfFacility;
	EditText etYourRelationshipToMeeting;

	private Context mContext;

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = this;
		pd = UtilityClass.getProgressDialog(this);

		setContentView(R.layout.activity_request_add_meeting);
		findViewById(R.id.ibSubmit).setOnClickListener(this);
		etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
		etMeetingName = (EditText) findViewById(R.id.etMeetingName);
		tvMeetingDay = (TextView) findViewById(R.id.tvMeetingDay);
		tvMeetingTime = (TextView) findViewById(R.id.tvMeetingTime);
		etAddress = (EditText) findViewById(R.id.etAddress);
		etCity = (EditText) findViewById(R.id.etCity);
		etZipcode = (EditText) findViewById(R.id.etZipcode);
		tvTypeOfMeeting = (TextView) findViewById(R.id.tvTypeOfMeeting);
		tvTypeOfFacility = (TextView) findViewById(R.id.tvTypeOfFacility);
		etYourRelationshipToMeeting = (EditText) findViewById(R.id.etYourRelationshipToMeeting);

		tvMeetingDay.setOnClickListener(this);
		tvMeetingTime.setOnClickListener(this);
		tvTypeOfMeeting.setOnClickListener(this);
		tvTypeOfFacility.setOnClickListener(this);

		// top back btn
		findViewById(R.id.topMenuBtn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.ibSubmit:
			submitMeeting();
			break;
		case R.id.tvMeetingDay:
			// Toast.makeText(RequestMeetingAdditionActivity.this,
			// "Meeting Day!", Toast.LENGTH_SHORT).show();

			String day = tvMeetingDay.getText().toString().trim();

			new DayPickerDialog(RequestMeetingAdditionActivity.this)
					.setDayDialogListener(new SelectDayDialogListener() {

						@Override
						public void onDoneClick(DayPickerDialog dialog,
								String daySelected) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							tvMeetingDay.setText(daySelected);
						}

						@Override
						public void onCancelClick(DayPickerDialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					}, day).show();

			break;
		case R.id.tvMeetingTime:
			// Toast.makeText(RequestMeetingAdditionActivity.this,
			// "Meeting Time!", Toast.LENGTH_SHORT).show();
			String time = tvMeetingTime.getText().toString().trim();

			new CustomTimePickerDialog(RequestMeetingAdditionActivity.this)
					.setTimePickerDialog(new TimePickerDialogListener() {

						@Override
						public void onDoneClick(CustomTimePickerDialog dialog,
								String dateSelected) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							tvMeetingTime.setText(dateSelected);
						}

						@Override
						public void onCancelClick(CustomTimePickerDialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();

						}
					}, time.isEmpty() ? null : getTime(time)).show();

			break;
		case R.id.tvTypeOfMeeting:
			String meetingType = tvTypeOfMeeting.getText().toString().trim();

			new MeetingTypeDialog(RequestMeetingAdditionActivity.this)
					.setMeetingTypeListener(new MeetingTypeDialogListener() {

						@Override
						public void onDoneClick(MeetingTypeDialog dialog,
								String meetingSelected) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							tvTypeOfMeeting.setText(meetingSelected);
						}

						@Override
						public void onCancelClick(MeetingTypeDialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();

						}
					}, meetingType).show();
			break;
		case R.id.tvTypeOfFacility:
			// Toast.makeText(RequestMeetingAdditionActivity.this,
			// "Type of Facility!", Toast.LENGTH_SHORT).show();
			String meetingFacility = tvTypeOfFacility.getText().toString()
					.trim();
			new MeetingFacilityDialog(RequestMeetingAdditionActivity.this)
					.setMeetingFacilityListener(
							new MeetingFacilityDialogListener() {

								@Override
								public void onDoneClick(
										MeetingFacilityDialog dialog,
										String meetingFacility) {
									// TODO Auto-generated method stub
									dialog.dismiss();
									tvTypeOfFacility.setText(meetingFacility);
								}

								@Override
								public void onCancelClick(
										MeetingFacilityDialog dialog) {
									// TODO Auto-generated method stub
									dialog.dismiss();

								}
							}, meetingFacility).show();
			break;
		}

	}

	public void submitMeeting() {
		String phoneNumber = etPhoneNumber.getText().toString().trim();
		String meetingName = etMeetingName.getText().toString().trim();
		String meetingDay = tvMeetingDay.getText().toString().trim();
		String meetingTime = tvMeetingTime.getText().toString().trim();
		String address = etAddress.getText().toString().trim();
		String meetingCity = etCity.getText().toString().trim();
		String zipCode = etZipcode.getText().toString().trim();
		String typeOfMeeting = tvTypeOfMeeting.getText().toString().trim();
		String typeOfFacility = tvTypeOfFacility.getText().toString().trim();
		String yourRelationshipToMeeting = etYourRelationshipToMeeting
				.getText().toString().trim();

		if (!ValidationUtils.validatePhoneNumber(phoneNumber)) {
			Toast.makeText(mContext,
					getString(R.string.enter_valid_phone_number),
					Toast.LENGTH_LONG).show();
			return;
		}

		if (meetingName.isEmpty()) {
			Toast.makeText(mContext, getString(R.string.enter_meeting_name),
					Toast.LENGTH_LONG).show();
			return;
		}

		if (meetingDay.isEmpty()) {
			Toast.makeText(mContext, getString(R.string.select_day),
					Toast.LENGTH_LONG).show();
			return;
		}

		if (meetingTime.isEmpty()) {
			Toast.makeText(mContext, getString(R.string.enter_meeting_name),
					Toast.LENGTH_LONG).show();
			return;
		}
		if (address.isEmpty()) {
			Toast.makeText(mContext, getString(R.string.enter_address),
					Toast.LENGTH_LONG).show();
			return;
		}
		if (meetingCity.isEmpty()) {
			Toast.makeText(mContext, getString(R.string.enter_city),
					Toast.LENGTH_LONG).show();
			return;
		}
		if (zipCode.isEmpty()) {
			Toast.makeText(mContext, getString(R.string.enter_zipcode),
					Toast.LENGTH_LONG).show();
			return;
		}
		if (typeOfMeeting.isEmpty()) {
			Toast.makeText(mContext,
					getString(R.string.select_type_of_meeting),
					Toast.LENGTH_LONG).show();
			return;
		}
		if (typeOfFacility.isEmpty()) {
			Toast.makeText(mContext,
					getString(R.string.select_type_of_facility),
					Toast.LENGTH_LONG).show();
			return;
		}
		if (yourRelationshipToMeeting.isEmpty()) {
			Toast.makeText(mContext,
					getString(R.string.enter_your_relationship_to_meeting),
					Toast.LENGTH_LONG).show();
			return;
		}

		if (!NetworkUtils.isNetworkAvailable(this)) {
			Toast.makeText(mContext,
					getString(R.string.no_internet_connection),
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		

		JSONObject json = new JSONObject();
		try {
			json.put("phone", phoneNumber);
			json.put("name", meetingName);
			json.put("codes", typeOfMeeting.split("-")[0].trim());
			json.put("on_day", meetingDay);
			json.put("on_time", meetingTime);
			// json.put("on_date",on);
			json.put("in_city", meetingCity);
			json.put("address", address);
			json.put("zip_code", zipCode);
			json.put("latitude", "33.1667");
			json.put("longitude", "73.6667");
			json.put("building_type", typeOfFacility);
			json.put("relation", yourRelationshipToMeeting);
			socketService.addMeeting(json);

			Log.e("json", json.toString());
			pd.show();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	public Calendar getTime(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		try {
			Date date = sdf.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Calendar.getInstance();
		}
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		// TODO Auto-generated method stub
		if (pd != null) {
			pd.dismiss();
		}
		if (event.equals(EventParams.EVENT_ADD_MEETING)) {
			Toast.makeText(mContext, "Meeting added successfully!", Toast.LENGTH_SHORT).show();
			onBackPressed();
		}
		
		

	}

	@Override
	public void onSocketResponseFailure(String message) {
		// TODO Auto-generated method stub
		if (pd != null) {
			pd.dismiss();
		}
		Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
	}
}
