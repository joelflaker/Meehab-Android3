package com.citrusbits.meehab;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.dialog.DeleteReviewConfirmDialog;
import com.citrusbits.meehab.dialog.DeleteReviewConfirmDialog.DeleteReviewConfirmDialogClickListener;
import com.citrusbits.meehab.model.MeetingReviewModel;
import com.citrusbits.meehab.model.MyReview;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DateTimeUtils;
import com.citrusbits.meehab.utils.UtilityClass;

public class MyReviewDetailActivity extends SocketActivity implements
		OnSocketResponseListener, OnBackendConnectListener {

	public static final String TAG = MyReviewDetailActivity.class
			.getSimpleName();

	private ProgressDialog pd;

	Object object;
	MeetingReviewModel reviewModel;
	MyReview myReview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pd = UtilityClass.getProgressDialog(MyReviewDetailActivity.this);
		setContentView(R.layout.activity_my_review_detail);
		object = (Object) getIntent().getSerializableExtra(
				MyReview.EXTRA_REVIEW);

		TextView tvReviewTitle = (TextView) this
				.findViewById(R.id.tvReviewTitle);
		RatingBar rating = (RatingBar) this.findViewById(R.id.rating);
		TextView tvMeetingName = (TextView) this
				.findViewById(R.id.tvMeetingName);
		TextView tvDateTime = (TextView) this.findViewById(R.id.tvDateTime);
		TextView tvComment = (TextView) this.findViewById(R.id.tvComment);

		if (object instanceof MyReview) {
			myReview = (MyReview) object;
			tvReviewTitle.setText(myReview.getReviewTitle());

			rating.setRating(myReview.getRating());

			tvMeetingName.setText(myReview.getMeetingName());
			tvDateTime.setText(DateTimeUtils.getDatetimeAdded(myReview
					.getDateTimeAdded()));

			tvComment.setText(myReview.getComment());
		} else if (object instanceof MeetingReviewModel) {
			reviewModel = (MeetingReviewModel) object;
			tvReviewTitle.setText(reviewModel.getTitle());

			rating.setRating(reviewModel.getStars());

			tvMeetingName.setText(reviewModel.getUsername());
			tvDateTime.setText(DateTimeUtils.getDatetimeAdded(reviewModel
					.getDatetimeAdded()));

			tvComment.setText(reviewModel.getComments());
			String commentUserId = reviewModel.getUserId() + "";
			String userId = AccountUtils.getUserId(this) + "";

			Button topRightBtn = (Button) findViewById(R.id.topRightBtn);

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
												// TODO Auto-generated method
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
												// TODO Auto-generated method
												// stub
												dialog.dismiss();

											}
										}).show();
					}
				});
	}

	public void deleteUserReview(String reviewId) {
		if (socketService != null) {
			pd.show();
			JSONObject object = new JSONObject();

			try {
				object.put("user_id", AccountUtils.getUserId(this));
				object.put("review_id", reviewId);
				socketService.deleteUserReviews(object);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onBackendConnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		// TODO Auto-generated method stub
		if (pd.isShowing()) {

			pd.dismiss();
		}

		if (event.equals(EventParams.EVENT_DELETE_USER_REVIEW)) {
			JSONObject data = ((JSONObject) obj);

			Log.e(TAG, data.toString());
			try {
				Toast.makeText(MyReviewDetailActivity.this,
						data.getString("message"), Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MyReviewDetailActivity.this.finish();

		}

	}

	@Override
	public void onSocketResponseFailure(String message) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
	}

}
