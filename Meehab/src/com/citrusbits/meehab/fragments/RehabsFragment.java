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
import com.citrusbits.meehab.RehabAdditionActivity;
import com.citrusbits.meehab.RehabDetailsActivity;
import com.citrusbits.meehab.RehabsFilterActivity;
import com.citrusbits.meehab.SocketFragment;
import com.citrusbits.meehab.adapters.MeetingsListAdapter;
import com.citrusbits.meehab.adapters.RehabListAdapter;
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
import com.citrusbits.meehab.model.RehabDayModel;
import com.citrusbits.meehab.model.RehabModel;
import com.citrusbits.meehab.model.RehabResponseModel;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.pojo.MeetingResponse;
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

	HashMap<Marker, RehabModel> spots;
	RehabListAdapter rehabAdapter;
	private HomeActivity homeActivity;
	private ProgressDialog pd;
	private boolean meetingUpdated;
	private EditText etSearch;
	private View focus_thief;

	AppPrefs prefs;

	LocationService locationService;

	Context mContext;

	RehabResponseModel rehabResponse = new RehabResponseModel();

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

	private boolean listWasInvisible = false;

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

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDatasource = new UserDatasource(getActivity());
		userDatasource.open();

		user = userDatasource.findUser(AccountUtils.getUserId(getActivity()));
		mContext = getActivity();

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
		etSearch = (EditText) v.findViewById(R.id.etSearch);

		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String inputText = s.toString().trim().toLowerCase();
				searchRehabs(inputText);
				if (inputText.trim().length() == 0) {
					if (listWasInvisible) {
						switchList();
						listWasInvisible = false;
					}
				}
			}
		});

		focus_thief = v.findViewById(R.id.focus_thief);

		rehabAdapter = new RehabListAdapter(getActivity(),
				R.layout.list_rehab_item, rehabResponse.getRehabs());

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
		map.clear();
		spots.clear();

		BitmapDescriptor icon = BitmapDescriptorFactory
				.fromResource(R.drawable.pin);
		// add marker
		List<RehabModel> rehabs = rehabResponse.getRehabs();
		for (int i = 0; i < rehabs.size(); i++) {
			RehabModel rehab = rehabs.get(i);
			// Creating an instance of MarkerOptions to set position
			int resourceId = R.drawable.pin_dark_pink;
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

		moveCamera(
				new LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
				false);

		// fitBounds();

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	void refreshRehab() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			App.alert(getString(R.string.no_internet_connection));
			return;
		}

		if (homeActivity.socketService != null) {

			pd.show();
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
		if (m != null) {

		}
		Intent intent = new Intent(getActivity(), RehabDetailsActivity.class);
		Bundle bundle = new Bundle();

		bundle.putSerializable(RehabDetailsActivity.KEY_REHAB, m);
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
		Intent intent = new Intent(getActivity(), RehabDetailsActivity.class);
		Bundle bundle = new Bundle();

		bundle.putSerializable(RehabDetailsActivity.KEY_REHAB, rehabResponse.getRehabModel(position));
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
			// listWasInvisible=true;
		} else {
			// btnList.setText("LIST");
			btnList.setImageResource(R.drawable.list_btn);
			btnFindMe.setVisibility(View.VISIBLE);
			list.setVisibility(View.GONE);
			// listWasInvisible=false;
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

		Toast.makeText(getActivity(), "onActivityResult is called ",
				Toast.LENGTH_SHORT).show();

		if (resultCode == getActivity().RESULT_OK) {
			if (requestCode == Filter_request) {
				/*
				 * MeetingFilterModel fm = (MeetingFilterModel) data
				 * .getSerializableExtra(MeetingsFilterActivity.MEETING_FILTER);
				 */

				RehaabFilterResultHolder resultHolder = (RehaabFilterResultHolder) data
						.getSerializableExtra(MeetingsFilterActivity.MEETING_FILTER);

				Toast.makeText(getActivity(),
						resultHolder.isanyDistance() + "", Toast.LENGTH_SHORT)
						.show();

				// meetingsAdapter.filter(resultHolder);

			}
		}
	}

	private void moveMapCamera(LatLng p) {
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(p, 12);
		map.animateCamera(cameraUpdate);
	}

	private double getDistance(double markerLatitude, double markerLongitude) {
		Location pinLocation = new Location("B");
		pinLocation.setLatitude(markerLatitude);
		pinLocation.setLongitude(markerLongitude);
		double distance = (double) (myLocation.distanceTo(pinLocation) * 0.000621371192f);

		distance = Math.floor(distance * 100) / 100f;
		return distance;
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {

		if (pd != null) {
			pd.dismiss();
		}

		if (event.equals(EventParams.EVENT_GET_ALL_REHABS)) {
			JSONObject data = ((JSONObject) obj);
			try {
				JSONObject allRehab = data.getJSONObject("allRehabs");

				JSONArray days = allRehab.getJSONArray("days");

				for (int i = 0; i < days.length(); i++) {
					JSONObject day = days.getJSONObject(i);
					int dayId = day.getInt("id");
					String dayName = day.getString("name");
					rehabResponse.addDay(dayId, dayName);
				}

				JSONArray rehabs = allRehab.getJSONArray("rehabs");

				for (int i = 0; i < rehabs.length(); i++) {
					JSONObject rehab = rehabs.getJSONObject(i);

					String phone = rehab.getString("phone");
					String about = rehab.getString("about");
					String state = rehab.getString("state");

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
					String insuranceAccepted = rehab
							.getString("insurance_accepted");
					String otherServices = rehab.getString("other_services");
					String status = rehab.getString("status");
					String website = rehab.getString("website");
					String hours = rehab.getString("hours");
					String zipCode = rehab.getString("zipcode");
					String dateTimeAdded = rehab.getString("datetime_added");
					String relation = rehab.getString("relation");
					int typeId = rehab.getInt("type_id");
					String userName = rehab.getString("user_name");
					String address = rehab.getString("address");
					String email = rehab.getString("email");

					String userPhone = rehab.getString("user_phone");
					String codes = rehab.getString("codes");

					JSONArray rehabDays = rehab.getJSONArray("rehab_days");
					List<RehabDayModel> rehabDayList = new ArrayList<>();

					for (int j = 0; j < rehabDays.length(); j++) {
						JSONObject rehabDayObjet = rehabDays.getJSONObject(j);

						String onThisDay = rehabDayObjet
								.getString("on_this_day");

						String endTime = rehabDayObjet.getString("end_time");

						String startTime = rehabDayObjet
								.getString("start_time");

						int dayId = rehabDayObjet.getInt("day_id");

						RehabDayModel rehabDay = new RehabDayModel();
						rehabDay.setOnThisDay(onThisDay);
						rehabDay.setEndTime(endTime);
						rehabDay.setStartTime(startTime);
						rehabDay.setDayId(dayId);
						rehabDay.setDayName(rehabResponse.getDay(dayId));

						rehabDayList.add(rehabDay);

					}

					RehabModel rehabModel = new RehabModel();
					rehabModel.setAbout(about);
					rehabModel.setAddress(address);
					rehabModel.setApproved(approved);
					rehabModel.setCity(city);
					rehabModel.setCodes(codes);
					rehabModel.setDateTimeAdded(dateTimeAdded);
					rehabModel.setEmail(email);
					rehabModel.setHours(hours);
					rehabModel.setId(id);
					rehabModel.setInsuranceAccepted(insuranceAccepted);
					rehabModel.setLatitude(latitude);
					rehabModel.setLongitude(longitude);
					rehabModel.setName(name);
					rehabModel.setOtherServices(otherServices);
					rehabModel.setPhone(phone);
					rehabModel.setRehabDays(rehabDayList);
					// rehabModel.setRehabInsurances(rehabInsurances);
					rehabModel.setRehabPhotoes(photoList);
					rehabModel.setRehabVideos(videoList);
					rehabModel.setRelation(relation);
					rehabModel.setState(state);
					rehabModel.setStatus(status);
					rehabModel.setTypeId(typeId);
					rehabModel.setUserEmail(userEmail);
					rehabModel.setUserName(userName);
					rehabModel.setWebsite(website);
					rehabModel.setZipCode(zipCode);
					rehabModel.setUserPhone(userPhone);
					rehabModel.setDistance(getDistance(latitude, longitude));
					rehabResponse.addRehabModel(rehabModel);

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			rehabAdapter.notifyDataSetChanged();

			addMarkers();
		}

	}

	@Override
	public void onSocketResponseFailure(String message) {
		pd.dismiss();

	}

	@Override
	public void onBackendConnected() {
		if (!meetingUpdated) {
			// refreshMeetingList();

			refreshRehab();
		}

	}

	public class MeetingInfoWindowAdapter implements
			GoogleMap.InfoWindowAdapter {

		@Override
		public View getInfoWindow(Marker marker) {
			View v = RehabsFragment.this.getActivity().getLayoutInflater()
					.inflate(R.layout.rehab_map_info, null);
			TextView tvRehabName = (TextView) v.findViewById(R.id.tvRehabName);
			TextView tvRehabLocation = (TextView) v
					.findViewById(R.id.tvRehabLocation);
			TextView tvDistance = (TextView) v.findViewById(R.id.tvDistance);

			TextView tvTime = (TextView) v.findViewById(R.id.tvTime);

			TextView tvStatus = (TextView) v.findViewById(R.id.tvStatus);

			RehabModel m = spots.get(marker);

			if (m == null) {
				return v;
			}

			tvRehabName.setText(m.getName());
			tvRehabLocation.setText(m.getAddress());
			tvDistance.setText(String.valueOf(m.getDistance() + " miles"));

			return v;
		}

		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}
	}

}
