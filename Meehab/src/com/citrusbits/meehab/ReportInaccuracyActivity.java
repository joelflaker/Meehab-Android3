package com.citrusbits.meehab;

import org.json.JSONException;
import org.json.JSONObject;

import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.services.SocketService;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ReportInaccuracyActivity extends SocketActivity implements
		OnSocketResponseListener, OnBackendConnectListener {
	public static final String KEY_MEETING_ID = "meeting_id";

	int meetingId;

	EditText editFeedback;
	ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_inaccuracy);
		editFeedback = (EditText) findViewById(R.id.editFeedback);
		meetingId = getIntent().getIntExtra(KEY_MEETING_ID, -1);
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
			App.alert(getString(R.string.no_internet_connection));
			return;
		}
		try {
			
			JSONObject params = new JSONObject();
			params.put("meetingid", meetingId);
			params.put("userid",
					AccountUtils.getUserId(ReportInaccuracyActivity.this));
			params.put("comments", message);

			Log.e("Json made to send ", params.toString());

			socketService.reportMeeting(params);
			pd.show();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
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
		// TODO Auto-generated method stub
		if (pd != null) {
			pd.dismiss();
		}
		JSONObject json = (JSONObject) obj;
		Log.e("Json", json.toString());

		try {

			Toast.makeText(ReportInaccuracyActivity.this,
					json.getString("message"), Toast.LENGTH_SHORT).show();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		onBackPressed();
	}

	@Override
	public void onSocketResponseFailure(String message) {
		// TODO Auto-generated method stub
		if (pd != null) {
			pd.dismiss();
			Toast.makeText(ReportInaccuracyActivity.this, message,
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onBackendConnected() {
		// TODO Auto-generated method stub

	}

}
