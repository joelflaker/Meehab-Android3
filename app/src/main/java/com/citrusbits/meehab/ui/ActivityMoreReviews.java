package com.citrusbits.meehab.ui;

import java.util.ArrayList;
import java.util.Collections;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.MeetingReviewModel;
import com.citrusbits.meehab.model.MyReview;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.ui.users.UserProfileActivity;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DateTimeUtils;
import com.citrusbits.meehab.utils.MeetingUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;

import org.json.JSONObject;

public class ActivityMoreReviews extends SocketActivity implements OnSocketResponseListener, OnClickListener {
	public static final String KEY_MORE_REVIEW = "more_reviews";
	ArrayList<MeetingReviewModel> meetingReviewModels;

	private long timeZoneOffset;
	private Dialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		timeZoneOffset = MeetingUtils.getTimeZoneOffset();
		setContentView(R.layout.activity_more_reviews);
		findViewById(R.id.btnBack).setOnClickListener(this);
		meetingReviewModels = (ArrayList<MeetingReviewModel>) getIntent()
				.getSerializableExtra(KEY_MORE_REVIEW);
		LinearLayout reviewsContainer = (LinearLayout) findViewById(R.id.reviewsContainer);
		Collections.reverse(meetingReviewModels);
		fillContainer(true, reviewsContainer, meetingReviewModels);

		pd = UtilityClass.getProgressDialog(this);

	}

	@Override
	public void onClick(View v) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
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

			MeetingReviewModel m = list.get(i);

			view.setTag(m);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					MeetingReviewModel rev = (MeetingReviewModel) v.getTag();
					Intent intent = new Intent(ActivityMoreReviews.this,
							MyReviewDetailActivity.class);
					intent.putExtra(MyReview.EXTRA_REVIEW, rev);
					startActivity(intent);

					overridePendingTransition(R.anim.activity_in,
							R.anim.activity_out);
				}
			});

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

					if(AccountUtils.getUserId(ActivityMoreReviews.this) == review.getUserId()) return;

//					Intent intent = new Intent(ActivityMoreReviews.this, MediaFullScreenActivity.class);
//					intent.putExtra(MediaFullScreenActivity.MEDIA_TYPE, MediaFullScreenActivity.TYPE_IMAGE);
//					intent.putExtra("link",  userImageUrl);
//					startActivity(intent);
//					overridePendingTransition(R.anim.bottom_in_instant,R.anim.abc_fade_out);
					pd.show();
					socketService.getUserById(review.getUserId());
				}
			});

			String userImage = m.getImage();

			Log.e("User image is ", ""+userImage);

			if(!TextUtils.isEmpty(userImage)){
				Picasso.with(ActivityMoreReviews.this).load(userImage)
						.placeholder(R.drawable.img_place_holder)
						.error(R.drawable.img_place_holder)
						.transform(new PicassoCircularTransform()).into(ivUserIcon);
			}/*else {
				Picasso.with(ActivityMoreReviews.this).load(R.drawable.img_place_holder).resize(R.dimen.review_image_size, R.dimen.review_image_size)
						.transform(new PicassoCircularTransform()).into(ivUserIcon);
			}*/


			tvReviewTitle.setText(m.getTitle());

			rating.setRating(m.getStars());

			tvMeetingName.setText(m.getUsername());

			String datetimeAdded = DateTimeUtils.getDatetimeAdded(
					m.getDatetimeAdded(), timeZoneOffset);

			tvDateTime.setText(datetimeAdded);

			tvComment.setText(m.getComments());

			linear.addView(view);
			View divider = new View(linear.getContext());
			divider.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 5));
			linear.addView(divider);

		}
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
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
		}
	}

	@Override
	public void onSocketResponseFailure(String event, String message) {
		if(EventParams.METHOD_USER_BY_ID.equals(event)){
			pd.dismiss();
		}
	}
}
