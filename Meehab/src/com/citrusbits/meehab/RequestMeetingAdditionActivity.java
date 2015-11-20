package com.citrusbits.meehab;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.citrusbits.meehab.dialog.CustomTimePickerDialog;
import com.citrusbits.meehab.dialog.CustomTimePickerDialog.TimePickerDialogListener;
import com.citrusbits.meehab.dialog.DayPickerDialog;
import com.citrusbits.meehab.dialog.DayPickerDialog.SelectDayDialogListener;
import com.citrusbits.meehab.dialog.MeetingFacilityDialog;
import com.citrusbits.meehab.dialog.MeetingFacilityDialog.MeetingFacilityDialogListener;
import com.citrusbits.meehab.dialog.MeetingTypeDialog;
import com.citrusbits.meehab.dialog.MeetingTypeDialog.MeetingTypeDialogListener;

public class RequestMeetingAdditionActivity extends Activity implements
		OnClickListener {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
			onBackPressed();
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
}
