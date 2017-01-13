package com.citrusbits.meehab.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.app.App;

public class PasswordConfirmationDialog extends Dialog implements
		View.OnClickListener {

	private Context context;



	private PasswordConfirmationDialogClickListener confirmDialogClickListener;
	private EditText editPassword;

	public PasswordConfirmationDialog(Context context) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		this.context = context;
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
		findViewById(R.id.ibDelete).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibCancel:
			if (confirmDialogClickListener != null) {
				confirmDialogClickListener.onCancelClick(this);
			}

			break;
		case R.id.ibDelete:
			if (confirmDialogClickListener != null) {
				String password = editPassword.getText().toString().trim();
				if(password.length() == 0) {
					App.toast("Password is required!");
					return;
				}
				confirmDialogClickListener.onDeleteClick(password,this);
			}
			break;
		}

	}

	public interface PasswordConfirmationDialogClickListener {

		public void onCancelClick(PasswordConfirmationDialog dialog);

		public void onDeleteClick(String password, PasswordConfirmationDialog dialog);
	}

}
