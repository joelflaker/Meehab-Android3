package com.citrusbits.meehab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.model.MyReview;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.UtilityClass;

public class ReportFriendActivity extends SocketActivity implements
		OnSocketResponseListener, OnClickListener {

	public static final String TAG = ReportFriendActivity.class.getSimpleName();

	public static final String EXTRA_FRIEND = "friend";

	ImageView ivBack;
	ImageButton ibSend;
	EditText etReportText;
	UserAccount userAccount;

	ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_friend);
		etReportText = (EditText) findViewById(R.id.etReportText);
		ivBack = (ImageView) findViewById(R.id.ivBack);
		ibSend = (ImageButton) findViewById(R.id.ibSend);

		userAccount = (UserAccount) getIntent().getSerializableExtra(
				EXTRA_FRIEND);

		ivBack.setOnClickListener(this);
		ibSend.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ivBack:
			onBackPressed();
			break;
		case R.id.ibSend:
			String msg = etReportText.getText().toString().trim();
			if (msg.isEmpty()) {
				etReportText.setError("Please enter your message");
				return;
			}
			reportUser(msg);
			DeviceUtils.hideSoftKeyboard(this);
			break;
		case R.id.etReportText:

			break;
		}
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		// TODO Auto-generated method stub
		if (pd.isShowing()) {

			pd.dismiss();
		}

		if (event.equals(EventParams.METHOD_REPORT_USER)) {
			JSONObject data = ((JSONObject) obj);
            try {
				String message=data.getString("message");
				Log.e(TAG, data.toString());
				Toast.makeText(ReportFriendActivity.this, message,
						Toast.LENGTH_SHORT).show();
				ReportFriendActivity.this.onBackPressed();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}

	}

	@Override
	public void onSocketResponseFailure(String message) {
		// TODO Auto-generated method stub
		if (pd.isShowing()) {

			pd.dismiss();
		}
	}

	public void reportUser(String message) {
		if (socketService != null) {
			pd = UtilityClass.getProgressDialog(this);
			pd.show();
			JSONObject object = new JSONObject();

			try {
				object.put("user_id", AccountUtils.getUserId(this));
				object.put("friend_id", userAccount.getId());
				object.put("message", message);

				Log.e("json send ", object.toString());
				socketService.reportUser(object);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
