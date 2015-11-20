package com.citrusbits.meehab.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.HomeActivity;
import com.citrusbits.meehab.MeetingDetailsActivity;
import com.citrusbits.meehab.MeetingsFilterActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.RehabsFilterActivity;
import com.citrusbits.meehab.adapters.MeetingsListAdapter;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.map.LocationService;
import com.citrusbits.meehab.map.LocationService.LocationListener;
import com.citrusbits.meehab.map.LocationService.MyLocalBinder;
import com.citrusbits.meehab.map.LocationUtils;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.model.RehaabFilterResultHolder;
import com.citrusbits.meehab.model.MeetingModel.MarkerColorType;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.pojo.MeetingResponse;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;



public class RehabsFragment extends Fragment implements
		OnSocketResponseListener, OnBackendConnectListener,
		View.OnClickListener, ListView.OnItemClickListener,
		GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
		GoogleMap.OnInfoWindowClickListener {

	/**
* 
*/
	public static final int Filter_request = 200;
	// Google Map
	SupportMapFragment mapFrag;
	GoogleMap map;
	private Location myLocation /* = new LatLng(33.671447, 73.069612) */;
	private ImageButton btnList, btnFindMe;
	private ListView list;
	ArrayList<MeetingModel> meetings = new ArrayList<MeetingModel>();
	HashMap<Marker, MeetingModel> spots;
	MeetingsListAdapter meetingsAdapter;
	private HomeActivity homeActivity;
	private ProgressDialog pd;
	private boolean meetingUpdated;
	private EditText editTopCenter;
	private View focus_thief;

	AppPrefs prefs;

	LocationService locationService;

	Context mContext;

	List<String> favMeetingIds = new ArrayList<String>();

	ServiceConnection locServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			locationService = ((MyLocalBinder) service).getService();
			locationService.addListener(locationListener);
		}
	};

	LocationListener locationListener = new LocationListener() {

		@Override
		public void onChangeLocation(Location location) {
			// TODO Auto-generated method stub
			myLocation = location;
		}
	};

	UserDatasource userDatasource;
	UserAccount user;

	MeetingProcessingTask meetingProcessinTask;

	public RehabsFragment() {
	}

	public RehabsFragment(HomeActivity homeActivity) {
		this.homeActivity = homeActivity;
		pd = UtilityClass.getProgressDialog(homeActivity);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		locationService.removeListener(locationListener);
		getActivity().unbindService(locServiceConnection);
		getActivity().stopService(new Intent(mContext, LocationService.class));

		if (meetingProcessinTask != null
				&& meetingProcessinTask.getStatus() == android.os.AsyncTask.Status.RUNNING) {
			meetingProcessinTask.cancel(true);
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDatasource = new UserDatasource(getActivity());
		userDatasource.open();

		user = userDatasource.findUser(AccountUtils.getUserId(getActivity()));
		mContext = getActivity();
		meetings = new ArrayList<>();
		spots = new HashMap<>();
		prefs = AppPrefs.getAppPrefs(getActivity());
		myLocation = LocationUtils.getLastLocation(getActivity());
		Intent i = new Intent(mContext, LocationService.class);
		mContext.bindService(i, locServiceConnection, Context.BIND_AUTO_CREATE);
		mContext.startService(i);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_rehabs, container, false);

		v.findViewById(R.id.topMenuBtn).setOnClickListener(this);
		v.findViewById(R.id.topRightBtn).setOnClickListener(this);
		list = (ListView) v.findViewById(R.id.list);
		btnList = (ImageButton) v.findViewById(R.id.btnList);
		btnFindMe = (ImageButton) v.findViewById(R.id.btnFindMe);
		editTopCenter = (EditText) v.findViewById(R.id.editTopCenter);
		focus_thief = v.findViewById(R.id.focus_thief);
		editTopCenter.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String text = editTopCenter.getText().toString().trim();
				if (TextUtils.isEmpty(text)) {
					editTopCenter.setCompoundDrawablesWithIntrinsicBounds(
							android.R.drawable.ic_menu_search, 0, 0, 0);
				} else {
					editTopCenter.setCompoundDrawables(null, null, null, null);
				}
				searchMeetings(text);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		btnList.setOnClickListener(this);
		btnFindMe.setOnClickListener(this);
		list.setOnItemClickListener(this);

		if (App.isPlayServiceOk) {
			if (map == null) {
				mapFrag = ((SupportMapFragment) getChildFragmentManager()
						.findFragmentById(R.id.map));
				mapFrag.getMapAsync(new OnMapReadyCallback() {

					@Override
					public void onMapReady(GoogleMap arg) {
						map = arg;

						map.setInfoWindowAdapter(new MeetingInfoWindowAdapter());
						// map.getUiSettings().setMyLocationButtonEnabled(false);
						map.setMyLocationEnabled(true);
						map.setOnMarkerClickListener(RehabsFragment.this);
						map.setOnMapClickListener(RehabsFragment.this);
						map.setOnInfoWindowClickListener(RehabsFragment.this);

					}
				});
			}

		}
		onBackendConnected();

		return v;
	}

	/**
	 * @param text
	 */
	protected void searchMeetings(String text) {
		if (!list.isShown()) {
			// btnList.setText("MAP");

			btnList.setImageResource(R.drawable.map_btn);
			btnFindMe.setVisibility(View.GONE);
			mapFrag.onPause();
			list.setVisibility(View.VISIBLE);
		}

		meetingsAdapter.filter(text);
	}

	protected void filterMeetings() {

		// meetingsAdapter.filter(text);

	}

	/**
* 
*/
	protected void addMarkers() {
		map.clear();
		spots.clear();

		BitmapDescriptor icon = BitmapDescriptorFactory
				.fromResource(R.drawable.pin);
		// add marker
		for (int i = 0; i < meetings.size(); i++) {
			MeetingModel m = meetings.get(i);
			// Creating an instance of MarkerOptions to set position
			int resourceId = R.drawable.pin_dark_pink;
			if (m.getMarkertypeColor() == MarkerColorType.GREEN) {
				resourceId = R.drawable.pin_green;
			} else if (m.getMarkertypeColor() == MarkerColorType.ORANGE) {
				resourceId = R.drawable.pin_orange;
			} else if (m.getMarkertypeColor() == MarkerColorType.RED) {
				resourceId = R.drawable.pin_dark_pink;
			}
			MarkerOptions markerOptions = new MarkerOptions()
					.position(new LatLng(m.getLatitude(), m.getLongitude()))
					.title(m.getName())
					.icon(BitmapDescriptorFactory.fromResource(resourceId));

			// markerOptions.icon(icon);
			// Setting position on the MarkerOptions

			// Adding marker on the GoogleMap
			Marker marker = map.addMarker(markerOptions);
			spots.put(marker, m);
		}

		moveCamera(
				new LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
				false);

		// fitBounds();

	}

	public void fitBounds() {

		Iterator<Entry<Marker, MeetingModel>> it = spots.entrySet().iterator();
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();

			// System.out.println(pair.getKey() + " = " + pair.getValue());
			// it.remove(); // avoids a ConcurrentModificationException

			Marker marker = (Marker) pair.getKey();
			builder.include(marker.getPosition());
			LatLngBounds bounds = builder.build();

			int padding = 0; // offset from edges of the map in pixels
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
					padding);
			map.moveCamera(cu);
		}

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	void refreshMeetingList() {
		if (homeActivity.socketService != null) {
			pd.show();
			homeActivity.socketService.updateMeetings(new JSONObject());
		}
	}

	void refrshFavList() {
		if (homeActivity.socketService != null) {
			// pd.show();
			JSONObject object = new JSONObject();
			try {
				object.put("id", user.getId());
				Log.e("Fav Object ", object.toString());
				homeActivity.socketService.favMeetingsList(object);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * @param location
	 *            destination
	 */
	protected void moveCamera(LatLng location, boolean animate) {
		if (map == null) {
			return;
		}

		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(
				myLocation.getLatitude(), myLocation.getLongitude()), 8);

		if (animate) {
			map.animateCamera(update);
		} else {
			map.moveCamera(update);
		}

	}

	/**
	 * @param string
	 */
	protected void toast(String string) {
		Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		MeetingModel m = spots.get(marker);
		if (m != null) {

		}
		Intent intent = new Intent(getActivity(), MeetingDetailsActivity.class);
		Bundle bundle = new Bundle();

		bundle.putSerializable("meeting", m);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return false;
	}

	@Override
	public void onMapClick(LatLng arg0) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(getActivity(), MeetingDetailsActivity.class);
		Bundle bundle = new Bundle();

		bundle.putSerializable("meeting", meetings.get(position));
		intent.putExtras(bundle);

		getActivity().startActivity(intent);
		getActivity().overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topMenuBtn:
			if (homeActivity.isDrawerOpen()) {
				homeActivity.changeDrawerVisibility(false);
			} else {
				homeActivity.changeDrawerVisibility(true);
			}
			break;
		case R.id.topRightBtn:
			// filter
			Intent intent = new Intent(getActivity(),
					RehabsFilterActivity.class);
			// put user id
			startActivityForResult(intent, Filter_request);
			getActivity().overridePendingTransition(R.anim.activity_back_in,
					R.anim.activity_back_out);
			break;
		case R.id.btnList:

			switchList();

			break;
		case R.id.btnFindMe:
			if (list.getVisibility() == View.VISIBLE) {
				switchList();
			}
			moveMapCamera(new LatLng(myLocation.getLatitude(),
					myLocation.getLongitude()));

			break;

		default:
			break;
		}
	}

	public void switchList() {
		if (!list.isShown()) {
			// btnList.setText("MAP");
			btnFindMe.setVisibility(View.GONE);
			mapFrag.onPause();
			btnList.setImageResource(R.drawable.map_btn);
			list.setVisibility(View.VISIBLE);
		} else {
			// btnList.setText("LIST");
			btnList.setImageResource(R.drawable.list_btn);
			btnFindMe.setVisibility(View.VISIBLE);
			list.setVisibility(View.GONE);
			mapFrag.onResume();
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_BACK) {
					// handle back button's click listener
					if (list.getVisibility() == View.VISIBLE) {

						switchList();
						return true;
					} else {
						return false;
					}

				}
				return false;
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Toast.makeText(getActivity(), "onActivityResult is called ", Toast.LENGTH_SHORT).show();

		if (resultCode == getActivity().RESULT_OK) {
			if (requestCode == Filter_request) {
				/*
				 * MeetingFilterModel fm = (MeetingFilterModel) data
				 * .getSerializableExtra(MeetingsFilterActivity.MEETING_FILTER);
				 */

				RehaabFilterResultHolder resultHolder = (RehaabFilterResultHolder) data
						.getSerializableExtra(MeetingsFilterActivity.MEETING_FILTER);

				meetingsAdapter.setLocation(map.getMyLocation());

				Toast.makeText(getActivity(), resultHolder.isanyDistance()+"", Toast.LENGTH_SHORT).show();

				//meetingsAdapter.filter(resultHolder);

			}
		}
	}

	private void moveMapCamera(LatLng p) {
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(p, 12);
		map.animateCamera(cameraUpdate);
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		pd.dismiss();
		// Location myLocation=map.getMyLocation();
		if (event.equals(EventParams.EVENT_MEETING_GET_ALL)) {

			meetingUpdated = true;
			try {
				JSONObject data = ((JSONObject) obj);

				Log.e("Meeting data", data.toString());

				Gson gson = new Gson();
				MeetingResponse response = gson.fromJson(data.toString(),
						MeetingResponse.class);
				meetings = (ArrayList<MeetingModel>) response
						.getGetAllMeetings();

				meetingProcessinTask = new MeetingProcessingTask();
				meetingProcessinTask.execute();

				/*
				 * if (meetings.size() > 0) {
				 * 
				 * 
				 * String prevDate = ""; for (int i = 0; i < meetings.size();
				 * i++) { MeetingModel meeting = meetings.get(i);
				 * meeting.setOnDateOrigin(meeting.getOnDate());
				 * 
				 * Location pinLocation = new Location("B");
				 * pinLocation.setLatitude(meeting.getLatitude());
				 * pinLocation.setLongitude(meeting.getLongitude()); long
				 * distance = (long) (myLocation .distanceTo(pinLocation) *
				 * 0.000621371192f); meeting.setDistanceInMiles(distance);
				 * 
				 * Log.e("Timing", meeting.getOnTime()); if
				 * (!prevDate.equals(meeting.getOnDate())) {
				 * meeting.setDateHeaderVisibility(true); prevDate =
				 * meeting.getOnDate();
				 * meeting.setOnDate(formateDate(meeting.getOnDate())); }
				 * 
				 * setStartInTime(meeting, meeting.getOnDateOrigion(),
				 * meeting.getOnTime()); }
				 * 
				 * meetingsAdapter = new MeetingsListAdapter(getActivity(),
				 * R.layout.list_item_meeting, meetings);
				 * list.setAdapter(meetingsAdapter);
				 * meetingsAdapter.notifyDataSetChanged(); addMarkers(); }
				 */
			} catch (Exception e) {

			}

			// refrshFavList();

		} else if (event.equals(EventParams.EVENT_FAVOURITE_LIST)) {
			JSONObject data = ((JSONObject) obj);
			favMeetingIds.clear();
			try {
				JSONArray allFavouriteArrays = data
						.getJSONArray("getAllFavorites");
				for (int i = 0; i < allFavouriteArrays.length(); i++) {
					JSONObject object = allFavouriteArrays.getJSONObject(i);
					String meetingId = object.getString("meetingID");
					favMeetingIds.add(meetingId);
				}

				for (MeetingModel meeting : meetings) {

					String meetingId = String.valueOf(meeting.getMeetingId());

					meeting.setFavourite(favMeetingIds.contains(meetingId) ? true
							: false);

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e("Favourite", data.toString());

		}

	}

	public class MeetingProcessingTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			if (meetings.size() > 0) {

				String prevDate = "";
				for (int i = 0; i < meetings.size(); i++) {
					MeetingModel meeting = meetings.get(i);
					meeting.setOnDateOrigin(meeting.getOnDate());

					Location pinLocation = new Location("B");
					pinLocation.setLatitude(meeting.getLatitude());
					pinLocation.setLongitude(meeting.getLongitude());
					long distance = (long) (myLocation.distanceTo(pinLocation) * 0.000621371192f);
					meeting.setDistanceInMiles(distance);

					Log.e("Timing", meeting.getOnTime());
					if (!prevDate.equals(meeting.getOnDate())) {
						meeting.setDateHeaderVisibility(true);
						prevDate = meeting.getOnDate();
						meeting.setOnDate(formateDate(meeting.getOnDate()));
					}

					setStartInTime(meeting, meeting.getOnDateOrigion(),
							meeting.getOnTime());
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			meetingsAdapter = new MeetingsListAdapter(getActivity(),
					R.layout.list_item_meeting, meetings);
			list.setAdapter(meetingsAdapter);
			meetingsAdapter.notifyDataSetChanged();
			addMarkers();
			refrshFavList();
		}

	}

	public String formateDate(String date) {
		SimpleDateFormat prevFormate = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat newFormate = new SimpleDateFormat("EEEE dd MMM yyyy");
		try {
			Date date2 = prevFormate.parse(date);
			return newFormate.format(date2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return date;
		}
	}

	public void setStartInTime(MeetingModel model, String date, String onTime) {
		SimpleDateFormat prevFormate = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date date2 = prevFormate.parse(date);
			SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
			final Date dateObj = _12HourSDF.parse(onTime);
			Calendar calendar = Calendar.getInstance();

			calendar.setTime(date2);

			calendar.set(Calendar.HOUR_OF_DAY, dateObj.getHours());
			calendar.set(Calendar.MINUTE, dateObj.getMinutes());
			calendar.set(Calendar.SECOND, 0);

			Calendar currentCalendar = Calendar.getInstance();

			long difference = calendar.getTimeInMillis()
					- currentCalendar.getTimeInMillis();

			long x = difference / 1000;
			Log.e("Difference ", x + "");

			long seconds = x % 60;
			x /= 60;
			long minutes = x % 60;
			x /= 60;
			long hours = x % 24;

			Log.e("Hours Difference ", hours + ":" + minutes);
			SimpleDateFormat mmmDate = new SimpleDateFormat(
					"dd/MM/yyyy hh:mm a");
			String mDate = mmmDate.format(calendar.getTime());
			String mNow = mmmDate.format(currentCalendar.getTime());
			Log.e("Meeting date time ", mDate);
			Log.e("Nnow date time ", mNow);

			if (hours > 1 || hours == 1 && minutes > 0) {
				model.setMarkerTypeColor(MarkerColorType.GREEN);
				model.setStartInTime("AFTER 1 HOUR");
			} else if (hours == 0 && minutes >= 0) {
				model.setMarkerTypeColor(MarkerColorType.ORANGE);
				model.setStartInTime("START IN HOUR");
			} else if (hours == 0 && minutes <= 0 || hours < 0 && hours > -2) {
				model.setMarkerTypeColor(MarkerColorType.RED);
				model.setStartInTime("ONGOING");
			} else if (hours <= -1) {
				model.setMarkerTypeColor(MarkerColorType.RED);
				model.setStartInTime("COMPLETED");
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onSocketResponseFailure(String message) {
		pd.dismiss();

	}

	@Override
	public void onBackendConnected() {
		if (!meetingUpdated) {
		//	refreshMeetingList();
		}

	}

	public class MeetingInfoWindowAdapter implements
			GoogleMap.InfoWindowAdapter {

		@Override
		public View getInfoWindow(Marker marker) {
			View v = RehabsFragment.this.getActivity().getLayoutInflater()
					.inflate(R.layout.map_meeting_info_window, null);
			TextView txtName = (TextView) v.findViewById(R.id.txtName);
			TextView txtLocationName = (TextView) v
					.findViewById(R.id.txtLocationName);
			TextView txtAddress = (TextView) v.findViewById(R.id.txtAddress);
			TextView txtReviewCounts = (TextView) v
					.findViewById(R.id.txtReviewCounts);
			RatingBar rating = (RatingBar) v.findViewById(R.id.rating);
			TextView txtTime = (TextView) v.findViewById(R.id.txtTime);
			TextView txtDistance = (TextView) v.findViewById(R.id.txtDistance);
			TextView txtStatus = (TextView) v.findViewById(R.id.txtStatus);

			MeetingModel m = spots.get(marker);

			txtName.setText(m.getName());
			txtTime.setText(m.getOnTime());
			txtReviewCounts.setText(String.valueOf(m.getReviewsCount())
					+ " reviews");
			txtLocationName.setText(m.getBuildingType());
			txtAddress.setText(m.getAddress());
			rating.setRating(m.getReviewsAvg());
			txtStatus.setText(m.getStartInTime());

			txtDistance.setText(UtilityClass.calculatDistance(m.getPosition()));

			return v;
		}

		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}
	}
	// String s= "Hello Everyone";
	// SpannableString ss1= new SpannableString(s);
	// ss1.setSpan(new RelativeSizeSpan(2f), 0,5, 0); // set size
	// ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);// set color
	// TextView tv= (TextView) findViewById(R.id.textview);
	// tv.setText(ss1);

}
