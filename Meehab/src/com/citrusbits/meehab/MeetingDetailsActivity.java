package com.citrusbits.meehab;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.dialog.AddReviewToCalendarDialog;
import com.citrusbits.meehab.dialog.AddReviewToCalendarDialog.AddReviewToCalendarDialogClickListener;
import com.citrusbits.meehab.dialog.ReportAnInAccuracyDialog;
import com.citrusbits.meehab.dialog.ReportAnInAccuracyDialog.ReportAnInAccuracyDialogClickListener;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.model.MeetingReviewModel;
import com.citrusbits.meehab.model.MyReview;
import com.citrusbits.meehab.model.TMeeting;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.pojo.MeetingReviewsResponse;
import com.citrusbits.meehab.popup.CodePopup;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DateTimeUtils;
import com.citrusbits.meehab.utils.MettingCodes;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

public class MeetingDetailsActivity extends SocketActivity implements
		OnSocketResponseListener, OnClickListener, OnMapClickListener {

	private MeetingModel meeting;
	private ArrayList<TMeeting> meetings;
	private LinearLayout reviewsContainer;
	private ImageButton ibSetting;
	private ImageButton ibRating;
	private Button btnAddReview;
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
	private Button btnMakeHomegroup;
	private TextView txtLocationAddress;
	private ImageButton btnRSVB;
	private Button btnMoreInfo;

	private ProgressDialog pd;
	protected GoogleMap map;
	protected float defaultZoom = 14;
	protected ArrayList<MeetingReviewModel> meetingReviewModels = new ArrayList<>();
	private boolean isFavorite;

	private TextView[] tvCodes = new TextView[10];

	UserDatasource userDatasource;
	UserAccount user;

	Button btnSeeMoreReviews;

	RelativeLayout rlAddReview;

	private boolean homeGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_meeting_details);

		btnSeeMoreReviews = (Button) findViewById(R.id.btnSeeMoreReviews);
		rlAddReview = (RelativeLayout) findViewById(R.id.rlAddReview);
		rlAddReview.setOnClickListener(this);

		userDatasource = new UserDatasource(MeetingDetailsActivity.this);
		userDatasource.open();

		user = userDatasource.findUser(AccountUtils.getUserId(this));

		// top back button
		findViewById(R.id.btnBack).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});

		tvCodes[0] = (TextView) findViewById(R.id.tvCode1);
		tvCodes[1] = (TextView) findViewById(R.id.tvCode2);
		tvCodes[2] = (TextView) findViewById(R.id.tvCode3);
		tvCodes[3] = (TextView) findViewById(R.id.tvCode4);
		tvCodes[4] = (TextView) findViewById(R.id.tvCode5);
		tvCodes[5] = (TextView) findViewById(R.id.tvCode6);
		tvCodes[6] = (TextView) findViewById(R.id.tvCode7);
		tvCodes[7] = (TextView) findViewById(R.id.tvCode8);
		tvCodes[8] = (TextView) findViewById(R.id.tvCode9);
		tvCodes[9] = (TextView) findViewById(R.id.tvCode10);

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

		btnAddReview = (Button) findViewById(R.id.btnAddReview);
		reviewsContainer = (LinearLayout) findViewById(R.id.reviewsContainer);
		btnGetDirection = (ImageButton) findViewById(R.id.btnGetDirection);
		txtLocationAddress = (TextView) findViewById(R.id.txtLocationAddress);
		btnRSVB = (ImageButton) findViewById(R.id.btnRSVB);
		btnMoreInfo = (Button) findViewById(R.id.btnMoreInfo);
		btnMakeHomegroup = (Button) findViewById(R.id.btnMakeHomegroup);
		cbHomeGroup = (CheckBox) findViewById(R.id.cbHomeGroup);

		ibRating.setOnClickListener(this);
		ibSetting.setOnClickListener(this);
		cbHomeGroup.setOnClickListener(this);
		btnSeeMoreReviews.setOnClickListener(this);

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
								MarkerOptions markerOptions = new MarkerOptions()
										.position(new LatLng(meeting
												.getLatitude(), meeting
												.getLongitude()));

								map.addMarker(markerOptions);
								map.moveCamera(CameraUpdateFactory
										.newLatLngZoom(
												markerOptions.getPosition(),
												defaultZoom));

							}
						});
			}
		}

		btnRSVB.setOnClickListener(this);
		btnMoreInfo.setOnClickListener(this);
		btnAddReview.setOnClickListener(this);
		btnGetDirection.setOnClickListener(this);

		Bundle extra = getIntent().getExtras();
		if (extra != null) {
			meeting = (MeetingModel) extra.getSerializable("meeting");

			txtDate.setText(formateDate(meeting.getOnDateOrigion()));
			txtMeetingName.setText(meeting.getName());
			txtTime.setText(meeting.getOnTime());
			numReviewsText.setText(String.valueOf(meeting.getReviewsCount())
					+ " Reviews");
			txtMeetingCurrentStatus.setText(meeting.getStartInTime());

			txtLocationName.setText(meeting.getBuildingType());
			txtLocationAddress.setText(meeting.getAddress() + ", "
					+ meeting.getZipCode());
			txtDistanceRight.setText(meeting.getDistanceInMiles()
					+ " MILES AWAY");
			rating.setRating(meeting.getReviewsAvg());
			String[] codes = meeting.getCodes().split(",");

			int i = 0;
			for (String value : codes) {
				tvCodes[i].setText(codes[i]);
				tvCodes[i].setTag(codes[i]);
				tvCodes[i].setVisibility(View.VISIBLE);
				tvCodes[i].setOnClickListener(codeClickListener);
				i++;
			}
			ibRating.setImageResource(meeting.isFavourite() ? R.drawable.star_pink
					: R.drawable.star_gray);

			if (user.getMeetingHomeGroup().toLowerCase().trim()
					.equals(meeting.getName().toLowerCase().trim())) {
				cbHomeGroup.setChecked(true);
				homeGroup = true;
			}
		}

		// init meeting adapter
	}

	OnClickListener codeClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String code = (String) v.getTag();
			new CodePopup(MeetingDetailsActivity.this,
					MettingCodes.meetingValuesFromCode(code)).show(v);
		}
	};

	@Override
	void onBackendConnected() {

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

	public void addHomeGroup() {

		try {
			pd.show();

			JSONObject params = new JSONObject();
			params.put("meeting_id", meeting.getMeetingId());
			params.put("user_id", user.getId());
			Log.e("Home Group", params.toString());
			socketService.homeGroupUser(params);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addUserFavourite() {

		try {

			JSONObject params = new JSONObject();
			params.put("meetingid", meeting.getMeetingId());
			params.put("userid", user.getId());
			socketService.addUserFavourite(params);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onMapClick(LatLng arg0) {
		Intent intent = new Intent(this, FullScreenMapActivity.class);
		intent.putExtra(FullScreenMapActivity.EXTRA_MEETING, meeting);

		startActivity(intent);
		
		overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
	}

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

		case R.id.btnRSVB:
			// confirmation
			/*
			 * new AlertDialog.Builder(this) .setTitle(
			 * "Would you like to add this meeting to your canlendar?")
			 * .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			 * 
			 * @Override public void onClick(DialogInterface dialog, int which)
			 * { addMeetingToCalendar(); } }).setNegativeButton("No",
			 * null).create().show();
			 */

			new AddReviewToCalendarDialog(MeetingDetailsActivity.this)
					.setAddReviewToCalendarDialogListener(
							new AddReviewToCalendarDialogClickListener() {

								@Override
								public void onYesClick(
										AddReviewToCalendarDialog dialog) {
									// TODO Auto-generated method stub
									dialog.dismiss();
									addMeetingToCalendar();
								}

								@Override
								public void onNoClick(
										AddReviewToCalendarDialog dialog) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).show();
			break;
		case R.id.btnGetDirection:

			String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + 33.3
					+ "," + 22.2 + "&daddr=" + 34.4 + "," + 24.4;
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
									overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
								}

								@Override
								public void onCancelClick(
										ReportAnInAccuracyDialog dialog) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).show();

			break;
		case R.id.ibRating:
			pd.show();
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

		default:
			break;
		}
	}

	/**
	 * 
	 */

	private void presentReportMenu() {
		final String[] options = { "Report an Inaccuray", "Cancel" };

		ArrayAdapter<String> cuteAdapter = new ArrayAdapter<String>(
				getApplicationContext(), android.R.layout.simple_list_item_1,
				options) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = ((LayoutInflater) MeetingDetailsActivity.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
							.inflate(android.R.layout.simple_list_item_1, null);
				}
				Log.d("", "" + position);
				TextView tv = (TextView) convertView
						.findViewById(android.R.id.text1);
				tv.setText(options[position]);
				tv.setGravity(Gravity.CENTER_VERTICAL
						| Gravity.CENTER_HORIZONTAL);
				return convertView;
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setAdapter(cuteAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();
				switch (item) {
				case 0:
					Intent i = new Intent(MeetingDetailsActivity.this,
							ReportInaccuracyActivity.class);
					i.putExtra(ReportInaccuracyActivity.KEY_MEETING_ID,
							meeting.getId());
					startActivity(i);
					overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
					break;

				default:
					break;
				}
			}
		});
		builder.show();

	}

	protected void addMeetingToCalendar() {
		Calendar cal = getCalFromDateTime(meeting.getOnDateOrigion(),
				meeting.getOnTime());
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("beginTime", cal.getTimeInMillis());
		// intent.putExtra("allDay", true);
		intent.putExtra("rrule", "FREQ=YEARLY");
		intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
		intent.putExtra("title", meeting.getName());

		try {
			startActivity(intent);
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
		} catch (ActivityNotFoundException ex) {

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
					// TODO Auto-generated method stub

					MeetingReviewModel rev = (MeetingReviewModel) v.getTag();
					Intent intent = new Intent(MeetingDetailsActivity.this,
							MyReviewDetailActivity.class);
					intent.putExtra(MyReview.EXTRA_REVIEW, rev);
					startActivity(intent);
					overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
				}
			});
			MeetingReviewModel m = list.get(i);

			// init
			TextView tvReviewTitle = (TextView) view
					.findViewById(R.id.tvReviewTitle);
			RatingBar rating = (RatingBar) view.findViewById(R.id.rating);
			TextView tvMeetingName = (TextView) view
					.findViewById(R.id.tvMeetingName);
			TextView tvDateTime = (TextView) view.findViewById(R.id.tvDateTime);
			TextView tvComment = (TextView) view.findViewById(R.id.tvComment);
			ImageView ivUserIcon = (ImageView) view
					.findViewById(R.id.ivUserIcon);

			String userImage = getString(R.string.url) + m.getImage();

			Log.e("User image is ", userImage);

			Picasso.with(MeetingDetailsActivity.this).load(userImage)
					.placeholder(R.drawable.profile_pic).resize(80, 80)
					.error(R.drawable.profile_pic)
					.transform(new PicassoCircularTransform()).into(ivUserIcon);

			tvReviewTitle.setText(m.getTitle());

			rating.setRating(m.getStars());

			tvMeetingName.setText(m.getUsername());

			String datetimeAdded = DateTimeUtils.getDatetimeAdded(m
					.getDatetimeAdded());

			tvDateTime.setText(datetimeAdded);

			tvComment.setText(m.getComments());

			linear.addView(view);
			View divider = new View(linear.getContext());
			divider.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 5));
			linear.addView(divider);
			if (!viewall && i == 2) {
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
		if (event.equals(EventParams.EVENT_MEETING_GET_REVIEWS)) {

			meetingReviewModels = (ArrayList<MeetingReviewModel>) ((MeetingReviewsResponse) obj)
					.getGetMeetingsReviews();

			fillContainer(false, reviewsContainer, meetingReviewModels);
		} else if (event.equals(EventParams.EVENT_ADD_USER_FAVOURITE)) {
			meeting.setFavourite(!meeting.isFavourite());
			ibRating.setImageResource(meeting.isFavourite() ? R.drawable.star_pink
					: R.drawable.star_gray);

			JSONObject data = ((JSONObject) obj);

		} else if (event.equals(EventParams.METHOD_HOME_GROUP_USER)) {

			JSONObject data = ((JSONObject) obj);

			homeGroup = !homeGroup;

			user.setMeetingHomeGroup(homeGroup ? meeting.getName() : "");
			
			userDatasource.updateHomeGroup(user);

			try {
				Toast.makeText(MeetingDetailsActivity.this,
						data.getString("message"), Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		Intent i = new Intent();
		i.putExtra("meeting", meeting);
		setResult(RESULT_OK, i);

		finish();
		// super.onBackPressed();

		// overridePendingTransition(R.anim.activity_back_in,
		// R.anim.activity_back_out);
	}

	@Override
	public void onSocketResponseFailure(String message) {
		pd.dismiss();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cal;
	}

	public String formateDate(String date) {
		SimpleDateFormat prevFormate = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat newFormate = new SimpleDateFormat("dd MMM yyyy");
		try {
			Date date2 = prevFormate.parse(date);
			return newFormate.format(date2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return date;
		}
	}

}
