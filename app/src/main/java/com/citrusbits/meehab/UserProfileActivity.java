package com.citrusbits.meehab;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.dialog.BlockFrindConfirmationDialog;
import com.citrusbits.meehab.dialog.BlockFrindConfirmationDialog.BlockFrindConfirmationDialogClickListener;
import com.citrusbits.meehab.dialog.BlockUserDialog;
import com.citrusbits.meehab.dialog.BlockUserDialog.BlockUserDialogClickListener;
import com.citrusbits.meehab.fragments.MyProfileFragment;
import com.citrusbits.meehab.images.PicassoBlurTransform;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.MyReview;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DateTimeUtils;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.MeetingUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.RecoverClockDateUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.squareup.picasso.Picasso;

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
		// TODO Auto-generated method stub
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

		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) ivBlurBg
				.getLayoutParams();
		int width = DeviceUtils.getDeviceWidth(this);
		params.height = (int) (width * 0.82f);
		ivBlurBg.setLayoutParams(params);
		llSendMessage.setOnClickListener(this);

		// init meeting adapter

		// Log.e("Date of Birth ", user.getDateOfBirth() + " Date Of Birth");

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

			if (user.getImage() != null) {
				/*
				 * profileNetworkImageView.setImageUrl(getString(R.string.url) +
				 * user.getImage(), App.getInstance().getImageLoader());
				 */
				String userImage = getString(R.string.url) + user.getImage();

				Picasso.with(this).load(userImage)
						.placeholder(R.drawable.profile_pic).resize(100, 100)
						.error(R.drawable.profile_pic)
						.transform(new PicassoCircularTransform())
						.into(ivUserIcon);

				Picasso.with(this)
						.load(userImage)
						.placeholder(R.drawable.profile_pic_big)
						// .resize(300, 200)
						.error(R.drawable.profile_pic_big)
						.transform(
								new PicassoBlurTransform(
										UserProfileActivity.this, 20))
						.into(ivBlurBg);
			}

			TextView tvProfileHeading = (TextView) findViewById(R.id.tvProfileHeading);

			tvProfileHeading.setText(String.format(
					this.getString(R.string.friend_profile_heading),
					user.getUsername()));

			usernameText.setText(user.getUsername());
			// String ageGenderEtcString = "24";

			tvAge.setText(String.valueOf(user.getAge()));
			tvGender.setText(user.getGender());
			tvOriendation.setText(user.getSexualOrientation());
			tvMaritalStatus.setText(user.getMaritalStatus());

			heightText.setText(user.getHeight());
			weightText.setText(user.getWeight());
			ethnicityText.setText(user.getEthnicity());
			occupationText.setText(user.getAccupation());

			if ("Choose Not to Answer".toLowerCase().toString()
					.equals(user.getIntrestedIn().toLowerCase())) {
				interestedInText.setText(R.string.dating_and_fellowshiping);
			} else {
				interestedInText.setText(user.getIntrestedIn());
			}
			kidsText.setText(user.getHaveKids());
			// homegroupText.setText(user.getWillingSponsor())
			homegroupText.setText(user.getMeetingHomeGroup());
			aaStoryText.setText(user.getAboutStory());

			String aaStoryTxt = user.getAboutStory();
			if (aaStoryTxt.length() > 100) {
				aaStoryText.setText(aaStoryTxt.substring(0, 100));
				ibSeeMore.setVisibility(View.VISIBLE);
			} else {
				aaStoryText.setText(aaStoryTxt);
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
									// TODO Auto-generated method stub
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
									// TODO Auto-generated method stub
									dialog.dismiss();
								}

								@Override
								public void onBlockUser(BlockUserDialog dialog) {
									// TODO Auto-generated method stub
									dialog.dismiss();

									new BlockFrindConfirmationDialog(
											UserProfileActivity.this)
											.setBlockFrindConfirmationDialogClickListener(
													new BlockFrindConfirmationDialogClickListener() {

														@Override
														public void onYesClick(
																BlockFrindConfirmationDialog dialog) {
															// TODO
															// Auto-generated
															// method stub
															dialog.dismiss();
															blockUser();
														}

														@Override
														public void onNoClick(
																BlockFrindConfirmationDialog dialog) {
															// TODO
															// Auto-generated
															// method stub
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
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onBackendConnected() {
		// TODO Auto-generated method stub
		getUserReviews();
	}

	public void getUserReviews() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.toast(getString(R.string.no_internet_connection));
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		// TODO Auto-generated method stub
		if (pd.isShowing()) {

			pd.dismiss();
		}

		reviews.clear();

		if (event.equals(EventParams.EVENT_GET_USER_REVIEWS)) {
			JSONObject data = ((JSONObject) obj);

			Log.e(TAG, data.toString());

			try {
				JSONArray userReviews = data.getJSONArray("getUserReviews");

				for (int i = 0; i < userReviews.length(); i++) {

					JSONObject reviewObject = userReviews.getJSONObject(i);

					String comment = reviewObject.getString("comments");
					String onDate = reviewObject.getString("on_date");
					String onTime = reviewObject.getString("on_time");

					String meetingName = reviewObject.getString("meeting_name");
					int rating = reviewObject.getInt("stars");
					int reviewId = reviewObject.getInt("id");
					String reviewTitle = reviewObject.getString("title");

					String dateTimeAdded = reviewObject
							.getString("datetime_added");

					MyReview myReview = new MyReview();
					myReview.setDateTimeAdded(dateTimeAdded);
					myReview.setComment(comment);
					myReview.setOnDate(onDate);
					myReview.setOnTime(onTime);
					myReview.setMeetingName(meetingName);
					myReview.setRating(rating);
					myReview.setReviewId(reviewId);
					myReview.setReviewTitle(reviewTitle);
					reviews.add(myReview);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
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
				// TODO Auto-generated catch block
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
							"User added to favourite!", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(UserProfileActivity.this,
							"User removed from favourite!", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);

		if (arg0 == REQUEST_CODE_CHAT && arg1 == RESULT_CODE_BLOCKED) {

			user.setBlocked(1);
			onBackPressed();
		}
	}

	public void favouriteUser() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}
		if (socketService != null) {
			pd = UtilityClass.getProgressDialog(this);
			pd.show();
			JSONObject object = new JSONObject();

			try {

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
			App.toast(getString(R.string.no_internet_connection));
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

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		if (pd.isShowing()) {
			pd.dismiss();

			Toast.makeText(UserProfileActivity.this, message,
					Toast.LENGTH_SHORT).show();
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

			MyReview myReview = list.get(i);

			tvReviewTitle.setText(myReview.getReviewTitle());

			rating.setRating(myReview.getRating());

			tvMeetingName.setText(myReview.getMeetingName());

			tvDateTime.setText(DateTimeUtils.getDatetimeAdded(
					myReview.getDateTimeAdded(), timeZoneOffest));

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
