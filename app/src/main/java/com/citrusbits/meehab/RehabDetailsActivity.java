package com.citrusbits.meehab;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.adapters.PhotosAdapter;
import com.citrusbits.meehab.adapters.PhotosAdapter.PhotoClickListener;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.DatabaseHandler;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.dialog.ReportAnInAccuracyDialog;
import com.citrusbits.meehab.dialog.ReportAnInAccuracyDialog.ReportAnInAccuracyDialogClickListener;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.map.LocationService;
import com.citrusbits.meehab.map.LocationUtils;
import com.citrusbits.meehab.map.LocationService.LocationListener;
import com.citrusbits.meehab.map.LocationService.MyLocalBinder;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.model.RehabModel;
import com.citrusbits.meehab.model.RehabResponseModel;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.MeetingUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.citrusbits.meehab.utils.ValidationUtils;
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

	private static final int CODE_FULL_SCREEN = 5;

	private ImageButton ibSetting;
	private ImageButton ibFav;



	private Dialog pd;
	protected GoogleMap map;
	protected float defaultZoom = 8;

	UserDatasource userDatasource;
	UserAccount user;

	TextView tvOtherServices;
	LinearLayout llOtherServices;
	TextView tvInsuranceAccepted;

	DatabaseHandler dbHandler;

//	private long timeZone = 0;

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

	private boolean isFavorite;

	private TextView tvPayments;

	protected LocationService locationService;
	
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

	protected Location myLocation;

	LocationListener locationListener = new LocationListener() {

		@Override
		public void onChangeLocation(Location location) {
			myLocation = location;
		}
	};

	private List<String> photoUrls;

	private List<String> videoUrls;

	protected void onStart() {
		super.onStart();
		
	};
	
	@Override
	protected void onStop() {
		super.onStop();
		
//		locationService.removeListener(locationListener);
//		unbindService(locServiceConnection);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_rehab_details);

		myLocation = LocationUtils.getLastLocation(this);
		Intent i = new Intent(getApplicationContext(), LocationService.class);
		this.bindService(i, locServiceConnection, Context.BIND_AUTO_CREATE);
		this.startService(i);
		
		rehab = (RehabModel) getIntent().getSerializableExtra(KEY_REHAB);

		tvOtherServices = (TextView) findViewById(R.id.tvOtherServices);
		tvPayments = (TextView) findViewById(R.id.tvPayments);
		llOtherServices = (LinearLayout)findViewById(R.id.llOtherServices);
		tvInsuranceAccepted = (TextView) findViewById(R.id.tvInsuranceAccepted);

		tvOtherServices.setOnClickListener(this);
		tvInsuranceAccepted.setOnClickListener(this);

		userDatasource = new UserDatasource(RehabDetailsActivity.this);

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
		ibFav = (ImageButton) findViewById(R.id.ibFav);
		ibSetting = (ImageButton) findViewById(R.id.ibSetting);

//		btnGetDirection = (ImageButton) findViewById(R.id.btnGetDirection);

		tvRehabName = (TextView) findViewById(R.id.tvRehabName);

		
		findViewById(R.id.rlGetDirection).setOnClickListener(this);
		
		RecyclerView recyclerviewPhotos = (RecyclerView) findViewById(R.id.recyclerviewPhotos);
		RecyclerView recyclerviewVideos = (RecyclerView) findViewById(R.id.recyclerviewVideos);
		
//		LinearLayoutManager layoutManager
//	    = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//		recyclerviewPhotos.setLayoutManager(layoutManager);
		GridLayoutManager layoutManager
	    = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL,false);
		recyclerviewPhotos.setLayoutManager(layoutManager);
		recyclerviewPhotos.setHasFixedSize(true);

		tvLocationName = (TextView) findViewById(R.id.tvLocationName);
		tvAbout = (TextView) findViewById(R.id.tvAbout);
		tvDistance = (TextView) findViewById(R.id.tvDistance);

		tvRehabStatus = (TextView) findViewById(R.id.tvRehabStatus);
		tvTime = (TextView) findViewById(R.id.tvTime);
		tvPhone = (TextView) findViewById(R.id.tvPhone);
		tvWebsite = (TextView) findViewById(R.id.tvWebsite);
		tvAddress = (TextView) findViewById(R.id.tvAddress);

		ibFav.setOnClickListener(this);
		ibSetting.setOnClickListener(this);

		tvInsuranceAccepted.setOnClickListener(this);

		if (App.isPlayServiceOk) {
			if (map == null) {
				((SupportMapFragment) getSupportFragmentManager()
						.findFragmentById(R.id.map))
						.getMapAsync(new OnMapReadyCallback() {

							@Override
							public void onMapReady(GoogleMap arg) {
								map = arg;

								map.getUiSettings().setMapToolbarEnabled(false);
								map.getUiSettings().setAllGesturesEnabled(false);
								map.setOnMapClickListener(RehabDetailsActivity.this);
								map.setMyLocationEnabled(false);
								addMarker(rehab);

							}
						});
			}
		}

		Bundle extra = getIntent().getExtras();
		//		boolean flag = false;
		if (extra != null ) {
			rehab = (RehabModel) extra.getSerializable(KEY_REHAB);

			tvRehabName.setText(rehab.getName());
			tvLocationName.setText(rehab.getTypeName());

			tvDistance.setText(rehab.getDistance() + " MILES AWAY");

			if(RehabResponseModel.getTodayRehabTiming(rehab.getRehabDays()) == null){
				tvRehabStatus.setVisibility(View.GONE);
			}else
			if(RehabResponseModel.isOpenNow(rehab.getRehabDays())){
				tvRehabStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.hours_bg_green));
				tvRehabStatus.setText(R.string.rehab_open_now_);
			}else{
				tvRehabStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.yellow_round_corners));
				tvRehabStatus.setText(R.string.rehab_close_now_);
			}
			isFavorite = rehab.isFavorite();
			resetFavoriteIcon();

			// private TextView tvRehabStatus;
			tvTime.setText(rehab.getHours());
			
			//to us format
			String phone = rehab.getPhone();
			phone = UtilityClass.phoneNumberNormal(phone);
			
			if(phone.length() >= 10){
				phone = phone.substring(phone.length() - 10);
				phone = UtilityClass.phoneNumberUsFormat(phone);
			}
			
			tvPhone.setText(phone);
			tvWebsite.setText(rehab.getWebsite());
			tvAddress.setText(rehab.getAddress());
			tvAbout.setText(rehab.getAbout());
			
			

			photoUrls = rehab.getRehabPhotos();
			
			if(photoUrls != null && photoUrls.size() != 0){
				PhotosAdapter adapter = new PhotosAdapter(this,photoUrls,getPhotosClickListener(),true);
				recyclerviewPhotos.setAdapter(adapter);
				recyclerviewPhotos.setHasFixedSize(true);
				recyclerviewPhotos.addItemDecoration(
						new EqualSpaceItemDecoration(getResources()
								.getDimensionPixelSize(R.dimen.activity_horizontal_margin)));
			}else{
				recyclerviewPhotos.setVisibility(View.GONE);
				findViewById(R.id.txtPhotos).setVisibility(View.GONE);
			}
			
			videoUrls = rehab.getRehabVideos();
			
			if(videoUrls != null && videoUrls.size() != 0){
				GridLayoutManager gm
			    = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL,false);
				recyclerviewVideos.setLayoutManager(gm);
				recyclerviewVideos.addItemDecoration(
						new EqualSpaceItemDecoration(getResources()
								.getDimensionPixelSize(R.dimen.activity_horizontal_margin)));
				recyclerviewVideos.setHasFixedSize(true);
				recyclerviewVideos.setAdapter(new PhotosAdapter(this,videoUrls,getVideosClickListener(),false));
			}else{
				recyclerviewVideos.setVisibility(View.GONE);
				findViewById(R.id.txtVideos).setVisibility(View.GONE);
			}
			
//			addPhotos(rehab.getRehabPhotos());
//			addVideos(rehab.getRehabVideos());

		}

		// init meeting adapter
	}

	private PhotoClickListener getPhotosClickListener() {
		PhotoClickListener listener = new PhotosAdapter.PhotoClickListener() {
			
			@Override
			public void onPhotoClick(int position) {
				String url  = photoUrls.get(position);

				if(TextUtils.isEmpty(url)){
					App.toast("Sorry broken link!");
					return;
				}

				//passing photo to media activity
				Intent intent = new Intent(RehabDetailsActivity.this, MediaFullScreenActivity.class);
				intent.putExtra(MediaFullScreenActivity.MEDIA_TYPE, MediaFullScreenActivity.TYPE_IMAGE);
				intent.putExtra("link", url);
				startActivity(intent);
				overridePendingTransition(R.anim.bottom_in_instant,R.anim.abc_fade_out);
			}
		};
		return listener;
	}
	
	
	private PhotoClickListener getVideosClickListener() {
		PhotoClickListener listener = new PhotosAdapter.PhotoClickListener() {
			
			@Override
			public void onPhotoClick(int position) {
				//passing video to media activity
				String url = videoUrls.get(position);

				if(TextUtils.isEmpty(url)){
					App.toast("Sorry broken link!");
					return;
				}

				Intent intent = new Intent(RehabDetailsActivity.this, MediaFullScreenActivity.class);
				intent.putExtra(MediaFullScreenActivity.MEDIA_TYPE, MediaFullScreenActivity.TYPE_VIDEO);
				intent.putExtra("link", url);
				startActivity(intent);
				overridePendingTransition(R.anim.bottom_in_instant,R.anim.abc_fade_out);
//				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//				try{
//					intent.setDataAndType(Uri.parse(url), "video/mp4");
//				startActivity(intent);
//				}catch(ActivityNotFoundException e){
//					App.toast("Video Player not found");
//				}
			}
		};
		return listener;
	}

	public class EqualSpaceItemDecoration extends RecyclerView.ItemDecoration {

		private final int mSpaceHeight;

		public EqualSpaceItemDecoration(int mSpaceHeight) {
			this.mSpaceHeight = mSpaceHeight;
		}

		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
				RecyclerView.State state) {
//			outRect.bottom = mSpaceHeight;
//			outRect.top = mSpaceHeight;
//			outRect.left = mSpaceHeight;
			outRect.right = mSpaceHeight;
		}
	}
	private void addOtherServices(boolean visible) {
		
		String otherServices = rehab.getOtherServices();
		if(TextUtils.isEmpty(otherServices)){
			return;
		}

		View dividerView = findViewById(R.id.dividerService);
		llOtherServices.removeAllViews();


		String[] osArr = otherServices.split(",");
		
		if(osArr.length > 0 && visible) {
			llOtherServices.setVisibility(View.VISIBLE);
			dividerView.setVisibility(View.VISIBLE);
			tvOtherServices.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.up_arrow), null);
		}else{
			tvOtherServices.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_arrow), null);
			llOtherServices.setVisibility(View.GONE);
			dividerView.setVisibility(View.GONE);
		}

		Resources r = getResources();
		final float scale = getResources().getDisplayMetrics().density;
		int px2dp = (int) (2 * scale + 0.5f);

		for (int i = 0; i < osArr.length; i++){
			String service = osArr[i];
			TextView tv = new TextView(this);
			tv.setText(service);
			LinearLayout.LayoutParams lllp1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
			tv.setLayoutParams(lllp1);
			tv.setPadding(0, 
					r.getDimensionPixelSize(R.dimen.wall_margin), 
					r.getDimensionPixelSize(R.dimen.wall_margin), 
					r.getDimensionPixelSize(R.dimen.wall_margin));
			llOtherServices.addView(tv);

			//divider
			if(i != osArr.length - 1){
				TextView iv = new TextView(this);
				iv.setBackgroundResource(R.drawable.filter_divider);
				LinearLayout.LayoutParams lllp2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, px2dp);
				iv.setLayoutParams(lllp2);
				iv.setPadding(0, 
						r.getDimensionPixelSize(R.dimen.wall_margin), 
						0, 
						0);
				llOtherServices.addView(iv);
			}
		}
		
	}
	
	private void addPayments(boolean visible) {
		View dividerView = findViewById(R.id.dividerPayments);

		
		ArrayList<String> insuraces = (ArrayList<String>) rehab.getRehabInsurances();
		if(insuraces.size() > 0 && visible){
			tvPayments.setVisibility(View.VISIBLE);
			dividerView.setVisibility(View.VISIBLE);
			tvInsuranceAccepted.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.up_arrow), null);
		}else{
			tvInsuranceAccepted.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_arrow), null);
			tvPayments.setVisibility(View.GONE);
			dividerView.setVisibility(View.GONE);
			return;
		}

		tvPayments.setText("");


		StringBuffer textPayments = new StringBuffer(); 
		for (int i = 0; i < insuraces.size(); i++){
			String service = insuraces.get(i);
			textPayments.append(service);
			if(insuraces.size() > 0 || insuraces.size() != i+1 ){
				textPayments.append("\n");
			}
		}
		
		if(insuraces.size() > 0){
			textPayments.append("\n");
		}
		ArrayList<String> payments = (ArrayList<String>) rehab.getRehabPayments();
		
		if(payments.size() > 0){
			textPayments.append("We accept ");
		}
		
		for (int i = 0; i < payments.size(); i++){
			String payment = payments.get(i);
			textPayments.append(payment);
			
			if(i + 1 != payments.size())
			textPayments.append(", ");
		}
		
		if(payments.size() > 0){
			textPayments.append(" and all major credit cards.");
		}
		
		tvPayments.setText(textPayments.toString());
		
	}

	@Override
	void onBackendConnected() {

	}

//	public void addPhotos(List<String> photos) {
//		rlPhotoes.removeAllViews();
//
//		//dummy
//		//		if( photos == null || photes.size() == 0 ) return;
////		photos.clear();
////
////		String url = "http://www.gettyimages.com/gi-resources/images/CreativeImages/Hero-527920799.jpg";
//
////		for (int i = 0; i < 5; i++) {
////			//			if(ValidationUtils.isValidUrl(url))
////			photos.add(url);
////		}
//
//		if(photos == null || photos.size() == 0){
//			findViewById(R.id.txtPhotos).setVisibility(View.GONE);
//			return;
//		}
//		
//		BitmapFactory.Options op = new BitmapFactory.Options();
//				op.inJustDecodeBounds = true;
//				BitmapFactory.decodeResource(this.getResources(),R.drawable.loading_img, op);
//			int	height = op.outHeight;
//		
//		int i = 0;
//		for (String photourl : photos) {
////			FrameLayout fl = new FrameLayout(this);
////			fl.setLayoutParams(new LayoutParams(-2, -2));
////			ImageView mask = new ImageView(this);
////			mask.setLayoutParams(new LayoutParams(-2, -2));
////			mask.setBackgroundResource(R.drawable.mask_img);
//			ImageView iv = new ImageView(this);
//			iv.setPadding(i == 0 ? 0 : getResources().getDimensionPixelSize(R.dimen.wall_margin), 
//					getResources().getDimensionPixelSize(R.dimen.wall_margin), 
//					0, 
//					getResources().getDimensionPixelSize(R.dimen.wall_margin));
//			Picasso.with(this).load(photourl).centerCrop()
//			.placeholder(R.drawable.loading_img).resize(height, height)
//			.error(R.drawable.loading_img)
//			.into(iv);
////			fl.addView(iv);
////			fl.addView(mask);
////			CardView cv = new CardView(this);
////			cv.setRadius(getResources().getDimension(R.dimen.dp2));
////			cv.addView(iv);
//			rlPhotoes.addView(iv);
//			i++;
//		}
//	}

//	public void addVideos(List<String> videos) {
//		rlVideos.removeAllViews();
//
//		//dummy
////		videos.clear();
////		//		if( videos == null || videos.size() == 0 ) return;
////
////		String url = "http://data.hdwallpapers.im/creativity_water_spray_drops_flower_rose_desktop_images.jpg";
////
////		for (int i = 0; i < 2; i++) {
////			videos.add(url);
////		}
//		
//		if(videos == null || videos.size() == 0){
//			findViewById(R.id.txtVideos).setVisibility(View.GONE);
//			return;
//		}
//		
//		BitmapFactory.Options op = new BitmapFactory.Options();
//		op.inJustDecodeBounds = true;
//		BitmapFactory.decodeResource(this.getResources(),R.drawable.loading_img, op);
//	int	height = op.outHeight;
//
//		int i = 0;
//		for (String videoUrl : videos) {
//			ImageView iv = new ImageView(this);
//			iv.setScaleType(ScaleType.CENTER_CROP);
//			iv.setPadding(i == 0 ? 0 : getResources().getDimensionPixelSize(R.dimen.wall_margin), 
//					getResources().getDimensionPixelSize(R.dimen.wall_margin), 
//					getResources().getDimensionPixelSize(R.dimen.wall_margin), 
//					getResources().getDimensionPixelSize(R.dimen.wall_margin));
//			Picasso.with(this).load(videoUrl)
//			.placeholder(R.drawable.loading_img).resize(height, height)
//			.error(R.drawable.loading_img)
//			.into(iv);
////			CardView cv = new CardView(this);
////			cv.setRadius(getResources().getDimension(R.dimen.dp2));
////			cv.addView(iv);
//			rlVideos.addView(iv);
//			i++;
//		}
//	}

	public void addMarker(RehabModel m) {
		if(map == null) return;
		int resourceId = RehabResponseModel.getMarkDrawableId(rehab.getPackageName());
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
			App.toast(getString(R.string.no_internet_connection));
			isFavorite = rehab.isFavorite();
			resetFavoriteIcon();
			return;
		}

		try {

			JSONObject params = new JSONObject();

			params.put("rehab_id", rehab.getId());
			if(rehab.isFavorite()){
				params.put("type", "remove");
			}else{
				params.put("type", "add");
			}
			//			type[2:05:01 PM] Bilal Jaffar: params: "rehab_id", "type"
			//			  types_array = ["add","remove"];
			//			  add = favorite
			//			remove = unfavorite
			// params.put("favorite", meeting.isFavourite() ? 0 : 1);

			pd.show();
			socketService.favouriteRehab(params);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	private void resetFavoriteIcon() {
		ibFav.setImageResource( isFavorite ? R.drawable.star_pink
				: R.drawable.star_white);		
	}

	@Override
	public void onMapClick(LatLng arg0) {
//		Intent intent = new Intent(this, FullScreenMapActivity.class);
//		intent.putExtra(FullScreenMapActivity.EXTRA_REHAB, rehab);
//
//		startActivityForResult(intent, CODE_FULL_SCREEN);
//
//		overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && resultCode == CODE_FULL_SCREEN) {
//			rehab = (RehabModel) data
//					.getSerializableExtra(FullScreenMapActivity.EXTRA_MEETING);
//			ibRating.setImageResource(meeting.isFavourite() ? R.drawable.star_pink
//					: R.drawable.star_white);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvOtherServices:
			addOtherServices(!llOtherServices.isShown());
			break;

		case R.id.rlGetDirection:
			if (myLocation == null) {
				AppPrefs prefs = AppPrefs
						.getAppPrefs(this);
				double latitude = prefs.getDoubletPrefs(AppPrefs.KEY_LATITUDE,
						0);
				double longitude = prefs.getDoubletPrefs(
						AppPrefs.KEY_LONGITUDE, 0);
				myLocation = new Location("");
				myLocation.setLatitude(latitude);
				myLocation.setLongitude(longitude);
			}
			String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="
					+ myLocation.getLatitude() + "," + myLocation.getLongitude()
					+ "&daddr=" + rehab.getLatitude() + ","
					+ rehab.getLongitude();
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse(uri));
			startActivity(Intent.createChooser(intent, "Select an application"));
			break;
//		case R.id.ibFav:
//			isFavorite = !isFavorite;
//			resetFavoriteIcon();
//			addUserFavourite();
//			break;
		case R.id.tvInsuranceAccepted:
			//toggling payments
			addPayments(!tvPayments.isShown());
			break;
		case R.id.ibSetting:
			// presentReportMenu();

			new ReportAnInAccuracyDialog(this)
			.setReportAnInAccuracyDialogListener(
					new ReportAnInAccuracyDialogClickListener() {

						@Override
						public void onInaccuracyClick(
								ReportAnInAccuracyDialog dialog) {
							dialog.dismiss();
							Intent i = new Intent(
									RehabDetailsActivity.this,
									ReportInaccuracyActivity.class);
							i.putExtra(
									ReportInaccuracyActivity.KEY_REHAB_ID,
									rehab.getId());
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
		default:
			break;
		}
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		if (pd != null) {
			pd.dismiss();
		}

		if(event.equals(EventParams.EVENT_REHAB_FAVOURITE)){
			rehab.setFavorite(isFavorite);
		}

	}

	@Override
	public void onSocketResponseFailure(String event, String message) {
		pd.dismiss();
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

		if(event.equals(EventParams.EVENT_REHAB_FAVOURITE)){
			isFavorite = rehab.isFavorite();
			resetFavoriteIcon();
		}
	}

	@Override
	public void onBackPressed() {
		Intent i = getIntent();
		//		i.putExtra("rehab", rehab);
		i.putExtra("isfav", isFavorite);

		setResult(RESULT_OK, i);

		finish();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
		// super.onBackPressed();

		// overridePendingTransition(R.anim.activity_back_in,
		// R.anim.activity_back_out);
	}
	
}
