package com.citrusbits.meehab.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.citrusbits.meehab.EditMyProfileActivity;
import com.citrusbits.meehab.HomeActivity;
import com.citrusbits.meehab.MyReviewDetailActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.UserProfileActivity;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.helpers.AgeHelper;
import com.citrusbits.meehab.images.PicassoBlurTransform;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.model.MyReview;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DateTimeUtils;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.UtilityClass;
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

	private ProgressDialog pd;

	private ImageView ivBlurBg;

	private ImageButton ibSeeMore;

	public MyProfileFragment() {
	}

	public MyProfileFragment(HomeActivity homeActivity) {
		this.homeActivity = homeActivity;
		pd = UtilityClass.getProgressDialog(homeActivity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDatasource = new UserDatasource(getActivity());
		userDatasource.open();

		user = userDatasource.findUser(AccountUtils.getUserId(getActivity()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_my_profile, container,
				false);

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

		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) ivBlurBg
				.getLayoutParams();
		int width = DeviceUtils.getDeviceWidth(getActivity());
		params.height = (int) (width * 0.82f);
		ivBlurBg.setLayoutParams(params);

		// init meeting adapter

		getUserReviews();

		return v;
	}

	private void resetUserInfo() {
		if (user != null) {

			if (user.getImage() != null) {
				/*
				 * profileNetworkImageView.setImageUrl(getString(R.string.url) +
				 * user.getImage(), App.getInstance().getImageLoader());
				 */
				String userImage = getString(R.string.url) + user.getImage();

				Picasso.with(getActivity()).load(userImage)
						.placeholder(R.drawable.profile_pic).resize(100, 100)
						.error(R.drawable.profile_pic)
						.transform(new PicassoCircularTransform())
						.into(ivUserIcon);

				Picasso.with(getActivity())
						.load(userImage)
						.placeholder(R.drawable.profile_pic_big)
						// .resize(300, 200)
						.error(R.drawable.profile_pic_big)
						.transform(new PicassoBlurTransform(getActivity(), 20))
						.into(ivBlurBg);
			}

			usernameText.setText(user.getUsername());

			tvAge.setText(AgeHelper.calculateAge(user.getDateOfBirth()) + "");
			tvGender.setText(user.getGender());
			tvOriendation.setText(user.getSexualOrientation());
			tvMaritalStatus.setText(user.getMaritalStatus());

			sponsorText.setText(user.getWillingSponsor());
			heightText.setText(user.getHeight());
			weightText.setText(user.getWeight());
			ethnicityText.setText(user.getEthnicity());
			occupationText.setText(user.getAccupation());

			if ("both".toLowerCase().toString()
					.equals(user.getIntrestedIn().toLowerCase())) {
				interestedInText.setText(R.string.dating_and_fellowshiping);
			} else {
				interestedInText.setText(user.getIntrestedIn());
			}
			kidsText.setText(user.getHaveKids());
			// homegroupText.setText(user.getWillingSponsor())
			homegroupText.setText(user.getMeetingHomeGroup());

			String aaStoryTxt = user.getAboutStory();
			if (aaStoryTxt.length() > 100) {
				aaStoryText.setText(aaStoryTxt.substring(0, 100));
				ibSeeMore.setVisibility(View.VISIBLE);
			} else {
				aaStoryText.setText(aaStoryTxt);
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
		userDatasource.open();
		user = userDatasource.findUser(AccountUtils.getUserId(getActivity()));
		resetUserInfo();
	}

	@Override
	public void onPause() {
		super.onPause();
		userDatasource.close();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onBackendConnected() {
		// TODO Auto-generated method stub
		getUserReviews();
	}

	public void getUserReviews() {
		if (homeActivity.socketService != null) {
			pd.show();
			JSONObject object = new JSONObject();

			try {
				object.put("user_id", user.getId());
				Log.e("json send ", object.toString());
				homeActivity.socketService.getUserReviews(object);
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

					String meetingName = reviewObject.getString("username");
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

			fillContainer(reviewsContainer, reviews);

		}

	}

	@Override
	public void onSocketResponseFailure(String message) {
		// TODO Auto-generated method stub

	}

	public void fillContainer(LinearLayout linear, ArrayList<MyReview> list) {

		linear.removeAllViews();

		Collections.reverse(list);

		LayoutInflater layoutInflater = (LayoutInflater) linear.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		for (int i = 0; i < list.size(); i++) {
			View view = layoutInflater.inflate(R.layout.list_item_my_review,
					null);
			view.setId(i);

			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					int id = v.getId();
					MyReview rev = reviews.get(id);
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

			tvDateTime.setText(DateTimeUtils.getDatetimeAdded(myReview
					.getDateTimeAdded()));

			tvComment.setText(myReview.getComment());

			linear.addView(view);

			View divider = new View(linear.getContext());
			divider.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 10));
			linear.addView(divider);
		}
	}

}
