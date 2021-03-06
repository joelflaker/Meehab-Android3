package com.citrusbits.meehab.ui;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.helpers.LogoutHelper;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.utils.AccountUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class TwoOptionActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_two_option);

		UserDatasource userDatasource = new UserDatasource(
				TwoOptionActivity.this);
		UserAccount user = userDatasource.findUser(AccountUtils.getUserId(this));
		
		//if null then logout
		if( user == null || user.getUsername() == null) {
			new LogoutHelper(this).attemptLogout();
			return;
		}
		TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
		String userName = user.getUsername();
		tvUserName.setText(String.format(
				getString(R.string.hello_username_str), userName));

		findViewById(R.id.ivMeetingsRehab).setOnClickListener(this);
		findViewById(R.id.jump2Friends).setOnClickListener(this);

		View.OnLongClickListener fun = new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				int xy[] = new int[2];
				String jump2String = "Navigate to Friends";
				findViewById(R.id.jump2Friends).getLocationOnScreen(xy);
				if (v.getId() == R.id.jump2Meetings) {
					jump2String = "Navigate to Meetings";
					findViewById(R.id.jump2Meetings).getLocationOnScreen(xy);
				}

				TextView tv = new TextView(TwoOptionActivity.this);
				tv.setText(jump2String);
				tv.setBackgroundColor(Color.TRANSPARENT);
				tv.setTextColor(Color.MAGENTA);
				tv.setTextSize(1, 24);
				tv.setPadding(8, 8, 8, 8);

				Toast toast = new Toast(TwoOptionActivity.this);
				toast.setDuration(Toast.LENGTH_SHORT);

				toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0,
						xy[1] - 150);
				toast.setView(tv);
				toast.show();
				return false;
			}

		};

		findViewById(R.id.jump2Meetings).setOnLongClickListener(fun);
		findViewById(R.id.jump2Friends).setOnLongClickListener(fun);
	}

	private int getRelativeLeft(View myView) {
		if (myView.getParent() == myView.getRootView())
			return myView.getLeft();
		else
			return myView.getLeft()
					+ getRelativeLeft((View) myView.getParent());
	}

	private int getRelativeTop(View myView) {
		if (myView.getParent() == myView.getRootView())
			return myView.getTop();
		else
			return myView.getTop() + getRelativeTop((View) myView.getParent());
	}

	@Override
	public void onClick(View v) {
		// TextView tv = new TextView(this);
		// tv.setText("Hello toast");
		// tv.setBackgroundColor(Color.GRAY);
		// final PopupWindow popupWindow = new
		// PopupWindow(tv,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		// popupWindow.showAsDropDown(findViewById(R.id.jump2Friends));

		//5 is for friends
		int jump2 = 4;
		if (v.getId() == R.id.ivMeetingsRehab) {
			//3 is for meeting
			jump2 = 3;
		}

		Intent intent = new Intent(TwoOptionActivity.this, HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(HomeActivity.EXTRA_FRAGMENT_POSITION,jump2);
		startActivity(intent);
		overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
		setResult(RESULT_OK);
		TwoOptionActivity.this.finish();
	}

}
