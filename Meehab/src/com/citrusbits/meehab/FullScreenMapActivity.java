package com.citrusbits.meehab;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.dialog.ReportAnInAccuracyDialog;
import com.citrusbits.meehab.dialog.ReportAnInAccuracyDialog.ReportAnInAccuracyDialogClickListener;
import com.citrusbits.meehab.map.LocationUtils;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class FullScreenMapActivity extends SocketActivity implements
		OnSocketResponseListener, OnClickListener {

	public static final String EXTRA_MEETING = "meeting";

	LatLng target = new LatLng(33, 73);
	private GoogleMap googleMap;
	protected Marker marker;
	private ImageButton btnGetDirection;
	private ImageButton btnFindMe;
	private ImageButton ibSettings;
	private ImageButton ibRating;

	private Location myLocation;
	private MeetingModel meeting;
	ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myLocation = LocationUtils.getLastLocation(this);
		setContentView(R.layout.activity_full_screen_map);

		pd = UtilityClass.getProgressDialog(this);
		meeting = (MeetingModel) getIntent()
				.getSerializableExtra(EXTRA_MEETING);
		target = new LatLng(meeting.getLatitude(), meeting.getLongitude());

		// top back button
		findViewById(R.id.topMenuBtn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});

		ibSettings = (ImageButton) findViewById(R.id.ibSetting);

		ibRating = (ImageButton) findViewById(R.id.ibRating);
		ibRating.setImageResource(meeting.isFavourite() ? R.drawable.star_yellow
				: R.drawable.star_pink);
		btnFindMe = (ImageButton) findViewById(R.id.btnFindMe);
		btnGetDirection = (ImageButton) findViewById(R.id.btnGetDirection);

		ibSettings.setOnClickListener(this);
		ibRating.setOnClickListener(this);

		btnFindMe.setOnClickListener(this);
		btnGetDirection.setOnClickListener(this);
		try {
			if (googleMap == null) {
				((SupportMapFragment) getSupportFragmentManager()
						.findFragmentById(R.id.map))
						.getMapAsync(new OnMapReadyCallback() {

							@Override
							public void onMapReady(GoogleMap arg0) {
								googleMap = arg0;
								googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
								marker = googleMap
										.addMarker(new MarkerOptions()
												.position(target).title("Loc"));

								moveMapCamera(new LatLng(myLocation
										.getLatitude(), myLocation
										.getLongitude()));

							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addUserFavourite() {

		try {

			JSONObject params = new JSONObject();
			params.put("meetingid", meeting.getMeetingId());
			params.put("userid", AccountUtils.getUserId(this));
			socketService.addUserFavourite(params);

			pd.show();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// menu
		case R.id.ibSetting:
			// presentReportMenu();

			// star fav
			new ReportAnInAccuracyDialog(this)
					.setReportAnInAccuracyDialogListener(
							new ReportAnInAccuracyDialogClickListener() {

								@Override
								public void onInaccuracyClick(
										ReportAnInAccuracyDialog dialog) {
									// TODO Auto-generated method stub
									dialog.dismiss();
									Intent i = new Intent(
											FullScreenMapActivity.this,
											ReportInaccuracyActivity.class);
									startActivity(i);
								}

								@Override
								public void onCancelClick(
										ReportAnInAccuracyDialog dialog) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).show();
			break;
		case R.id.btnGetDirection:
			String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="
					+ myLocation.getLatitude() + ","
					+ myLocation.getLongitude() + "&daddr=" + target.latitude
					+ "," + target.longitude;
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse(uri));
			startActivity(Intent.createChooser(intent, "Select an application"));

			break;
		case R.id.btnFindMe:

			moveMapCamera(new LatLng(myLocation.getLatitude(),
					myLocation.getLongitude()));
			break;
		case R.id.ibRating:

			addUserFavourite();
			break;
		default:
			break;
		}
	}

	private void moveMapCamera(LatLng p) {
		if (googleMap != null) {
			CameraUpdate cameraUpdate = CameraUpdateFactory
					.newLatLngZoom(p, 12);
			googleMap.animateCamera(cameraUpdate);
		}

	}

	private void presentReportMenu() {
		final String[] options = { "Report an Inaccuray", "Cancel" };

		ArrayAdapter<String> cuteAdapter = new ArrayAdapter<String>(
				getApplicationContext(), android.R.layout.simple_list_item_1,
				options) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = ((LayoutInflater) FullScreenMapActivity.this
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
					Intent i = new Intent(FullScreenMapActivity.this,
							ReportInaccuracyActivity.class);
					startActivity(i);
					break;

				default:
					break;
				}
			}
		});
		builder.show();

	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		// TODO Auto-generated method stub
		pd.dismiss();
		if (event.equals(EventParams.EVENT_ADD_USER_FAVOURITE)) {
			meeting.setFavourite(!meeting.isFavourite());
			ibRating.setImageResource(meeting.isFavourite() ? R.drawable.star_pink
					: R.drawable.star_gray);

			JSONObject data = ((JSONObject) obj);

		}
	}

	@Override
	public void onSocketResponseFailure(String message) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
	}
}
