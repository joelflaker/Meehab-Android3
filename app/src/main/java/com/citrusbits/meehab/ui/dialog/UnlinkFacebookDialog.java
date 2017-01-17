package com.citrusbits.meehab.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.citrusbits.meehab.R;

public class UnlinkFacebookDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private UnlinkFacebookDialogListener unlinkFacebookDialogListener;

	public UnlinkFacebookDialog(Context context) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public UnlinkFacebookDialog setUnlinkFacebookDialogListener(
			UnlinkFacebookDialogListener listener) {

		this.unlinkFacebookDialogListener = listener;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.dialog_disconnect_facebook);
		findViewById(R.id.ibCancel).setOnClickListener(this);
		findViewById(R.id.ibDisconnect).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibCancel:
			if (unlinkFacebookDialogListener != null) {
				unlinkFacebookDialogListener.onCancelClick(this);
			}

			break;
		case R.id.ibDisconnect:
			if (unlinkFacebookDialogListener != null) {
				unlinkFacebookDialogListener.onDisconnectClick(this);
			}
			break;
		}

	}

	public interface UnlinkFacebookDialogListener {

		public void onCancelClick(UnlinkFacebookDialog dialog);

		public void onDisconnectClick(UnlinkFacebookDialog dialog);
	}

}
