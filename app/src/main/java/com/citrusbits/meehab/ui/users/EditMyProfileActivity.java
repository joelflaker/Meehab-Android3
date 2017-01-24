package com.citrusbits.meehab.ui.users;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.ui.dialog.DobPickerDialog;
import com.citrusbits.meehab.ui.dialog.DobPickerDialog.DatePickerDialogListener;
import com.citrusbits.meehab.ui.dialog.EthenticityPickerDialog;
import com.citrusbits.meehab.ui.dialog.EthenticityPickerDialog.EthenticityDialogListener;
import com.citrusbits.meehab.ui.dialog.HeightPickerDialog;
import com.citrusbits.meehab.ui.dialog.HeightPickerDialog.HeightPickerDialogListener;
import com.citrusbits.meehab.ui.dialog.ImageSelectDialog;
import com.citrusbits.meehab.ui.dialog.ImageSelectDialog.ImageSelectDialogListener;
import com.citrusbits.meehab.ui.dialog.MaritalStatusPickerDialog;
import com.citrusbits.meehab.ui.dialog.MaritalStatusPickerDialog.MaritalStatusDialogListener;
import com.citrusbits.meehab.ui.dialog.SexualOrientationPickerDialog;
import com.citrusbits.meehab.ui.dialog.SexualOrientationPickerDialog.SexualOrientationDialogListener;
import com.citrusbits.meehab.ui.dialog.WeightPickerDialog;
import com.citrusbits.meehab.ui.dialog.WeightPickerDialog.WeightDialogListener;
import com.citrusbits.meehab.images.PicassoBlurTransform;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.ui.HomeActivity;
import com.citrusbits.meehab.ui.SocketActivity;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.CropUtil;
import com.citrusbits.meehab.utils.DateTimeUtils;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.UploadImageUtility;
import com.citrusbits.meehab.utils.UtilityClass;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

public class EditMyProfileActivity extends SocketActivity implements
		OnSocketResponseListener, View.OnClickListener {

	// static variables
	private static final int REQUEST_FROM_CAMERA = 1;
	protected static final int REQUEST_FROM_GALLERY = 2;
	private static final int REQUEST_RESIZE_CROP = 3;

	// picture variables
	private Uri fileUri;
	private File file;
	private Bitmap newbitmap;

	// UI variables
	private Button dobBtn, maritalStatusBtn;
	private CheckBox maleBtn, femaleBtn, otherBtn, datingBtn,
			fellowshippingBtn;
	private ImageView profilePic;
	private String fullPath;
	private EditText genderOtherEdit;

	private UserDatasource userDatasource;
	private UserAccount mUser;
	private UserAccount updateUser;
	private TextView topCenterText;
	private EditText aaStoryEdit;
	private CheckBox sponserToggle;
	private CheckBox haveKidsYesBtn, haveKidsNoBtn, haveKidsNoAnsBtn;
	private EditText occupationEdit;
	private Button weightBtn, heightBtn, sexualOrientBtn, ethnicityBtn,
			soberDateBtn;
	private Dialog pd;
	private String[] ethnicities;
	private EditText sexualOrientOtherEdit;
	private LinearLayout llOther;
	private ImageView ivBlurBg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_my_profile);

		// top back button
		findViewById(R.id.topMenuBtn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});

		// if(App.getInstance().socketIO == null){
		// App.getInstance().initConnectNodeJS();
		// }
		userDatasource = new UserDatasource(this);

		mUser = userDatasource.findUser(AccountUtils.getUserId(this));
		updateUser = new UserAccount();

		pd = UtilityClass.getProgressDialog(this);
		// ui vars
		findViewById(R.id.doneBtn).setOnClickListener(this);
		dobBtn = (Button) findViewById(R.id.dobBtn);
		ivBlurBg = (ImageView) findViewById(R.id.ivBlurBg);
		maritalStatusBtn = (Button) findViewById(R.id.maritalStatusBtn);
		llOther = (LinearLayout) findViewById(R.id.llOther);
		profilePic = (ImageView) findViewById(R.id.profilePic);
		maleBtn = (CheckBox) findViewById(R.id.maleBtn);
		femaleBtn = (CheckBox) findViewById(R.id.femaleBtn);
		otherBtn = (CheckBox) findViewById(R.id.otherBtn);
		genderOtherEdit = (EditText) findViewById(R.id.otherEdit);
		datingBtn = (CheckBox) findViewById(R.id.datingBtn);
		fellowshippingBtn = (CheckBox) findViewById(R.id.fellowshippingBtn);
		aaStoryEdit = (EditText) findViewById(R.id.aaStoryEdit);
		sponserToggle = (CheckBox) findViewById(R.id.sponserToggle);
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

		aaStoryEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					aaStoryEdit.setSelection(0);
				}
			}
		});

		aaStoryEdit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				aaStoryEdit.getParent()
						.requestDisallowInterceptTouchEvent(true);
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_UP:
					aaStoryEdit.getParent().requestDisallowInterceptTouchEvent(
							false);
					break;
				}
				return false;
			}
		});
		heightBtn.setOnClickListener(this);
		weightBtn.setOnClickListener(this);
		sexualOrientBtn.setOnClickListener(this);
		ethnicityBtn.setOnClickListener(this);
		soberDateBtn.setOnClickListener(this);
		haveKidsYesBtn.setOnClickListener(this);
		haveKidsNoBtn.setOnClickListener(this);
		haveKidsNoAnsBtn.setOnClickListener(this);

		profilePic.setOnClickListener(this);
		dobBtn.setOnClickListener(this);
		maritalStatusBtn.setOnClickListener(this);
		maleBtn.setOnClickListener(this);
		femaleBtn.setOnClickListener(this);
		otherBtn.setOnClickListener(this);
		heightBtn.setOnClickListener(this);
		weightBtn.setOnClickListener(this);
		sexualOrientBtn.setOnClickListener(this);
		ethnicityBtn.setOnClickListener(this);
		soberDateBtn.setOnClickListener(this);
		haveKidsYesBtn.setOnClickListener(this);
		haveKidsNoBtn.setOnClickListener(this);
		haveKidsNoAnsBtn.setOnClickListener(this);

		// arrays
		sextualOrientations = getResources().getStringArray(
				R.array.sextualOrientations);
		ethnicities = getResources().getStringArray(R.array.ethnicities);

		genderOtherEdit.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int DRAWABLE_LEFT = 0;
				final int DRAWABLE_TOP = 1;
				final int DRAWABLE_RIGHT = 2;
				final int DRAWABLE_BOTTOM = 3;

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (event.getRawX() >= (genderOtherEdit.getRight() - genderOtherEdit
							.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds()
							.width())) {
						// your action here
						genderOtherEdit.setText("");
						return true;
					}
				}
				return false;
			}
		});

		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) ivBlurBg
				.getLayoutParams();
		int width = DeviceUtils.getDeviceWidth(this);
		params.height = (int) (width * 0.82f);
		ivBlurBg.setLayoutParams(params);

		resetUserInfo();
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
			new HeightPickerDialog(EditMyProfileActivity.this)
					.setHeightPickerDialogListenere(
							new HeightPickerDialogListener() {

								@Override
								public void onDoneClick(
										HeightPickerDialog dialog,
										String heightSelected) {
									dialog.dismiss();
									heightBtn.setText(heightSelected);
									updateUser.setHeight(heightSelected);
								}

								@Override
								public void onCancelClick(
										HeightPickerDialog dialog) {
									dialog.dismiss();
								}
							}, height).show();

			break;
		case R.id.weightBtn:
			String weightText = weightBtn.getText().toString().trim();
			// .replace("lbs", "").trim().trim().replace("lb", "").trim();
			/*
			 * int weight = weightText.isEmpty() ? 50 : Integer
			 * .parseInt(weightText);
			 */
			if (weightText.isEmpty()) {
				weightText = "100 Lbs";
			}
			new WeightPickerDialog(this).setWeightDialogListener(
					new WeightDialogListener() {

						@Override
						public void onDoneClick(WeightPickerDialog dialog,
								String weight) {
							dialog.dismiss();
							// weightBtn.setText(weight + " lbs");
							weightBtn.setText(weight);

							/*
							 * updateUser.setWeight(String.valueOf(weight) +
							 * " lbs");
							 */
							updateUser.setWeight(weight);
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
									dialog.dismiss();
									sexualOrientBtn.setText(sexualOrientation);
									updateUser.setSexualOrientation(sexualOrientation);
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
							dialog.dismiss();
							ethnicityBtn.setText(ethenticity);
							updateUser.setEthnicity(ethenticity);
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

									Calendar dbCal = DateTimeUtils
											.dateToCalendar(dateSelected);
									if (!dbCal.before(Calendar.getInstance())) {
										Toast.makeText(
												EditMyProfileActivity.this,
												"Can not set future date!",
												Toast.LENGTH_SHORT).show();
										return;
									}

									soberDateBtn.setText(dateSelected);
									updateUser.setSoberSence(dateSelected);
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
		case R.id.profilePic:
			// presentImagePicker();

			new ImageSelectDialog(this).setImageSelectDialogListner(
					new ImageSelectDialogListener() {

						@Override
						public void onGalleryClick(ImageSelectDialog dialog) {
							dialog.cancel();
							String state = Environment
									.getExternalStorageState();
							if (Environment.MEDIA_MOUNTED.equals(state)) {
								file = new File(Environment
										.getExternalStorageDirectory(),
										UploadImageUtility.genarateFileName());
							} else {
								file = new File(getFilesDir(),
										UploadImageUtility.genarateFileName());
							}

							Intent photoPickerIntent = new Intent(
									Intent.ACTION_PICK);
							photoPickerIntent.setType("image/jpeg");
							startActivityForResult(photoPickerIntent, 2);
						}

						@Override
						public void onCancelClick(ImageSelectDialog dialog) {
							dialog.cancel();

						}

						@Override
						public void onCameraClick(ImageSelectDialog dialog) {
							dialog.cancel();

							fileUri = UploadImageUtility.genarateUri();
							if (fileUri != null) {
								Intent intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								 intent.putExtra(MediaStore.EXTRA_OUTPUT,
								 fileUri);
								startActivityForResult(intent,
										REQUEST_FROM_CAMERA);
							}
						}
					}).show();

			break;
		case R.id.maleBtn:
			llOther.setVisibility(View.GONE);
			maleBtn.setChecked(true);
			femaleBtn.setChecked(false);
			otherBtn.setChecked(false);
			break;
		case R.id.femaleBtn:
			llOther.setVisibility(View.GONE);
			femaleBtn.setChecked(true);
			maleBtn.setChecked(false);
			otherBtn.setChecked(false);
			break;
		case R.id.otherBtn:
			llOther.setVisibility(View.VISIBLE);
			genderOtherEdit.requestFocus();
			otherBtn.setChecked(true);
			maleBtn.setChecked(false);
			femaleBtn.setChecked(false);
			break;
		case R.id.dobBtn:
			// presentDobPicker();

			String dob = dobBtn.getText().toString().trim();
			new DobPickerDialog(EditMyProfileActivity.this)
					.setDobDialogListener(new DatePickerDialogListener() {

						@Override
						public void onDoneClick(DobPickerDialog dialog,
								String dateSelected) {
							dialog.dismiss();
							dobBtn.setText(dateSelected);
							updateUser.setDateOfBirth(dateSelected);
						}

						@Override
						public void onCancelClick(DobPickerDialog dialog) {
							dialog.dismiss();
						}
					}, dob.isEmpty() ? null : DateTimeUtils.dateToCalendar(dob))
					.show();

			break;
		case R.id.maritalStatusBtn:
			// presentMaritalStatusPicker();

			String maritalStatus = maritalStatusBtn.getText().toString().trim();
			new MaritalStatusPickerDialog(this).setMaritalStatusListenere(
					new MaritalStatusDialogListener() {

						@Override
						public void onDoneClick(
								MaritalStatusPickerDialog dialog,
								String maritalStatusSelected) {
							dialog.dismiss();
							maritalStatusBtn.setText(maritalStatusSelected);
							updateUser.setMaritalStatus(maritalStatusSelected);

						}

						@Override
						public void onCancelClick(
								MaritalStatusPickerDialog dialog) {
							dialog.dismiss();

						}
					}, maritalStatus.isEmpty() ? null : maritalStatus).show();

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

	private void resetUserInfo() {
		if (mUser != null) {

			// UtilityClass.setDefaultIfNull(mUser);
			if (!TextUtils.isEmpty(mUser.getImage())) {

				String userImage = mUser.getImage();
				Picasso.with(EditMyProfileActivity.this).load(userImage)
						.placeholder(R.drawable.profile_pic_border).resize(100, 100)
						.error(R.drawable.profile_pic_border)
						.transform(new PicassoCircularTransform())
						.into(profilePic);

				Picasso.with(this)
						.load(userImage)
						.placeholder(R.drawable.profile_pic_border)
						// .resize(300, 200)
						.error(R.drawable.profile_pic_border)
						.transform(
								new PicassoBlurTransform(
										EditMyProfileActivity.this, 20))
						.into(ivBlurBg);

			} else {

			}

			if ("male".equals(mUser.getGender().toLowerCase())) {
				maleBtn.setChecked(true);
			} else if ("female".equals(mUser.getGender().toLowerCase())) {
				femaleBtn.setChecked(true);
			} else {
				otherBtn.setChecked(true);
				llOther.setVisibility(View.VISIBLE);
				genderOtherEdit.setText(""+mUser.getGender());
			}
			if (!TextUtils.isEmpty(mUser.getDateOfBirth())) {
				dobBtn.setText(mUser.getDateOfBirth());
			}

			if (!TextUtils.isEmpty(mUser.getMaritalStatus())) {
				maritalStatusBtn.setText(mUser.getMaritalStatus());
			}

			if ("Dating & Fellowshipping".trim().toLowerCase()
					.equals(mUser.getIntrestedIn().toLowerCase())) {
				datingBtn.setChecked(true);
				fellowshippingBtn.setChecked(true);
			} else if (EventParams.UPDATE_INTRESTED_IN.Dating.toString()
					.toLowerCase().equals(mUser.getIntrestedIn().toLowerCase())) {
				datingBtn.setChecked(true);
			} else if (EventParams.UPDATE_INTRESTED_IN.Fellowshipping
					.toString().toLowerCase()
					.equals(mUser.getIntrestedIn().toLowerCase())) {
				fellowshippingBtn.setChecked(true);
			}

			aaStoryEdit.setText(mUser.getAboutStory());

			if (mUser.getWillingSponsor().toLowerCase().equals("yes")) {
				sponserToggle.setChecked(true);
			} else {
				sponserToggle.setChecked(false);
			}
			heightBtn.setText(mUser.getHeight());
			weightBtn.setText(mUser.getWeight());
			ethnicityBtn.setText(mUser.getEthnicity());
			occupationEdit.setText(mUser.getAccupation());
			soberDateBtn.setText(mUser.getSoberSence());
			sexualOrientBtn.setText(mUser.getSexualOrientation());

			if ("YES".trim().toLowerCase()
					.equals(mUser.getHaveKids().toLowerCase())) {
				haveKidsYesBtn.setChecked(true);
			} else if ("No".trim().toLowerCase()
					.equals(mUser.getHaveKids().toLowerCase())) {
				haveKidsNoBtn.setChecked(true);
			} else if ("".equals(mUser.getHaveKids())) {
				haveKidsNoAnsBtn.setChecked(true);
			}

		}
	}

	private void updateProfile() {
		if (NetworkUtil.getConnectivityStatus(this) == 0) {
			App.toast("It's seems to be network problem");
			return;
		} else {

			JSONObject params = new JSONObject();
			try {

				if (updateUser.getImage() != null) {
					params.put("image", updateUser.getImage());
				}else {
					Toast.makeText(this,
							"Please select profile image!", Toast.LENGTH_SHORT).show();
					return;
				}

				// gender
				String genderString = null;
				if (maleBtn.isChecked()) {
					genderString = "Male";
				} else if (femaleBtn.isChecked()) {
					genderString = "Female";
				} else if (otherBtn.isChecked()) {
					if (genderOtherEdit.getText().toString().trim().length() > 0) {
						genderString = genderOtherEdit.getText().toString();
					} else {
						genderString = "other";
					}
				}
				if (genderString != null) {
					params.put("gender", genderString);
				}/*else {
					Toast.makeText(this,
							"Please select gender!", Toast.LENGTH_SHORT).show();
					return;
				}*/
				// dob date
				if (updateUser.getDateOfBirth() != null) {
					params.put("date_of_birth", updateUser.getDateOfBirth());
				}else {
					Toast.makeText(this,
							"Please select date of birth!", Toast.LENGTH_SHORT).show();
					return;
				}

				if (updateUser.getMaritalStatus() != null) {
					params.put("marital_status", updateUser.getMaritalStatus());
				}else {
					Toast.makeText(this,
							"Please select marital status!", Toast.LENGTH_SHORT).show();
					return;
				}

				// about Story
				if (aaStoryEdit.getText().toString().trim().length() > 0) {
					// updatedUser.setAboutStory(aaStoryEdit.getText().toString());
					params.put("about_story", aaStoryEdit.getText().toString());
				}

				String accupation = occupationEdit.getText().toString().trim();

				if (!accupation.isEmpty()) {
					params.put("accupation", accupation);
				}else {
					Toast.makeText(this,
							"Please select occupation!", Toast.LENGTH_SHORT).show();
					return;
				}

				// interested in
				String interestString = null;
				if (fellowshippingBtn.isChecked() && datingBtn.isChecked()) {
					interestString = EventParams.UPDATE_INTRESTED_IN.Both
							.toString();
					interestString = "Dating & Fellowshipping";
				} else if (fellowshippingBtn.isChecked()) {
					interestString = EventParams.UPDATE_INTRESTED_IN.Fellowshipping
							.toString();
				} else if (datingBtn.isChecked()) {
					interestString = EventParams.UPDATE_INTRESTED_IN.Dating
							.toString();
				}

				if (interestString != null) {
					params.put("intrested_in", interestString);
				}else {
					Toast.makeText(this,
							"Please select interested in!", Toast.LENGTH_SHORT).show();
					return;
				}

				// willing_sponsor
				// if (sponserToggle.isChecked()) {
				// updatedUser.setAboutStory(aaStoryEdit.getText().toString());
				params.put("willing_sponsor", sponserToggle.isChecked() ? "Yes"
						: "No");
				// }

				// Toast.makeText(this, itemName,
				// Toast.LENGTH_SHORT).show();

				// update fields
				// height
				if (updateUser.getHeight() != null) {
					params.put("height", updateUser.getHeight());
				}else {
					Toast.makeText(this,
							"Please select height!", Toast.LENGTH_SHORT).show();
					return;
				}
				// weight
				if (updateUser.getWeight() != null) {
					params.put("weight", updateUser.getWeight());
				}else {
					Toast.makeText(this,
							"Please select weight!", Toast.LENGTH_SHORT).show();
					return;
				}
				// sexual_orientation
				if (updateUser.getSexualOrientation() != null) {
					params.put("sexual_orientation",
							updateUser.getSexualOrientation());
				}else {
					Toast.makeText(this,
							"Please select sexual orientation!", Toast.LENGTH_SHORT).show();
					return;
				}

				// get Occupation
				if (updateUser.getWeight() != null) {
					params.put("weight", updateUser.getWeight());
				}

				// getEthnicity
				if (updateUser.getEthnicity() != null) {
					params.put("ethnicity", updateUser.getEthnicity());
				}else {
					Toast.makeText(this,
							"Please enter ethnicity!", Toast.LENGTH_SHORT).show();
					return;
				}

				// sober date
				if (updateUser.getSoberSence() != null) {
					params.put("sober_sence", updateUser.getSoberSence());
				}else {
					Toast.makeText(this,
							"Please select sober date!", Toast.LENGTH_SHORT).show();
					return;
				}

				// have kids : default noans
				String hasKidsString = null;
				if (haveKidsYesBtn.isChecked()) {
					hasKidsString = "YES";
				} else if (haveKidsNoBtn.isChecked()) {
					hasKidsString = "No";
				} else if (haveKidsNoAnsBtn.isChecked()) {
					hasKidsString = "";
				}

				if(hasKidsString != null){
					params.put("have_kids", hasKidsString);
				}

			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}

			if (params != null && params.length() > 0) {
				pd.show();
				socketService.updateAccount(params);
			}
		}
		//
		// // intent = new Intent(this,ProfileSetupMoreActivity.class);
		// // startActivity(intent);
		// // overridePendingTransition(R.anim.activity_in,
		// R.anim.activity_out);
		// // setResult(RESULT_OK);
		// // ProfileSetupActivity.this.finish();
	}

	private void presentMaritalStatusPicker() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.picker_age, null);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setTitle("Select Marital Status");

		final String[] maritalStatuses = getResources().getStringArray(
				R.array.maritalStatuses);
		final NumberPicker np = (NumberPicker) dialogView
				.findViewById(R.id.picker);
		np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np.setMinValue(0);
		np.setMaxValue(4);
		np.setDisplayedValues(maritalStatuses);
		np.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);

		dialogBuilder.setPositiveButton("Set",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						maritalStatusBtn.setText(maritalStatuses[np.getValue()]);
						updateUser.setMaritalStatus((maritalStatuses[np
								.getValue()]));
					}
				});

		dialogBuilder.setNegativeButton("Cancel", null);

		dialogBuilder.show();
	}

	/**
	 * 
	 */
	private void presentDobPicker() {
		final Calendar now = Calendar.getInstance();
		new DatePickerDialog(this, mDateSetListener, now.get(Calendar.YEAR),
				now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();

	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Calendar newDate = Calendar.getInstance();
			newDate.clear();
			newDate.set(year, monthOfYear, dayOfMonth);
			String nowAsISO = new SimpleDateFormat("MM/dd/yyyy").format(newDate
					.getTime());
			// String dobString = ""+ year +"";
			dobBtn.setText(nowAsISO);
			updateUser.setDateOfBirth(nowAsISO);
		}
	};
	private String[] sextualOrientations;

	/**
	 * 
	 */
	private void presentSexualOrientationPicker() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.picker_age, null);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setTitle("Select Sexual Orientation");

		final NumberPicker np = (NumberPicker) dialogView
				.findViewById(R.id.picker);
		np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np.setMinValue(0);
		np.setMaxValue(sextualOrientations.length - 1);
		np.setDisplayedValues(sextualOrientations);
		np.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);

		dialogBuilder.setPositiveButton("Set",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (np.getValue() == sextualOrientations.length - 1) {
							sexualOrientOtherEdit.setVisibility(View.VISIBLE);
							updateUser
									.setSexualOrientation(sextualOrientations[np
											.getValue()]);
						} else {
							sexualOrientOtherEdit.setVisibility(View.GONE);
							sexualOrientBtn.setText(sextualOrientations[np
									.getValue()]);
						}
						updateUser.setSexualOrientation(sextualOrientations[np
								.getValue()]);
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

		final NumberPicker np = (NumberPicker) dialogView
				.findViewById(R.id.picker);
		np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np.setMinValue(0);
		np.setMaxValue(ethnicities.length - 1);
		np.setDisplayedValues(ethnicities);
		np.setWrapSelectorWheel(false);
		// np.setOnValueChangedListener(this);

		dialogBuilder.setPositiveButton("Set",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ethnicityBtn.setText(ethnicities[np.getValue()]);
						updateUser.setEthnicity(ethnicities[np.getValue()]);
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
						updateUser.setSoberSence(nowAsISO);
					}
				}, now.get(Calendar.YEAR), now.get(Calendar.MONTH),
				now.get(Calendar.DAY_OF_MONTH)).show();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(fileUri != null){
			outState.putString("fileUri",fileUri.getPath());
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onRestoreInstanceState(savedInstanceState, persistentState);
		if (savedInstanceState.containsKey("fileUri")){
			fileUri = Uri.parse(savedInstanceState.getString("fileUri"));
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_FROM_CAMERA) {
				try {

//					if (data.getExtras().get("data") != null) {

//						try {
//							FileOutputStream fileOutputStream = new FileOutputStream(fileUri.getPath());
//							Bitmap bm = (Bitmap) data.getExtras().get("data");
//							bm.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
//							fileOutputStream.flush();
//							fileOutputStream.close();
//
//						} catch (Exception e) {
//							e.printStackTrace();
//							return;
//						}
						file = new File(fileUri.getPath());

						doScanFile(file.getAbsolutePath());

						File cropFilePath = new File(UploadImageUtility
								.genarateUri().getPath());
						fullPath = cropFilePath.getAbsolutePath();

						Uri outputUri = Uri.fromFile(cropFilePath);
						Crop.of(fileUri,outputUri).asSquare()
								.withMaxSize(400, 400)
								.start(this, REQUEST_RESIZE_CROP);
						// Intent intent = new Intent(this,
						// com.droid4you.util.cropimage.CropImage.class);
						// intent.putExtra("image-path", fullPath);
						// intent.putExtra("scale", true);
						// intent.putExtra("aspectX", 1);
						// intent.putExtra("aspectY", 1);
						// intent.putExtra("outputX", 500);
						// intent.putExtra("outputY", 500);
						// intent.putExtra("return-data", true);
						// startActivityForResult(intent, 3);

//					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == REQUEST_FROM_GALLERY) {

				try {

					InputStream inputStream = getContentResolver()
							.openInputStream(data.getData());
					FileOutputStream fileOutputStream = new FileOutputStream(
							file);
					copyStream(inputStream, fileOutputStream);
					fileOutputStream.close();
					inputStream.close();

					// newbitmap = BitmapFactory.decodeFile(file.getPath());
					// imgProfile.setImageBitmap(newbitmap);

					File cropFilePath = new File(UploadImageUtility
							.genarateUri().getPath());
					fullPath = cropFilePath.getAbsolutePath();

					Uri outputUri = Uri.fromFile(cropFilePath);
					Crop.of(Uri.fromFile(file),outputUri).asSquare()
							.withMaxSize(400, 400)
							.start(this, REQUEST_RESIZE_CROP);

					// Intent intent = new Intent(this,
					// com.droid4you.util.cropimage.CropImage.class);
					// intent.putExtra("image-path", fullPath);
					// intent.putExtra("scale", true);
					// intent.putExtra("aspectX", 1);
					// intent.putExtra("aspectY", 1);
					// intent.putExtra("outputX", 500);
					// intent.putExtra("outputY", 500);
					// intent.putExtra("return-data", true);
					// startActivityForResult(intent, 3);
				} catch (Exception e) {

				}

			} else if (requestCode == REQUEST_RESIZE_CROP) {
				newbitmap = fixRotation((Uri) data
						.getParcelableExtra(MediaStore.EXTRA_OUTPUT));

				File imageFileFixed = new File(getCacheDir(),"image_fixed.png");
				try {
					FileOutputStream fileOutputStream = new FileOutputStream(
							imageFileFixed);
					newbitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
					fileOutputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				// profilePic.setImageBitmap(newbitmap);
				Bitmap circularBitamp = new PicassoCircularTransform()
						.transform(newbitmap.copy(Bitmap.Config.ARGB_8888,false));
				Bitmap blurBitmap = new PicassoBlurTransform(
						EditMyProfileActivity.this, 20).transform(newbitmap.copy(Bitmap.Config.ARGB_8888,false));

				ivBlurBg.setImageBitmap(blurBitmap);
				profilePic.setImageBitmap(circularBitamp);

				file.delete();
				try {
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					newbitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
					newbitmap.recycle();
					byte[] byteArray = output.toByteArray();
					// updatedUser.setImage("data:image/png;base64,"+Base64.encodeToString(byteArray,
					// Base64.DEFAULT));

					updateUser.setImage("data:image/png;base64,"
							+ Base64.encodeToString(byteArray, Base64.NO_WRAP));
					byteArray = null;
				} catch (Exception e) {
					e.printStackTrace();
				}

				imageFileFixed.delete();
			}
		}
	}

	private Bitmap fixRotation(Uri parcelableExtra) {
		int exifRotation = CropUtil.getExifRotation(CropUtil.getFromMediaUri(
				this, getContentResolver(), parcelableExtra));
		Bitmap rotateBitmap = null;
		InputStream is = null;
		try {
			is = getContentResolver().openInputStream(parcelableExtra);
			BitmapFactory.Options option = new BitmapFactory.Options();
			Bitmap bmp = BitmapFactory.decodeStream(is, null, option);
			Matrix m = new Matrix();
			m.postRotate(exifRotation);
			rotateBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
					bmp.getHeight(), m, true);
			// bmp.recycle();

		} catch (IOException e) {
		} catch (OutOfMemoryError e) {
		} finally {
			CropUtil.closeSilently(is);
		}
		return rotateBitmap;
	}

	void doScanFile(String fileName) {
		String[] filesToScan = { fileName };
		MediaScannerConnection.scanFile(this, filesToScan, null,
				new MediaScannerConnection.OnScanCompletedListener() {

					@Override
					public void onScanCompleted(String path, Uri uri) {
					}
				});
	}

	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}
//
//	/**
//	 *
//	 */
//	private void presentImagePicker() {
//		final CharSequence[] options = { "Take Photo", "Choose from Gallery",
//				"Cancel" };
//
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("Add Photo!");
//		builder.setItems(options, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int item) {
//				dialog.dismiss();
//				if (options[item].equals("Take Photo")) {
//
//					// Toast.makeText(getActivity(), "Button clicked",
//					// Toast.LENGTH_SHORT).show();
//					// file = getOutputMediaFile(1);
//					fileUri = UploadImageUtility.genarateUri();
//					if (fileUri != null) {
//						Intent intent = new Intent(
//								MediaStore.ACTION_IMAGE_CAPTURE);
//						 intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//						startActivityForResult(intent, REQUEST_FROM_CAMERA);
//					}
//				} else if (options[item].equals("Choose from Gallery")) {
//					String state = Environment.getExternalStorageState();
//					if (Environment.MEDIA_MOUNTED.equals(state)) {
//						file = new File(Environment
//								.getExternalStorageDirectory(),
//								UploadImageUtility.genarateFileName());
//					} else {
//						file = new File(getFilesDir(), UploadImageUtility
//								.genarateFileName());
//					}
//
//					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//					photoPickerIntent.setType("image/*");
//					startActivityForResult(photoPickerIntent, 2);
//
//				}
//			}
//		});
//		builder.show();
//	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		pd.dismiss();

		if (event.equals(EventParams.EVENT_USER_UPDATE)) {
			App.toast("Updated successfully");
			EditMyProfileActivity.this.sendBroadcast(new Intent(
					HomeActivity.ACTION_PROFILE_UPDATE));
			EditMyProfileActivity.this.finish();
		}

	}

	@Override
	public void onSocketResponseFailure(String onEvent,String message) {
		pd.dismiss();
		App.toast(message);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_back_in,
				R.anim.activity_back_out);
	}

	@Override
	public void onBackendConnected() {

	}
}
