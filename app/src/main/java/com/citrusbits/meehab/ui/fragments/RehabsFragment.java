package com.citrusbits.meehab.ui.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
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
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.ui.HomeActivity;
import com.citrusbits.meehab.ui.meetings.MeetingsFilterActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.ui.rehabs.RehabDetailsActivity;
import com.citrusbits.meehab.ui.rehabs.RehabsFilterActivity;
import com.citrusbits.meehab.adapters.RehabListAdapter;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.map.LocationService;
import com.citrusbits.meehab.map.LocationService.LocationListener;
import com.citrusbits.meehab.map.LocationService.MyLocalBinder;
import com.citrusbits.meehab.map.LocationUtils;
import com.citrusbits.meehab.model.RehaabFilterResultHolder;
import com.citrusbits.meehab.model.NearesDateTime;
import com.citrusbits.meehab.model.RehabDayModel;
import com.citrusbits.meehab.model.RehabModel;
import com.citrusbits.meehab.model.RehabResponseModel;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RehabsFragment extends Fragment implements
		OnSocketResponseListener, OnBackendConnectListener,
		View.OnClickListener, ListView.OnItemClickListener,
		GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
		GoogleMap.OnInfoWindowClickListener {

	/**
* 
*/
	public static final int Filter_request = 200;
	private static final int REQUEST_REHAB_DETAILS = 234;
	// Google Map
	SupportMapFragment mapFrag;
	GoogleMap map;
	private Location myLocation /* = new LatLng(33.671447, 73.069612) */;
	private ImageButton btnList, btnFindMe;
	private ListView list;

	HashMap<Marker, RehabModel> spots;
	RehabListAdapter rehabAdapter;
	private HomeActivity homeActivity;
	private Dialog pd;
	private boolean rehabsUpdated;
	private EditText etSearch;
	private View focus_thief;

	AppPrefs prefs;

	LocationService locationService;

	Context mContext;

	RehabResponseModel rehabResponse = new RehabResponseModel();

	ServiceConnection locServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			locationService = ((MyLocalBinder) service).getService();
			locationService.addListener(locationListener);
		}
	};

	LocationListener locationListener = new LocationListener() {

		@Override
		public void onChangeLocation(Location location) {
			myLocation = location;
			refreshMyLocationMarker(false);
		}
	};

	UserDatasource userDatasource;
	UserAccount user;

	private boolean listWasInvisible = false;
	private boolean rehabsUpdating;
	private int mReqPosition = -1;
	private ResponseProcessingTask responseProcessinTask;
	private Marker myLocMarker;
	private View emptyList;
	private View textViewCross;

	public RehabsFragment() {
	}

	protected void refreshMyLocationMarker(boolean isMove) {
		if(map != null && myLocation != null){
			if(myLocMarker != null){
				myLocMarker.remove();
			}
//			location_point_black
			MarkerOptions markerOptions = new MarkerOptions()
			.title("mylocation")
			.position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_point_black));
			
			myLocMarker = map.addMarker(markerOptions);
			if(isMove){
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(
					markerOptions.getPosition(), Consts.MAP_ZOOM));
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(locationService != null){
		locationService.removeListener(locationListener);
		getActivity().unbindService(locServiceConnection);
		}
		getActivity().stopService(new Intent(mContext, LocationService.class));
		if (responseProcessinTask != null
				&& responseProcessinTask.getStatus() == android.os.AsyncTask.Status.RUNNING) {
			responseProcessinTask.cancel(true);
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDatasource = new UserDatasource(getActivity());

		user = userDatasource.findUser(AccountUtils.getUserId(getActivity()));
		mContext = getActivity();

		spots = new HashMap<>();
		prefs = AppPrefs.getAppPrefs(getActivity());
		myLocation = LocationUtils.getLastLocation(getActivity());
		Intent i = new Intent(mContext.getApplicationContext(), LocationService.class);
		mContext.bindService(i, locServiceConnection, Context.BIND_AUTO_CREATE);
		mContext.startService(i);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_rehabs, container, false);

		this.homeActivity = (HomeActivity) getActivity();
		pd = UtilityClass.getProgressDialog(homeActivity);

		v.findViewById(R.id.topMenuBtn).setOnClickListener(this);
		v.findViewById(R.id.topRightBtn).setOnClickListener(this);
		list = (ListView) v.findViewById(R.id.list);
		emptyList = v.findViewById(R.id.emptyList);
		btnList = (ImageButton) v.findViewById(R.id.btnList);
		btnFindMe = (ImageButton) v.findViewById(R.id.btnFindMe);
		textViewCross = v.findViewById(R.id.textViewCross);
		textViewCross.setOnClickListener(this);
		etSearch = (EditText) v.findViewById(R.id.etSearch);

		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String inputText = s.toString().trim().toLowerCase();
				searchRehabs(inputText);
				if (inputText.trim().length() == 0) {
					textViewCross.setVisibility(View.INVISIBLE);
					if (listWasInvisible) {
						switchList();
						listWasInvisible = false;
					}
				}else{
					textViewCross.setVisibility(View.VISIBLE);
				}
				
				updateEmptyViewVisibility();
			}
		});

		focus_thief = v.findViewById(R.id.focus_thief);

		rehabAdapter = new RehabListAdapter(getActivity(),
				R.layout.list_rehab_item, rehabResponse);

		list.setAdapter(rehabAdapter);

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

						map.setInfoWindowAdapter(new RehabInfoWindowAdapter());
						 map.getUiSettings().setMyLocationButtonEnabled(false);
						map.getUiSettings().setMapToolbarEnabled(false);
//						map.setMyLocationEnabled(false);
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
	protected void searchRehabs(String text) {
		if (!list.isShown()) {
			// btnList.setText("MAP");

			btnList.setImageResource(R.drawable.map_btn);
			btnFindMe.setVisibility(View.GONE);
			mapFrag.onPause();
			list.setVisibility(View.VISIBLE);
			listWasInvisible = true;
		}

		rehabAdapter.filter(text);
	}

	protected void filterMeetings() {

		// meetingsAdapter.filter(text);

	}

	/**
* 
*/
	protected void addMarkers() {
		if(map == null) return;
		
		map.clear();
		spots.clear();
		
		BitmapDescriptor icon = BitmapDescriptorFactory
				.fromResource(R.drawable.pin);
		// add marker
		List<RehabModel> rehabs = rehabResponse.getRehabs();
		for (int i = 0; i < rehabs.size(); i++) {
			RehabModel rehab = rehabs.get(i);
			// Creating an instance of MarkerOptions to set position
			
			int resourceId = RehabResponseModel.getMarkDrawableId(rehab.getPackageName());
			/*
			 * if (m.getMarkertypeColor() == MarkerColorType.GREEN) { resourceId
			 * = R.drawable.pin_green; } else if (m.getMarkertypeColor() ==
			 * MarkerColorType.ORANGE) { resourceId = R.drawable.pin_orange; }
			 * else if (m.getMarkertypeColor() == MarkerColorType.RED) {
			 * resourceId = R.drawable.pin_dark_pink; }
			 */
			MarkerOptions markerOptions = new MarkerOptions()
					.position(
							new LatLng(rehab.getLatitude(), rehab
									.getLongitude())).title(rehab.getName())
					.icon(BitmapDescriptorFactory.fromResource(resourceId));

			// markerOptions.icon(icon);
			// Setting position on the MarkerOptions

			// Adding marker on the GoogleMap
			Marker marker = map.addMarker(markerOptions);
			spots.put(marker, rehab);
		}
		
		refreshMyLocationMarker(true);

//		moveCamera(
//				new LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
//				false);

		// fitBounds();

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	void refreshRehab() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}

		if (homeActivity.socketService != null && !rehabsUpdated && !rehabsUpdating) {

			pd.show();
			rehabsUpdating = true;
			homeActivity.socketService.getAllRehab(new JSONObject());
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
		RehabModel m = spots.get(marker);
		if (m == null) {
			return;
		}
		Intent intent = new Intent(getActivity(), RehabDetailsActivity.class);
		Bundle bundle = new Bundle();

		bundle.putSerializable(RehabDetailsActivity.KEY_REHAB, m);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (marker.getId().equals(myLocMarker.getId())){
			return true;
		}
		marker.showInfoWindow();
		return false;
	}

	@Override
	public void onMapClick(LatLng arg0) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(getActivity(), RehabDetailsActivity.class);
		Bundle bundle = new Bundle();
		
		mReqPosition = position;

		bundle.putSerializable(RehabDetailsActivity.KEY_REHAB, rehabResponse.getRehabModel(position));
		intent.putExtras(bundle);

		startActivityForResult(intent, REQUEST_REHAB_DETAILS);
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
			case R.id.textViewCross:
				etSearch.setText("");
				UtilityClass.hideSoftKeyboard(getContext(), etSearch);
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
			// listWasInvisible=true;
		} else {
			// btnList.setText("LIST");
			btnList.setImageResource(R.drawable.list_btn);
			btnFindMe.setVisibility(View.VISIBLE);
			list.setVisibility(View.GONE);
			// listWasInvisible=false;
			mapFrag.onResume();
		}
		updateEmptyViewVisibility();
	}
	
	private void updateEmptyViewVisibility() {
		if(list.getVisibility() == View.VISIBLE && rehabAdapter.getCount() == 0){
			emptyList.setVisibility(View.VISIBLE);
			btnList.setVisibility(View.GONE);
//			makeListVisible();
		}else{
			btnList.setVisibility(View.VISIBLE);
			emptyList.setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {
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

//		Toast.makeText(getActivity(), "onActivityResult is called ",
//				Toast.LENGTH_SHORT).show();

		
		if (resultCode == getActivity().RESULT_OK) {
			if (requestCode == Filter_request) {
				/*
				 * MeetingFilterModel fm = (MeetingFilterModel) data
				 * .getSerializableExtra(MeetingsFilterActivity.MEETING_FILTER);
				 */

				RehaabFilterResultHolder resultHolder = (RehaabFilterResultHolder) data
						.getSerializableExtra(MeetingsFilterActivity.MEETING_FILTER);

				App.toast("Filtering rehabs");


//				if (myLocation == null) {
//					AppPrefs prefs = AppPrefs.getAppPrefs(getActivity());
//					double latitude = prefs.getDoubletPrefs(
//							AppPrefs.KEY_LATITUDE, 0);
//					double longitude = prefs.getDoubletPrefs(
//							AppPrefs.KEY_LONGITUDE, 0);
//					myLocation = new Location("");
//					myLocation.setLatitude(latitude);
//					myLocation.setLongitude(longitude);
//				}
//
//				rehabAdapter.setLocation(myLocation);

				rehabAdapter.filter(resultHolder);
				makeListVisible();
				addMarkers();
				updateEmptyViewVisibility();

			}else if (requestCode == REQUEST_REHAB_DETAILS){
				if (mReqPosition != -1) {

					if(data.hasExtra("isfav")){
					boolean isFav =  data.getBooleanExtra("isfav",false);
					
						rehabResponse.getRehabModel(mReqPosition).setFavorite(isFav);
					}

//					if (m.getCheckInMeeting() == 1) {
//						unCheckAllMeetings();
//					}
//					meetingsAdapter.setMeeting(mReqPosition, m);
//					// meetings.set(mReqPosition, m);
//					setMeetingModel(m);
					// makeListVisible();

				}
			}
		}else if (resultCode == RehabsFilterActivity.CLEAR_FILTER) {
			rehabAdapter.clearFilters();
			// makeListVisible();
			// refreshMap();
			addMarkers();
			updateEmptyViewVisibility();
		}
		
		rehabAdapter.notifyDataSetChanged();
			
	}

	public void makeListVisible() {
		if (!list.isShown()) {
			// btnList.setText("MAP");

			btnList.setImageResource(R.drawable.map_btn);
			btnFindMe.setVisibility(View.GONE);
			mapFrag.onPause();
			// mapFrag.onPause();
			// customMap.onPause();
			list.setVisibility(View.VISIBLE);
			listWasInvisible = true;
		}
	}

	private void moveMapCamera(LatLng p) {
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(p, Consts.MAP_ZOOM);
		map.animateCamera(cameraUpdate);
	}

	private double getDistance(double markerLatitude, double markerLongitude) {
		Location pinLocation = new Location("B");
		pinLocation.setLatitude(markerLatitude);
		pinLocation.setLongitude(markerLongitude);
		double distance = (double) (myLocation.distanceTo(pinLocation) * 0.000621371192f);

		distance = Math.floor(distance * 10) / 10f;
		return distance;
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		rehabsUpdating = false;

		if (event.equals(EventParams.EVENT_GET_ALL_REHABS)) {
			
			if (pd != null) {
				pd.dismiss();
			}
			rehabsUpdated = true;
			
			responseProcessinTask = new ResponseProcessingTask(obj);
			responseProcessinTask.execute();
		}

	}

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		rehabsUpdating = false;
		pd.dismiss();

	}

	@Override
	public void onBackendConnected() {
			// refreshMeetingList();

			refreshRehab();

	}

	public class RehabInfoWindowAdapter implements
			GoogleMap.InfoWindowAdapter {

		@Override
		public View getInfoWindow(Marker marker) {
			
			//check for my location
			
//			if (marker.getId().equals(myLocMarker.getId())){ 
//				View emptyView = new View(getContext());
//				emptyView.setLayoutParams(new LinearLayout.LayoutParams(1, 1));
//				return emptyView;
//				}
			
			View v = RehabsFragment.this.getActivity().getLayoutInflater()
					.inflate(R.layout.map_rehab_info_window, null);
			TextView tvRehabName = (TextView) v.findViewById(R.id.tvRehabName);
//			TextView tvRehabLocation = (TextView) v
//					.findViewById(R.id.tvRehabLocation);
			TextView tvRehabIns  = (TextView) v
					.findViewById(R.id.tvRehabIns);
			
			TextView tvDistance = (TextView) v.findViewById(R.id.tvDistance);

			TextView tvTime = (TextView) v.findViewById(R.id.tvTime);

			TextView tvStatus = (TextView) v.findViewById(R.id.tvStatus);

			RehabModel m = spots.get(marker);

			if (m == null) {
				return v;
			}

			tvRehabName.setText(m.getName());
			tvRehabIns.setText(m.getTypeName());
			tvTime.setText(m.getHours());
			tvDistance.setText(m.getDistance() == 1f ? m.getDistance() + " MILE":m.getDistance() + " MILES");
			
			if(RehabResponseModel.getTodayRehabTiming(m.getRehabDays()) == null){
				tvStatus.setVisibility(View.GONE);
			}else if(rehabResponse.isOpenNow(m.getRehabDays())){
				tvStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.hours_bg_green));
				tvStatus.setText(R.string.rehab_open_now_);
			}else{
				tvStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.yellow_round_corners));
				tvStatus.setText(R.string.rehab_close_now_);
			}

			return v;
		}

		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}
	}
	
	public class ResponseProcessingTask extends AsyncTask<Void, Void, Void> {

		private Object obj;

		public ResponseProcessingTask(Object obj) {
			this.obj = obj; 
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				JSONObject data = ((JSONObject) obj);
				JSONArray rehabs = data.getJSONArray("allRehabs");

//				JSONArray days = allRehab.getJSONArray("days");
//
//				for (int i = 0; i < days.length(); i++) {
//					JSONObject day = days.getJSONObject(i);
//					int dayId = day.getInt("id");
//					String dayName = day.getString("name");
//					rehabResponse.addDay(dayId, dayName);
//				}

//				JSONArray rehabs = allRehab.getJSONArray("rehabs");

//				int rideCount = 0;
//				for(wear condom; if fuck is successfull && water emited; rideCount++) {
//				   voice: ohhhhooohhoo
//				};
				SimpleDateFormat amFormater = new SimpleDateFormat("hh:mma", Locale.US);
				for (int i = 0; i < rehabs.length(); i++) {
					JSONObject rehab = rehabs.getJSONObject(i);

					if(rehab == null) continue;
					
					String phone = rehab.getString("phone");
					String about = rehab.getString("about");
					String state = rehab.getString("state");

					/*
					 * "name":"test rehab", "longitude":33.12345505,
					 * "insurance_accepted":"no", "other_services":"",
					 * "status":"active", "website":"citrusbits", "hours":"",
					 * "zipcode":"",
					 * "datetime_added":"2016-01-15T10:22:04.000Z",
					 * "relation":"owner", "type_id":0, "user_name":"yasir",
					 */

					String name = rehab.getString("name");
					

					double latitude = rehab.getDouble("latitude");
					double longitude = rehab.getDouble("longitude");
					String insuranceAccepted = rehab.getString("insurance_accepted");
					String otherServices = rehab.getString("other_services");
					String status = rehab.getString("status");
					String website = rehab.getString("website");
					
					String zipCode = rehab.getString("zipcode");
					String dateTimeAdded = rehab.getString("datetime_added");
					String relation = rehab.getString("relation");
					String typeName = rehab.getString("type_name");
					String rehabPackageName = rehab.getString("rehab_package_name"); //"gold";
					String userName = rehab.getString("user_name");
					String address = rehab.getString("address");
					String email = rehab.getString("email");

					String userPhone = rehab.getString("user_phone");
//					String codes = rehab.getString("codes");
					
					boolean isFavorite = rehab.getInt("favorite") == 1;
					
//					String days = rehab.getString("days");
					
					JSONArray rehabDays = rehab.getJSONArray("rehab_days");
					List<RehabDayModel> rehabDayList = new ArrayList<>();

					for (int j = 0; j < rehabDays.length(); j++) {
						JSONObject rehabDayObjet = rehabDays.getJSONObject(j);

						String onThisDay = rehabDayObjet.getString("on_this_day");

						String endTime = rehabDayObjet.getString("end_time");

						String startTime = rehabDayObjet.getString("start_time");
						String dayName = rehabDayObjet.getString("name");
						

						int dayId = rehabDayObjet.getInt("day_id");

						RehabDayModel rehabDay = new RehabDayModel();
						rehabDay.setOnThisDay(onThisDay);
						rehabDay.setEndTime(endTime);
						rehabDay.setStartTime(startTime);
						rehabDay.setDayId(dayId);
						rehabDay.setDayName(dayName);

						rehabDayList.add(rehabDay);

					}
					
					JSONArray videosArray = rehab.getJSONArray("rehab_videos");
					List<String> videoList = new ArrayList<>();

					for (int j = 0; j < videosArray.length(); j++) {
						JSONObject video = videosArray.getJSONObject(j);
						String link = video.getString("link");
						videoList.add(link);

					}

					String userEmail = rehab.getString("user_email");
					String city = rehab.getString("city");
					int id = rehab.getInt("id");
					String approved = rehab.getString("approved");

					JSONArray photosArray = rehab.getJSONArray("rehab_photos");
					List<String> photoList = new ArrayList<>();

					for (int j = 0; j < photosArray.length(); j++) {
						JSONObject photo = photosArray.getJSONObject(j);
						String link = photo.getString("link");
						photoList.add(link);

					}
					
					JSONArray rehab_paymentsJArray = rehab.getJSONArray("rehab_payments");
					List<String> rehab_payments = new ArrayList<>();

					for (int j = 0; j < rehab_paymentsJArray.length(); j++) {
						JSONObject payment = rehab_paymentsJArray.getJSONObject(j);
						String pname = payment.getString("name");
						rehab_payments.add(pname);

					}
					
					//rehab_insurances
					JSONArray rehab_insurancesArray = rehab.getJSONArray("rehab_insurances");
					List<String> rehab_insurancesList = new ArrayList<>();

					for (int j = 0; j < rehab_insurancesArray.length(); j++) {
						JSONObject rehab_insurance = rehab_insurancesArray.getJSONObject(j);
						String insName = rehab_insurance.getString("name");
						rehab_insurancesList.add(insName);
					}

					
					
					RehabModel rehabModel = new RehabModel();
					rehabModel.setId(id);
					rehabModel.setAbout(about);
					rehabModel.setAddress(address);
					rehabModel.setApproved(approved);
					rehabModel.setCity(city);
					rehabModel.setTypeName(typeName);
					rehabModel.setDateTimeAdded(dateTimeAdded);
					rehabModel.setEmail(email);
//					rehabModel.setDays(days);
					rehabModel.setRehabPayments(rehab_payments);
					rehabModel.setRehabInsurances(rehab_insurancesList);
					rehabModel.setInsuranceAccepted(insuranceAccepted);
					rehabModel.setLatitude(latitude);
					rehabModel.setLongitude(longitude);
					rehabModel.setName(name);
					rehabModel.setOtherServices(otherServices);
					rehabModel.setPhone(phone);
					rehabModel.setFavorite(isFavorite);
					rehabModel.setRehabDays(rehabDayList);
					rehabModel.setPackageName(rehabPackageName);
					rehabModel.setRehabPhotos(photoList);
					rehabModel.setRehabVideos(videoList);
					rehabModel.setRelation(relation);
					rehabModel.setState(state);
					rehabModel.setStatus(status);
//					rehabModel.setTypeId(typeId);
					rehabModel.setUserEmail(userEmail);
					rehabModel.setUserName(userName);
					rehabModel.setWebsite(website);
					rehabModel.setZipCode(zipCode);
					rehabModel.setUserPhone(userPhone);
					rehabModel.setDistance(getDistance(latitude, longitude));
					
					
					//today hours open and close
					RehabDayModel rehabDay = RehabResponseModel.getTodayRehabTiming(rehabModel.getRehabDays());
					if(rehabDay == null){
						rehabModel.setHours(Consts.CLOSED_TODAY);
					}else{
						String openTime = amFormater.format(rehabDay.getOpenDate());
						String closeTime = amFormater.format(rehabDay.getCloseDate());
						rehabModel.setHours(openTime + " - " + closeTime);
					}
					
					rehabResponse.addRehabModel(rehabModel);

				}

				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}

		public void sortData() {
//			Collections.sort(meetings, new Comparator<MeetingModel>() {
//				public int compare(MeetingModel o1, MeetingModel o2) {
//
//					if (o1.getDateObj() == null || o2.getDateObj() == null)
//						return 0;
//					return o1.getDateObj().compareTo(o2.getDateObj());
//				}
//			});
		}

		String daysInWeek[] = { "monday", "tuesday", "wednesday", "thursday",
				"friday", "saturday", "sunday" };

		public NearesDateTime getOnDate(String days, String times, int position) {

			List<NearesDateTime> nearestDateTimes = new ArrayList<>();

			String dayArray[] = days.split(",");
			String timeArray[] = times.split(",");
			int nearPosition = 0;
			int minDayDiffer = Integer.MAX_VALUE;
			for (int j = 0; j < dayArray.length; j++) {
				String onDay = dayArray[j];
				String onTime = timeArray[j];

				Calendar sCalendar = Calendar.getInstance();
				String dayLongName = sCalendar.getDisplayName(
						Calendar.DAY_OF_WEEK, Calendar.LONG,
						Locale.getDefault());
				int onDayPositon = -1;
				int todayPosition = -1;

				for (int i = 0; i < daysInWeek.length; i++) {
					if (onDay.toLowerCase(Locale.US).equals(daysInWeek[i])) {
						onDayPositon = i;
					}

					if (dayLongName.toLowerCase(Locale.US).equals(daysInWeek[i])) {
						todayPosition = i;
					}
				}

				if (onDayPositon > todayPosition) {
					sCalendar.add(Calendar.DAY_OF_MONTH,
							(onDayPositon - todayPosition));
					int dayDiffer = onDayPositon - todayPosition;
					if (minDayDiffer > dayDiffer) {
						minDayDiffer = dayDiffer;
						nearPosition = j;
					}

				} else if (onDayPositon < todayPosition) {
					sCalendar.add(Calendar.DAY_OF_MONTH, ((daysInWeek.length)
							- todayPosition + onDayPositon));
					
					int dayDiffer = ((daysInWeek.length) - todayPosition + onDayPositon);
					if (minDayDiffer > dayDiffer) {
						minDayDiffer = dayDiffer;
						nearPosition = j;
					}

				} else if (onDayPositon == todayPosition) {
					SimpleDateFormat _12HourSDF = new SimpleDateFormat(
							"hh:mm a", Locale.US);
					try {
						final Date dateObj = _12HourSDF.parse(onTime);
						sCalendar.set(Calendar.HOUR_OF_DAY,
								dateObj.getHours() + 1);
						sCalendar.set(Calendar.MINUTE, dateObj.getMinutes());
						if (sCalendar.before(Calendar.getInstance())) {
							sCalendar.add(Calendar.DAY_OF_MONTH,
									(daysInWeek.length));

							int dayDiffer = daysInWeek.length;
							if (minDayDiffer > dayDiffer) {
								minDayDiffer = dayDiffer;
								nearPosition = j;
							}

						} else {
//							meetings.get(position).setTodayMeeting(true);
							int dayDiffer = 0;
							if (minDayDiffer > dayDiffer) {
								minDayDiffer = dayDiffer;
								nearPosition = j;
							}
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}

				SimpleDateFormat dateFormate = new SimpleDateFormat(
						"dd/MM/yyyy");
				String dateMade = dateFormate.format(sCalendar.getTime());

				NearesDateTime nearDateTime = new NearesDateTime();
				nearDateTime.setDate(dateMade);
				nearDateTime.setTime(onTime);
				nearestDateTimes.add(nearDateTime);
			}

			return nearestDateTimes.get(nearPosition);

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			rehabAdapter = new RehabListAdapter(getActivity(),
					R.layout.list_item_meeting, rehabResponse);
			if(!isAdded()) return;
			list.setAdapter(rehabAdapter);
			if(RehabsFilterActivity.filterModel.isApplied()){
				rehabAdapter.filter(RehabsFilterActivity.filterModel);				
			}
			rehabAdapter.notifyDataSetChanged();

			addMarkers();
			
//			meetingsAdapter = new MeetingsListAdapter(getActivity(),
//					R.layout.list_item_meeting, meetings);
//			list.setAdapter(meetingsAdapter);
//			meetingsAdapter.notifyDataSetChanged();
//			addMarkers();
//			// refrshFavList();
//
//			meetingsAdapter.filter(resultHolder);

			// refreshMap();
		}

	}

}
