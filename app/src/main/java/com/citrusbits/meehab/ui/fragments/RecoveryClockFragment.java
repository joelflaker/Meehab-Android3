package com.citrusbits.meehab.ui.fragments;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.ui.HomeActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.ui.dialog.ResetRecoverClockDialog;
import com.citrusbits.meehab.ui.dialog.ResetRecoverClockDialog.ResetRecoveryClockDialogListener;
import com.citrusbits.meehab.managers.RCChip;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.RecoverClockDateUtils;
import com.citrusbits.meehab.utils.UtilityClass;

public class RecoveryClockFragment extends Fragment implements OnClickListener,
		OnSocketResponseListener {

	private HomeActivity homeActivity;

	TextView tvSoberDate;
	TextView tvSoberDateList;
	TextView tvTodayDate;
	TextView tvNextChipDate;

	TextView tvChipCounter;
	ImageView ivChip;

	UserDatasource userDatasource;

	UserAccount user;

	String mDateSelected;
	private Dialog pd;

	public RecoveryClockFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.homeActivity = (HomeActivity) getActivity();
		pd = UtilityClass.getProgressDialog(homeActivity);

		userDatasource = new UserDatasource(getActivity());
//		userDatasource.open();

		user = userDatasource.findUser(AccountUtils.getUserId(getActivity()));
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

		tvChipCounter = (TextView) v.findViewById(R.id.tvChipCounter);
		ivChip = (ImageView) v.findViewById(R.id.ivChip);

		tvTodayDate.setText(RecoverClockDateUtils
				.fomateRecoverClockDate(Calendar.getInstance().getTime()));

		String soberDate = user.getSoberSence();
		Log.i("Sober date", soberDate);
		if(soberDate == null){
			App.toast("User sober date= "+soberDate+ " to fix this situation!");
			return v;
		}
		mDateSelected = soberDate;
		tvSoberDate.setText(RecoverClockDateUtils
				.getDateWithMonthName(soberDate));
		RCChip chip = RecoverClockDateUtils.getRcpChip(soberDate);

		tvSoberDateList.setText(RecoverClockDateUtils.getSoberDifference(
				soberDate, false, getActivity()));

		setChip(chip);

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
			new ResetRecoverClockDialog(getActivity()).setRecoveryClockDialogListener(
							new ResetRecoveryClockDialogListener() {

								@Override
								public void onDoneClick(
										ResetRecoverClockDialog dialog,
										String dateSelected) {
									dialog.dismiss();
									if (!RecoverClockDateUtils
											.isValidSoberDate(dateSelected)) {
										Toast.makeText(getActivity(),
												"Is not valid sober date!",
												Toast.LENGTH_SHORT).show();
										return;
									}

									mDateSelected = dateSelected;
									tvSoberDate
											.setText(RecoverClockDateUtils
													.getDateWithMonthName(dateSelected));

									tvSoberDateList
											.setText(RecoverClockDateUtils
													.getSoberDifference(
															dateSelected,
															false,
															getActivity()));

									RCChip chip = RecoverClockDateUtils
											.getRcpChip(dateSelected);

									setChip(chip);
									updateProfile(mDateSelected);

								}

								@Override
								public void onCancelClick(
										ResetRecoverClockDialog dialog) {
									// TODO Auto-generated method stub
									dialog.dismiss();

								}
							}, !soberDate.isEmpty() ? RecoverClockDateUtils
									.getCalendarFromDate(soberDate) : null)
					.show();
			break;
		}
	}

	public void setChip(RCChip chip) {
		int number = chip.getNumbers();
		tvChipCounter.setVisibility(View.GONE);

		Log.e("Selected Date ", mDateSelected);
		Calendar cal = RecoverClockDateUtils
				.getCalendarFromDayMonthYear(mDateSelected);
		
		if(cal == null){ return; }

		Log.e("Calendar Date ", cal + " Cal");

		switch (chip.getRcChipType()) {
		case DAYS:

			Log.e("Days ", chip.getNumbers() + "");

			if (number >= 90) {
				ivChip.setImageResource(R.drawable.sober_chips_90_days);

				cal.add(Calendar.DAY_OF_MONTH, 180);
				tvNextChipDate.setText(RecoverClockDateUtils
						.getDateWithMonthNameNextChip(cal.getTime()));

			} else if (number >= 60 && number <= 89) {
				ivChip.setImageResource(R.drawable.sober_chips_60_days);
				cal.add(Calendar.DAY_OF_MONTH, 90);
				tvNextChipDate.setText(RecoverClockDateUtils
						.getDateWithMonthNameNextChip(cal.getTime()));
			} else if (number >= 30 && number <= 59) {
				ivChip.setImageResource(R.drawable.sober_chips_30_days);
				cal.add(Calendar.DAY_OF_MONTH, 60);
				tvNextChipDate.setText(RecoverClockDateUtils
						.getDateWithMonthNameNextChip(cal.getTime()));
			} else {
				ivChip.setImageResource(R.drawable.sober_chips_welcome);
				cal.add(Calendar.DAY_OF_MONTH, 30);
				tvNextChipDate.setText(RecoverClockDateUtils
						.getDateWithMonthNameNextChip(cal.getTime()));
			}
			break;
		case MONTH:
			if (number >= 9) {
				ivChip.setImageResource(R.drawable.sober_chips_9_months);

				cal.add(Calendar.YEAR, 1);
				tvNextChipDate.setText(RecoverClockDateUtils
						.getDateWithMonthNameNextChip(cal.getTime()));

			} else if (number >= 6 && number < 9) {
				ivChip.setImageResource(R.drawable.sober_chips_6_months);

				cal.add(Calendar.MONTH, 9);
				tvNextChipDate.setText(RecoverClockDateUtils
						.getDateWithMonthNameNextChip(cal.getTime()));

			} else if (number >= 3 && number < 6) {
				ivChip.setImageResource(R.drawable.sober_chips_90_days);
				cal.add(Calendar.MONTH, 6);
				tvNextChipDate.setText(RecoverClockDateUtils
						.getDateWithMonthNameNextChip(cal.getTime()));
			}
			Log.e("Month ", chip.getNumbers() + "");
			break;
		case YEARS:

			Log.e("Years ", chip.getNumbers() + "");
			tvChipCounter.setVisibility(View.VISIBLE);
			ivChip.setImageResource(R.drawable.sober_chips_53_years);
			tvChipCounter.setText(String.valueOf(number));

			cal.add(Calendar.YEAR, 1 + number);
			tvNextChipDate.setText(RecoverClockDateUtils
					.getDateWithMonthNameNextChip(cal.getTime()));

		}
	}

	public void updateProfile(String soberDate) {

		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			App.toast(getString(R.string.no_internet_connection));
			return;
		}

		JSONObject params = new JSONObject();
		try {
			pd.show();
			params.put("sober_sence", soberDate);
			homeActivity.socketService.updateAccount(params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		if (pd != null) {
			pd.dismiss();
		}
		if (event.equals(EventParams.EVENT_USER_UPDATE)) {
			Toast.makeText(getActivity(), "Sober has been updated",
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		if (pd != null) {
			pd.dismiss();
		}
		Toast.makeText(homeActivity, message, Toast.LENGTH_SHORT).show();
	}

}
