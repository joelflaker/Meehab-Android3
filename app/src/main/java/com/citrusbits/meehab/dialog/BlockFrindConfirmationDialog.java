package com.citrusbits.meehab.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.citrusbits.meehab.R;



public class BlockFrindConfirmationDialog extends Dialog implements
		android.view.View.OnClickListener {
	
	public static final int STATUS_SINGLE_BLOCK=0;
	public static final int STATUS_MULTIPLE_BLOCK=1;
	public static final int STATUS_UNBLOCK=2;

	private Context context;
	private BlockFrindConfirmationDialogClickListener blockFriendDialogClickListener;

	private int status;

	public BlockFrindConfirmationDialog(Context context) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public BlockFrindConfirmationDialog setBlockFrindConfirmationDialogClickListener(
			BlockFrindConfirmationDialogClickListener listenern,
			int multiple) {
		
		this.status=multiple;

		this.blockFriendDialogClickListener = listenern;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.dialog_block_friends);

		TextView tvConfirmationText = (TextView) findViewById(R.id.tvConfirmationText);

		tvConfirmationText.setText(getCommentText());

		tvConfirmationText.setText(getCommentText());

		findViewById(R.id.ibNo).setOnClickListener(this);
		findViewById(R.id.ibYes).setOnClickListener(this);
	}

	public String getCommentText() {

		
		
		if(status==STATUS_SINGLE_BLOCK){
			return context.getString(R.string.block_friend_confirmation_text);
		}else if(status==STATUS_MULTIPLE_BLOCK){
			return context.getString(R.string.block_friends_confirmation_text);
		}else{
			return context.getString(R.string.unblock_friend_confirmation_text);
		}
				
				
				

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibNo:
			if (blockFriendDialogClickListener != null) {
				blockFriendDialogClickListener.onNoClick(this);
			}

			break;
		case R.id.ibYes:
			if (blockFriendDialogClickListener != null) {
				blockFriendDialogClickListener.onYesClick(this);
			}
			break;
		}

	}

	public interface BlockFrindConfirmationDialogClickListener {

		public void onNoClick(BlockFrindConfirmationDialog dialog);

		public void onYesClick(BlockFrindConfirmationDialog dialog);
	}

	public enum RsvpAction {
		RSVP, UNRSVP, CHECKIN, CHECKOUT;
	}

}
