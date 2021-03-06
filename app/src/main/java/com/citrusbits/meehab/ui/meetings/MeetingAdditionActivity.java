package com.citrusbits.meehab.ui.meetings;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.ui.dialog.CustomTimePickerDialog;
import com.citrusbits.meehab.ui.dialog.CustomTimePickerDialog.TimePickerDialogListener;
import com.citrusbits.meehab.ui.dialog.DayPickerDialog;
import com.citrusbits.meehab.ui.dialog.DayPickerDialog.SelectDayDialogListener;
import com.citrusbits.meehab.ui.dialog.MeetingFacilityDialog;
import com.citrusbits.meehab.ui.dialog.MeetingFacilityDialog.MeetingFacilityDialogListener;
import com.citrusbits.meehab.ui.dialog.MeetingTypeDialog;
import com.citrusbits.meehab.ui.dialog.MeetingTypeDialog.MeetingTypeDialogListener;
import com.citrusbits.meehab.ui.dialog.MessageDialog;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.ui.SocketActivity;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.citrusbits.meehab.utils.ValidationUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

public class MeetingAdditionActivity extends SocketActivity implements
		OnClickListener, OnSocketResponseListener,GoogleApiClient.ConnectionCallbacks {

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";

	private static final String OUT_JSON = "/json";

	private static final String API_KEY = "AIzaSyAwwH_CGi-GyPYmDqEVDv3eC9AfOgsLO7U";

	private static final String TAG = "places";

	private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2322;

	private static final int REQUEST_PLACE_PICKER = 2342;


	EditText etPhoneNumber;
	EditText etMeetingName;
	TextView tvMeetingDay;
	TextView tvMeetingTime;
	EditText etAddress;
	EditText etCity;
	EditText etZipcode;
	TextView tvTypeOfMeeting;
	TextView tvTypeOfFacility;
	private View rlOtherFacility;
	private EditText etOtherFacilities;
	EditText etYourRelationshipToMeeting;

	private Context mContext;

	private Dialog pd;

	private String lat = "33.1667";
	private String lng = "73.6667";
	private View viewFocusHacker;
	private List<String> selectedDays = new ArrayList<>();
	private boolean isGoogleConnected;
    private String mAddress;


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
		viewFocusHacker = findViewById(R.id.viewFocusHacker);
		tvMeetingTime = (TextView) findViewById(R.id.tvMeetingTime);
		etAddress = (EditText) findViewById(R.id.etAddress);
		etCity = (EditText) findViewById(R.id.etCity);
		etZipcode = (EditText) findViewById(R.id.etZipcode);
		etOtherFacilities = (EditText) findViewById(R.id.etOtherFacilities);
		rlOtherFacility = findViewById(R.id.rlOtherFacility);
		tvTypeOfMeeting = (TextView) findViewById(R.id.tvTypeOfMeeting);
		findViewById(R.id.ivOtherCross).setOnClickListener(this);
		tvTypeOfFacility = (TextView) findViewById(R.id.tvTypeOfFacility);
		etYourRelationshipToMeeting = (EditText) findViewById(R.id.etYourRelationshipToMeeting);

		//places
//		aetAddress.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item_spinner));
//		aetAddress.setOnItemClickListener(this);

		etAddress.setFocusable(false);
		etAddress.setClickable(true);
		etAddress.setOnClickListener(this);
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

		etPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher("US"));
		etPhoneNumber.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (editPhone.getText().toString().trim().length() > 13) {
//                    btnNext.setVisibility(View.VISIBLE);
//                } else {
//                    btnNext.setVisibility(View.INVISIBLE);
//                }
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		etPhoneNumber.requestFocus();
		showKeyboard(etPhoneNumber);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ibSubmit:
				submitMeeting();
				break;
			case R.id.etAddress:
				findPlace(v);
				break;
			case R.id.ivOtherCross:
				etOtherFacilities.setText("");
				break;
			case R.id.tvMeetingDay:
				hideKeyboard();
				final String[] daysArray = getResources().getStringArray(
						R.array.days_arr);
				final List<String> daysList = Arrays.asList(daysArray);

				final boolean[] checkedDays = new boolean[daysArray.length];


				for (String selectedDay : selectedDays) {
					checkedDays[daysList.indexOf(selectedDay)] = true;
				}

				final List<String> tempSelectedDays = new ArrayList<>(selectedDays);

				new AlertDialog.Builder(this)
						.setTitle("Select Days")
						.setNegativeButton("Cancel",null)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								selectedDays.clear();
								selectedDays.addAll(tempSelectedDays);

								StringBuilder stringBuilder = new StringBuilder();
								boolean commaNeeded = false;
								for (String selectedDay : selectedDays) {
									stringBuilder
											.append(commaNeeded? ", " : "")
											.append(selectedDay);
									commaNeeded = true;
								}
								tvMeetingDay.setText(stringBuilder.toString());
							}
						})
						.setMultiChoiceItems(daysArray, checkedDays, new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which, boolean isChecked) {

								if(isChecked){
									tempSelectedDays.add(daysArray[which]);
								}else {
									tempSelectedDays.remove(daysArray[which]);
								}
							}
						}).show();

				break;
			case R.id.tvMeetingTime:
				// Toast.makeText(RequestMeetingAdditionActivity.this,
				// "Meeting Time!", Toast.LENGTH_SHORT).show();
				String time = tvMeetingTime.getText().toString().trim();

				hideKeyboard();
				new CustomTimePickerDialog(MeetingAdditionActivity.this)
						.setTimePickerDialog(new TimePickerDialogListener() {

							@Override
							public void onDoneClick(CustomTimePickerDialog dialog,
													String dateSelected) {
								dialog.dismiss();
								tvMeetingTime.setText(dateSelected);
								viewFocusHacker.requestFocus();
							}

							@Override
							public void onCancelClick(CustomTimePickerDialog dialog) {
								dialog.dismiss();

							}
						}, time).show();

				break;
			case R.id.tvTypeOfMeeting:
				String meetingType = tvTypeOfMeeting.getText().toString().trim();

				hideKeyboard();
				new MeetingTypeDialog(MeetingAdditionActivity.this)
						.setMeetingTypeListener(new MeetingTypeDialogListener() {

							@Override
							public void onDoneClick(MeetingTypeDialog dialog,
													String meetingSelected) {
								dialog.dismiss();
								tvTypeOfMeeting.setText(meetingSelected);
								viewFocusHacker.requestFocus();
							}

							@Override
							public void onCancelClick(MeetingTypeDialog dialog) {
								dialog.dismiss();

							}
						}, meetingType).show();
				break;
			case R.id.tvTypeOfFacility:
				hideKeyboard();
				// Toast.makeText(RequestMeetingAdditionActivity.this,
				// "Type of Facility!", Toast.LENGTH_SHORT).show();
				String meetingFacility = tvTypeOfFacility.getText().toString()
						.trim();
				new MeetingFacilityDialog(MeetingAdditionActivity.this)
						.setMeetingFacilityListener(
								new MeetingFacilityDialogListener() {

									@Override
									public void onDoneClick(
											MeetingFacilityDialog dialog,
											String meetingFacility) {
										dialog.dismiss();

										if(meetingFacility.toLowerCase().equals("other")){
											rlOtherFacility.setVisibility(View.VISIBLE);
											etOtherFacilities.requestFocus();
										}else{
											rlOtherFacility.setVisibility(View.GONE);
											etYourRelationshipToMeeting.requestFocus();
										}
										tvTypeOfFacility.setText(meetingFacility);
									}

									@Override
									public void onCancelClick(
											MeetingFacilityDialog dialog) {
										dialog.dismiss();

									}
								}, meetingFacility).show();
				break;
		}
	}

	public void findPlace(View view) {
//    try {
//        Intent intent = 
//                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
//                        .build(this);
//                        .setFilter(typeFilter)
//        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//    } catch (GooglePlayServicesRepairableException e) {
//    } catch (GooglePlayServicesNotAvailableException e) {
//    }

		// Construct an intent for the place picker
		try {
			PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
			Intent intent = intentBuilder.build(this);
			// Start the intent by requesting a result,
			// identified by a request code.
			startActivityForResult(intent, REQUEST_PLACE_PICKER);

		} catch (GooglePlayServicesRepairableException e) {
			// ...
		} catch (GooglePlayServicesNotAvailableException e) {
			// ...
		}
	}

	// A place has been received; use requestCode to track the request.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
//        if (resultCode == RESULT_OK) {
//            Place place = PlaceAutocomplete.getPlace(this, data);
//            Log.i(TAG, "Place: " + place.getName());
//            
//            String placeDetailsStr = place.getName() + "\n"
//                    + place.getId() + "\n"
//                    + place.getLatLng().toString() + "\n"
//                    + place.getAddress() + "\n"
//                    + place.getAttributions();
//            etAddress.setText(placeDetailsStr);
//        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
//            Status status = PlaceAutocomplete.getStatus(this, data);
//            Log.i(TAG, status.getStatusMessage());
//
//        } else if (resultCode == RESULT_CANCELED) {
//            // The user canceled the operation.
//        }
//    }

		if (requestCode == REQUEST_PLACE_PICKER
				&& resultCode == Activity.RESULT_OK) {

			// The user has selected a place. Extract the name and address.
			final Place place = PlacePicker.getPlace(data, this);

			LatLng latlng = place.getLatLng();
			if(latlng != null){
				lat = String.valueOf(latlng.latitude);
				lng = String.valueOf(latlng.longitude);
			}
			CharSequence name = place.getName();
			CharSequence address = place.getAddress();
			String attributions = PlacePicker.getAttributions(data);
			if (attributions == null) {
				attributions = "";
			}

			if(name != null){
                etAddress.setText(name);
            }else if (address != null) {
                String s = address.toString();
                if(s.contains(",")) {
                    etAddress.setText(s.substring(0, s.indexOf(',')));
                }
            }

			if(TextUtils.isEmpty(address)){
				address = "Pin point location";
                etAddress.setText(address);
            }else {
                this.mAddress = address.toString();
                if(place != null && !TextUtils.isEmpty(place.getId())) {

					Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
					List<Address> addresses;

					// Attempt to Geocode from place lat & long
					try {

						addresses = geocoder.getFromLocation(
								place.getLatLng().latitude,
								place.getLatLng().longitude,
								1);

						if (addresses.size() > 0) {

							// Here are some results you can geocode
							String ZIP;
							String city;
							String state;
							String country;

							if (addresses.get(0).getPostalCode() != null) {
								ZIP = addresses.get(0).getPostalCode();
								Log.d("ZIP", ZIP);
								etZipcode.setText(ZIP);
							}
//							String streetAddress = addresses.get(0).getThoroughfare();
//							if (streetAddress != null) {
//								Log.d("streetAddress", streetAddress);
//								etAddress.setText(streetAddress);
//							}else {
//								String s = address.toString();
//								etAddress.setText(s.substring(0, s.indexOf(',')));
//							}
							if (addresses.get(0).getPostalCode() != null) {
								ZIP = addresses.get(0).getPostalCode();
								Log.d("ZIP", ZIP);
								etZipcode.setText(ZIP);

							}

							if (addresses.get(0).getLocality() != null) {
								city = addresses.get(0).getLocality();
								Log.d("city", city);
								etCity.setText(city);
							}

							if (addresses.get(0).getAdminArea() != null) {
								state = addresses.get(0).getAdminArea();
								Log.d("state", state);
							}

							if (addresses.get(0).getCountryName() != null) {
								country = addresses.get(0).getCountryName();
								Log.d("country", country);
							}

						}else {
							etAddress.setText(address);
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}


	public void submitMeeting() {
		String phone = UtilityClass.phoneNumberNormal(etPhoneNumber.getText().toString().trim());
		String meetingName = etMeetingName.getText().toString().trim();
		String meetingDay = tvMeetingDay.getText().toString().trim();
		String meetingTime = tvMeetingTime.getText().toString().trim();
		String address = mAddress == null ? "": mAddress;
		String meetingCity = etCity.getText().toString().trim();
		String zipCode = etZipcode.getText().toString().trim();
		String typeOfMeeting = tvTypeOfMeeting.getText().toString().trim();
		String typeOfFacility = tvTypeOfFacility.getText().toString().trim();

		typeOfFacility = typeOfFacility.toLowerCase().equals("other") ? etOtherFacilities.getText().toString().trim() : typeOfFacility;

		String yourRelationshipToMeeting = etYourRelationshipToMeeting
				.getText().toString().trim();

		if (phone.isEmpty() || phone.length() != 10 || !ValidationUtils.validatePhoneNumber(phone)) {
			Toast.makeText(mContext,
					getString(R.string.enter_valid_phone_number),
					Toast.LENGTH_LONG).show();
			return;
		}

		if (meetingName.isEmpty() || ValidationUtils.isSpecialChar(meetingName)) {
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
		if (meetingCity.isEmpty() || ValidationUtils.isSpecialChar(meetingCity)) {
			Toast.makeText(mContext, getString(R.string.enter_city),
					Toast.LENGTH_LONG).show();
			return;
		}
		if (zipCode.isEmpty() || zipCode.length() != 5) {
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

		if (yourRelationshipToMeeting.isEmpty() || ValidationUtils.isSpecialChar(yourRelationshipToMeeting)) {
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
			JSONArray meetingDays = new JSONArray(selectedDays);
//			if(meetingDay.equals("EVERY DAY")){
//				meetingDays.put("Monday");
//				meetingDays.put("Tuesday");
//				meetingDays.put("Wednesday");
//				meetingDays.put("Thursday");
//				meetingDays.put("Friday");
//				meetingDays.put("Saturday");
//				meetingDays.put("Sunday");
//			}else {
//				meetingDays.put(meetingDay);
//			}
			JSONArray meetingTimes = new JSONArray();
			for (int i = 0; i < selectedDays.size();i++) {
				meetingTimes.put(meetingTime);
			}

			json.put("phone", phone);
			json.put("name", meetingName);
			json.put("codes", typeOfMeeting.split("-")[0].trim());
			json.put("on_day", meetingDays);
			json.put("on_time", meetingTimes);
			// json.put("on_date",on);
			json.put("in_city", meetingCity);
			json.put("address", address);
			json.put("zip_code", zipCode);
			json.put("latitude", lat);
			json.put("longitude", lng);
			json.put("building_type", typeOfFacility);
			json.put("relation", yourRelationshipToMeeting);

			socketService.addMeeting(json);

			Log.e("json", json.toString());
			pd.show();

		} catch (JSONException e) {
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
			e.printStackTrace();
			return Calendar.getInstance();
		}
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		if (pd != null) {
			pd.dismiss();
		}
		if (event.equals(EventParams.EVENT_ADD_MEETING)) {

//			Toast.makeText(mContext, "Meeting added successfully!", Toast.LENGTH_SHORT).show();
			new MessageDialog(this,R.string.thanks_on_meeting_addition)
					.setDialogClickListener(new MessageDialog.MessageDialogClickListener() {
						@Override
						public void onOkClick(MessageDialog dialog) {
							dialog.dismiss();
							onBackPressed();
						}
					}).show();
		}



	}

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		if (pd != null) {
			pd.dismiss();
		}
		Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
	}

	public static ArrayList autocomplete(String input) {
		ArrayList resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?key=" + API_KEY);
			sb.append("&components=country:gr");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e("", "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e("", "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
				System.out.println("============================================================");
				resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
			}
		} catch (JSONException e) {
			Log.e("", "Cannot process JSON results", e);
		}

		return resultList;
	}

	@Override
	public void onConnected(Bundle bundle) {
		isGoogleConnected = true;
	}

	@Override
	public void onConnectionSuspended(int i) {
		isGoogleConnected = false;
	}

class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {

		private ArrayList resultList;

		public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);

		}

		@Override

		public int getCount() {

			return resultList.size();

		}

		@Override

		public String getItem(int index) {
			return (String) resultList.get(index);

		}

		@Override

		public Filter getFilter() {

			Filter filter = new Filter() {

				@Override

				protected FilterResults performFiltering(CharSequence constraint) {

					FilterResults filterResults = new FilterResults();

					if (constraint != null) {

						// Retrieve the autocomplete results.

						resultList = autocomplete(constraint.toString());


						// Assign the data to the FilterResults

						filterResults.values = resultList;

						filterResults.count = resultList.size();

					}

					return filterResults;

				}



				@Override

				protected void publishResults(CharSequence constraint, FilterResults results) {

					if (results != null && results.count > 0) {

						notifyDataSetChanged();

					} else {

						notifyDataSetInvalidated();

					}

				}

			};

			return filter;

		}

	}

}
