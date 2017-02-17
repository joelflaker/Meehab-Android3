package com.citrusbits.meehab.ui.meetings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.ui.ActivityMoreReviews;
import com.citrusbits.meehab.ui.FullScreenMapActivity;
import com.citrusbits.meehab.ui.MyReviewDetailActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.ui.ReportInaccuracyActivity;
import com.citrusbits.meehab.ui.RsvpActivity;
import com.citrusbits.meehab.ui.users.UserProfileActivity;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.DatabaseHandler;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.ui.dialog.AddReviewToCalendarDialog;
import com.citrusbits.meehab.ui.dialog.AddReviewToCalendarDialog.AddReviewToCalendarDialogClickListener;
import com.citrusbits.meehab.ui.dialog.AddReviewToCalendarDialog.RsvpAction;
import com.citrusbits.meehab.ui.dialog.ReportAnInAccuracyDialog;
import com.citrusbits.meehab.ui.dialog.ReportAnInAccuracyDialog.ReportAnInAccuracyDialogClickListener;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.model.MeetingModel.MarkerColorType;
import com.citrusbits.meehab.model.MeetingReviewModel;
import com.citrusbits.meehab.model.MyReview;
import com.citrusbits.meehab.model.RSVPModel;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.pojo.MeetingReviewsResponse;
import com.citrusbits.meehab.ui.popup.CodePopup;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.ui.SocketActivity;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DateTimeUtils;
import com.citrusbits.meehab.utils.MeetingUtils;
import com.citrusbits.meehab.utils.MettingCodes;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class MeetingDetailsActivity extends SocketActivity implements
		OnSocketResponseListener, OnClickListener, OnMapClickListener, AdapterView.OnItemClickListener {

	public static final int CODE_FULL_SCREEN = 5;

	private MeetingModel meeting;
	private LinearLayout reviewsContainer;
	private ImageButton ibSetting;
	private ImageButton ibRating;

	private TextView txtMeetingName;
	private TextView txtDate;
	private TextView txtDistanceRight;
	private TextView numReviewsText;
	private RatingBar rating;
	private TextView txtLocationName;
	private TextView txtMeetingCurrentStatus;
	private TextView txtTime;

	private ImageButton btnGetDirection;
	private CheckBox cbHomeGroup;

	private TextView txtLocationAddress;
	private ImageButton btnRSVB;

	private Dialog pd;
	protected GoogleMap map;
	protected float defaultZoom = 8;
	protected ArrayList<MeetingReviewModel> meetingReviewModels = new ArrayList<>();

	UserDatasource userDatasource;
	UserAccount user;

	Button btnSeeMoreReviews;

	RelativeLayout rlAddReview;
	RelativeLayout rlRSVP;

	TextView tvRSVPAttending;

	private boolean homeGroup;

	DatabaseHandler dbHandler;

	private long timeZone = 0;

	LinearLayout llMultipleMeetings;

	TextView tvChooseADifferentDate;

	HashMap<Integer, MultiDatesManager> multidateMap;

	String mNearestDay;

	RsvpAction rsvpAction;
	private GridView gridMeetingCode;
	private String[] meetingCodes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_meeting_details);
		llMultipleMeetings = (LinearLayout) findViewById(R.id.llMultipleMeetings);
		tvChooseADifferentDate = (TextView) findViewById(R.id.tvChooseADifferentDate);
		timeZone = MeetingUtils.getTimeZoneOffset();

		btnSeeMoreReviews = (Button) findViewById(R.id.btnSeeMoreReviews);
		rlAddReview = (RelativeLayout) findViewById(R.id.rlAddReview);
		rlRSVP = (RelativeLayout) findViewById(R.id.rlRSVP);
		tvRSVPAttending = (TextView) findViewById(R.id.tvRSVPAttending);

		rlAddReview.setOnClickListener(this);
		rlRSVP.setOnClickListener(this);

		userDatasource = new UserDatasource(MeetingDetailsActivity.this);

		user = userDatasource.findUser(AccountUtils.getUserId(this));

		dbHandler = DatabaseHandler.getInstance(this);

		// top back button
		findViewById(R.id.btnBack).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});

		gridMeetingCode = (GridView)findViewById(R.id.gridMeetingCode);
		gridMeetingCode.setOnItemClickListener(this);

		pd = UtilityClass.getProgressDialog(this);
		ibRating = (ImageButton) findViewById(R.id.ibRating);
		ibSetting = (ImageButton) findViewById(R.id.ibSetting);

		txtMeetingName = (TextView) findViewById(R.id.txtMeetingName);
		txtLocationName = (TextView) findViewById(R.id.txtLocationName);
		rating = (RatingBar) findViewById(R.id.rating);
		numReviewsText = (TextView) findViewById(R.id.numReviewsText);
		txtDistanceRight = (TextView) findViewById(R.id.txtDistanceRight);
		txtDate = (TextView) findViewById(R.id.txtDate);
		txtTime = (TextView) findViewById(R.id.txtTime);
		txtMeetingCurrentStatus = (TextView) findViewById(R.id.txtMeetingCurrentStatus);

		reviewsContainer = (LinearLayout) findViewById(R.id.reviewsContainer);
		btnGetDirection = (ImageButton) findViewById(R.id.btnGetDirection);
		txtLocationAddress = (TextView) findViewById(R.id.txtLocationAddress);
		btnRSVB = (ImageButton) findViewById(R.id.btnRSVB);

		cbHomeGroup = (CheckBox) findViewById(R.id.cbHomeGroup);

		ibRating.setOnClickListener(this);
		ibSetting.setOnClickListener(this);
		cbHomeGroup.setOnClickListener(this);
		btnSeeMoreReviews.setOnClickListener(this);
		tvChooseADifferentDate.setOnClickListener(this);

		if (App.isPlayServiceOk) {
			if (map == null) {
				((SupportMapFragment) getSupportFragmentManager()
						.findFragmentById(R.id.map))
						.getMapAsync(new OnMapReadyCallback() {

							@Override
							public void onMapReady(GoogleMap arg) {
								map = arg;

								map.getUiSettings()
										.setAllGesturesEnabled(false);
								map.setOnMapClickListener(MeetingDetailsActivity.this);
								map.setMyLocationEnabled(true);
								addMarker(meeting);

							}
						});
			}
		}

		btnRSVB.setOnClickListener(this);

		btnGetDirection.setOnClickListener(this);

		Bundle extra = getIntent().getExtras();
		if (extra != null) {
			meeting = (MeetingModel) extra.getSerializable("meeting");

			//recalculate relative time
			MeetingUtils.setStartInTime(meeting, meeting.getOnDateOrigion(),
					meeting.getNearestTime());
			/*
			 * if (meeting.getMarkertypeColor() == MarkerColorType.RED) {
			 * setCheckInButton(); } else { //setRSVPButton(); }
			 */

			txtDate.setText(formateDate(meeting.getOnDateOrigion()));
			txtMeetingName.setText(meeting.getName());
			txtTime.setText(meeting.getOnTime());
			numReviewsText.setText(String.valueOf(meeting.getReviewsCount()) + " REVIEWS");
			txtMeetingCurrentStatus.setText(meeting.getStartInTime());

			if (meeting.getMarkertypeColor() == MarkerColorType.GREEN) {
				txtMeetingCurrentStatus
						.setBackgroundResource(R.drawable.hours_bg_green);
			} else if (meeting.getMarkertypeColor() == MarkerColorType.ORANGE) {
				txtMeetingCurrentStatus
						.setBackgroundResource(R.drawable.start_in_hour_btn);
			} else if (meeting.getMarkertypeColor() == MarkerColorType.RED) {
				txtMeetingCurrentStatus
						.setBackgroundResource(R.drawable.ongoing_btn);
			}

			txtLocationName.setText(meeting.getBuildingType().toUpperCase());
			txtLocationAddress.setText(meeting.getAddress() + ", "
					+ meeting.getZipCode());
			txtDistanceRight.setText(meeting.getDistanceInMiles()
					+ " MILES AWAY");
			rating.setRating(meeting.getReviewsAvg());

			meetingCodes = meeting.getCodes().split(",");

			ArrayAdapter<String> codesAdapter = new ArrayAdapter<>(this,R.layout.list_item_meeting_code,meetingCodes);
			gridMeetingCode.setAdapter(codesAdapter);

			ibRating.setImageResource(meeting.isFavourite() ? R.drawable.star_pink
					: R.drawable.star_white);

			if (!TextUtils.isEmpty(user.getMeetingHomeGroup()) && user.getMeetingHomeGroup().toLowerCase().trim()
					.equals(meeting.getName().toLowerCase().trim())) {
				cbHomeGroup.setChecked(true);
				homeGroup = true;
			}

			String favAttendingMsg = meeting.getRsvpCount() == 0 ? "" : String
					.format(getString(R.string.num_favourites_attending), meeting.getRsvpCount());

			tvRSVPAttending.setText(favAttendingMsg);

			String days = meeting.getOnDay();
			String onTime = meeting.getOnTime();

			multidateMap = getMultiDate(days, onTime);
			MultiDatesManager nearMultidate = setNearestDay(days);

			setMultidaylist(nearMultidate.getDay());
			if (meeting.getMarkertypeColor() == MarkerColorType.RED) {
				setCheckInButton();
			} else {
				setRSVPButton();
				// dbHandler.removeAllCheckIn(String.valueOf(meeting
				// .getMeetingId()));
			}

		}

	}

	/**
	 *  gridMeetingCode onClick method
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String code = meetingCodes[position];
		new CodePopup(MeetingDetailsActivity.this,
				MettingCodes.meetingValuesFromCode(code)).show(view);
	}

	public MultiDatesManager setNearestDay(String days) {
		String dayStr[] = days.split(",");
		int minDayDiffer = Integer.MAX_VALUE;
		int nearPosition = Integer.MAX_VALUE;

		for (int i = 0; i < dayStr.length; i++) {
			MultiDatesManager multidate = multidateMap.get(i);
			if (multidate.getDayDiffer() == 0){
				nearPosition = i;
				break;
			}else if (minDayDiffer > multidate.getDayDiffer()) {
				minDayDiffer = multidate.getDayDiffer();
				nearPosition = i;
			}

		}
		MultiDatesManager multidate = multidateMap.get(nearPosition);
		if (multidate != null) {
			txtDate.setText(multidate.getDayFormated());
			mNearestDay = multidate.getDayFormated();
			txtTime.setText(multidate.getTime());
		}

		return multidate;

	}

	private MultiDatesManager getMultiDateFromDayFormated(String dayformated) {

		for (MultiDatesManager multidate : multidateMap.values()) {
			if (multidate.getDayFormated().toLowerCase()
					.equals(dayformated.toLowerCase())) {
				return multidate;
			}
		}
		return null;
	}

	public void setMultidaylist(String insertedDay) {
		// String dayStr[] = days.split(",");

		llMultipleMeetings.removeAllViews();

		List<MultiDatesManager> multidateList = getMultidateListExceptNearest(insertedDay);

		LayoutInflater inflater = LayoutInflater
				.from(MeetingDetailsActivity.this);

		for (int i = 0; i < multidateList.size(); i++) {
			MultiDatesManager multiDate = multidateList.get(i);

			if (multiDate == null) {
				continue;
			}
			String day = multiDate.getDayFormated();
			String time = multiDate.getTime();
			View row = inflater.inflate(R.layout.list_multiple_meeting, null);
			TextView tvRowDay = (TextView) row.findViewById(R.id.tvRowDay);
			TextView tvRowTime = (TextView) row.findViewById(R.id.tvRowTime);
			tvRowDay.setText(day);
			tvRowTime.setText(time);
			row.setTag(multiDate.getDayFormated());
			row.setOnClickListener(multipleDayClickListenere);
			llMultipleMeetings.addView(row);
		}

		/*
		 * for (int j = 0; j < dayStr.length; j++) {
		 * 
		 * MultiDatesManager multiDate = multidateMap.get(j);
		 * 
		 * if (multiDate == null) { continue; } String day =
		 * multiDate.getDayFormated(); String time = multiDate.getTime(); View
		 * row = inflater.inflate(R.layout.list_multiple_meeting, null);
		 * TextView tvRowDay = (TextView) row.findViewById(R.id.tvRowDay);
		 * TextView tvRowTime = (TextView) row.findViewById(R.id.tvRowTime);
		 * tvRowDay.setText(day); tvRowTime.setText(time); row.setTag(j);
		 * row.setOnClickListener(multipleDayClickListenere);
		 * llMultipleMeetings.addView(row);
		 * 
		 * }
		 */

	}

	public List<MultiDatesManager> getMultidateListExceptNearest(String day) {
		List<MultiDatesManager> multidateList = new ArrayList<>();
		String days = meeting.getOnDay();
		String dayStr[] = days.split(",");
		for (int j = 0; j < dayStr.length; j++) {
			MultiDatesManager multiDate = multidateMap.get(j);
			if (!day.toLowerCase().equals(multiDate.getDay().toLowerCase())) {
				multidateList.add(multiDate);
			}
		}

		Collections.sort(multidateList, new Comparator<MultiDatesManager>() {
			public int compare(MultiDatesManager lhs, MultiDatesManager rhs) {

				if (lhs.getDayDiffer() > rhs.getDayDiffer()) {
					return 1;
				} else if (lhs.getDayDiffer() < rhs.getDayDiffer()) {
					return -1;
				} else {
					return 0;
				}

			}
		});

		return multidateList;

	}

	public class MultiDatesManager {

		private Calendar mCalendar;
		private String day;
		private String dayFormated;
		private String time;
		private int dayDiffer;

		public Calendar getmCalendar() {
			return mCalendar;
		}

		public void setmCalendar(Calendar mCalendar) {
			this.mCalendar = mCalendar;
		}

		public String getDay() {
			return day;
		}

		public void setDay(String day) {
			this.day = day;
		}

		public String getDayFormated() {
			return dayFormated;
		}

		public void setDayFormated(String dayFormated) {
			this.dayFormated = dayFormated;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public int getDayDiffer() {
			return dayDiffer;
		}

		public void setDayDiffer(int dayDiffer) {
			this.dayDiffer = dayDiffer;
		}

	}

	String daysInWeek[] = { "monday", "tuesday", "wednesday", "thursday",
			"friday", "saturday", "sunday" };

	public HashMap<Integer, MultiDatesManager> getMultiDate(String days,String times) {
		HashMap<Integer, MultiDatesManager> multidateMap = new HashMap<>();
		String daysStr[] = days.split(",");
		String timeStr[] = times.split(",");
		for (int k = 0; k < daysStr.length; k++) {
			String onDay = daysStr[k];
			String onTime = timeStr[k];
			MultiDatesManager multiDate = new MultiDatesManager();
			Calendar sCalendar = Calendar.getInstance();
			String dayLongName = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK,
					Calendar.LONG, Locale.getDefault());
			int onDayPositon = -1;
			int todayPosition = -1;

			for (int i = 0; i < daysInWeek.length; i++) {
				if (onDay.toLowerCase().equals(daysInWeek[i])) {
					onDayPositon = i;
				}

				if (dayLongName.toLowerCase().equals(daysInWeek[i])) {
					todayPosition = i;
				}
			}

			if (onDayPositon > todayPosition) {
				int dayDiffer = onDayPositon - todayPosition;
				sCalendar.add(Calendar.DAY_OF_MONTH, dayDiffer);

				multiDate.setDayDiffer(dayDiffer);
				multiDate.setDayFormated(dayDiffer == 1 ? "Tomorrow" : onDay);

			} else if (onDayPositon < todayPosition && onDayPositon != -1) {
				sCalendar.add(Calendar.DAY_OF_MONTH, ((daysInWeek.length)
						- todayPosition + onDayPositon));

				multiDate
						.setDayDiffer(((daysInWeek.length) - todayPosition + onDayPositon));
				multiDate.setDayFormated(onDay);

			} else if (onDayPositon == todayPosition) {
				SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
				try {
					final Date dateObj = _12HourSDF.parse(onTime);
					sCalendar.set(Calendar.HOUR_OF_DAY, dateObj.getHours() + 1);
					sCalendar.set(Calendar.MINUTE, dateObj.getMinutes());
					if (sCalendar.before(Calendar.getInstance())) {
						sCalendar.add(Calendar.DAY_OF_MONTH,
								(daysInWeek.length));
						multiDate.setDayDiffer(daysInWeek.length);
						multiDate.setDayFormated(onDay);
					} else {
						multiDate.setDayDiffer(0);
						multiDate.setDayFormated("Today");
					}

					sCalendar.set(
							Calendar.HOUR_OF_DAY,
							sCalendar.get(Calendar.HOUR_OF_DAY)
									- (dateObj.getHours() + 1));
					sCalendar.set(
							Calendar.MINUTE,
							sCalendar.get(Calendar.MINUTE)
									- dateObj.getMinutes());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			multiDate.setTime(onTime);
			multiDate.setDay(onDay);
			multiDate.setmCalendar(sCalendar);
			multidateMap.put(k, multiDate);

		}
		return multidateMap;
	}

	OnClickListener multipleDayClickListenere = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String dayformated = (String) v.getTag();

			MultiDatesManager multiDate = getMultiDateFromDayFormated(dayformated);

			if (multiDate == null) {
				return;
			}
			String day = multiDate.getDayFormated();
			String time = multiDate.getTime();

			txtDate.setText(day);
			txtTime.setText(time);

			btnRSVB.setVisibility(View.VISIBLE);

			setMultidaylist(multiDate.getDay());
			llMultipleMeetings.setVisibility(View.GONE);

			if (meeting.getMarkertypeColor() == MarkerColorType.RED
					&& day.equals(mNearestDay)) {
				setCheckInButton();
			} else {
				setRSVPButton();
			}

		}
	};

	public void setRSVPButton() {
		/*
		 * btnRSVB.setImageResource(meeting.getRsvp() == 1 ?
		 * R.drawable.unrsvp_btn : R.drawable.rsvp_btn);
		 */

		
		MultiDatesManager multidate = getMultiDateFromDayFormated(txtDate.getText().toString().trim());
		
		/*
		 * String calendarUri = dbHandler.getEventUri(meeting.getMeetingId(),
		 * multidate.getDay());
		 * 
		 * btnRSVB.setImageResource(calendarUri != null ? R.drawable.unrsvp_btn
		 * : R.drawable.rsvp_btn);
		 */

		boolean rsvp = dbHandler.isMeetingRSVP(meeting.getMeetingId(),
				multidate.getDay());
		
		

		btnRSVB.setImageResource(rsvp ? R.drawable.unrsvp_btn
				: R.drawable.rsvp_btn);
	}

	public void setCheckInButton() {

		btnRSVB.setImageResource(meeting.getCheckInMeeting() == 1 ? R.drawable.check_out_btn
				: R.drawable.check_in_btn);

		/*
		 * if (multidateMap == null || multidateMap.isEmpty()) { return; }
		 * MultiDatesManager multidate = getMultiDateFromDayFormated(txtDate
		 * .getText().toString().trim()); if (multidate == null) { return; }
		 * boolean checkIn = dbHandler.isCheckIn(meeting.getMeetingId(),
		 * multidate.getDay());
		 * 
		 * btnRSVB.setImageResource(checkIn ? R.drawable.check_out_btn :
		 * R.drawable.check_in_btn);
		 */
	}

	@Override
	public void onBackendConnected() {

		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}

		if (meeting != null) {
			try {
				pd.show();
				JSONObject params = new JSONObject();
				params.put("id", meeting.getMeetingId());
				socketService.getMeetingReviews(params);
			} catch (Exception e) {

			}
		}

	}

	public void addRSVPEvent(String day) {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}

		try {
			pd.show();

			JSONObject params = new JSONObject();
			params.put("meeting_id", meeting.getMeetingId());
			params.put("option", "add");
			params.put("meeting_day", day);
			Log.e("Rsvp group", params.toString());
			socketService.rsvpMeeting(params);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void checkIncheckOut(String check, MultiDatesManager multidate) {

		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}

		try {
			pd.show();

			JSONObject params = new JSONObject();
			params.put("meeting_id", meeting.getMeetingId());
			params.put("option", check);
			params.put("meeting_day", multidate.getDay());
			params.put("meeting_time", getTimeIn24Hour(multidate.getTime()));
			Log.e("CheckIn Group", params.toString());
			socketService.checkInMeeting(params);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void addMarker(MeetingModel m) {
		if(map == null) return;
		
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
		Marker marker = map.addMarker(markerOptions);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(
				markerOptions.getPosition(), defaultZoom));
	}

	public void removeRSVPEvent(String day) {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}

		try {
			pd.show();

			JSONObject params = new JSONObject();
			params.put("meeting_id", meeting.getMeetingId());
			params.put("option", "remove");
			params.put("meeting_day", day);
			Log.e("Home Group", params.toString());
			socketService.rsvpMeeting(params);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addHomeGroup() {

		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}

		try {
			pd.show();

			JSONObject params = new JSONObject();
			params.put("meeting_id", meeting.getMeetingId());
			params.put("user_id", user.getId());
			Log.e("Home Group", params.toString());
			socketService.homeGroupUser(params);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void addUserFavourite() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}

		try {

			JSONObject params = new JSONObject();

			params.put("meeting_ids", meeting.getMeetingId());
			params.put("favorite", meeting.isFavourite() ? 0 : 1);

			pd.show();
			socketService.addUserFavourite(params);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onMapClick(LatLng arg0) {
		Intent intent = new Intent(this, FullScreenMapActivity.class);
		intent.putExtra(FullScreenMapActivity.EXTRA_MEETING, meeting);

		startActivityForResult(intent, CODE_FULL_SCREEN);

		overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == RESULT_OK && arg0 == CODE_FULL_SCREEN) {
			meeting = (MeetingModel) arg2
					.getSerializableExtra(FullScreenMapActivity.EXTRA_MEETING);
			ibRating.setImageResource(meeting.isFavourite() ? R.drawable.star_pink
					: R.drawable.star_white);
		}
	}

	String calendarUri;
	private boolean rsvp;
	MultiDatesManager multidate;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.cbHomeGroup:
			if (NetworkUtil.getConnectivityStatus(MeetingDetailsActivity.this) == 1) {
				addHomeGroup();
			} else {
				cbHomeGroup.setChecked(!cbHomeGroup.isChecked());
				Toast.makeText(MeetingDetailsActivity.this,
						getString(R.string.no_internet_connection),
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.rlRSVP:
			/*if (meeting.getRsvpCount() == 0) {
				return;
			}*/
			Intent rsvpIntent = new Intent(this, RsvpActivity.class);
			rsvpIntent.putExtra(RsvpActivity.EXTRA_MEETING_ID, meeting.getId());
			startActivity(rsvpIntent);
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

			break;

		case R.id.btnRSVB:

			rsvpAction = RsvpAction.RSVP;
			String dayFormated = txtDate.getText().toString().trim();
			multidate = getMultiDateFromDayFormated(dayFormated);

			if (meeting.getMarkertypeColor() == MarkerColorType.RED
					&& dayFormated.equals(mNearestDay)) {

				if (meeting.getCheckInMeeting() == 1) {
					rsvpAction = RsvpAction.CHECKOUT;
				} else {
					rsvpAction = RsvpAction.CHECKIN;
				}

				/*
				 * if (dbHandler.isCheckIn(meeting.getMeetingId(),
				 * multidate.getDay())) { rsvpAction = RsvpAction.CHECKOUT; }
				 * else { rsvpAction = RsvpAction.CHECKIN; }
				 */
			} else {

				calendarUri = dbHandler.getEventUri(meeting.getMeetingId(),
						multidate.getDay());

				rsvp = dbHandler.isMeetingRSVP(meeting.getMeetingId(),
						multidate.getDay());
				/*
				 * if (meeting.getRsvp() == 1) { rsvpAction = RsvpAction.UNRSVP;
				 * } else { rsvpAction = RsvpAction.RSVP; }
				 */

				if (rsvp) {
					rsvpAction = RsvpAction.UNRSVP;
					removeRSVPEvent(multidate.getDay());

				} else {
					rsvpAction = RsvpAction.RSVP;
					addRSVPEvent(multidate.getDay());

				}

				return;
			}

			new AddReviewToCalendarDialog(MeetingDetailsActivity.this)
					.setAddReviewToCalendarDialogListener(
							new AddReviewToCalendarDialogClickListener() {

								@Override
								public void onNoClick(
										AddReviewToCalendarDialog dialog) {
									dialog.dismiss();
								}

								@Override
								public void onYesClick(
										AddReviewToCalendarDialog dialog,
										RsvpAction rsvpAction) {
									dialog.dismiss();
									// addMeetingToCalendar();
									switch (rsvpAction) {
									/*
									 * case RSVP: //
									 * addEventToCalendar(multidate);
									 * 
									 * break; case UNRSVP:
									 * 
									 * // deleteCalendarEvent(calendarUri);
									 */
									// break;
									case CHECKIN:
										checkIncheckOut("in", multidate);
										// addCheckIn(multidate);
										break;
									case CHECKOUT:
										checkIncheckOut("out", multidate);
										// deleteCheckIn(multidate);
										break;
									}

								}
							}, rsvpAction).show();
			break;
		case R.id.btnGetDirection:
			Location location = map.getMyLocation();
			if (location == null) {
				AppPrefs prefs = AppPrefs
						.getAppPrefs(MeetingDetailsActivity.this);
				double latitude = prefs.getDoubletPrefs(AppPrefs.KEY_LATITUDE,
						0);
				double longitude = prefs.getDoubletPrefs(
						AppPrefs.KEY_LONGITUDE, 0);
				location = new Location("");
				location.setLatitude(latitude);
				location.setLongitude(longitude);
			}
			String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="
					+ location.getLatitude() + "," + location.getLongitude()
					+ "&daddr=" + meeting.getLatitude() + ","
					+ meeting.getLongitude();
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse(uri));
			startActivity(Intent.createChooser(intent, "Select an application"));
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

			break;
		case R.id.ibSetting:
			// presentReportMenu();

			new ReportAnInAccuracyDialog(MeetingDetailsActivity.this)
					.setReportAnInAccuracyDialogListener(
							new ReportAnInAccuracyDialogClickListener() {

								@Override
								public void onInaccuracyClick(
										ReportAnInAccuracyDialog dialog) {
									// TODO Auto-generated method stub
									dialog.dismiss();
									Intent i = new Intent(
											MeetingDetailsActivity.this,
											ReportInaccuracyActivity.class);
									i.putExtra(
											ReportInaccuracyActivity.KEY_MEETING_ID,
											meeting.getId());
									startActivity(i);
									overridePendingTransition(
											R.anim.activity_in,
											R.anim.activity_out);
								}

								@Override
								public void onCancelClick(
										ReportAnInAccuracyDialog dialog) {
									dialog.dismiss();
								}
							}).show();

			break;
		case R.id.ibRating:
			addUserFavourite();
			break;
		case R.id.btnSeeMoreReviews:
			// fillContainer(true, reviewsContainer, meetingReviewModels);

			Intent moreReviewIntent = new Intent(MeetingDetailsActivity.this,
					ActivityMoreReviews.class);
			moreReviewIntent.putExtra(ActivityMoreReviews.KEY_MORE_REVIEW,
					meetingReviewModels);
			startActivity(moreReviewIntent);
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

			break;

		case R.id.rlAddReview:
			Intent i = new Intent(this, AddReviewOverMeetingActivity.class);
			Bundle bundle = new Bundle();

			bundle.putSerializable("meeting", meeting);
			i.putExtras(bundle);
			startActivity(i);
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
			break;
		case R.id.tvChooseADifferentDate:
			if (llMultipleMeetings.getVisibility() == View.VISIBLE) {
				llMultipleMeetings.setVisibility(View.GONE);
				String chooseText = getString(R.string.choose_a_different_date);
				tvChooseADifferentDate.setText(chooseText);

				Drawable downArrow = getResources().getDrawable(
						R.drawable.down_arrow_white);

				tvChooseADifferentDate.setCompoundDrawablesWithIntrinsicBounds(
						null, null, downArrow, null);
				btnRSVB.setVisibility(View.VISIBLE);

			} else {
				llMultipleMeetings.setVisibility(View.VISIBLE);
				String chooseText = getString(R.string.back_to_rsvp_ing);
				tvChooseADifferentDate.setText(chooseText);
				Drawable upArrow = getResources().getDrawable(
						R.drawable.up_arrow_white);

				tvChooseADifferentDate.setCompoundDrawablesWithIntrinsicBounds(
						null, null, upArrow, null);
				btnRSVB.setVisibility(View.GONE);

			}

			break;

		default:
			break;
		}
	}

	/**
	 * 
	 */

	public void addCheckIn(MultiDatesManager multidate) {

		RSVPModel rsvp = new RSVPModel();
		rsvp.setMeetingId(meeting.getMeetingId());
		rsvp.setMeetingDay(multidate.getDay());
		rsvp.setCalendarUri("");
		rsvp.setCheckIn(1);
		dbHandler.addRsvp(rsvp);
	}

	public void deleteCheckIn(MultiDatesManager multidate) {

		RSVPModel rsvp = new RSVPModel();
		rsvp.setMeetingId(meeting.getMeetingId());
		rsvp.setMeetingDay(multidate.getDay());
		rsvp.setCalendarUri("");
		rsvp.setCheckIn(1);
		dbHandler.deleteCheckIn(meeting.getMeetingId(), multidate.getDay());
	}

	public void addEventToCalendar(MultiDatesManager multidate) {
		Calendar cal = getCalFromDateTime(multidate.getmCalendar(),
				multidate.getTime());
		ContentResolver cr = this.getContentResolver();
		ContentValues values = new ContentValues();

		values.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis());

		values.put(CalendarContract.Events.TITLE, meeting.getName());
		values.put(CalendarContract.Events.DESCRIPTION, meeting.getAddress());

		TimeZone timeZone = TimeZone.getDefault();
		values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

		// default calendar
		// values.put(CalendarContract.Events.CALENDAR_ID, meeting.getId());
		values.put(CalendarContract.Events.CALENDAR_ID, 1);

		// values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY");
		// for one hour
		values.put(CalendarContract.Events.DURATION, "+P1H");

		values.put(CalendarContract.Events.HAS_ALARM, 1);

		// insert event to calendar
		Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

		Log.i("Uri", uri.toString());
		RSVPModel rsvp = new RSVPModel();
		rsvp.setMeetingId(meeting.getMeetingId());
		rsvp.setMeetingDay(multidate.getDay());
		rsvp.setCalendarUri(uri.toString());
		dbHandler.updatedRsvpEventUri(rsvp);
	}

	public void deleteCalendarEvent(String calendarUri) {
		/*
		 * Uri eventsUri; int osVersion = android.os.Build.VERSION.SDK_INT; if
		 * (osVersion <= 7) { // up-to Android 2.1 eventsUri =
		 * Uri.parse("content://calendar/events"); } else { // 8 is Android 2.2
		 * (Froyo) //
		 * (http://developer.android.com/reference/android/os/Build.VERSION_CODES
		 * .html) eventsUri =
		 * Uri.parse("content://com.android.calendar/events"); }
		 */
		ContentResolver resolver = this.getContentResolver();

		Log.i("Calendar uri ", "Calendar Uri:" + calendarUri);

		// deleteEvent(resolver, eventsUri, meeting.getId());
		if (calendarUri != null) {
			resolver.delete(Uri.parse(calendarUri), null, null);
			dbHandler.deleteRsvp(calendarUri);
		}

	}

	public void fillContainer(boolean viewall, LinearLayout linear,
			ArrayList<MeetingReviewModel> list) {

		Collections.reverse(list);

		linear.removeAllViews();
		LayoutInflater layoutInflater = (LayoutInflater) linear.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		for (int i = 0; i < list.size(); i++) {
			View view = layoutInflater.inflate(
					R.layout.list_item_meeting_review, null);
			view.setId(i);

			view.setTag(list.get(i));
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					MeetingReviewModel rev = (MeetingReviewModel) v.getTag();
					Intent intent = new Intent(MeetingDetailsActivity.this,
							MyReviewDetailActivity.class);
					intent.putExtra(MyReview.EXTRA_REVIEW, rev);
					startActivity(intent);
					overridePendingTransition(R.anim.activity_in,
							R.anim.activity_out);
				}
			});
			final MeetingReviewModel m = list.get(i);

			// init
			final TextView tvReviewTitle = (TextView) view
					.findViewById(R.id.tvReviewTitle);
			final RatingBar rating = (RatingBar) view.findViewById(R.id.rating);
			final TextView tvMeetingName = (TextView) view
			 		.findViewById(R.id.tvMeetingName);
			final TextView tvDateTime = (TextView) view.findViewById(R.id.tvDateTime);
			final TextView tvComment = (TextView) view.findViewById(R.id.tvComment);
			final ImageView ivUserIcon = (ImageView) view
					.findViewById(R.id.ivUserIcon);
			ivUserIcon.setTag(m);
			ivUserIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MeetingReviewModel review = (MeetingReviewModel) v.getTag();

					if(AccountUtils.getUserId(MeetingDetailsActivity.this) == review.getUserId()) {
						App.toast(getString(R.string.message_your_profile));
						return;
					}

					if(NetworkUtil.isConnected(MeetingDetailsActivity.this)){
						pd.show();
						socketService.getUserById(review.getUserId());
					}else {
						App.toast(getResources().getString(R.string.no_internet_connection));
					}


				}
			});
			String userImage = m.getImage();

			if(!TextUtils.isEmpty(userImage)) {
				Picasso.with(MeetingDetailsActivity.this).load("" + userImage)
						.placeholder(R.drawable.profile_pic_border).resize(80, 80)
						.error(R.drawable.profile_pic_border)
						.transform(new PicassoCircularTransform()).into(ivUserIcon);
			}else {
				Picasso.with(MeetingDetailsActivity.this).load(R.drawable.profile_pic_border)
						.resize(80, 80)
						.into(ivUserIcon);
			}
			tvReviewTitle.setText(m.getTitle());

			rating.setRating(m.getStars());

			tvMeetingName.setText(m.getUsername());

			final String datetimeAdded = DateTimeUtils.getDatetimeReview(
					m.getDatetimeAdded(), timeZone);

			tvDateTime.setText(datetimeAdded);

			tvComment.setText(m.getComments());

			linear.addView(view);
			final View divider = new View(linear.getContext());
			divider.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 5));
			linear.addView(divider);
			if (!viewall && i == 2 && list.size() > 3) {
				String totreview = String.format(
						getString(R.string.see_more_reveiw), list.size() - 3);
				btnSeeMoreReviews.setText(totreview);
				btnSeeMoreReviews.setVisibility(View.VISIBLE);
				break;
			}

			if (i == list.size() - 1) {
				btnSeeMoreReviews.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		pd.dismiss();
		if(EventParams.METHOD_USER_BY_ID.equals(event)){
			pd.dismiss();
			JSONObject data = (JSONObject) obj;

			UserAccount account = new Gson().fromJson(data.optJSONObject("user").toString(),
					UserAccount.class); ;
//
			Intent intent = new Intent(this, UserProfileActivity.class);
			intent.putExtra(UserProfileActivity.EXTRA_USER_ACCOUNT, account);

			// put friend
			startActivity(intent);
			overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
		}else if (event.equals(EventParams.EVENT_MEETING_GET_REVIEWS)) {

			meetingReviewModels = (ArrayList<MeetingReviewModel>) ((MeetingReviewsResponse) obj)
					.getGetMeetingsReviews();

			fillContainer(false, reviewsContainer, meetingReviewModels);
		} else if (event.equals(EventParams.EVENT_ADD_USER_FAVOURITE)) {
			meeting.setFavourite(!meeting.isFavourite());
			ibRating.setImageResource(meeting.isFavourite() ? R.drawable.star_pink
					: R.drawable.star_white);

		} else if (event.equals(EventParams.METHOD_HOME_GROUP_USER)) {

			JSONObject data = ((JSONObject) obj);

			homeGroup = !homeGroup;

			user.setMeetingHomeGroup(homeGroup ? meeting.getName() : "");

			userDatasource.updateHomeGroup(user);

			try {
				Toast.makeText(MeetingDetailsActivity.this,
						data.getString("message"), Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else if (event.equals(EventParams.METHOD_RSVP)) {
			JSONObject data = ((JSONObject) obj);
			String message;
			try {
				message = data.getString("message");
				Toast.makeText(MeetingDetailsActivity.this, message,
						Toast.LENGTH_SHORT).show();
				meeting.setRsvp(meeting.getRsvp() == 1 ? 0 : 1);
				if (rsvpAction == RsvpAction.RSVP) {
					RSVPModel rsvp = new RSVPModel();
					rsvp.setMeetingId(meeting.getMeetingId());
					rsvp.setMeetingDay(multidate.getDay());
					rsvp.setCalendarUri("");
					rsvp.setRsvp(1);
					if (dbHandler.rsvpFlagExists(meeting.getMeetingId(),
							multidate.getDay())) {
						dbHandler.updateRsvp(rsvp);
					} else {
						dbHandler.addRsvp(rsvp);
					}

				} else if (rsvpAction == RsvpAction.UNRSVP) {
					RSVPModel rsvp = new RSVPModel();
					rsvp.setMeetingId(meeting.getMeetingId());
					rsvp.setMeetingDay(multidate.getDay());

					rsvp.setRsvp(0);
					dbHandler.updateRsvp(rsvp);
				}

				setRSVPButton();
				
				if (rsvpAction == RsvpAction.RSVP) {
					if (calendarUri != null && !calendarUri.isEmpty()) {
						Toast.makeText(MeetingDetailsActivity.this,
								"Meeting is already in calendar!",
								Toast.LENGTH_SHORT).show();
						return;
					}

				} else if (rsvpAction == RsvpAction.UNRSVP) {

					if (calendarUri == null || calendarUri.isEmpty()) {
						return;
					}

				}

				new AddReviewToCalendarDialog(MeetingDetailsActivity.this)
						.setAddReviewToCalendarDialogListener(
								new AddReviewToCalendarDialogClickListener() {

									@Override
									public void onNoClick(
											AddReviewToCalendarDialog dialog) {
										dialog.dismiss();
									}

									@Override
									public void onYesClick(
											AddReviewToCalendarDialog dialog,
											RsvpAction rsvpAction) {
										dialog.dismiss();
										// addMeetingToCalendar();
										if (rsvpAction != null) {
											switch (rsvpAction) {
											case RSVP:
												addEventToCalendar(multidate);
												// addRSVPEvent(multidate.getDay());
												Toast.makeText(MeetingDetailsActivity.this,
														"Meeting is added to calendar!",
														Toast.LENGTH_SHORT).show();
												break;
											case UNRSVP:

												deleteCalendarEvent(calendarUri);
												// removeRSVPEvent(multidate.getDay());
												Toast.makeText(MeetingDetailsActivity.this,
														"Meeting is removed from calendar!",
														Toast.LENGTH_SHORT).show();
												break;
											}
											setRSVPButton();
										}

									}
								}, rsvpAction).show();

			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else if (event.equals(EventParams.METHOD_CHECK_IN_MEETING)) {
			JSONObject data = ((JSONObject) obj);

			try {
				String message = data.getString("message");
				Toast.makeText(MeetingDetailsActivity.this, message,
						Toast.LENGTH_SHORT).show();
				meeting.setCheckInMeeting(meeting.getCheckInMeeting() == 1 ? 0 : 1);

				if (rsvpAction != null) {
					switch (rsvpAction) {
					case CHECKIN:

						addCheckIn(multidate);
						break;
					case CHECKOUT:

						deleteCheckIn(multidate);
						break;
					}
				}

				setCheckInButton();
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onBackPressed() {

		Intent i = new Intent();
		i.putExtra("meeting", meeting);
		setResult(RESULT_OK, i);

		finish();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
		// super.onBackPressed();

		// overridePendingTransition(R.anim.activity_back_in,
		// R.anim.activity_back_out);
	}

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		pd.dismiss();
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	private String getTimeIn24Hour(String onTime) {
		SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
		SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
		String time = "";
		try {
			final Date dateObj = _12HourSDF.parse(onTime);
			dateObj.setSeconds(0);
			time = _24HourSDF.format(dateObj);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return time;
	}

	private Calendar getCalFromDateTime(Calendar cal, String onTime) {

		try {

			SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
			final Date dateObj = _12HourSDF.parse(onTime);

			cal.set(Calendar.HOUR_OF_DAY, dateObj.getHours());
			cal.set(Calendar.MINUTE, dateObj.getMinutes());
			cal.set(Calendar.SECOND, 0);

			Log.e("Year:", cal.get(Calendar.YEAR) + "");
			Log.e("Month:", cal.get(Calendar.MONTH) + "");
			Log.e("Day:", cal.get(Calendar.DAY_OF_MONTH) + "");

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cal;
	}

	private Calendar getCalFromDateTime(String date, String onTime) {
		SimpleDateFormat prevFormate = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = Calendar.getInstance();
		try {

			SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
			final Date dateObj = _12HourSDF.parse(onTime);

			Date dateGetted = prevFormate.parse(date);
			/*
			 * cal.set(Calendar.YEAR, dateGetted.getYear() + 1);
			 * cal.set(Calendar.MONTH, dateGetted.getMonth());
			 * cal.set(Calendar.DAY_OF_MONTH, dateGetted.getDate());
			 */
			cal.setTime(dateGetted);
			cal.set(Calendar.HOUR_OF_DAY, dateObj.getHours());
			cal.set(Calendar.MINUTE, dateObj.getMinutes());
			cal.set(Calendar.SECOND, 0);

			Log.e("Year:", cal.get(Calendar.YEAR) + "");
			Log.e("Month:", cal.get(Calendar.MONTH) + "");
			Log.e("Day:", cal.get(Calendar.DAY_OF_MONTH) + "");

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cal;
	}

	public String formateDate(String date) {
		SimpleDateFormat prevFormate = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat newFormate = new SimpleDateFormat("MMM dd yyyy");
		try {
			Date date2 = prevFormate.parse(date);
			return newFormate.format(date2);
		} catch (Exception e) {
			e.printStackTrace();
			return date;
		}
	}

}
