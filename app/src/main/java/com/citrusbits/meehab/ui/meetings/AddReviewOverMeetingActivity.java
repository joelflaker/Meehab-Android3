package com.citrusbits.meehab.ui.meetings;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONObject;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.ui.SocketActivity;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.MeetingUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
	private Dialog pd;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
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
			String dateTimeAdded = getCurrentDateTime();
			String dateTime[] = dateTimeAdded.split("@");
			tvMeetingDate.setText(dateTime[0]);
			tvMeetingTime.setText(dateTime[1]);
		}
	}

	public String getCurrentDateTime() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat newDatetime = new SimpleDateFormat(
				"MMM dd, yyyy @ hh:mm a");

		return newDatetime.format(calendar.getTime());

	}

	/**
	 * 
	 */
	protected void postReview() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}

		if (socketService != null) {
			String titleString = etReviewtitle.getText().toString();
			String commentsString = etReviewComment.getText().toString();

			if (TextUtils.isEmpty(titleString)
					|| TextUtils.isEmpty(commentsString)) {
				App.toast("Title and comment required!");
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
			App.toast("Review successfully Added!");
			finish();

		}
	}

	@Override
	public void onSocketResponseFailure(String onEvent,String message) {
		pd.dismiss();
	}

}
