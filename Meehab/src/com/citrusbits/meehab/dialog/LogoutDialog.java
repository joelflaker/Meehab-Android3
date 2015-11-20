package com.citrusbits.meehab.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.citrusbits.meehab.R;

public class LogoutDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private LogoutDialogClickListener LogoutDialogClickListener;

	public LogoutDialog(Context context) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public LogoutDialog setLogoutDialogListener(
			LogoutDialogClickListener listener) {

		this.LogoutDialogClickListener = listener;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.dialog_logout);
		findViewById(R.id.ibCancel).setOnClickListener(this);
		findViewById(R.id.ibLogout).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibCancel:
			if (LogoutDialogClickListener != null) {
				LogoutDialogClickListener.onCancelClick(this);
			}

			break;
		case R.id.ibLogout:
			if (LogoutDialogClickListener != null) {
				LogoutDialogClickListener.onLogoutClick(this);
			}
			break;
		}

	}

	public interface LogoutDialogClickListener {

		public void onCancelClick(LogoutDialog dialog);

		public void onLogoutClick(LogoutDialog dialog);
	}

}
