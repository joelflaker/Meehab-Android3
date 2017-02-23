package com.citrusbits.meehab.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.map.LocationUtils;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.model.NearestDateTime;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.ui.dialog.DeleteReviewConfirmDialog;
import com.citrusbits.meehab.ui.dialog.DeleteReviewConfirmDialog.DeleteReviewConfirmDialogClickListener;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.MeetingReviewModel;
import com.citrusbits.meehab.model.MyReview;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.ui.meetings.MeetingDetailsActivity;
import com.citrusbits.meehab.ui.users.UserProfileActivity;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DateTimeUtils;
import com.citrusbits.meehab.utils.MeetingUtils;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class MyReviewDetailActivity extends SocketActivity implements
		OnSocketResponseListener, OnBackendConnectListener {

	public static final String TAG = MyReviewDetailActivity.class
			.getSimpleName();

	private Dialog pd;

	Object object;
	MeetingReviewModel reviewModel;
	MyReview myReview;
	
	long timeZoneOffset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		timeZoneOffset = MeetingUtils.getTimeZoneOffset();
		pd = UtilityClass.getProgressDialog(MyReviewDetailActivity.this);
		setContentView(R.layout.activity_my_review_detail);
		object = getIntent().getSerializableExtra(MyReview.EXTRA_REVIEW);

		final TextView tvReviewTitle = (TextView) this
				.findViewById(R.id.tvReviewTitle);
		final RatingBar rating = (RatingBar) this.findViewById(R.id.rating);
		final TextView tvMeetingName = (TextView) this
				.findViewById(R.id.tvMeetingName);
		final TextView tvDateTime = (TextView) this.findViewById(R.id.tvDateTime);
		final TextView tvComment = (TextView) this.findViewById(R.id.tvComment);

		final ImageView ivUserIcon =(ImageView) findViewById(R.id.ivUserIcon);

		final ImageButton topRightBtn = (ImageButton) findViewById(R.id.topRightBtn);
		final String userId = AccountUtils.getUserId(this) + "";
		if (object instanceof MyReview) {
			myReview = (MyReview) object;
			tvReviewTitle.setText(myReview.getReviewTitle());
			rating.setRating(myReview.getRating());

			tvMeetingName.setText(myReview.getMeetingName());
			tvMeetingName.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					getMeetingById("" + myReview.getMeetingId());
				}
			});

			tvDateTime.setText(DateTimeUtils.getDatetimeReview(myReview
					.getDateTimeAdded(),timeZoneOffset));
			tvComment.setText(myReview.getComment());
			final String commentUserId = myReview.getUserId() + "";

			ivUserIcon.setVisibility(View.GONE);

			topRightBtn
					.setVisibility(userId.equals(commentUserId) ? View.VISIBLE
							: View.GONE);


		} else if (object instanceof MeetingReviewModel) {
			reviewModel = (MeetingReviewModel) object;
			tvReviewTitle.setText(reviewModel.getTitle());
			ivUserIcon.setTag(reviewModel);
			ivUserIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MeetingReviewModel review = (MeetingReviewModel) v.getTag();

					if(AccountUtils.getUserId(MyReviewDetailActivity.this) == review.getUserId()) {
						App.toast(getString(R.string.message_your_profile));
						return;
					}

					pd.show();
					socketService.getUserById(review.getUserId());
				}
			});

			rating.setRating(reviewModel.getStars());

			tvMeetingName.setText(reviewModel.getUsername());

			tvDateTime.setText(DateTimeUtils.getDatetimeReview(reviewModel
					.getDatetimeAdded(),timeZoneOffset));

			tvComment.setText(reviewModel.getComments());
			final String commentUserId = reviewModel.getUserId() + "";

			final String userImage = reviewModel.getImage();

			Log.e("User image is ",""+ userImage);

			if(!TextUtils.isEmpty(userImage)) {
				Picasso.with(MyReviewDetailActivity.this).load(userImage)
						.placeholder(R.drawable.profile_pic_border).resize(80, 80)
						.error(R.drawable.profile_pic_border)
						.transform(new PicassoCircularTransform()).into(ivUserIcon);
			}else {
				Picasso.with(MyReviewDetailActivity.this)
						.load(R.drawable.profile_pic_border)
						.resize(80, 80).into(ivUserIcon);
			}
			topRightBtn
					.setVisibility(userId.equals(commentUserId) ? View.VISIBLE
							: View.GONE);

		}
		
		
		

		// top back button
		findViewById(R.id.topMenuBtn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});

		findViewById(R.id.topRightBtn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// confirmation
						/*
						 * new AlertDialog.Builder(MyReviewDetailActivity.this)
						 * .setTitle(
						 * "Are you sure you want to delete this review?")
						 * .setPositiveButton("Delete", new
						 * DialogInterface.OnClickListener() {
						 * 
						 * @Override public void onClick( DialogInterface
						 * dialog, int which) { // delete String
						 * reviewId=myReview
						 * !=null?myReview.getReviewId()+"":reviewModel
						 * .getId()+""; deleteUserReview(reviewId);
						 * 
						 * } }).setNegativeButton("Cancel", null)
						 * .create().show();
						 */

						new DeleteReviewConfirmDialog(
								MyReviewDetailActivity.this)
								.setDeleteReviewConfirmDialogListener(
										new DeleteReviewConfirmDialogClickListener() {

											@Override
											public void onDeleteClick(
													DeleteReviewConfirmDialog dialog) {
												// stub
												dialog.dismiss();

												String reviewId = myReview != null ? myReview
														.getReviewId() + ""
														: reviewModel.getId()
																+ "";
												deleteUserReview(reviewId);

											}

											@Override
											public void onCancelClick(
													DeleteReviewConfirmDialog dialog) {
												// stub
												dialog.dismiss();

											}
										}).show();
					}
				});
	}

	public void deleteUserReview(String reviewId) {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}
		if (socketService != null) {
			if (!NetworkUtils.isNetworkAvailable(this)) {
				App.toast(getString(R.string.no_internet_connection));
				return;
			}

			
			JSONObject object = new JSONObject();

			try {
				object.put("user_id", AccountUtils.getUserId(this));
				object.put("review_id", reviewId);
				socketService.deleteUserReviews(object);
				pd.show();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			

		}
	}

	@Override
	public void onBackendConnected() {

	}

	private void getMeetingById(String meetingId) {
		if (NetworkUtil
				.getConnectivityStatus(this) == 0) {

			Toast.makeText(this,
					getString(R.string.no_internet_connection),
					Toast.LENGTH_SHORT).show();

			return;
		}
		pd.show();
		socketService.getMeetingById(meetingId);
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		if (pd.isShowing()) {
			pd.dismiss();
		}
		if (event.equals(EventParams.METHOD_MEETING_BY_ID)) {
			JSONObject data = ((JSONObject) obj);
			Log.d(TAG,""+data);
			MeetingModel meeting = new Gson().fromJson(data.optJSONObject("meeting").toString(), MeetingModel.class);

			meeting.setFavourite(meeting.getFavouriteMeeting() == 1);
			NearestDateTime nearDateTime = MeetingUtils.getNearestDate(meeting.getOnDay(),
					meeting.getOnTime());

			Location myLocation = LocationUtils.getLastLocation(this);
			//distance calculation
			float[] results = new float[1];
			Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(), meeting.getLatitude(), meeting.getLongitude(), results);
			//
			double distance = results[0] * 0.000621371192f;
			distance = Math.floor(distance * 10) / 10f;
			meeting.setDistanceInMiles(distance);

			if (nearDateTime != null) {
				meeting.setTodayMeeting(nearDateTime.isToday());
				meeting.setOnDateOrigin(nearDateTime.getDate());
				meeting.setNearestTime(nearDateTime.getTime());
				meeting.setNearestDateTime(nearDateTime.getDateTime());
				meeting.setOnDate(MeetingUtils.formateDate(nearDateTime.getDateTime()));
				MeetingUtils.setStartInTime(meeting, meeting.getNearestDateTime());
			}

			Intent intent = new Intent(this, MeetingDetailsActivity.class);
			intent.putExtra("meeting",meeting);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
		}else
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
		}else if (event.equals(EventParams.EVENT_DELETE_USER_REVIEW)) {
			JSONObject data = ((JSONObject) obj);

			Log.e(TAG, data.toString());
			try {
				Toast.makeText(MyReviewDetailActivity.this,
						data.getString("message"), Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			MyReviewDetailActivity.this.finish();

		}

	}

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

}
