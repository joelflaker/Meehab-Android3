package com.citrusbits.meehab;

import org.json.JSONException;
import org.json.JSONObject;

import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.UtilityClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class CannotFindInsuranceActivity extends SocketActivity implements OnSocketResponseListener,
		OnClickListener {

	EditText etInsuranceProvider;
	
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		pd = UtilityClass.getProgressDialog(this);
		setContentView(R.layout.activity_cannot_find_insurance);
		findViewById(R.id.ibCancel).setOnClickListener(this);
		findViewById(R.id.ibSubmit).setOnClickListener(this);
		etInsuranceProvider = (EditText) findViewById(R.id.etInsuranceProvider);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibCancel:
			onBackPressed();
			break;
		case R.id.ibSubmit:
			final String insurance=etInsuranceProvider.getText().toString().trim();
			if(insurance.isEmpty()){
				Toast.makeText(CannotFindInsuranceActivity.this, "Please enter insurnace name ", Toast.LENGTH_SHORT).show();
				return;
			}
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setMessage("Thank you for filling in your insurance provider. We will get back you to you shortly regarding your addition");
			dialog.setButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					addInsurance(insurance);
				}
			});
			dialog.show();
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
		JSONObject obj=new JSONObject();
		try {
			obj.put("insurance", insurance);
			socketService.addInsurance(obj);
			pd.show();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		// TODO Auto-generated method stub
		if(pd!=null&&pd.isShowing()){
			pd.dismiss();
		}
		
		
		if(event.equals(EventParams.EVENT_ADD_INSURANCE)){
			Toast.makeText(CannotFindInsuranceActivity.this, "Insurance added successfully!", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(CannotFindInsuranceActivity.this, HomeActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
		}
	}

	@Override
	public void onSocketResponseFailure(String message) {
		// TODO Auto-generated method stub
		
		if(pd!=null&&pd.isShowing()){
			pd.dismiss();
		}
	}

}
