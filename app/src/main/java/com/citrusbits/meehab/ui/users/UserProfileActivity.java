package com.citrusbits.meehab.ui.users;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.app.MeehabApp;
import com.citrusbits.meehab.helpers.AgeHelper;
import com.citrusbits.meehab.map.LocationUtils;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.model.NearestDateTime;
import com.citrusbits.meehab.ui.MyReviewDetailActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.ui.dialog.BlockFrindConfirmationDialog;
import com.citrusbits.meehab.ui.dialog.BlockFrindConfirmationDialog.BlockFrindConfirmationDialogClickListener;
import com.citrusbits.meehab.ui.dialog.BlockUserDialog;
import com.citrusbits.meehab.ui.dialog.BlockUserDialog.BlockUserDialogClickListener;
import com.citrusbits.meehab.ui.fragments.MyProfileFragment;
import com.citrusbits.meehab.images.PicassoBlurTransform;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.MyReview;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.ui.SocketActivity;
import com.citrusbits.meehab.ui.meetings.MeetingDetailsActivity;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DateTimeUtils;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.MeetingUtils;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.RecoverClockDateUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class UserProfileActivity extends SocketActivity implements
		OnSocketResponseListener, OnBackendConnectListener, OnClickListener {

	public static final String EXTRA_USER_ACCOUNT = "user_account";

	public static final String TAG = MyProfileFragment.class.getSimpleName();

	public static final int RESULT_CODE_BLOCKED = 7;
	public static final int REQUEST_CODE_CHAT = 5;
	private UserAccount user;
	private TextView usernameText, interestedInText, ethnicityText, heightText,
			aaStoryText;
	private ImageView ivUserIcon;
	private LinearLayout reviewsContainer;
	private TextView weightText;
	private TextView occupationText;
	private TextView kidsText;
	private TextView homegroupText;
	private TextView SoberDateText;
	private ArrayList<MyReview> reviews = new ArrayList<MyReview>();

	private TextView tvAge;
	private TextView tvGender;
	private TextView tvOriendation;
	private TextView tvMaritalStatus;

	private Dialog pd;

	private ImageView ivFavourite;
	private ImageView ivBlurBg;
	private LinearLayout llSendMessage;

	private long timeZoneOffest;

	private ImageButton ibSeeMore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_user_profile);
		ibSeeMore = (ImageButton) this.findViewById(R.id.ibSeeMore);
		ibSeeMore.setOnClickListener(this);
		timeZoneOffest = MeetingUtils.getTimeZoneOffset();

		pd = UtilityClass.getProgressDialog(this);
		user = (UserAccount) getIntent().getSerializableExtra(
				EXTRA_USER_ACCOUNT);
		ivFavourite = (ImageView) findViewById(R.id.ivFavourite);
		ivBlurBg = (ImageView) findViewById(R.id.ivBlurBg);

		findViewById(R.id.ivBack).setOnClickListener(this);
		findViewById(R.id.ivActionOnProfile).setOnClickListener(this);
		ivFavourite.setOnClickListener(this);

		ivUserIcon = (ImageView) findViewById(R.id.ivUserIcon);

		llSendMessage = (LinearLayout) findViewById(R.id.llSendMessage);

		usernameText = (TextView) findViewById(R.id.userNameText);

		heightText = (TextView) findViewById(R.id.heightText);
		weightText = (TextView) findViewById(R.id.weightText);
		ethnicityText = (TextView) findViewById(R.id.ethnicityText);
		occupationText = (TextView) findViewById(R.id.occupationText);
		interestedInText = (TextView) findViewById(R.id.interestedInText);
		kidsText = (TextView) findViewById(R.id.kidsText);
		homegroupText = (TextView) findViewById(R.id.homegroupText);
		SoberDateText = (TextView) findViewById(R.id.SoberDateText);
		aaStoryText = (TextView) findViewById(R.id.aaStoryText);

		reviewsContainer = (LinearLayout) findViewById(R.id.reviewsContainer);

		tvAge = (TextView) findViewById(R.id.tvAge);
		tvGender = (TextView) findViewById(R.id.tvGender);
		tvOriendation = (TextView) findViewById(R.id.tvOriendation);
		tvMaritalStatus = (TextView) findViewById(R.id.tvMaritalStatus);

//		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) ivBlurBg
//				.getLayoutParams();
//		int width = DeviceUtils.getDeviceWidth(this);
//		params.height = (int) (width * 0.82f);
//		ivBlurBg.setLayoutParams(params);
		llSendMessage.setOnClickListener(this);

		// init meeting adapter

		// Log.e("Date of Birth ", user.getDateOfBirth() + " Date Of Birth");

		Picasso.with(this)
				.load(R.drawable.img_place_holder)
				.transform(
						new PicassoBlurTransform(
								UserProfileActivity.this, Consts.IMAGE_BLURR_RADIUS))
				.into(ivBlurBg);

		resetUserInfo();
		getUserReviews();
		setFavourite();

	}

	public void setFavourite() {
		ivFavourite
				.setImageResource(user.isFavourite() == 1 ? R.drawable.star_pink
						: R.drawable.star_white);

	}

	private void resetUserInfo() {
		if (user != null) {



			if (!TextUtils.isEmpty(user.getImage())) {
				/*
				 * profileNetworkImageView.setImageUrl(getString(R.string.url) +
				 * user.getImage(), MeehabApp.getInstance().getImageLoader());
				 */
				String userImage = user.getImage();

				Picasso.with(this).load(userImage)
						.error(R.drawable.img_place_holder)
						.transform(new PicassoCircularTransform())
						.into(ivUserIcon);

				Picasso.with(this)
						.load(userImage)
						.placeholder(R.drawable.img_place_holder)
						.error(R.drawable.img_place_holder)
						.transform(
								new PicassoBlurTransform(
										UserProfileActivity.this, Consts.IMAGE_BLURR_RADIUS))
						.into(ivBlurBg);
			}

			TextView tvProfileHeading = (TextView) findViewById(R.id.tvProfileHeading);

			tvProfileHeading.setText(String.format(
					this.getString(R.string.friend_profile_heading),
					user.getUsername()));

			usernameText.setText(user.getUsername());
			// String ageGenderEtcString = "24";

			tvAge.setText(String.valueOf(user.getAge()) + " YEAR"+(user.getAge() > 1 ? "S" :""));
			tvGender.setText(user.getGender());
			tvOriendation.setText(user.getSexualOrientation() == null? "" : user.getSexualOrientation());
			tvMaritalStatus.setText(user.getMaritalStatus() == null? "" : user.getMaritalStatus());

			if(!TextUtils.isEmpty(user.getDateOfBirth())){
				tvAge.setText(""+ AgeHelper.calculateAge(user.getDateOfBirth()) + " "+getString(R.string.label_years));
				findViewById(R.id.tvLine1).setVisibility(View.VISIBLE);
			}else {
				tvAge.setText("");
				findViewById(R.id.tvLine1).setVisibility(View.GONE);
			}
			tvGender.setText(""+user.getGender());
			if(!TextUtils.isEmpty(user.getSexualOrientation())){
				tvOriendation.setText(user.getSexualOrientation());
				findViewById(R.id.tvLine2).setVisibility(View.VISIBLE);
			}else {
				tvOriendation.setText("");
				findViewById(R.id.tvLine2).setVisibility(View.GONE);
			}
			if(!TextUtils.isEmpty(user.getMaritalStatus())){
				tvMaritalStatus.setText(user.getMaritalStatus());
				findViewById(R.id.tvLine3).setVisibility(View.VISIBLE);
			}else {
				tvMaritalStatus.setText("");
				findViewById(R.id.tvLine3).setVisibility(View.GONE);
			}

			heightText.setText(user.getHeight() == null? "" : user.getHeight());
			weightText.setText(user.getWeight() == null? "" : user.getWeight());
			ethnicityText.setText(user.getEthnicity() == null? "" : user.getEthnicity());
			occupationText.setText(user.getAccupation() == null? "" : user.getAccupation());

			if ("Choose Not to Answer".toLowerCase().toString()
					.equals(user.getIntrestedIn().toLowerCase())) {
				interestedInText.setText(R.string.dating_and_fellowshiping);
			} else {
				interestedInText.setText(user.getIntrestedIn());
			}
			kidsText.setText(user.getHaveKids());
			// homegroupText.setText(user.getWillingSponsor())
			homegroupText.setText(user.getMeetingHomeGroup() == null? "" : user.getMeetingHomeGroup());
			aaStoryText.setText(user.getAboutStory() == null? "" : user.getAboutStory());

			String aaStoryTxt = user.getAboutStory();
			if (aaStoryTxt != null && aaStoryTxt.length() > 100) {
				aaStoryText.setText(aaStoryTxt.substring(0, 100));
				ibSeeMore.setVisibility(View.VISIBLE);
			} else {
				aaStoryText.setText(aaStoryTxt == null? "" : aaStoryTxt);
				ibSeeMore.setVisibility(View.GONE);
			}

			SoberDateText.setText(RecoverClockDateUtils.getSoberDifference(
					user.getSoberSence(), true, this));

			ivFavourite.setVisibility(user.isBlocked() == 1 ? View.GONE
					: View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			onBackPressed();
			break;
		case R.id.ivActionOnProfile:
			// performAction();
			new BlockUserDialog(this)
					.setBlocked(user.isBlocked() == 1)
					.setBlockUserDialogListener(
							new BlockUserDialogClickListener() {

								@Override
								public void onReportUser(BlockUserDialog dialog) {
									dialog.dismiss();
									Intent i = new Intent(
											UserProfileActivity.this,
											ReportFriendActivity.class);
									i.putExtra(
											ReportFriendActivity.KEY_FRIEND_ID,
											user.getId());
									startActivity(i);
								}

								@Override
								public void onCancelClick(BlockUserDialog dialog) {
									dialog.dismiss();
								}

								@Override
								public void onBlockUser(BlockUserDialog dialog) {
									dialog.dismiss();

									new BlockFrindConfirmationDialog(
											UserProfileActivity.this)
											.setBlockFrindConfirmationDialogClickListener(
													new BlockFrindConfirmationDialogClickListener() {

														@Override
														public void onYesClick(
																BlockFrindConfirmationDialog dialog) {
															dialog.dismiss();
															blockUser();
														}

														@Override
														public void onNoClick(
																BlockFrindConfirmationDialog dialog) {
															dialog.dismiss();
														}
													},
													user.isBlocked() == 1 ? BlockFrindConfirmationDialog.STATUS_UNBLOCK
															: BlockFrindConfirmationDialog.STATUS_SINGLE_BLOCK)
											.show();

								}
							}).show();
			break;
		case R.id.ivFavourite:
			// Toast.makeText(UserProfileActivity.this, "Favourite ",
			// Toast.LENGTH_SHORT).show();

			favouriteUser();
			break;
		case R.id.llSendMessage:

			/*
			 * if (!user.getCheckinType().equals("online")) {
			 * Toast.makeText(UserProfileActivity.this, "User is offline",
			 * Toast.LENGTH_SHORT).show(); return; }
			 */

			if (user.isBlocked() == 1) {
				Toast.makeText(UserProfileActivity.this, "User is blocked",
						Toast.LENGTH_SHORT).show();
				return;
			}

			Intent chatIntent = new Intent(UserProfileActivity.this,
					ChatActivity.class);
			chatIntent.putExtra(ChatActivity.KEY_FRIEND_ID, user.getId());
			chatIntent.putExtra(ChatActivity.KEY_FRIEND_NAME,
					user.getUsername());

			startActivityForResult(chatIntent, REQUEST_CODE_CHAT);

			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

			break;

		case R.id.ibSeeMore:

			aaStoryText.setText(user.getAboutStory());

			ibSeeMore.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}


	@Override
	public void onBackendConnected() {
		getUserReviews();
	}

	public void getUserReviews() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			MeehabApp.toast(getString(R.string.no_internet_connection));
			return;
		}
		if (socketService != null) {
			pd = UtilityClass.getProgressDialog(this);
			pd.show();
			JSONObject object = new JSONObject();

			try {
				object.put("user_id", user.getId());
				Log.e("json send ", object.toString());
				socketService.getUserReviews(object);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		if (pd.isShowing()) {
			pd.dismiss();
		}

		reviews.clear();
		if (event.equals(EventParams.METHOD_MEETING_BY_ID)) {
			JSONObject data = ((JSONObject) obj);
			Log.d(TAG,""+data);
			MeetingModel meeting = new Gson().fromJson(data.optJSONObject("meeting").toString(), MeetingModel.class);

			if(meeting.getFavouriteMeeting() != null)
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
				MeetingUtils.setMeetingTimingStatus(meeting, meeting.getNearestDateTime());
			}

			Intent intent = new Intent(this, MeetingDetailsActivity.class);
			intent.putExtra("meeting",meeting);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
		}else
		if (event.equals(EventParams.EVENT_GET_USER_REVIEWS)) {
			JSONObject data = ((JSONObject) obj);

			Log.e(TAG, data.toString());

			try {
				JSONArray userReviews = data.getJSONArray("getUserReviews");

				for (int i = 0; i < userReviews.length(); i++) {

					JSONObject reviewObject = userReviews.getJSONObject(i);

					String comment = reviewObject.optString("comments");
					String image = reviewObject.optString("image");

					String meetingName = reviewObject.optString("meeting_name");
					String meetingId = reviewObject.optString("meeting_id");
					int rating = reviewObject.optInt("stars");
					int reviewId = reviewObject.optInt("id");
					String reviewTitle = reviewObject.optString("title");

					String datetimeUpdated = reviewObject
							.optString("datetime_updated");

					MyReview myReview = new MyReview();
					myReview.setDatetimeUpdated(datetimeUpdated);
					myReview.setComment(comment);
					myReview.setImage(image);
					myReview.setMeetingId(meetingId);
					myReview.setMeetingName(meetingName);
					myReview.setRating(rating);
					myReview.setReviewId(reviewId);
					myReview.setReviewTitle(reviewTitle);
					reviews.add(myReview);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			reviewsContainer.removeAllViews();
			fillContainer(reviewsContainer, reviews);

		} else if (event.equals(EventParams.METHOD_BLOCK_USER)) {
			JSONObject data = ((JSONObject) obj);

			try {
				String message = data.getString("message");

				user.setBlocked(user.isBlocked() == 1 ? 0 : 1);
				if (user.isBlocked() == 1) {
					Toast.makeText(UserProfileActivity.this,
							"User has been blocked!", Toast.LENGTH_SHORT)
							.show();

				} else {
					Toast.makeText(UserProfileActivity.this,
							"User has been unblocked!", Toast.LENGTH_SHORT)
							.show();
				}

				onBackPressed();
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else if (event.equals(EventParams.METHOD_FAVOURITE_USER)) {
			JSONObject data = ((JSONObject) obj);

			try {
				String message = data.getString("message");
				user.setFavourite(user.isFavourite() == 1 ? 0 : 1);
				setFavourite();
				if (user.isFavourite() == 1) {
					Toast.makeText(UserProfileActivity.this,
							"User added to favorites!", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(UserProfileActivity.this,
							"User removed from favorites", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else if (event.equals(EventParams.METHOD_BLOCK_USER_NOTIFY)) {
			JSONObject data = ((JSONObject) obj);
			try {

				String message = data.getString("message");

				Toast.makeText(UserProfileActivity.this, message,
						Toast.LENGTH_SHORT).show();
				user.setBlocked(user.isBlocked() == 1 ? 0 : 1);
				onBackPressed();
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}


	private void getMeetingById(String meetingId) {
		if(noNetworkToast()) return;
		pd.show();
		socketService.getMeetingById(meetingId);
	}

	private boolean noNetworkToast() {
		if (NetworkUtil
				.getConnectivityStatus(this) == 0) {

			Toast.makeText(this,
					getString(R.string.no_internet_connection),
					Toast.LENGTH_SHORT).show();

			return true;
		}
		return false;
	}

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		if (pd.isShowing()) {
			pd.dismiss();

			Toast.makeText(UserProfileActivity.this, message,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);

		if (arg0 == REQUEST_CODE_CHAT && arg1 == RESULT_CODE_BLOCKED) {

			user.setBlocked(1);
			onBackPressed();
		}
	}

	public void favouriteUser() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			MeehabApp.toast(getString(R.string.no_internet_connection));
			return;
		}
		if (socketService != null) {
			pd = UtilityClass.getProgressDialog(this);
			pd.show();
			JSONObject object = new JSONObject();

			try {

				object.put("user_id", AccountUtils.getUserId(this));
				object.put("friend_ids", user.getId());

				object.put("favorite", user.isFavourite() == 1 ? 0 : 1);

				Log.e("json send ", object.toString());
				socketService.favourteUser(object);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	public void blockUser() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			MeehabApp.toast(getString(R.string.no_internet_connection));
			return;
		}
		if (socketService != null) {
			pd = UtilityClass.getProgressDialog(this);

			JSONObject object = new JSONObject();

			try {
				pd.show();
				object.put("user_id", AccountUtils.getUserId(this));
				object.put("friend_ids", user.getId());
				object.put("block", user.isBlocked() == 1 ? 0 : 1);

				Log.e("json send ", object.toString());
				socketService.blockUser(object);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	public void fillContainer(LinearLayout linear, ArrayList<MyReview> list) {

		linear.removeAllViews();

		Collections.reverse(list);

		LayoutInflater layoutInflater = (LayoutInflater) linear.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		for (int i = 0; i < list.size(); i++) {
			View view = layoutInflater.inflate(R.layout.list_item_my_review,
					null);
			view.setTag(i);

			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					int id = (int) v.getTag();
					MyReview rev = reviews.get(id);
					rev.setUserId(user.getId() + "");
					Intent intent = new Intent(UserProfileActivity.this,
							MyReviewDetailActivity.class);
					intent.putExtra(MyReview.EXTRA_REVIEW, rev);
					startActivity(intent);
					overridePendingTransition(R.anim.activity_in,
							R.anim.activity_out);
				}
			});

			TextView tvReviewTitle = (TextView) view
					.findViewById(R.id.tvReviewTitle);
			RatingBar rating = (RatingBar) view.findViewById(R.id.rating);
			TextView tvMeetingName = (TextView) view
					.findViewById(R.id.tvMeetingName);
			TextView tvDateTime = (TextView) view.findViewById(R.id.tvDateTime);
			TextView tvComment = (TextView) view.findViewById(R.id.tvComment);

			final MyReview myReview = list.get(i);

			tvReviewTitle.setText(myReview.getReviewTitle());

			rating.setRating(myReview.getRating());

			tvMeetingName.setText(myReview.getMeetingName());
			tvMeetingName.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					getMeetingById("" + myReview.getMeetingId());
				}
			});
			tvDateTime.setText(DateTimeUtils.getDatetimeAdded(
					myReview.getDatetimeUpdated(), timeZoneOffest));

			tvComment.setText(myReview.getComment());

			linear.addView(view);

			View divider = new View(linear.getContext());
			divider.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 10));
			linear.addView(divider);
		}
	}

	@Override
	public void onBackPressed() {

		Intent i = new Intent();
		i.putExtra(EXTRA_USER_ACCOUNT, user);
		setResult(RESULT_OK, i);

		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

}
