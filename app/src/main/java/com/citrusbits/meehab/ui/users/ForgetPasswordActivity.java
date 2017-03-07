package com.citrusbits.meehab.ui.users;

import org.json.JSONException;
import org.json.JSONObject;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.ui.SocketActivity;
import com.citrusbits.meehab.app.MeehabApp;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.UtilityClass;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ForgetPasswordActivity extends SocketActivity implements OnSocketResponseListener, View.OnClickListener {

	private EditText emailEdit;
	private Button nextBtn;
	private Dialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);

//		if(MeehabApp.getInstance().socketIO == null){
//			MeehabApp.getInstance().initConnectNodeJS();
//		}
		//top back button
		findViewById(R.id.topMenuBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ForgetPasswordActivity.this.finish();
				overridePendingTransition(R.anim.activity_back_in,
						R.anim.activity_back_out);
			}});
		
		pd = UtilityClass.getProgressDialog(this);
		emailEdit = (EditText) findViewById(R.id.emailEdit);
		nextBtn = (Button)findViewById(R.id.nextBtn);

		nextBtn.setOnClickListener(this);

		emailEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String emailString  = emailEdit.getText().toString();
				if (UtilityClass.isValidEmail(emailString)){
					nextBtn.setEnabled(true);
				}else{
					nextBtn.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nextBtn:
			requestPassword();
			break;

		default:
			break;
		}
	}
	
	private void requestPassword() {
		// All fields Contain values
		String emailString = emailEdit.getText().toString();
		
		if(emailEdit.getError() != null){
			emailEdit.requestFocus();
			emailEdit.selectAll();
			return;
		}

		hideKeyboard();

		if(NetworkUtil.getConnectivityStatus(this) == 0){
			MeehabApp.toast(getString(R.string.network_problem));
		}else{
			JSONObject params = new JSONObject();
			try {
				// Toast.makeText(this, itemName,
				// Toast.LENGTH_SHORT).show();
				params.put(EventParams.SIGNUP_EMAIL, emailString);
				pd.show();
				socketService.forgotPassword(params);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,R.anim.activity_back_out);
	}
	
	@Override
	public void onBackendConnected() {
		
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		pd.dismiss();
		MeehabApp.toast(getString(R.string.email_has_been_send));
	}

	@Override
	public void onSocketResponseFailure(String onEvent,String message) {
		pd.dismiss();
		MeehabApp.toast(""+message);
	}
}
