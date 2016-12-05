package com.citrusbits.meehab;

import java.util.ArrayList;
import java.util.Collections;

import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.MeetingReviewModel;
import com.citrusbits.meehab.model.MyReview;
import com.citrusbits.meehab.utils.DateTimeUtils;
import com.citrusbits.meehab.utils.MeetingUtils;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;

public class ActivityMoreReviews extends Activity implements OnClickListener {
	public static final String KEY_MORE_REVIEW = "more_reviews";
	ArrayList<MeetingReviewModel> meetingReviewModels;

	private long timeZoneOffset;

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

			view.setTag(list.get(i));
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

			String userImage = Consts.SOCKET_URL + m.getImage();

			Log.e("User image is ", userImage);

			Picasso.with(ActivityMoreReviews.this).load(userImage)
					.placeholder(R.drawable.profile_pic).resize(80, 80)
					.error(R.drawable.profile_pic)
					.transform(new PicassoCircularTransform()).into(ivUserIcon);

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

}
