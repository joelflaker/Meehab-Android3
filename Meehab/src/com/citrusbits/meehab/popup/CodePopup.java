package com.citrusbits.meehab.popup;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.citrusbits.meehab.R;

public class CodePopup implements OnClickListener {

	private Context mContext;
	PopupWindow popupWindow;

	View popupView;
	String meetingValue;

	PopCalClickLlistener popClickListener;
	private boolean isShown;

	public CodePopup(Context context, String meetingValue) {
		this.mContext = context;
		this.meetingValue = meetingValue;
		init();
	}

	private void init() {

		LayoutInflater layoutInflater = (LayoutInflater) mContext
				.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

		popupView = layoutInflater.inflate(R.layout.layout_meeting_popup, null,
				false);
		popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		TextView tvCodePopup = (TextView) popupView
				.findViewById(R.id.tvCodePopup);
		if (meetingValue != null) {

			tvCodePopup.setText(meetingValue);

		}

		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);

		setClickListenerOnButtons(((LinearLayout) popupView));
		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				// isShown = false;
			}
		});

	}

	public CodePopup setListener(PopCalClickLlistener popcal) {
		this.popClickListener = popcal;
		return this;
	}

	public void show(View view) {
		int location[] = new int[2];
		view.getLocationOnScreen(location);
		// location[0] = location[0] - popupView.getMeasuredWidth();
		
		Resources r = mContext.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, r.getDisplayMetrics());
		
		popupWindow.showAtLocation(popupView, Gravity.NO_GRAVITY, location[0],
				(int) (location[1] + view.getHeight()+px));
		isShown = true;
		// popupWindow.update(0, 0, popupView.getMeasuredWidth(),
		// popupView.getMeasuredHeight());
	}

	public void setClickListenerOnButtons(LinearLayout group) {

		for (int i = 0; i < group.getChildCount(); i++) {
			View view = group.getChildAt(i);
			if (view instanceof Button) {
				view.setOnClickListener(this);
			} else if (view instanceof LinearLayout) {
				setClickListenerOnButtons((LinearLayout) view);
			}
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (popClickListener != null) {
			Button btn = (Button) v;
			popClickListener.onClick(btn.getTag().toString());
		}
	}

	public interface PopCalClickLlistener {
		public void onClick(String text);
	}

	/**
	 * @return
	 */
	public boolean isShown() {
		return isShown;
	}

	/**
	 * 
	 */
	public void dismiss() {
		popupWindow.dismiss();
		isShown = false;
	}

}
