package com.citrusbits.meehab.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.citrusbits.meehab.R;

public class BlockUserDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private BlockUserDialogClickListener BlockUserDialogClickListener;

	public BlockUserDialog(Context context) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public BlockUserDialog setBlockUserDialogListener(
			BlockUserDialogClickListener listener) {

		this.BlockUserDialogClickListener = listener;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.dialog_block_user);
		findViewById(R.id.ibCancel).setOnClickListener(this);
		findViewById(R.id.ibReportUser).setOnClickListener(this);
		findViewById(R.id.ibBlockUser).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibCancel:
			if (BlockUserDialogClickListener != null) {
				BlockUserDialogClickListener.onCancelClick(this);
			}

			break;
		case R.id.ibBlockUser:
			if (BlockUserDialogClickListener != null) {
				BlockUserDialogClickListener.onBlockUser(this);
			}
			break;

		case R.id.ibReportUser:
			if (BlockUserDialogClickListener != null) {
				BlockUserDialogClickListener.onReportUser(this);
			}
			break;
		}

	}

	public interface BlockUserDialogClickListener {

		public void onCancelClick(BlockUserDialog dialog);

		public void onBlockUser(BlockUserDialog dialog);

		public void onReportUser(BlockUserDialog dialog);

	}

}
