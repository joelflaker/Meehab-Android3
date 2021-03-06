package com.citrusbits.meehab.ui.fragments;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.map.LocationUtils;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.model.NearestDateTime;
import com.citrusbits.meehab.ui.meetings.MeetingDetailsActivity;
import com.citrusbits.meehab.ui.users.EditMyProfileActivity;
import com.citrusbits.meehab.ui.HomeActivity;
import com.citrusbits.meehab.ui.MyReviewDetailActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.app.MeehabApp;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.helpers.AgeHelper;
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
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.RecoverClockDateUtils;
import com.citrusbits.meehab.utils.TimestampUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class MyProfileFragment extends Fragment implements
		OnSocketResponseListener, OnBackendConnectListener, OnClickListener {

	public static final String TAG = MyProfileFragment.class.getSimpleName();

	private UserDatasource userDatasource;
	private UserAccount user;
	private TextView usernameText, interestedInText, ethnicityText,
			sponsorText, heightText, aaStoryText;
	private ImageView ivUserIcon;
	private LinearLayout reviewsContainer;
	private TextView weightText;
	private TextView occupationText;
	private TextView kidsText;
	private TextView homegroupText;
	private TextView SoberDateText;
	private ArrayList<MyReview> reviews = new ArrayList<MyReview>();

	private HomeActivity homeActivity;

	private TextView tvAge;
	private TextView tvGender;
	private TextView tvOriendation;
	private TextView tvMaritalStatus;

	private Dialog pd;

	private ImageView ivBlurBg;

	private ImageButton ibSeeMore;
	
	private long timeZone;

	public MyProfileFragment() {
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDatasource = new UserDatasource(getActivity());
		this.homeActivity = (HomeActivity) getActivity();
		pd = UtilityClass.getProgressDialog(homeActivity);

		user = userDatasource.findUser(AccountUtils.getUserId(getActivity()));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		homeActivity = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_my_profile, container,
				false);
		
		timeZone = TimestampUtils.getTimeZoneOffset();

		v.findViewById(R.id.topMenuBtn).setOnClickListener(this);
		v.findViewById(R.id.topRightBtn).setOnClickListener(this);
		ivUserIcon = (ImageView) v.findViewById(R.id.ivUserIcon);
		ibSeeMore = (ImageButton) v.findViewById(R.id.ibSeeMore);
		ibSeeMore.setOnClickListener(this);
		usernameText = (TextView) v.findViewById(R.id.userNameText);

		sponsorText = (TextView) v.findViewById(R.id.sponsorText);

		heightText = (TextView) v.findViewById(R.id.heightText);
		weightText = (TextView) v.findViewById(R.id.weightText);
		ethnicityText = (TextView) v.findViewById(R.id.ethnicityText);
		occupationText = (TextView) v.findViewById(R.id.occupationText);
		interestedInText = (TextView) v.findViewById(R.id.interestedInText);
		kidsText = (TextView) v.findViewById(R.id.kidsText);
		homegroupText = (TextView) v.findViewById(R.id.homegroupText);
		SoberDateText = (TextView) v.findViewById(R.id.SoberDateText);
		aaStoryText = (TextView) v.findViewById(R.id.aaStoryText);
		ivBlurBg = (ImageView) v.findViewById(R.id.ivBlurBg);

		reviewsContainer = (LinearLayout) v.findViewById(R.id.reviewsContainer);

		tvAge = (TextView) v.findViewById(R.id.tvAge);
		tvGender = (TextView) v.findViewById(R.id.tvGender);
		tvOriendation = (TextView) v.findViewById(R.id.tvOriendation);
		tvMaritalStatus = (TextView) v.findViewById(R.id.tvMaritalStatus);

		// init meeting adapter
		getUserReviews();

		return v;
	}

	private void resetUserInfo() {
		if (user != null) {

			Picasso.with(getActivity())
					.load(R.drawable.img_place_holder)
					.transform(new PicassoBlurTransform(getActivity(), Consts.IMAGE_BLURR_RADIUS))
					.into(ivBlurBg);

			if (!TextUtils.isEmpty(user.getImage())) {
				/*
				 * profileNetworkImageView.setImageUrl(getString(R.string.url) +
				 * user.getImage(), MeehabApp.getInstance().getImageLoader());
				 */
				String userImage = user.getImage();

				Picasso.with(getActivity()).load(userImage)
						.placeholder(R.drawable.img_place_holder)
						.error(R.drawable.img_place_holder)
						.transform(new PicassoCircularTransform())
						.into(ivUserIcon);

				Picasso.with(getActivity())
						.load(userImage)
						.placeholder(R.drawable.img_place_holder)
						.error(R.drawable.img_place_holder)
						.transform(new PicassoBlurTransform(getActivity(), Consts.IMAGE_BLURR_RADIUS))
						.into(ivBlurBg);
			}

			usernameText.setText(""+user.getUsername());

			if(!TextUtils.isEmpty(user.getDateOfBirth())){
				int yearsOld = AgeHelper.calculateAge(user.getDateOfBirth());
				if(yearsOld > 0) {
					tvAge.setText("" + yearsOld + " " + getString(R.string.label_years));
					getView().findViewById(R.id.tvLine1).setVisibility(View.VISIBLE);
				}else {
					tvAge.setText("");
					getView().findViewById(R.id.tvLine1).setVisibility(View.GONE);
				}
			}else {
				tvAge.setText("");
				getView().findViewById(R.id.tvLine1).setVisibility(View.GONE);
			}
			tvGender.setText(""+user.getGender());
			if(!TextUtils.isEmpty(user.getSexualOrientation())){
				tvOriendation.setText(user.getSexualOrientation());
				getView().findViewById(R.id.tvLine2).setVisibility(View.VISIBLE);
			}else {
				tvOriendation.setText("");
				getView().findViewById(R.id.tvLine2).setVisibility(View.GONE);
			}
			if(!TextUtils.isEmpty(user.getMaritalStatus())){
				tvMaritalStatus.setText(user.getMaritalStatus());
				getView().findViewById(R.id.tvLine3).setVisibility(View.VISIBLE);
			}else {
				tvMaritalStatus.setText("");
				getView().findViewById(R.id.tvLine3).setVisibility(View.GONE);
			}

			if(user.getWillingSponsor().equalsIgnoreCase("yes")){
				sponsorText.setText("Willing to Sponsor");
				getView().findViewById(R.id.llNameContainer).setBackgroundResource(R.drawable.name_bg);
			}else{
				sponsorText.setText("Not Willing to Sponsor");
				getView().findViewById(R.id.llNameContainer).setBackgroundResource(R.drawable.name_bg_gray);
//				sponsorText.setText("");
			}
			heightText.setText(user.getHeight() == null? "" : user.getHeight());
			weightText.setText(user.getWeight() == null? "" : user.getWeight());
			ethnicityText.setText(user.getEthnicity() == null? "" : user.getEthnicity());
			occupationText.setText(user.getAccupation() == null? "" : user.getAccupation());

			SoberDateText.setText(""+RecoverClockDateUtils.getSoberDifference(
					user.getSoberSence(), true, getActivity()));

			if (TextUtils.isEmpty(user.getIntrestedIn())){
				interestedInText.setText("Interested in Nothing!");
			} else if ("both".toString().equalsIgnoreCase(user.getIntrestedIn())) {
				interestedInText.setText(R.string.dating_and_fellowshiping);
			}else{
				interestedInText.setText(""+user.getIntrestedIn());
			}

			if(user.getHaveKids().equalsIgnoreCase("yes")){
				kidsText.setText("Yes");
			}else if (user.getHaveKids().equalsIgnoreCase("no")){
				kidsText.setText("No");
			}else {
				kidsText.setText("No Answer");
			}

			homegroupText.setText(TextUtils.isEmpty(user.getMeetingHomeGroup()) ? getString(R.string.label_not_assign) : user.getMeetingHomeGroup());

			String aaStoryTxt = user.getAboutStory();
			if (!TextUtils.isEmpty(aaStoryTxt) && aaStoryTxt.length() > 100) {
				aaStoryText.setText(aaStoryTxt.substring(0, 100).trim());
				ibSeeMore.setVisibility(View.VISIBLE);
			} else {
				aaStoryText.setText(aaStoryTxt == null? "" : aaStoryTxt.trim());
				ibSeeMore.setVisibility(View.GONE);
			}

		}
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
			Intent intent = new Intent(getActivity(),
					EditMyProfileActivity.class);
			// put user id
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
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
		super.onResume();
		user = userDatasource.findUser(AccountUtils.getUserId(getActivity()));
		resetUserInfo();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onBackendConnected() {
		getUserReviews();
	}

	public void getUserReviews() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			MeehabApp.toast(getString(R.string.no_internet_connection));
			return;
		}
		if (homeActivity.socketService != null) {
			pd.show();
			JSONObject object = new JSONObject();

			try {
				object.put("user_id", user.getId());
				Log.e("json send ", object.toString());
				homeActivity.socketService.getUserReviews(object);
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

		if (event.equals(EventParams.METHOD_MEETING_BY_ID)) {
			JSONObject data = ((JSONObject) obj);
			Log.d(TAG,""+data);
			MeetingModel meeting = new Gson().fromJson(data.optJSONObject("meeting").toString(), MeetingModel.class);

			meeting.setFavourite(meeting.getFavouriteMeeting() == 1);
			NearestDateTime nearDateTime = MeetingUtils.getNearestDate(meeting.getOnDay(),
					meeting.getOnTime());

			Location myLocation = LocationUtils.getLastLocation(getContext());
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
			//navigate to meeting details
			Intent intent = new Intent(getActivity(), MeetingDetailsActivity.class);
			intent.putExtra("meeting",meeting);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
		}else
		if (event.equals(EventParams.EVENT_GET_USER_REVIEWS)) {
			reviews.clear();
			JSONObject data = ((JSONObject) obj);

			Log.e(TAG, data.toString());

			try {
				JSONArray userReviews = data.getJSONArray("getUserReviews");

				for (int i = 0; i < userReviews.length(); i++) {

					JSONObject reviewObject = userReviews.getJSONObject(i);

					String comment = reviewObject.optString("comments");

					String meetingName = reviewObject.optString("meeting_name");
					String image = reviewObject.optString("image");
					String meetingId = reviewObject.optString("meetingID");
					int rating = reviewObject.optInt("stars");
					int reviewId = reviewObject.optInt("id");
					String reviewTitle = reviewObject.optString("title");

					String dateTimeAdded = reviewObject
							.optString("datetime_added");

					MyReview myReview = new MyReview();
					myReview.setDatetimeUpdated(dateTimeAdded);
					myReview.setComment(comment);
					myReview.setImage(image);
					myReview.setMeetingId(meetingId);
					myReview.setMeetingName(meetingName);
					myReview.setRating(rating);
					myReview.setReviewId(reviewId);
					myReview.setReviewTitle(reviewTitle);
					reviews.add(myReview);
				}

				reviewsContainer.removeAllViews();
				fillContainer(reviewsContainer, reviews);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		pd.dismiss();
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
					Intent intent = new Intent(getActivity(),
							MyReviewDetailActivity.class);
					intent.putExtra(MyReview.EXTRA_REVIEW, rev);
					startActivity(intent);
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

			tvMeetingName.setTag(myReview);
			tvMeetingName.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					MyReview rev = (MyReview) v.getTag();
					getMeetingById(rev.getMeetingId());
				}
			});

			tvDateTime.setText(DateTimeUtils.getDatetimeReview(myReview
					.getDatetimeUpdated(),timeZone));

			tvComment.setText(myReview.getComment());

			linear.addView(view);

			View divider = new View(linear.getContext());
			divider.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 10));
			linear.addView(divider);
		}
	}

	private void getMeetingById(String meetingId) {
		if (NetworkUtil
				.getConnectivityStatus(homeActivity) == 0) {

			Toast.makeText(getContext(),
					getString(R.string.no_internet_connection),
					Toast.LENGTH_SHORT).show();

			return;
		}
		pd.show();
		homeActivity.socketService.getMeetingById(meetingId);
	}

}
