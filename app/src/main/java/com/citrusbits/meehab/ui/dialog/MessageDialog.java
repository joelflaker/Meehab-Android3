package com.citrusbits.meehab.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.citrusbits.meehab.R;

public class MessageDialog extends Dialog implements
		View.OnClickListener {

	private int messageRes;
	private Context context;

	private MessageDialogClickListener clickListener;

	public MessageDialog(Context context,@StringRes int messageRes) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		this.context = context;
		this.messageRes = messageRes;
	}

	public MessageDialog setDialogClickListener(
			MessageDialogClickListener listener) {
		this.clickListener = listener;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.dialog_message);
		findViewById(R.id.ibOk).setOnClickListener(this);
		setCancelable(false);
		TextView textView = (TextView) findViewById(R.id.txtMessage);
		textView.setText(messageRes);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ibOk:
				if (clickListener != null) {
					clickListener.onOkClick(this);
				}
		}
	}

	public interface MessageDialogClickListener {
		void onOkClick(MessageDialog dialog);
	}

}
