package com.citrusbits.meehab;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class ProvideAppFeedBackActivity extends Activity implements
		OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_provide_app_feed_back);

		// top back button
		findViewById(R.id.topMenuBtn).setOnClickListener(this);
		findViewById(R.id.ibSend).setOnClickListener(this);

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		this.overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.topMenuBtn:
			onBackPressed();
			break;
		case R.id.ibSend:
			onBackPressed();
			break;
		}

	}

}
