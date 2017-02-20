package com.citrusbits.meehab.ui;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.citrusbits.meehab.utils.ValidationUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class CannotFindInsuranceActivity extends SocketActivity implements OnSocketResponseListener,
		OnClickListener {

	EditText etInsuranceProvider;
	
	private Dialog pd;

	private String insurance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pd = UtilityClass.getProgressDialog(this);
		setContentView(R.layout.activity_cannot_find_insurance);
		findViewById(R.id.ibCancel).setOnClickListener(this);
		findViewById(R.id.ibSubmit).setOnClickListener(this);
		etInsuranceProvider = (EditText) findViewById(R.id.etInsuranceProvider);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibCancel:
			onBackPressed();
			break;
		case R.id.ibSubmit:
			final String insurance = etInsuranceProvider.getText().toString().trim();
			if(insurance.isEmpty()){
				Toast.makeText(CannotFindInsuranceActivity.this, "Please enter insurance name ", Toast.LENGTH_SHORT).show();
				return;
			}
			if (insurance.isEmpty() || ValidationUtils.isSpecialChar(insurance) || "no insurance".equalsIgnoreCase(insurance)) {
				Toast.makeText(this,
						"Enter valid insurance provider name!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			
				if (!NetworkUtils.isNetworkAvailable(this)) {
					Toast.makeText(this,
							getString(R.string.no_internet_connection),
							Toast.LENGTH_SHORT).show();
					return;
				}
			addInsurance(insurance);

//			AlertDialog dialog = new AlertDialog.Builder(this).create();
//			dialog.setMessage("Thank you for filling in your insurance provider. We will get back you to you shortly regarding your addition.");
//			dialog.setButton(DialogInterface.BUTTON_POSITIVE,"Ok", new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					addInsurance(insurance);
//				}
//			});
//			dialog.show();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}
	
	public void addInsurance(String insurance){
		this.insurance = insurance;
		socketService.addInsurance(insurance);
	}

	
	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		if(pd != null && pd.isShowing()){
			pd.dismiss();
		}

		if(event.equals(EventParams.EVENT_ADD_INSURANCE)) {
//			Toast.makeText(CannotFindInsuranceActivity.this, "Insurance added successfully!", Toast.LENGTH_SHORT).show();
				new AlertDialog.Builder(this)
						.setMessage(R.string.msg_thanks_for_add_insurance)
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent i = new Intent(CannotFindInsuranceActivity.this, HomeActivity.class);
								i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
										| Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(i);
								overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
							}
						})
						.show();

		}else if (event.equals(EventParams.EVENT_USER_UPDATE)){
			UserDatasource uds = new UserDatasource(this);
			UserAccount user = uds.findUser(AccountUtils.getUserId(this));
			user.setInsurance(insurance);
			uds.update(user);
			Toast.makeText(CannotFindInsuranceActivity.this, "Insurance added successfully!", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(CannotFindInsuranceActivity.this, HomeActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
		}
	}

	@Override
	public void onSocketResponseFailure(String event,String message) {
		if(event.equals(EventParams.EVENT_ADD_INSURANCE)) {
			Toast.makeText(CannotFindInsuranceActivity.this, "Error "+message, Toast.LENGTH_SHORT).show();
		}
			if(pd != null && pd.isShowing()){
			pd.dismiss();
		}
	}

}
