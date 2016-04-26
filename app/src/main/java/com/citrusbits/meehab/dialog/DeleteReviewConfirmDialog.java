package com.citrusbits.meehab.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.citrusbits.meehab.R;

public class DeleteReviewConfirmDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;



	private DeleteReviewConfirmDialogClickListener DeleteReviewConfirmDialogClickListener;

	public DeleteReviewConfirmDialog(Context context) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public DeleteReviewConfirmDialog setDeleteReviewConfirmDialogListener(
			DeleteReviewConfirmDialogClickListener listener) {

		this.DeleteReviewConfirmDialogClickListener = listener;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.dialog_delete_review_confirm);
		findViewById(R.id.ibCancel).setOnClickListener(this);
		findViewById(R.id.ibDelete).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibCancel:
			if (DeleteReviewConfirmDialogClickListener != null) {
				DeleteReviewConfirmDialogClickListener.onCancelClick(this);
			}

			break;
		case R.id.ibDelete:
			if (DeleteReviewConfirmDialogClickListener != null) {
				DeleteReviewConfirmDialogClickListener.onDeleteClick(this);
			}
			break;
		}

	}

	public interface DeleteReviewConfirmDialogClickListener {

		public void onCancelClick(DeleteReviewConfirmDialog dialog);

		public void onDeleteClick(DeleteReviewConfirmDialog dialog);
	}

}
