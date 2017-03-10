package com.citrusbits.meehab.ui.users;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.app.MeehabApp;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.ui.dialog.MessageDialog;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.ui.SocketActivity;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;

public class ReportFriendActivity extends SocketActivity implements
		OnSocketResponseListener, OnClickListener {

	public static final String TAG = ReportFriendActivity.class.getSimpleName();

	public static final String KEY_FRIEND_ID="friend_id";

	ImageView ivBack;
	ImageButton ibSend;
	EditText etReportText;
	int friendId;

	private Dialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_friend);
		etReportText = (EditText) findViewById(R.id.etReportText);
//        final TextView txtReportText = (TextView) findViewById(R.id.txtReportText);
		ibSend = (ImageButton) findViewById(R.id.ibSend);
		ibSend.setAlpha(0.3f);
		
		etReportText.addTextChangedListener(new TextWatcher() {

            public boolean insideChange;

            @Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text=etReportText.getText().toString().trim();
				if(text.length()==0||text.length()==1){
					ibSend.setAlpha(text.length()==0?0.3f:1f);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}
			
			@Override
			public void afterTextChanged(Editable s) {
//                txtReportText.setText(s.toString());
//                if(insideChange) return;
//                if(s.length() > 0) {
//                    insideChange = true;
//                    if (s.length() == 1) {
//                        etReportText.setText(String.valueOf(s.toString().charAt(0)).toUpperCase());
//                    } else if (s.length() > 1) {
//                        etReportText.setText(String.valueOf(s.toString().charAt(0)).toUpperCase()+ s.toString().substring(1,s.length() -1));
//                    }
//                    insideChange = false;
//                }
			}
		});
//        etReportText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
		ivBack = (ImageView) findViewById(R.id.ivBack);
		

		friendId=getIntent().getIntExtra(KEY_FRIEND_ID, -1);

		ivBack.setOnClickListener(this);
		ibSend.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			onBackPressed();
			break;
		case R.id.ibSend:
			String msg = etReportText.getText().toString().trim();
			if (msg.isEmpty()) {
				
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
		if (pd!=null&&pd.isShowing()) {

			pd.dismiss();
		}

		if (event.equals(EventParams.METHOD_REPORT_USER)) {

			JSONObject data = ((JSONObject) obj);
			Log.e(TAG, data.toString());
			new MessageDialog(this,R.string.thanks_for_reporting)
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
		if (pd.isShowing()) {

			pd.dismiss();
		}
	}

	public void reportUser(String message) {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			MeehabApp.toast(getString(R.string.no_internet_connection));
			return;
		}
		if (socketService != null) {
			pd = UtilityClass.getProgressDialog(this);
			
			JSONObject object = new JSONObject();

			try {
				object.put("user_id", AccountUtils.getUserId(this));
				object.put("friend_id", friendId);
				object.put("message", message);

				Log.e("json send ", object.toString());
				socketService.reportUser(object);
				pd.show();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
