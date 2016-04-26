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

public class BlockUserDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private BlockUserDialogClickListener BlockUserDialogClickListener;

	private boolean blocked;

	public BlockUserDialog(Context context) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		this.context = context;
	}

	public BlockUserDialog setBlocked(boolean blocked) {
		this.blocked = blocked;
		return this;
	}

	public BlockUserDialog setBlockUserDialogListener(
			BlockUserDialogClickListener listener) {

		this.BlockUserDialogClickListener = listener;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.dialog_block_user);
		ImageButton ibCancel = (ImageButton) findViewById(R.id.ibCancel);
		ibCancel.setOnClickListener(this);
		ImageButton ibReportUser = (ImageButton) findViewById(R.id.ibReportUser);
		ibReportUser.setOnClickListener(this);
		ImageButton ibBlockUser = (ImageButton) findViewById(R.id.ibBlockUser);
		ibBlockUser.setOnClickListener(this);
		if (blocked) {
			ibReportUser.setVisibility(View.GONE);
			ibBlockUser.setImageResource(R.drawable.unblock_user_btn);
		}
	}

	@Override
	public void onClick(View v) {
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

		 void onCancelClick(BlockUserDialog dialog);

		 void onBlockUser(BlockUserDialog dialog);

		 void onReportUser(BlockUserDialog dialog);

	}

}
