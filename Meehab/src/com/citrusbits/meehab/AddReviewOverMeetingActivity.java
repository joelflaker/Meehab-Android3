package com.citrusbits.meehab;

import org.json.JSONObject;

import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DateTimeUtils;
import com.citrusbits.meehab.utils.UtilityClass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class AddReviewOverMeetingActivity extends SocketActivity implements
		OnSocketResponseListener {

	private MeetingModel meeting;
	private RatingBar rating;
	private TextView tvMeetingDate;
	private TextView tvMeetingTime;
	private EditText etReviewtitle;
	private EditText etReviewComment;
	private ProgressDialog pd;
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_review_over_meeting);

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
						postReview();

					}
				});
		pd = UtilityClass.getProgressDialog(this);
		rating = (RatingBar) findViewById(R.id.rating);
		tvMeetingDate = (TextView) findViewById(R.id.tvMeetingDate);
		tvMeetingTime = (TextView) findViewById(R.id.tvMeetingTime);
		etReviewtitle = (EditText) findViewById(R.id.etReviewTitle);
		etReviewComment = (EditText) findViewById(R.id.etReviewComments);

		Bundle extra = getIntent().getExtras();
		if (extra != null) {
			meeting = (MeetingModel) extra.getSerializable("meeting");
			String dateTimeAdded=DateTimeUtils.getDatetimeAdded(meeting.getDatetimeAdded());
			String dateTime[]=dateTimeAdded.split("@");
			tvMeetingDate.setText(dateTime[0]);
			tvMeetingTime.setText(dateTime[1]);
		}
	}

	/**
	 * 
	 */
	protected void postReview() {
		if (socketService != null) {
			String titleString = etReviewtitle.getText().toString();
			String commentsString = etReviewComment.getText().toString();

			if (TextUtils.isEmpty(titleString)
					|| TextUtils.isEmpty(commentsString)) {
				App.alert("Title and comment required!");
				return;
			}

			int stars = (int) rating.getRating();
			try {
				JSONObject params = new JSONObject();
				params.put("userid", "" + AccountUtils.getUserId(this));
				params.put("meetingid", String.valueOf(meeting.getMeetingId()));
				params.put("stars", "" + stars);
				params.put("title", titleString);
				params.put("comments", commentsString);

				pd.show();
				socketService.addReview(params);
			} catch (Exception e) {

			}
		}
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		if (event.equals(EventParams.EVENT_MEETING_ADD_REVIEW)) {
			pd.dismiss();
			App.alert("Review successfully Added!");
			finish();

		}
	}

	@Override
	public void onSocketResponseFailure(String message) {
		pd.dismiss();
	}

}
