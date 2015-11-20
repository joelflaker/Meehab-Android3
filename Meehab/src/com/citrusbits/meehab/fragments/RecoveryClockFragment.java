package com.citrusbits.meehab.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.HomeActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.dialog.ResetRecoverClockDialog;
import com.citrusbits.meehab.dialog.ResetRecoverClockDialog.ResetRecoveryClockDialogListener;
import com.citrusbits.meehab.utils.RecoverClockDateUtils;

public class RecoveryClockFragment extends Fragment implements OnClickListener {

	private HomeActivity homeActivity;

	TextView tvSoberDate;
	TextView tvSoberDateList;
	TextView tvTodayDate;
	TextView tvNextChipDate;

	public RecoveryClockFragment() {
	}

	public RecoveryClockFragment(HomeActivity homeActivity) {
		this.homeActivity = homeActivity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_recoveryclock, container,
				false);

		v.findViewById(R.id.topMenuBtn).setOnClickListener(this);
		v.findViewById(R.id.ivReset).setOnClickListener(this);
		tvSoberDate = (TextView) v.findViewById(R.id.tvSoberDate);
		tvSoberDateList = (TextView) v.findViewById(R.id.tvSoberDateList);
		tvNextChipDate = (TextView) v.findViewById(R.id.tvNextChipDate);
		tvTodayDate = (TextView) v.findViewById(R.id.tvTodayDate);
		tvTodayDate.setText(RecoverClockDateUtils
				.fomateRecoverClockDate(Calendar.getInstance().getTime()));

		tvNextChipDate
				.setText(RecoverClockDateUtils
						.getDateWithMonthNameNextChip(Calendar.getInstance()
								.getTime()));

		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topMenuBtn:
			if (homeActivity.isDrawerOpen()) {
				homeActivity.changeDrawerVisibility(false);
			} else {
				homeActivity.changeDrawerVisibility(true);
			}
			break;
		case R.id.ivReset:
			// App.alert("In dev");
			String soberDate = tvSoberDate.getText().toString().trim();
			new ResetRecoverClockDialog(getActivity())
					.setRecoveryClockDialogListener(
							new ResetRecoveryClockDialogListener() {

								@Override
								public void onDoneClick(
										ResetRecoverClockDialog dialog,
										String dateSelected) {
									// TODO Auto-generated method stub
									dialog.dismiss();
									if (!RecoverClockDateUtils
											.isValidSoberDate(dateSelected)) {
										Toast.makeText(getActivity(),
												"Is not valid sober date!",
												Toast.LENGTH_SHORT).show();
										return;
									}
									tvSoberDate
											.setText(RecoverClockDateUtils
													.getDateWithMonthName(dateSelected));

									tvSoberDateList
											.setText(RecoverClockDateUtils
													.getSoberDifference(
															dateSelected,
															getActivity()));

								}

								@Override
								public void onCancelClick(
										ResetRecoverClockDialog dialog) {
									// TODO Auto-generated method stub
									dialog.dismiss();

								}
							},
							!soberDate.isEmpty() ? RecoverClockDateUtils
									.getCalendarFromDate(soberDate) : null)
					.show();
			break;
		}
	}

}
