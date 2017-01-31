package com.citrusbits.meehab.ui.fragments;

import java.util.Calendar;

import org.joda.time.Period;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
		Log.i("Sober date",""+ soberDate);
		if(soberDate == null){
			App.toast("User sober date= "+soberDate+ " to fix this situation!");
			return v;
		}
		mDateSelected = soberDate;
		tvSoberDate.setText(RecoverClockDateUtils
				.getDateWithMonthName(soberDate));

		tvSoberDateList.setText(RecoverClockDateUtils.getSoberDifference(
				soberDate, false, getActivity()));

//		setChip(mDateSelected);

		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		if(!TextUtils.isEmpty(mDateSelected)) {
			setChip(mDateSelected);
		}
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

									setChip(dateSelected);
									updateProfile(mDateSelected);

								}

								@Override
								public void onCancelClick(
										ResetRecoverClockDialog dialog) {
									dialog.dismiss();

								}
							}, !soberDate.isEmpty() ? RecoverClockDateUtils
									.getCalendarFromDate(soberDate) : null)
					.show();
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		homeActivity = null;
		pd = null;
	}

	public void setChip(String dateSelected) {

		Period chip = RecoverClockDateUtils
				.getRcpChip(dateSelected);
//		int number = chip.getNumbers();
		tvChipCounter.setVisibility(View.GONE);

		Log.e("Selected Date ", mDateSelected);
		Calendar cal = RecoverClockDateUtils
				.getCalendarFromDayMonthYear(mDateSelected);

		// milliseconds
//		long different = chip.getTime() - startDate.getTime();
//
//		long secondsInMilli = 1000;
//		long minutesInMilli = secondsInMilli * 60;
//		long hoursInMilli = minutesInMilli * 60;
//		long daysInMilli = hoursInMilli * 24;
//
//		Days.standardDaysIn(chip.withPeriodType())
//		long days = chip.toStandardSeconds().getSeconds() / 60 / 24;

		if(cal == null || chip == null){ return; }
		Log.e("Calendar Date ", cal + " Cal");

		if(chip.getYears() > 0){
			Log.e("Years ", chip.getYears() + "");
			tvChipCounter.setVisibility(View.VISIBLE);
			ivChip.setImageResource(R.drawable.sober_chips_53_years);
			tvChipCounter.setText(String.valueOf(chip.getYears()));

			cal.add(Calendar.YEAR, chip.getYears() + 1);
		}else if(chip.getMonths() >= 6) {
			Log.e("Month ", chip.getMonths()+ "");
			if (chip.getMonths() >= 9) {
				ivChip.setImageResource(R.drawable.sober_chips_9_months);
				cal.add(Calendar.MONTH, chip.getMonths() + 3);
			} else if (chip.getMonths() >= 6) {
				ivChip.setImageResource(R.drawable.sober_chips_6_months);
				cal.add(Calendar.MONTH, chip.getMonths() + 3);
			}
		}else {

			long days = RecoverClockDateUtils.getRcpChipForDays(dateSelected).getStandardDays();

			if (days >= 90) {
				ivChip.setImageResource(R.drawable.sober_chips_90_days);
				cal.add(Calendar.MONTH, 6);
			} else if (days >= 60) {
				ivChip.setImageResource(R.drawable.sober_chips_60_days);
//				cal.add(Calendar.MONTH, chip.getMonths() + 1);
				cal.add(Calendar.DAY_OF_MONTH, 90);
			}else if (days >= 30) {
				ivChip.setImageResource(R.drawable.sober_chips_30_days);
//				cal.add(Calendar.MONTH, chip.getMonths() + 1);
				cal.add(Calendar.DAY_OF_MONTH, 60);
			}else {
				ivChip.setImageResource(R.drawable.sober_chips_welcome);
				cal.add(Calendar.DAY_OF_MONTH, 30);
//				cal.add(Calendar.MONTH, 1);
			}
			Log.e(getClass().getCanonicalName(),"Days : "+ days);
		}
		tvNextChipDate.setText(RecoverClockDateUtils
				.getDateWithMonthNameNextChip(cal.getTime()));
		//Care about month length
//		if(chip.getYears() > 0){
//			Log.e("Years ", chip.getYears() + "");
//			tvChipCounter.setVisibility(View.VISIBLE);
//			ivChip.setImageResource(R.drawable.sober_chips_53_years);
//			tvChipCounter.setText(String.valueOf(chip.getYears()));
//
//			cal.add(Calendar.YEAR, chip.getYears() + 1);
//		}else if(chip.getMonths() > 0) {
//			Log.e("Month ", chip.getMonths()+ "");
//			if (chip.getMonths() >= 9) {
//				ivChip.setImageResource(R.drawable.sober_chips_9_months);
//				cal.add(Calendar.MONTH, chip.getMonths() + 3);
//			} else if (chip.getMonths() >= 6) {
//				ivChip.setImageResource(R.drawable.sober_chips_6_months);
//				cal.add(Calendar.MONTH, chip.getMonths() + 3);
//			} else if (chip.getMonths() >= 3) {
//				ivChip.setImageResource(R.drawable.sober_chips_90_days);
//				cal.add(Calendar.MONTH, chip.getMonths() + 3);
//			} else if (chip.getMonths() >= 2) {
//				ivChip.setImageResource(R.drawable.sober_chips_60_days);
//				cal.add(Calendar.MONTH, chip.getMonths() + 1);
//			}else if (chip.getMonths() >= 1) {
//				ivChip.setImageResource(R.drawable.sober_chips_30_days);
//				cal.add(Calendar.MONTH, chip.getMonths() + 1);
//			}
//		}else {
//			Log.e("Days ", chip.getDays() + "");
//
//			ivChip.setImageResource(R.drawable.sober_chips_welcome);
//			cal.add(Calendar.MONTH, 1);
//		}
//		tvNextChipDate.setText(RecoverClockDateUtils
//				.getDateWithMonthNameNextChip(cal.getTime()));
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
//			user = userDatasource.findUser(AccountUtils.getUserId(getActivity()));
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
