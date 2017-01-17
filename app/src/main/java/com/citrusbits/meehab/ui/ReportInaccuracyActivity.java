package com.citrusbits.meehab.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.ui.dialog.MessageDialog;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ReportInaccuracyActivity extends SocketActivity implements
		OnSocketResponseListener, OnBackendConnectListener {
	public static final String KEY_MEETING_ID = "meeting_id";
	public static final String KEY_REHAB_ID = "rehab_id";

	int reportId;

	EditText editFeedback;
	private Dialog pd;
	private boolean isMeetingId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_inaccuracy);
		editFeedback = (EditText) findViewById(R.id.editFeedback);
		
		if(getIntent().hasExtra(KEY_REHAB_ID)){
			isMeetingId = false;
			reportId = getIntent().getIntExtra(KEY_REHAB_ID, -1);
			App.toast("Reporting Rehab");
		}else{
			isMeetingId = true;
			reportId = getIntent().getIntExtra(KEY_MEETING_ID, -1);
		}
		pd = UtilityClass.getProgressDialog(this);

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
						String feedback = editFeedback.getText().toString()
								.trim();
						if (feedback.isEmpty()) {

							Toast.makeText(ReportInaccuracyActivity.this,
									"Please enter report! ", Toast.LENGTH_SHORT)
									.show();

							return;
						}

						if (NetworkUtil
								.getConnectivityStatus(ReportInaccuracyActivity.this) == 0) {

							Toast.makeText(ReportInaccuracyActivity.this,
									getString(R.string.no_internet_connection),
									Toast.LENGTH_SHORT).show();

							return;
						}

						reportMeetings(feedback);

					}
				});

	}

	public void reportMeetings(String message) {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}
		try {
			
			JSONObject params = new JSONObject();
			params.put("comments", message);
			if(isMeetingId){
				params.put("userid", AccountUtils.getUserId(this));
				params.put("meetingid", reportId);
				socketService.reportMeeting(params);
			}else{
				params.put("rehab_id", reportId);
				socketService.reportRehab(params);
			}
			
			Log.e("Json made to send ", params.toString());

			pd.show();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		if (pd != null) {
			pd.dismiss();
		}
		JSONObject json = (JSONObject) obj;
		Log.e(ReportInaccuracyActivity.class.getCanonicalName(), json.toString());

		if(event.equals(EventParams.EVENT_REHAB_REPORT) || event.equals(EventParams.METHOD_REPORT_MEETING)) {
			new MessageDialog(this, R.string.thanks_for_reporting)
					.setDialogClickListener(new MessageDialog.MessageDialogClickListener() {
						@Override
						public void onOkClick(MessageDialog dialog) {
							dialog.dismiss();
							onBackPressed();
						}
					}).show();
		}
	}

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		if (pd != null) {
			pd.dismiss();
			Toast.makeText(ReportInaccuracyActivity.this, message,
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onBackendConnected() {

	}

}
