package com.citrusbits.meehab;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.dialog.DobPickerDialog;
import com.citrusbits.meehab.dialog.DobPickerDialog.DatePickerDialogListener;
import com.citrusbits.meehab.dialog.EthenticityPickerDialog;
import com.citrusbits.meehab.dialog.EthenticityPickerDialog.EthenticityDialogListener;
import com.citrusbits.meehab.dialog.HeightPickerDialog;
import com.citrusbits.meehab.dialog.HeightPickerDialog.HeightPickerDialogListener;
import com.citrusbits.meehab.dialog.SexualOrientationPickerDialog;
import com.citrusbits.meehab.dialog.SexualOrientationPickerDialog.SexualOrientationDialogListener;
import com.citrusbits.meehab.dialog.WeightPickerDialog;
import com.citrusbits.meehab.dialog.WeightPickerDialog.WeightDialogListener;
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
	private ProgressDialog pd;
	private EditText sexualOrientOtherEdit;
	
	private boolean updateRequest=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_setup_more);

		// if(App.getInstance().socketIO == null){
		// App.getInstance().initConnectNodeJS();
		// }
		userDatasource = new UserDatasource(this);
		userDatasource.open();

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
									// TODO Auto-generated method stub
									dialog.dismiss();
									heightBtn.setText(heightSelected);
									user.setHeight(heightSelected);
								}

								@Override
								public void onCancelClick(
										HeightPickerDialog dialog) {
									// TODO Auto-generated method stub
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
							// TODO Auto-generated method stub
							dialog.dismiss();
							weightBtn.setText(weight);
							user.setWeight(weight);
						}

						@Override
						public void onCancelClick(WeightPickerDialog dialog) {
							// TODO Auto-generated method stub

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
									// TODO Auto-generated method stub
									dialog.dismiss();
									sexualOrientBtn.setText(sexualOrientation);
									user.setSexualOrientation(sexualOrientation);
								}

								@Override
								public void onCancelClick(
										SexualOrientationPickerDialog dialog) {
									// TODO Auto-generated method stub
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
							// TODO Auto-generated method stub
							dialog.dismiss();
							ethnicityBtn.setText(ethenticity);
							user.setEthnicity(ethenticity);
							;
						}

						@Override
						public void onCancelClick(EthenticityPickerDialog dialog) {
							// TODO Auto-generated method stub
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
									// TODO Auto-generated method stub
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
									// TODO Auto-generated method stub
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
			// TODO Auto-generated catch block
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
			App.alert(getString(R.string.network_problem));
			return;
		} else {

			JSONObject params = new JSONObject();
			try {

				// update fields
				// height

				if (user.getHeight() == null) {
					Toast.makeText(ProfileSetupMoreActivity.this,
							"Please set user height!", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				if (user.getHeight() != null) {
					params.put("height", user.getHeight());
				}

				if (user.getWeight() == null) {
					Toast.makeText(ProfileSetupMoreActivity.this,
							"Please set user weight!", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				// weight
				if (user.getWeight() != null) {
					params.put("weight", user.getWeight());
				}

				if (user.getSexualOrientation() == null) {
					Toast.makeText(ProfileSetupMoreActivity.this,
							"Please set sexual orientation!",
							Toast.LENGTH_SHORT).show();
					return;
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

				if (user.getSexualOrientation() == null) {
					Toast.makeText(ProfileSetupMoreActivity.this,
							"Please set sexual orientation!",
							Toast.LENGTH_SHORT).show();
					return;
				}

				String occupation = occupationEdit.getText().toString().trim();
				if (occupation.isEmpty()) {
					Toast.makeText(ProfileSetupMoreActivity.this,
							"Please enter user occupation!", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				// get Occupation

				user.setAccupation(occupation);

				params.put("accupation", occupationEdit.getText().toString());

				if (user.getEthnicity() == null) {
					Toast.makeText(ProfileSetupMoreActivity.this,
							"Please set user ethenticity!", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				// getEthnicity
				if (user.getEthnicity() != null) {
					params.put("ethnicity", user.getEthnicity());
				}

				// sober date

				if (user.getSoberSence() == null) {
					Toast.makeText(ProfileSetupMoreActivity.this,
							"Please set Sober sence!", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (user.getSoberSence() != null) {
					params.put("sober_sence", user.getSoberSence());
				}

				// have kids : default noans
				String hasKidsString = "trick";
				if (haveKidsYesBtn.isChecked()) {
					hasKidsString = "YES";
				} else if (haveKidsNoBtn.isChecked()) {
					hasKidsString = "No";
				} else if (haveKidsNoAnsBtn.isChecked()) {
					hasKidsString = "";
				}

				if (hasKidsString.equals("trick")) {
					Toast.makeText(ProfileSetupMoreActivity.this,
							"Please set have kids", Toast.LENGTH_SHORT).show();
					return;
				}

				params.put("have_kids", hasKidsString);
				// Toast.makeText(this, itemName,
				// Toast.LENGTH_SHORT).show();
				// jobj.put("phone", UtilityClass
				// .phoneNumberNormal(strPhoneNumber));

			} catch (JSONException e) {
				e.printStackTrace();
				params = null;
			}

			if (params != null && params.length() > 0) {
				pd.show();
				updateRequest=true;
				socketService.updateAccount(params);
			}
		}

		// intent = new Intent(this,ProfileSetupMoreActivity.class);
		// startActivity(intent);
		// overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
		// setResult(RESULT_OK);
		// ProfileSetupActivity.this.finish();
	}

	@Override
	void onBackendConnected() {

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
	public void onSocketResponseFailure(String message) {
		pd.dismiss();
		App.alert(message);
	}
}
