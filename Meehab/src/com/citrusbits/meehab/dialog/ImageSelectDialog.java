package com.citrusbits.meehab.dialog;

import com.citrusbits.meehab.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

public class ImageSelectDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private ImageButton ibTakePhoto;
	private ImageButton ibChoosePhoto;
	private ImageButton ibCancel;

	private ImageSelectDialogListener imageSelectDialogListener;

	public ImageSelectDialog(Context context) {
		
		super(context, android.R.style.Theme_Black_NoTitleBar);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public ImageSelectDialog setImageSelectDialogListner(
			ImageSelectDialogListener listener) {

		this.imageSelectDialogListener = listener;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.dialog_image_select);
		findViewById(R.id.ibTakePhoto).setOnClickListener(this);
		findViewById(R.id.ibChoosePhoto).setOnClickListener(this);
		findViewById(R.id.ibCancel).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibTakePhoto:
			if (imageSelectDialogListener != null) {
				imageSelectDialogListener.onCameraClick(this);
			}

			break;
		case R.id.ibChoosePhoto:
			if (imageSelectDialogListener != null) {
				imageSelectDialogListener.onGalleryClick(this);
			}
			break;
		case R.id.ibCancel:
			if (imageSelectDialogListener != null) {
				imageSelectDialogListener.onCancelClick(this);
			}
			break;
		}

	}

	public interface ImageSelectDialogListener {

		public void onCameraClick(ImageSelectDialog dialog);

		public void onGalleryClick(ImageSelectDialog dialog);
		
		public void onCancelClick(ImageSelectDialog dialog);
	}

}
