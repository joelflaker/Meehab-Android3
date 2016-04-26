package com.citrusbits.meehab;

import org.json.JSONObject;

import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class ProvideAppFeedBackActivity extends SocketActivity implements
		OnClickListener,
		OnSocketResponseListener {

	private Dialog pd;
	private EditText editFeedback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_provide_app_feed_back);

		pd = UtilityClass.getProgressDialog(this);
		// top back button
		findViewById(R.id.topMenuBtn).setOnClickListener(this);
		findViewById(R.id.ibSend).setOnClickListener(this);
		editFeedback = (EditText)findViewById(R.id.editFeedback);

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		if (event.equals(EventParams.EVENT_APP_FEED_BACK)) {
			pd.dismiss();
			App.toast("Thanks for your feedback!");
			finish();

		}
	}

	@Override
	public void onSocketResponseFailure(String onEvent,String message) {
		pd.dismiss();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topMenuBtn:
			onBackPressed();
			break;
		case R.id.ibSend:
			submitFeeback();
			break;
		}

	}

	/**
	 * feed back over application
	 */
	private void submitFeeback() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}

		if (socketService != null) {
			String feebackString = editFeedback.getText().toString();

			if (TextUtils.isEmpty(feebackString)) {
				App.toast("Write some feedback first!");
				return;
			}

			try {
				JSONObject params = new JSONObject();
				params.put("feedback", feebackString);

				pd.show();
				socketService.sendFeedBack(params);
			} catch (Exception e) {

			}
		}
	}

}
