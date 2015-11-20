package com.citrusbits.meehab;

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
			String weightText = weightBtn.getText().toString().trim()
					.replace("lbs", "").trim().trim().replace("lb", "").trim();
			;
			int weight = weightText.isEmpty() ? 50 : Integer
					.parseInt(weightText);
			new WeightPickerDialog(this).setWeightDialogListener(
					new WeightDialogListener() {

						@Override
						public void onDoneClick(WeightPickerDialog dialog,
								int weight) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							weightBtn.setText(weight + " lbs");
							user.setWeight(String.valueOf(weight) + " lbs");
						}

						@Override
						public void onCancelClick(WeightPickerDialog dialog) {
							// TODO Auto-generated method stub

							dialog.dismiss();

						}
					}, weight).show();
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
					.setDobDialogListener(new DatePickerDialogListener() {

						@Override
						public void onDoneClick(DobPickerDialog dialog,
								String dateSelected) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							soberDateBtn.setText(dateSelected);
							user.setSoberSence(dateSelected);
						}

						@Override
						public void onCancelClick(DobPickerDialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					}, date.isEmpty() ? null : getCalendarFromDb(date)).show();
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
				String hasKidsString = "";
				if (haveKidsYesBtn.isChecked()) {
					hasKidsString = "yes";
				} else if (haveKidsNoBtn.isChecked()) {
					hasKidsString = "no";
				} else if (haveKidsNoAnsBtn.isChecked()) {
					hasKidsString = "";
				}

				if (hasKidsString.isEmpty()) {
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
				socketService.updateAccount(params);
			}
		}

		// intent = new Intent(this,ProfileSetupMoreActivity.class);
		// startActivity(intent);
		// overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
		// setResult(RESULT_OK);
		// ProfileSetupActivity.this.finish();
	}

	public void presentHeightPicker() {

		final String[] values = new String[] { "3'", "4'", "5'", "6'", "7'" };
		final String[] valuesInches = new String[] { "0''", "1''", "2''",
				"3''", "4''", "5''", "6''", "7''", "8''", "9''", "10''", "11''" };

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.height_picker, null);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setTitle("Select Height");

		final NumberPicker np = (NumberPicker) dialogView
				.findViewById(R.id.heightPickerFeet);
		np.setMaxValue(4);
		np.setMinValue(0);
		np.setDisplayedValues(values);
		// String index = (String) btnHeight.getTag();
		// if (index != null) {
		// np.setValue(values[Integer.parseInt(index)]);
		// }
		np.setWrapSelectorWheel(false);

		final NumberPicker npInches = (NumberPicker) dialogView
				.findViewById(R.id.heightPickerInches);
		npInches.setMaxValue(11);
		npInches.setMinValue(0);
		npInches.setDisplayedValues(valuesInches);
		// String index = (String) btnHeight.getTag();
		// if (index != null) {
		// np.setValue(values[Integer.parseInt(index)]);
		// }
		npInches.setWrapSelectorWheel(false);

		dialogBuilder.setPositiveButton("Set",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// btnHeight.setTag(np.getValue());
						heightBtn.setText(values[np.getValue()] + " "
								+ valuesInches[npInches.getValue()]);
						user.setHeight(values[np.getValue()] + " "
								+ valuesInches[npInches.getValue()]);
					}
				});

		dialogBuilder.setNegativeButton("Cancel", null);

		dialogBuilder.show();
	}

	public void presentWeightPicker() {

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.picker_weight, null);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setTitle("Select Weight");

		final NumberPicker np = (NumberPicker) dialogView
				.findViewById(R.id.picker);
		np.setMaxValue(500);
		np.setMinValue(50);
		// String str = btnWeight.getText().toString();
		// if (str.compareTo("Select") != 0) {
		// String weight = str.substring(0, str.indexOf(" "));
		// np.setValue(Integer.parseInt(weight));
		// }
		np.setWrapSelectorWheel(false);

		dialogBuilder.setPositiveButton("Set",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						weightBtn.setText(String.valueOf(np.getValue())
								+ " lbs");
						user.setWeight(String.valueOf(np.getValue()) + " lbs");
					}
				});

		dialogBuilder.setNegativeButton("Cancel", null);

		dialogBuilder.show();
	}

	/**
	 * 
	 */
	private void presentSexualOrientationPicker() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.picker_age, null);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setTitle("Select Sexual Orientation");

		final String[] values = getResources().getStringArray(
				R.array.sextualOrientations);
		final NumberPicker np = (NumberPicker) dialogView
				.findViewById(R.id.picker);
		np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np.setMinValue(0);
		np.setMaxValue(values.length - 1);
		np.setDisplayedValues(values);
		np.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);

		dialogBuilder.setPositiveButton("Set",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (np.getValue() == values.length - 1) {
							sexualOrientOtherEdit.setVisibility(View.VISIBLE);
							user.setSexualOrientation(values[np.getValue()]);
						} else {
							sexualOrientOtherEdit.setVisibility(View.GONE);
							sexualOrientBtn.setText(values[np.getValue()]);
							user.setSexualOrientation(values[np.getValue()]);
						}
					}
				});

		dialogBuilder.setNegativeButton("Cancel", null);

		dialogBuilder.show();
	}

	private void presentEthnicityPicker() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.picker_age, null);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setTitle("Select Sexual Orientation");

		final String[] values = getResources().getStringArray(
				R.array.ethnicities);
		final NumberPicker np = (NumberPicker) dialogView
				.findViewById(R.id.picker);
		np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np.setMinValue(0);
		np.setMaxValue(values.length - 1);
		np.setDisplayedValues(values);
		np.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);

		dialogBuilder.setPositiveButton("Set",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ethnicityBtn.setText(values[np.getValue()]);
						user.setEthnicity(values[np.getValue()]);
					}
				});

		dialogBuilder.setNegativeButton("Cancel", null);

		dialogBuilder.show();
	}

	private void presentSoberDatePicker() {
		final Calendar now = Calendar.getInstance();
		new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						Calendar newDate = Calendar.getInstance();
						newDate.clear();
						newDate.set(year, monthOfYear, dayOfMonth);
						String nowAsISO = new SimpleDateFormat("MM/dd/yyyy")
								.format(newDate.getTime());
						// String dobString = ""+ year +"";
						soberDateBtn.setText(nowAsISO);
						user.setSoberSence(nowAsISO);
					}
				}, now.get(Calendar.YEAR), now.get(Calendar.MONTH),
				now.get(Calendar.DAY_OF_MONTH)).show();

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
		pd.dismiss();
		ProfileSetupMoreActivity.this.finish();
		AppPrefs.getAppPrefs(ProfileSetupMoreActivity.this).saveBooleanPrefs(
				AppPrefs.KEY_PROFILE_SETUP_MORE, true);
		navigateToTwoOptions();
	}

	@Override
	public void onSocketResponseFailure(String message) {
		pd.dismiss();
		App.alert(message);
	}
}
