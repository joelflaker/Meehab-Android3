package com.citrusbits.meehab;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class TermsAndConditionsActivity extends SocketActivity {

	public static final String EXTRA_TERMS = "terms";
	private TextView txtTerms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terms_and_conditions);

		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);

		if(getIntent().hasExtra(EXTRA_TERMS)){
			setTitle(R.string.terms_conditions);
			txtTitle.setText(R.string.terms_conditions);
		}else {
			setTitle(R.string.privacy_policy);
			txtTitle.setText(R.string.privacy_policy);
		}

		txtTerms = (TextView)findViewById(R.id.txtTerms);

		//top back button
		findViewById(R.id.topMenuBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}});
	}

	@Override
	void onBackendConnected() {

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
	}
}
