package com.citrusbits.meehab;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.db.DatabaseHandler;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.RehabModel;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.MeetingUtils;
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
import com.squareup.picasso.Picasso;

public class RehabDetailsActivity extends SocketActivity implements
		OnSocketResponseListener, OnClickListener, OnMapClickListener {

	public static final String KEY_REHAB = "rehab";

	private ImageButton ibSetting;
	private ImageButton ibRating;

	private RatingBar rating;

	private ImageButton btnGetDirection;

	private ProgressDialog pd;
	protected GoogleMap map;
	protected float defaultZoom = 8;

	private boolean isFavorite;

	UserDatasource userDatasource;
	UserAccount user;

	RelativeLayout rlOtherServices;
	RelativeLayout rlInsuranceAccepted;

	RelativeLayout rlPhotoes;
	RelativeLayout rlVideos;

	DatabaseHandler dbHandler;

	private long timeZone = 0;

	RehabModel rehab;

	TextView tvRehabName;
	TextView tvLocationName;
	TextView tvDistance;

	private TextView tvRehabStatus;
	private TextView tvTime;
	private TextView tvPhone;
	private TextView tvWebsite;
	private TextView tvAddress;
	private TextView tvAbout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_rehab_details);

		rehab = (RehabModel) getIntent().getSerializableExtra(KEY_REHAB);

		timeZone = MeetingUtils.getTimeZoneOffset();

		rlOtherServices = (RelativeLayout) findViewById(R.id.rlOtherServices);
		rlInsuranceAccepted = (RelativeLayout) findViewById(R.id.rlInsuranceAccepted);

		rlOtherServices.setOnClickListener(this);
		rlInsuranceAccepted.setOnClickListener(this);

		userDatasource = new UserDatasource(RehabDetailsActivity.this);
		userDatasource.open();

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

		pd = UtilityClass.getProgressDialog(this);
		ibRating = (ImageButton) findViewById(R.id.ibRating);
		ibSetting = (ImageButton) findViewById(R.id.ibSetting);

		rating = (RatingBar) findViewById(R.id.rating);

		btnGetDirection = (ImageButton) findViewById(R.id.btnGetDirection);

		tvRehabName = (TextView) findViewById(R.id.tvRehabName);

		rlPhotoes = (RelativeLayout) findViewById(R.id.rlPhotoes);
		rlVideos = (RelativeLayout) findViewById(R.id.rlVideos);

		tvLocationName = (TextView) findViewById(R.id.tvLocationName);
		tvAbout = (TextView) findViewById(R.id.tvAbout);
		tvDistance = (TextView) findViewById(R.id.tvDistance);

		tvRehabStatus = (TextView) findViewById(R.id.tvRehabStatus);
		tvTime = (TextView) findViewById(R.id.tvTime);
		tvPhone = (TextView) findViewById(R.id.tvPhone);
		tvWebsite = (TextView) findViewById(R.id.tvWebsite);
		tvAddress = (TextView) findViewById(R.id.tvAddress);

		ibRating.setOnClickListener(this);
		ibSetting.setOnClickListener(this);

		rlInsuranceAccepted.setOnClickListener(this);
		rlOtherServices.setOnClickListener(this);

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
								map.setOnMapClickListener(RehabDetailsActivity.this);
								map.setMyLocationEnabled(true);
								addMarker(rehab);

							}
						});
			}
		}

		btnGetDirection.setOnClickListener(this);

		Bundle extra = getIntent().getExtras();
 boolean flag=false;
		if (extra != null&&flag) {
			rehab = (RehabModel) extra.getSerializable(KEY_REHAB);

			tvRehabName.setText(rehab.getName());
			// tvLocationName.setText(rehab.get);

			tvDistance.setText(rehab.getDistance() + " MILES AWAY");

			// private TextView tvRehabStatus;
			tvTime.setText(rehab.getRehabDays().get(0).getStartTime() + "-"
					+ rehab.getRehabDays().get(0).getEndTime());
			tvPhone.setText(rehab.getPhone());
			tvWebsite.setText(rehab.getWebsite());
			tvAddress.setText(rehab.getAddress());
			tvAbout.setText(rehab.getAbout());
			
			addPhotoes(rehab.getRehabPhotoes());
			addVideos(rehab.getRehabVideos());

		}

		// init meeting adapter
	}

	@Override
	void onBackendConnected() {

		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.alert(getString(R.string.no_internet_connection));
			return;
		}

	}

	public void addPhotoes(List<String> photes) {
		rlPhotoes.removeAllViews();

		String url = "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcT72QJV4C2LzOy4H_D1bBCsAdlw9WbrEBmG8v9s_ictnA_r8P8P";

		for (int i = 0; i < 5; i++) {
			photes.add(url);
		}

		for (String photourl : photes) {
			ImageView iv = new ImageView(this);
			Picasso.with(this).load(photourl)
					.placeholder(R.drawable.profile_pic).resize(100, 100)
					.error(R.drawable.profile_pic)
					.transform(new PicassoCircularTransform()).into(iv);
			rlPhotoes.addView(iv);
		}
	}

	public void addVideos(List<String> videos) {
		rlVideos.removeAllViews();

		String url = "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcT72QJV4C2LzOy4H_D1bBCsAdlw9WbrEBmG8v9s_ictnA_r8P8P";

		for (int i = 0; i < 5; i++) {
			videos.add(url);
		}

		for (String videoUrl : videos) {
			ImageView iv = new ImageView(this);
			Picasso.with(this).load(videoUrl)
					.placeholder(R.drawable.profile_pic).resize(100, 100)
					.error(R.drawable.profile_pic)
					.transform(new PicassoCircularTransform()).into(iv);
			rlVideos.addView(iv);
		}
	}

	public void addMarker(RehabModel m) {
		int resourceId = R.drawable.pin_dark_pink;
		/*
		 * if (m.getMarkertypeColor() == MarkerColorType.GREEN) { resourceId =
		 * R.drawable.pin_green; } else if (m.getMarkertypeColor() ==
		 * MarkerColorType.ORANGE) { resourceId = R.drawable.pin_orange; } else
		 * if (m.getMarkertypeColor() == MarkerColorType.RED) { resourceId =
		 * R.drawable.pin_dark_pink; }
		 */

		MarkerOptions markerOptions = new MarkerOptions()
				.position(new LatLng(m.getLatitude(), m.getLongitude()))
				.title(m.getName())
				.icon(BitmapDescriptorFactory.fromResource(resourceId));
		Marker marker = map.addMarker(markerOptions);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(
				markerOptions.getPosition(), defaultZoom));
	}

	public void addUserFavourite() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.alert(getString(R.string.no_internet_connection));
			return;
		}

		try {

			JSONObject params = new JSONObject();

			params.put("meeting_ids", rehab.getId());
			// params.put("favorite", meeting.isFavourite() ? 0 : 1);

			socketService.addUserFavourite(params);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onMapClick(LatLng arg0) {
		Intent intent = new Intent(this, FullScreenMapActivity.class);
		// intent.putExtra(FullScreenMapActivity.EXTRA_MEETING, meeting);

		// startActivityForResult(intent, CODE_FULL_SCREEN);

		// overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlOtherServices:

			break;

		case R.id.rlInsuranceAccepted:

			break;

		default:
			break;
		}
	}

	/**
* 
*/

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		if (pd != null) {
			pd.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		Intent i = new Intent();
		// i.putExtra("meeting", meeting);
		setResult(RESULT_OK, i);

		finish();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
		// super.onBackPressed();

		// overridePendingTransition(R.anim.activity_back_in,
		// R.anim.activity_back_out);
	}

	@Override
	public void onSocketResponseFailure(String message) {
		pd.dismiss();
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

}
