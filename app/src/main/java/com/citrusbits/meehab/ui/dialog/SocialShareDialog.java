package com.citrusbits.meehab.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.citrusbits.meehab.R;

public class SocialShareDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private SocialShareDialogClickListener SocialShareDialogClickListener;

	public SocialShareDialog(Context context) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		this.context = context;
	}

	public SocialShareDialog setSocialShareDialogListener(
			SocialShareDialogClickListener listener) {

		this.SocialShareDialogClickListener = listener;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFade;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.dialog_share);
		findViewById(R.id.ibCancel).setOnClickListener(this);
		findViewById(R.id.ibEmail).setOnClickListener(this);
		findViewById(R.id.ibSMS).setOnClickListener(this);
		findViewById(R.id.ibFacebook).setOnClickListener(this);
		findViewById(R.id.ibTwitter).setOnClickListener(this);
		findViewById(R.id.ibInstagram).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibCancel:
			if (SocialShareDialogClickListener != null) {
				SocialShareDialogClickListener.onCancelClick(this);
			}

			break;
		case R.id.ibFacebook:
			if (SocialShareDialogClickListener != null) {
				SocialShareDialogClickListener.onFacebookClick(this);
			}
			break;

		case R.id.ibTwitter:
			if (SocialShareDialogClickListener != null) {
				SocialShareDialogClickListener.onTwitterClick(this);
			}
			break;
		case R.id.ibInstagram:
			if (SocialShareDialogClickListener != null) {
				SocialShareDialogClickListener.onInstagramClick(this);
			}
			break;

		case R.id.ibEmail:
			if (SocialShareDialogClickListener != null) {
				SocialShareDialogClickListener.onEmailClick(this);
			}
			break;
		case R.id.ibSMS:
			if (SocialShareDialogClickListener != null) {
				SocialShareDialogClickListener.onSMSClick(this);
			}
			break;

		}

	}

	public interface SocialShareDialogClickListener {

		void onCancelClick(SocialShareDialog dialog);

		void onFacebookClick(SocialShareDialog dialog);

		void onTwitterClick(SocialShareDialog dialog);

		void onEmailClick(SocialShareDialog dialog);

		void onSMSClick(SocialShareDialog dialog);

		void onInstagramClick(SocialShareDialog dialog);

	}

}
