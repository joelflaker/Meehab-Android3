package com.citrusbits.meehab.ui.users;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.ui.SocketActivity;
import com.citrusbits.meehab.ui.TwoOptionActivity;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.ui.dialog.DobPickerDialog;
import com.citrusbits.meehab.ui.dialog.DobPickerDialog.DatePickerDialogListener;
import com.citrusbits.meehab.ui.dialog.EthenticityPickerDialog;
import com.citrusbits.meehab.ui.dialog.EthenticityPickerDialog.EthenticityDialogListener;
import com.citrusbits.meehab.ui.dialog.HeightPickerDialog;
import com.citrusbits.meehab.ui.dialog.HeightPickerDialog.HeightPickerDialogListener;
import com.citrusbits.meehab.ui.dialog.SexualOrientationPickerDialog;
import com.citrusbits.meehab.ui.dialog.SexualOrientationPickerDialog.SexualOrientationDialogListener;
import com.citrusbits.meehab.ui.dialog.WeightPickerDialog;
import com.citrusbits.meehab.ui.dialog.WeightPickerDialog.WeightDialogListener;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DateTimeUtils;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.UtilityClass;

public class ProfileSetupMoreActivity extends SocketActivity implements
		OnSocketResponseListener, View.OnClickListener {

	private UserDatasource userDatasource;
	private UserAccount mUser;
	private UserAccount user;
	private CheckBox haveKidsYesBtn;
	private CheckBox haveKidsNoBtn;
	private CheckBox haveKidsNoAnsBtn;
	private EditText occupationEdit;
	private Button weightBtn;
	private Button heightBtn;
	private Button sexualOrientBtn;
	private Button ethnicityBtn;
	private Button soberDateBtn;
	private Dialog pd;
	private EditText sexualOrientOtherEdit;
	
	private boolean updateRequest=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_setup_more);

		userDatasource = new UserDatasource(this);

		mUser = userDatasource.findUser(AccountUtils.getUserId(this));
		user = new UserAccount();

		pd = UtilityClass.getProgressDialog(this);

		// top back button
		// findViewById(R.id.topRightBtn).setOnClickListener(this);
		findViewById(R.id.doneBtn).setOnClickListener(this);
		haveKidsYesBtn = (CheckBox) findViewById(R.id.hasKidsYesBtn);
		haveKidsNoBtn = (CheckBox) findViewById(R.id.hasKidsNoBtn);
		haveKidsNoAnsBtn = (CheckBox) findViewById(R.id.hasKidsNoAnsBtn);
		heightBtn = (Button) findViewById(R.id.heightBtn);
		weightBtn = (Button) findViewById(R.id.weightBtn);
		sexualOrientBtn = (Button) findViewById(R.id.sexualOrientBtn);
		sexualOrientOtherEdit = (EditText) findViewById(R.id.sexualOrientOtherEdit);
		occupationEdit = (EditText) findViewById(R.id.occupationEdit);
		ethnicityBtn = (Button) findViewById(R.id.ethnicityBtn);
		soberDateBtn = (Button) findViewById(R.id.soberDateBtn);

		heightBtn.setOnClickListener(this);
		weightBtn.setOnClickListener(this);
		sexualOrientBtn.setOnClickListener(this);
		ethnicityBtn.setOnClickListener(this);
		soberDateBtn.setOnClickListener(this);
		haveKidsYesBtn.setOnClickListener(this);
		haveKidsNoBtn.setOnClickListener(this);
		haveKidsNoAnsBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.doneBtn:
			// save changes
			updateProfile();
			break;

		case R.id.heightBtn:
			// presentHeightPicker();
			String height = heightBtn.getText().toString().trim();
			new HeightPickerDialog(ProfileSetupMoreActivity.this)
					.setHeightPickerDialogListenere(
							new HeightPickerDialogListener() {

								@Override
								public void onDoneClick(
										HeightPickerDialog dialog,
										String heightSelected) {
									dialog.dismiss();
									heightBtn.setText(heightSelected);
									user.setHeight(heightSelected);
								}

								@Override
								public void onCancelClick(
										HeightPickerDialog dialog) {
									dialog.dismiss();
								}
							}, height).show();

			break;
		case R.id.weightBtn:
			// presentWeightPicker();
			String weightText = weightBtn.getText().toString().trim();
					/*.replace("lbs", "").trim().trim().replace("lb", "").trim();*/
			/*;
			int weight = weightText.isEmpty() ? 50 : Integer
					.parseInt(weightText);*/
			if(weightText.isEmpty()){
				weightText="100 Lbs";
			}
			new WeightPickerDialog(this).setWeightDialogListener(
					new WeightDialogListener() {

						@Override
						public void onDoneClick(WeightPickerDialog dialog,
								String weight) {
							dialog.dismiss();
							weightBtn.setText(weight);
							user.setWeight(weight);
						}

						@Override
						public void onCancelClick(WeightPickerDialog dialog) {

							dialog.dismiss();

						}
					}, weightText).show();
			break;
		case R.id.sexualOrientBtn:
			// presentSexualOrientationPicker();
			String sexualOrientation = sexualOrientBtn.getText().toString()
					.trim();
			new SexualOrientationPickerDialog(this)
					.setSexualOrientationListener(
							new SexualOrientationDialogListener() {

								@Override
								public void onDoneClick(
										SexualOrientationPickerDialog dialog,
										String sexualOrientation) {
									dialog.dismiss();
									sexualOrientBtn.setText(sexualOrientation);
									user.setSexualOrientation(sexualOrientation);
								}

								@Override
								public void onCancelClick(
										SexualOrientationPickerDialog dialog) {
									dialog.dismiss();
								}
							}, sexualOrientation).show();
			break;
		case R.id.ethnicityBtn:
			// presentEthnicityPicker();
			String ethenticity = ethnicityBtn.getText().toString().trim();
			new EthenticityPickerDialog(this).setEthenticityDialogListener(
					new EthenticityDialogListener() {

						@Override
						public void onDoneClick(EthenticityPickerDialog dialog,
								String ethenticity) {
							dialog.dismiss();
							ethnicityBtn.setText(ethenticity);
							user.setEthnicity(ethenticity);
							;
						}

						@Override
						public void onCancelClick(EthenticityPickerDialog dialog) {
							dialog.dismiss();

						}
					}, ethenticity).show();
			break;
		case R.id.soberDateBtn:
			// presentSoberDatePicker();
			String date = soberDateBtn.getText().toString().trim();
			new DobPickerDialog(this).setSober(true)
					.setDobDialogListener(
							new DatePickerDialogListener() {

								@Override
								public void onDoneClick(DobPickerDialog dialog,
										String dateSelected) {
									dialog.dismiss();
									
									Calendar dbCal=DateTimeUtils.dateToCalendar(dateSelected);
									if(!dbCal.before(Calendar.getInstance())){
										Toast.makeText(ProfileSetupMoreActivity.this, "Can not set future date!", Toast.LENGTH_SHORT).show();
										return;
									}
									
									
									soberDateBtn.setText(dateSelected);
									user.setSoberSence(dateSelected);
								}

								@Override
								public void onCancelClick(DobPickerDialog dialog) {
									dialog.dismiss();
								}
							},
							date.isEmpty() ? null : DateTimeUtils
									.dateToCalendar(date)).show();
			break;
		case R.id.hasKidsYesBtn:
			haveKidsYesBtn.setChecked(true);
			haveKidsNoBtn.setChecked(false);
			haveKidsNoAnsBtn.setChecked(false);
			break;
		case R.id.hasKidsNoBtn:
			haveKidsNoBtn.setChecked(true);
			haveKidsYesBtn.setChecked(false);
			haveKidsNoAnsBtn.setChecked(false);
			break;
		case R.id.hasKidsNoAnsBtn:
			haveKidsNoAnsBtn.setChecked(true);
			haveKidsYesBtn.setChecked(false);
			haveKidsNoBtn.setChecked(false);
			break;
		default:
			break;
		}
	}

	private Calendar getCalendarFromDb(String db) {
		SimpleDateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = Calendar.getInstance();
		try {
			Date date = dateFormater.parse(db);
			cal.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return cal;
	}

	/**
	 * 
	 */
	private void navigateToTwoOptions() {
		Intent intent = new Intent(ProfileSetupMoreActivity.this,
				TwoOptionActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("Exit me", true);
		startActivity(intent);
		overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
		setResult(RESULT_OK);
		ProfileSetupMoreActivity.this.finish();
	}

	private void updateProfile() {
		if (NetworkUtil.getConnectivityStatus(this) == 0) {
			App.toast(getString(R.string.network_problem));
			return;
		} else {

			JSONObject params = new JSONObject();
			try {

				// update fields
				// height
				if (user.getHeight() != null) {
					params.put("height", user.getHeight());
				}

				// weight
				if (user.getWeight() != null) {
					params.put("weight", user.getWeight());
				}


				// sexual_orientation
				if (user.getSexualOrientation() != null) {
					if ("Other".equals(user.getSexualOrientation())) {
						String other = sexualOrientOtherEdit.getText()
								.toString().trim();
						if (other.length() > 0) {
							user.setSexualOrientation(other);
						}
					}
					params.put("sexual_orientation",
							user.getSexualOrientation());
				}

				String occupation = occupationEdit.getText().toString().trim();

				// get Occupation

				user.setAccupation(occupation);

				params.put("accupation", occupation);

				// getEthnicity
				if (user.getEthnicity() != null) {
					params.put("ethnicity", user.getEthnicity());
				}

				// sober date
				if (user.getSoberSence() != null) {
					params.put("sober_sence", user.getSoberSence());
				}

				// have kids : default noans
				String hasKidsString = null;
				if (haveKidsYesBtn.isChecked()) {
					hasKidsString = "yes";
				} else if (haveKidsNoBtn.isChecked()) {
					hasKidsString = "no";
				} else if (haveKidsNoAnsBtn.isChecked()) {
					hasKidsString = "";
				}

				if (hasKidsString == null) {
					Toast.makeText(ProfileSetupMoreActivity.this,
							"Please select if you have kids!", Toast.LENGTH_SHORT).show();
					return;
				}
				params.put("have_kids", hasKidsString);

			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}

			if (params != null && params.length() > 0) {
				pd.show();
				updateRequest=true;
				socketService.updateAccount(params);
			}
		}
	}

	@Override
	public void onBackendConnected() {

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		
		if(updateRequest){
			pd.dismiss();
			if (event.equals(EventParams.EVENT_USER_UPDATE)) {
				ProfileSetupMoreActivity.this.finish();
				AppPrefs.getAppPrefs(ProfileSetupMoreActivity.this)
						.saveBooleanPrefs(AppPrefs.KEY_PROFILE_SETUP_MORE, true);
				navigateToTwoOptions();
			}
		}
		

	}

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		pd.dismiss();
		App.toast(message);
	}
}
