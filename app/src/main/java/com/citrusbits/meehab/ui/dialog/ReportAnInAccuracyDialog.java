package com.citrusbits.meehab.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.citrusbits.meehab.R;

public class ReportAnInAccuracyDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;

	private ReportAnInAccuracyDialogClickListener ReportAnInAccuracyDialogClickListener;

	public ReportAnInAccuracyDialog(Context context) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		this.context = context;
	}

	public ReportAnInAccuracyDialog setReportAnInAccuracyDialogListener(
			ReportAnInAccuracyDialogClickListener listener) {

		this.ReportAnInAccuracyDialogClickListener = listener;

		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.dialog_report_an_inaccuracy);
		findViewById(R.id.ibCancel).setOnClickListener(this);
		findViewById(R.id.ibReportAnInAccuracy).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibCancel:
			if (ReportAnInAccuracyDialogClickListener != null) {
				ReportAnInAccuracyDialogClickListener.onCancelClick(this);
			}

			break;
		case R.id.ibReportAnInAccuracy:
			if (ReportAnInAccuracyDialogClickListener != null) {
				ReportAnInAccuracyDialogClickListener.onInaccuracyClick(this);
			}
			break;
		}

	}

	public interface ReportAnInAccuracyDialogClickListener {

		public void onCancelClick(ReportAnInAccuracyDialog dialog);

		public void onInaccuracyClick(ReportAnInAccuracyDialog dialog);
	}

}
