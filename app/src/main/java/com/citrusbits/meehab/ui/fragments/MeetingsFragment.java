package com.citrusbits.meehab.ui.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.app.MeehabApp;
import com.citrusbits.meehab.model.NearestDateTime;
import com.citrusbits.meehab.ui.HomeActivity;
import com.citrusbits.meehab.ui.meetings.MeetingDetailsActivity;
import com.citrusbits.meehab.ui.meetings.MeetingsFilterActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.adapters.MeetingsListAdapter;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.ui.customviews.CustomMapFragment;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.map.LocationService;
import com.citrusbits.meehab.map.LocationService.LocationListener;
import com.citrusbits.meehab.map.LocationService.MyLocalBinder;
import com.citrusbits.meehab.map.LocationUtils;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.model.MeetingModel.MarkerColorType;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.pojo.MeetingResponse;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.MeetingUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.TimestampUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

public class MeetingsFragment extends Fragment implements
		OnSocketResponseListener, OnBackendConnectListener,
		View.OnClickListener, ListView.OnItemClickListener,
		GoogleMap.OnInfoWindowClickListener, OnMarkerClickListener {

	public static final int REQUEST_MEETING_DETAILS = 2;

	public static final int Filter_request = 200;
	// Google Map
	// SupportMapFragment mapFrag;
	GoogleMap map;
	private Location myLocation /* = new LatLng(33.671447, 73.069612) */;
	private ImageButton btnList, btnFindMe;
	private ListView list;
	ArrayList<MeetingModel> meetings = new ArrayList<>();

	ArrayList<MeetingModel> mapMeetings = new ArrayList<>();

	HashMap<Marker, MeetingModel> spots;
	MeetingsListAdapter meetingsAdapter;
	private HomeActivity homeActivity;
	private Dialog pd;
	private boolean meetingUpdated;
	private EditText editTopCenter;

	AppPrefs prefs;

	LocationService locationService;

	Context mContext;

	private int mReqPosition = -1;

	CustomMapFragment customMap;

	private boolean listWasInvisible = false;

	public static FilterResultHolder resultHolder = new FilterResultHolder();

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

	MeetingProcessingTask meetingProcessinTask;

	private long timeZone;

	private Marker myLocMarker;

	private View emptyList;
	private TextView topRightBtn;
	private boolean startSearchEditing;

	public MeetingsFragment() {
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

		try {

			locationService.removeListener(locationListener);
			getActivity().unbindService(locServiceConnection);
			getActivity().stopService(
					new Intent(mContext, LocationService.class));

		} catch (Exception e) {
		}

		if (meetingProcessinTask != null
				&& meetingProcessinTask.getStatus() == android.os.AsyncTask.Status.RUNNING) {
			meetingProcessinTask.cancel(true);
		}
		homeActivity = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.homeActivity = (HomeActivity) getActivity();
		pd = UtilityClass.getProgressDialog(homeActivity);

		userDatasource = new UserDatasource(getActivity());
		timeZone = TimestampUtils.getTimeZoneOffset();
		user = userDatasource.findUser(AccountUtils.getUserId(getActivity()));
		mContext = getActivity();
		meetings = new ArrayList<>();
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
		View v = inflater.inflate(R.layout.fragment_meetings, container, false);

		v.findViewById(R.id.topMenuBtn).setOnClickListener(this);
		topRightBtn = (TextView)v.findViewById(R.id.topRightBtn);
		topRightBtn.setOnClickListener(this);
		list = (ListView) v.findViewById(R.id.list);
		emptyList = v.findViewById(R.id.emptyList);
		btnList = (ImageButton) v.findViewById(R.id.btnList);
		btnFindMe = (ImageButton) v.findViewById(R.id.btnFindMe);
		editTopCenter = (EditText) v.findViewById(R.id.editTopCenter);
		meetingsAdapter = new MeetingsListAdapter(getActivity(),
				R.layout.list_item_meeting, meetings);

		/*editTopCenter.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int DRAWABLE_LEFT = 0;
				final int DRAWABLE_TOP = 1;
				final int DRAWABLE_RIGHT = 2;
				final int DRAWABLE_BOTTOM = 3;

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (event.getRawX() >= (editTopCenter.getRight() - editTopCenter
							.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds()
							.width())) {
						// your action here
						editTopCenter.setText("");
						return true;
					}
				}
				return false;
			}
		});*/
		setSearchEditing(false);
		editTopCenter.addTextChangedListener(new TextWatcher() {

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
				searchMeetings(inputText);
				if (inputText.trim().length() == 0) {
//					topRightBtn.setText(R.string.filter);
					if (listWasInvisible) {
						switchList();
						listWasInvisible = false;
					}
				}else{
//					topRightBtn.setText("Cancel");
				}
				updateEmptyViewVisibility();
			}
		});
		editTopCenter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					UtilityClass.hideSoftKeyboard(getActivity().getCurrentFocus());
				}
				return false;
			}
		});
		btnList.setOnClickListener(this);
		btnFindMe.setOnClickListener(this);
		list.setOnItemClickListener(this);

		if (MeehabApp.isPlayServiceOk) {
			if (map == null) {
				/*
				 * mapFrag = ((SupportMapFragment) getChildFragmentManager()
				 * .findFragmentById(R.id.map)); mapFrag.getMapAsync(new
				 * OnMapReadyCallback() {
				 * 
				 * @Override public void onMapReady(GoogleMap arg) { map = arg;
				 * 
				 * map.setInfoWindowAdapter(new MeetingInfoWindowAdapter()); //
				 * map.getUiSettings().setMyLocationButtonEnabled(false);
				 * map.setMyLocationEnabled(true);
				 * map.getUiSettings().setScrollGesturesEnabled(true);
				 * map.getUiSettings().setZoomGesturesEnabled(true); //
				 * map.setOnMarkerClickListener(MeetingsFragment.this);
				 * 
				 * map.setOnInfoWindowClickListener(MeetingsFragment.this);
				 * 
				 * } });
				 */

				Fragment frag = getChildFragmentManager().findFragmentById(
						R.id.map);
				customMap = (CustomMapFragment) frag;

				map = customMap.getMap();
//				map.setMyLocationEnabled(true);
				map.getUiSettings().setMyLocationButtonEnabled(false);
				map.getUiSettings().setMapToolbarEnabled(false);
				map.setInfoWindowAdapter(new MeetingInfoWindowAdapter());
				map.getUiSettings().setScrollGesturesEnabled(true);
				map.getUiSettings().setZoomGesturesEnabled(true);
				map.setOnMarkerClickListener(MeetingsFragment.this);
				map.setOnInfoWindowClickListener(MeetingsFragment.this);
			}

		}

		onBackendConnected();

		return v;
	}

	/**
	 * @param text
	 */
	protected void searchMeetings(String text) {
		makeListVisible();
		meetingsAdapter.filter(text);
	}

	public void makeListVisible() {
		if (!list.isShown()) {
			// btnList.setText("MAP");

			btnList.setImageResource(R.drawable.map_btn);
			btnFindMe.setVisibility(View.GONE);
			// mapFrag.onPause();
			// customMap.onPause();
			list.setVisibility(View.VISIBLE);
			listWasInvisible = true;
		}
	}

	/**
	 *
	 */
	protected void addMarkers() {
		if(map == null) return;
		map.clear();
		spots.clear();
		mapMeetings.clear();

//		BitmapDescriptor icon = BitmapDescriptorFactory
//				.fromResource(R.drawable.pin);
		// add marker
		for (int i = 0; i < meetings.size(); i++) {

			MeetingModel m = meetings.get(i);

			if (!m.isTodayMeeting()) {
				continue;
			}

			mapMeetings.add(m);

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

//		moveCamera(
//				new LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
//				false);

		refreshMyLocationMarker(true);

		// fitBounds();

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	void refreshMeetingList() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			MeehabApp.toast(getString(R.string.no_internet_connection));
			return;
		}
		if (homeActivity.socketService != null) {
			pd.show();
			homeActivity.socketService.getMeeting(new JSONObject());
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
				myLocation.getLatitude(), myLocation.getLongitude()), Consts.MAP_ZOOM);

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
	public boolean onMarkerClick(Marker marker) {
		if (marker.getId().equals(myLocMarker.getId())){
			return true;
		}
		marker.showInfoWindow();
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		MeetingModel m = spots.get(marker);
		if (m != null) {

		}
		/*
		 * Intent intent = new Intent(getActivity(),
		 * MeetingDetailsActivity.class); Bundle bundle = new Bundle();
		 * 
		 * bundle.putSerializable("meeting", m); intent.putExtras(bundle); //
		 * startActivity(intent); mReqPosition = meetings.indexOf(m);
		 * 
		 * Toast.makeText(getActivity(), "Info Window is clicked!",
		 * Toast.LENGTH_SHORT).show(); startActivityForResult(intent,
		 * REQUEST_MEETING_DETAILS);
		 */

		/*
		 * int position = -1; List<MeetingModel>
		 * meetings=meetingsAdapter.getMeetings(); for (int i = 0; i <
		 * meetings.size(); i++) { if
		 * (m.getMeetingId().equals(meetings.get(i).getMeetingId())) { position
		 * = i; break; } } if (position == -1) {
		 * 
		 * return; }
		 */

		int position = getPostion(m.getMeetingId());

		if (position == -1) {

			return;
		}

		startDetailActivity(position);

	}

	public int getPostion(int meetingId) {
		int position = -1;
		List<MeetingModel> meetings_ = meetingsAdapter.getMeetingCache();
		for (int i = 0; i < meetings_.size(); i++) {
			if (meetings_.get(i).getMeetingId().equals(meetingId)) {
				position = i;
				break;
			}
		}
		return position;
	}

	public void unCheckAllMeetings() {
		List<MeetingModel> meetingCache = meetingsAdapter.getMeetingCache();
		for (int i = 0; i < meetingCache.size(); i++) {
			if (meetingCache.get(i).getCheckInMeeting() == 1) {
				meetingCache.get(i).setCheckInMeeting(0);
				// Toast.makeText(mContext, "Meeting Index "+i,
				// Toast.LENGTH_SHORT).show();
				break;
			}
		}

		for (int i = 0; i < meetings.size(); i++) {
			if (meetings.get(i).getCheckInMeeting() == 1) {
				meetings.get(i).setCheckInMeeting(0);
				// Toast.makeText(mContext, "Meeting Index "+i,
				// Toast.LENGTH_SHORT).show();
				break;
			}
		}

		for (int i = 0; i < mapMeetings.size(); i++) {
			if (mapMeetings.get(i).getCheckInMeeting() == 1) {
				mapMeetings.get(i).setCheckInMeeting(0);
				// Toast.makeText(mContext, "Meeting Index "+i,
				// Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

	public void setMeetingModel(MeetingModel meeting) {
		for (int i = 0; i < meetings.size(); i++) {
			if (meeting.getMeetingId().equals(meetings.get(i).getMeetingId())) {
				meetings.set(i, meeting);
				// Toast.makeText(mContext, "Meeting Index "+i,
				// Toast.LENGTH_SHORT).show();
				break;
			}
		}

		for (int i = 0; i < mapMeetings.size(); i++) {
			if (meeting.getMeetingId()
					.equals(mapMeetings.get(i).getMeetingId())) {
				mapMeetings.set(i, meeting);
				// Toast.makeText(mContext, "Map Meeting Index "+i,
				// Toast.LENGTH_SHORT).show();
				break;
			}
		}

	}

	/*
	 * @Override public boolean onMarkerClick(Marker marker) {
	 * marker.showInfoWindow(); return false; }
	 */

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		// startDetailActivity(position);
		int _position = getPostion(meetings.get(position).getMeetingId());

		startDetailActivity(_position);
	}

	public void startDetailActivity(int position) {
		Intent intent = new Intent(getActivity(), MeetingDetailsActivity.class);
		Bundle bundle = new Bundle();

		bundle.putSerializable("meeting", meetingsAdapter.getMeetingCache()
				.get(position));

		intent.putExtras(bundle);

		// getActivity().startActivity(intent);
		mReqPosition = position;
		// Toast.makeText(mContext, "Request Position is "+mReqPosition,
		// Toast.LENGTH_SHORT).show();
		startActivityForResult(intent, REQUEST_MEETING_DETAILS);
		getActivity().overridePendingTransition(R.anim.activity_in,
				R.anim.activity_out);
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
			case R.id.editTopCenter:
				setSearchEditing(true);
				break;
			case R.id.topRightBtn:

				if(startSearchEditing){
					setSearchEditing(false);
					return;
				}

				// filter
				Intent intent = new Intent(getActivity(),
						MeetingsFilterActivity.class);
				intent.putExtra(MeetingsFilterActivity.MEETING_FILTER, resultHolder);
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

	private void setSearchEditing(boolean enable) {
		startSearchEditing = enable;
		if (enable) {
			editTopCenter.setOnClickListener(null);
			editTopCenter.setFocusable(true);
			editTopCenter.setFocusableInTouchMode(true);
			editTopCenter.setCursorVisible(true);
			editTopCenter.setKeyListener((KeyListener) editTopCenter.getTag());
			editTopCenter.requestFocus();

			topRightBtn.setText("Cancel");
			UtilityClass.showSoftKeyboard(editTopCenter);

			emptyList.setPadding(0,0,0,getActivity().getResources().getDimensionPixelSize(R.dimen.softkeyboard_height));
		}else {
			emptyList.setPadding(0,0,0,0);
			UtilityClass.hideSoftKeyboard(editTopCenter);
			topRightBtn.setText(R.string.filter);

			editTopCenter.setText("");
			editTopCenter.setOnClickListener(this);
			editTopCenter.setFocusable(false);
			editTopCenter.setFocusableInTouchMode(false);
			editTopCenter.setCursorVisible(false);
			editTopCenter.setTag(editTopCenter.getKeyListener());
			editTopCenter.setKeyListener(null);
		}
	}

	public void switchList() {
		if (!list.isShown()) {
			// btnList.setText("MAP");
			btnFindMe.setVisibility(View.GONE);
			// mapFrag.onPause();
			// customMap.onPause();
			btnList.setImageResource(R.drawable.map_btn);
			list.setVisibility(View.VISIBLE);
		} else {
			// btnList.setText("LIST");
			btnList.setImageResource(R.drawable.list_btn);
			btnFindMe.setVisibility(View.VISIBLE);
			list.setVisibility(View.GONE);
			// mapFrag.onResume();
		}
		updateEmptyViewVisibility();
	}

	private void updateEmptyViewVisibility() {
		if(list.getVisibility() == View.VISIBLE && meetingsAdapter.getCount() == 0){
			emptyList.setVisibility(View.VISIBLE);
		}else{
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
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								// list.setVisibility(View.GONE);
								switchList();
							}
						}, 300);

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

		//-1 = RESULT_OK
		if (resultCode == -1) {
			if (requestCode == Filter_request) {
				/*
				 * MeetingFilterModel fm = (MeetingFilterModel) data
				 * .getSerializableExtra(MeetingsFilterActivity.MEETING_FILTER);
				 */
				resultHolder = (FilterResultHolder) data
						.getSerializableExtra(MeetingsFilterActivity.MEETING_FILTER);

//				Location location = map.getMyLocation();
//				if (location == null) {
//					AppPrefs prefs = AppPrefs.getAppPrefs(getActivity());
//					double latitude = prefs.getDoubletPrefs(
//							AppPrefs.KEY_LATITUDE, 0);
//					double longitude = prefs.getDoubletPrefs(
//							AppPrefs.KEY_LONGITUDE, 0);
//					location = new Location("");
//					location.setLatitude(latitude);
//					location.setLongitude(longitude);
//				}

//				meetingsAdapter.setLocation(location);

//				List<String> days = resultHolder.getDays();
//				List<String> times = resultHolder.getTimes();
//
//				Log.i("If Any Day ", resultHolder.getAnyDay() + "");
//
//				for (int i = 0; i < days.size(); i++) {
//					Log.e("Day ", days.get(i) + "");
//				}
//
//				Log.e("If Any Time ", resultHolder.getAnyTime() + "");
//
//				for (int i = 0; i < times.size(); i++) {
//					Log.e("Time ", times.get(i) + "");
//				}

				meetingsAdapter.filter(resultHolder);

				makeListVisible();
				addMarkers();

				updateEmptyViewVisibility();

			} else if (requestCode == REQUEST_MEETING_DETAILS) {

				if (mReqPosition != -1) {

					MeetingModel m = (MeetingModel) data
							.getSerializableExtra("meeting");

					if (m.getCheckInMeeting() == 1) {
						unCheckAllMeetings();
					}
					meetingsAdapter.setMeeting(mReqPosition, m);
					// meetings.set(mReqPosition, m);
					setMeetingModel(m);
					// makeListVisible();

				}
			}
		} else if (resultCode == MeetingsFilterActivity.CLEAR_FILTER) {
			resultHolder = new FilterResultHolder();
			meetingsAdapter.filter("");
			meetingsAdapter.notifyDataSetChanged();
			addMarkers();
			updateEmptyViewVisibility();
		}
	}

	private void moveMapCamera(LatLng p) {
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(p, 12);
		map.animateCamera(cameraUpdate);
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {

		if (pd != null && pd.isShowing()) {
			pd.dismiss();

		}

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

			} catch (Exception e) {
				e.printStackTrace();
				String message = e.getMessage();

			}

			// refrshFavList();

		}

	}

	public class MeetingProcessingTask extends AsyncTask<Void, Void, Void> {


		@Override
		protected Void doInBackground(Void... params) {

			if (meetings.size() > 0) {

				for (int i = 0; i < meetings.size(); i++) {
					MeetingModel meeting = meetings.get(i);
					meeting.setFavourite(meeting.getFavouriteMeeting() == 1);

					/*
					 * meeting.setOnTime(DateTimeUtils.getTimeWRTTimeZone(
					 * meeting.getOnTime(), timeZone));
					 */

					//distance calculation
					float[] results = new float[1];
					Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(), meeting.getLatitude(), meeting.getLongitude(), results);
					//
					double distance = results[0] * 0.000621371192f;
//					double distance = distance(meeting.getLatitude(), meeting.getLongitude(),
//							myLocation.getLatitude(), myLocation.getLongitude());

//					
//					Location pinLocation = new Location("");
//					pinLocation.setLatitude(meeting.getLatitude());
//					pinLocation.setLongitude(meeting.getLongitude());
//					double distance = (double) (myLocation
//							.distanceTo(pinLocation) * 0.000621371192f);
//
					distance = Math.floor(distance * 10) / 10f;

					meeting.setDistanceInMiles(distance);

					NearestDateTime nearDateTime = MeetingUtils.getNearestDate(meeting.getOnDay(),
							meeting.getOnTime());


					if (nearDateTime != null) {
						meeting.setTodayMeeting(nearDateTime.isToday());
						meeting.setOnDateOrigin(nearDateTime.getDate());
						meeting.setNearestTime(nearDateTime.getTime());
						meeting.setNearestDateTime(nearDateTime.getDateTime());
						meeting.setOnDate(MeetingUtils.formateDate(nearDateTime.getDateTime()));
					}

					MeetingUtils.setMeetingTimingStatus(meeting, meeting.getNearestDateTime());
				}

//				sorting for nearest meeting first
				MeetingUtils.sortByDistance(meetings);
				//sort by soonest
//				MeetingUtils.sortByDate(meetings);
			}

			return null;
		}


		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			meetingsAdapter = new MeetingsListAdapter(getActivity(),
					R.layout.list_item_meeting, meetings);
			//avoid crashing
			if(!isAdded()) return;
			list.setAdapter(meetingsAdapter);
			meetingsAdapter.notifyDataSetChanged();
			meetingsAdapter.filter(resultHolder);

			addMarkers();
			updateEmptyViewVisibility();
			// refreshMap();
		}

	}


	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		pd.dismiss();

	}

	@Override
	public void onBackendConnected() {

		if (!meetingUpdated) {
			if (NetworkUtils.isNetworkAvailable(getActivity())) {
				try {
					refreshMeetingList();
				} finally {
				}

			} else {
				MeehabApp.toast(getString(R.string.no_internet_connection));
			}

		}

	}

	public class MeetingInfoWindowAdapter implements
			GoogleMap.InfoWindowAdapter {

		@Override
		public View getInfoWindow(Marker marker) {
			View v = MeetingsFragment.this.getActivity().getLayoutInflater()
					.inflate(R.layout.map_meeting_info_window, null);
			TextView txtName = (TextView) v.findViewById(R.id.txtName);
			TextView txtLocationName = (TextView) v
					.findViewById(R.id.txtLocationName);

			TextView txtReviewCounts = (TextView) v
					.findViewById(R.id.txtReviewCounts);
			RatingBar rating = (RatingBar) v.findViewById(R.id.rating);
			TextView txtTime = (TextView) v.findViewById(R.id.txtTime);
			TextView txtDistance = (TextView) v.findViewById(R.id.txtDistance);
			TextView txtStatus = (TextView) v.findViewById(R.id.txtStatus);

			MeetingModel m = spots.get(marker);

			txtName.setText(m.getName());
			// txtTime.setText(m.getOnTime());
			txtTime.setText(m.getNearestTime());
			txtReviewCounts.setText(String.valueOf(m.getReviewsCount())
					+ " reviews");
			txtLocationName.setText(m.getBuildingType());

			rating.setRating(m.getReviewsAvg());
			txtStatus.setText(m.getStartInTime());

			if (m.getMarkertypeColor() == MarkerColorType.GREEN) {
				txtStatus.setBackgroundResource(R.drawable.hours_bg_green);
			} else if (m.getMarkertypeColor() == MarkerColorType.ORANGE) {
				txtStatus.setBackgroundResource(R.drawable.yellow_round_corners);
			} else if (m.getMarkertypeColor() == MarkerColorType.RED) {
				txtStatus.setBackgroundResource(R.drawable.ongoing_btn);
			}

			// txtDistance.setText(UtilityClass.calculatDistance(m.getPosition()));
			txtDistance.setText(m.getDistanceInMiles() + " MILES");

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
