package com.citrusbits.meehab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class CannotFindInsuranceActivity extends Activity implements
		OnClickListener {

	EditText etInsuranceProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setMessage("Thank you for filling in your insurance provider. We will get back you to you shortly regarding your addition");
			dialog.setButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					startActivity(new Intent(CannotFindInsuranceActivity.this,
							HomeActivity.class));
					overridePendingTransition(R.anim.activity_in,
							R.anim.activity_out);
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

}
