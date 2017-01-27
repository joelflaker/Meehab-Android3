package com.citrusbits.meehab.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.utils.AccountUtils;

public class PasswordConfirmationDialog extends Dialog implements
		View.OnClickListener {

	private final String message;
	private Context context;



	private PasswordConfirmationDialogClickListener confirmDialogClickListener;
	private EditText editPassword;

	public PasswordConfirmationDialog(Context context,String message) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		this.context = context;
		this.message = message;
	}

	public PasswordConfirmationDialog setConfirmationListener(
			PasswordConfirmationDialogClickListener listener) {

		this.confirmDialogClickListener = listener;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.dialog_password_confirmation);
		editPassword = (EditText)findViewById(R.id.editPassword);
		findViewById(R.id.ibCancel).setOnClickListener(this);
		findViewById(R.id.btnOk).setOnClickListener(this);

		TextView textView1 = (TextView) findViewById(R.id.txtMessage);
		textView1.setText(message);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibCancel:
			if (confirmDialogClickListener != null) {
				confirmDialogClickListener.onCancelClick(this);
			}

			break;
		case R.id.btnOk:
			if (confirmDialogClickListener != null) {
				String password = editPassword.getText().toString().trim();
				if(password.length() == 0) {
					App.toast("Password is required!");
					return;
				}

				if(AccountUtils.getPassword(context).equals(password)){
					confirmDialogClickListener.onOkClick(password,this);
				}else {
					App.toast("Password is doesn't match!");
				}
			}
			break;
		}

	}

	public interface PasswordConfirmationDialogClickListener {

		void onCancelClick(PasswordConfirmationDialog dialog);

		void onOkClick(String password, PasswordConfirmationDialog dialog);
	}

}
