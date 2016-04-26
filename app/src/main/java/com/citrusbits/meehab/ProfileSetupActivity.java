package com.citrusbits.meehab;

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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.dialog.DobPickerDialog;
import com.citrusbits.meehab.dialog.DobPickerDialog.DatePickerDialogListener;
import com.citrusbits.meehab.dialog.ImageSelectDialog;
import com.citrusbits.meehab.dialog.ImageSelectDialog.ImageSelectDialogListener;
import com.citrusbits.meehab.dialog.MaritalStatusPickerDialog;
import com.citrusbits.meehab.dialog.MaritalStatusPickerDialog.MaritalStatusDialogListener;
import com.citrusbits.meehab.images.PicassoBlurTransform;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DateTimeUtils;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UploadImageUtility;
import com.citrusbits.meehab.utils.UtilityClass;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropUtil;

public class ProfileSetupActivity extends SocketActivity implements
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
	private Button maritalStatusBtn;
	private TextView createBtn, dobBtn;
	private CheckBox maleBtn, femaleBtn, otherBtn, datingBtn,
			fellowshippingBtn;
	private ImageView profilePic;
	private String fullPath;
	private EditText otherEdit;

	private UserDatasource userDatasource;
	private UserAccount mUser;
	private UserAccount user;
	private TextView topCenterText;
	private EditText aaStoryEdit;
	private CheckBox sponserToggle;

	private ImageView ivBlurBg;
	private Dialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_setup);

		newbitmap = App.getInstance().globleBitmap;

		userDatasource = new UserDatasource(this);

		mUser = userDatasource.findUser(AccountUtils.getUserId(this));
		user = new UserAccount();

		pd = UtilityClass.getProgressDialog(this);

		topCenterText = (TextView) findViewById(R.id.topCenterText);
		dobBtn = (Button) findViewById(R.id.dobBtn);
		maritalStatusBtn = (Button) findViewById(R.id.maritalStatusBtn);
		createBtn = (TextView) findViewById(R.id.createBtn);
		profilePic = (ImageView) findViewById(R.id.profilePic);
		maleBtn = (CheckBox) findViewById(R.id.maleBtn);
		femaleBtn = (CheckBox) findViewById(R.id.femaleBtn);
		otherBtn = (CheckBox) findViewById(R.id.otherBtn);
		otherEdit = (EditText) findViewById(R.id.otherEdit);
		datingBtn = (CheckBox) findViewById(R.id.datingBtn);
		fellowshippingBtn = (CheckBox) findViewById(R.id.fellowshippingBtn);
		aaStoryEdit = (EditText) findViewById(R.id.aaStoryEdit);
		sponserToggle = (CheckBox) findViewById(R.id.sponserToggle);

		aaStoryEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus) {
					aaStoryEdit.setSelection(0);
				}
			}
		});

		aaStoryEdit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

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

		profilePic.setOnClickListener(this);
		createBtn.setOnClickListener(this);
		dobBtn.setOnClickListener(this);
		maritalStatusBtn.setOnClickListener(this);

		maleBtn.setOnClickListener(onToggleClickListener);
		femaleBtn.setOnClickListener(onToggleClickListener);
		otherBtn.setOnClickListener(onToggleClickListener);

		if (App.getInstance().globleBitmap != null) {
			profilePic.setImageBitmap(App.getInstance().globleBitmap);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			newbitmap.compress(CompressFormat.PNG, 100, output);
			byte[] byteArray = output.toByteArray();
			user.setImage(EventParams.BASE64_IMAGE_PNG_STRING
					+ Base64.encodeToString(byteArray, Base64.NO_WRAP));
		}

		ivBlurBg = (ImageView) findViewById(R.id.ivBlurBg);

		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) ivBlurBg
				.getLayoutParams();
		int width = DeviceUtils.getDeviceWidth(this);
		params.height = (int) (width * 0.82f);
		ivBlurBg.setLayoutParams(params);

		otherEdit.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int DRAWABLE_LEFT = 0;
				final int DRAWABLE_TOP = 1;
				final int DRAWABLE_RIGHT = 2;
				final int DRAWABLE_BOTTOM = 3;

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (event.getRawX() >= (otherEdit.getRight() - otherEdit
							.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds()
							.width())) {
						// your action here
						otherEdit.setText("");
						return true;
					}
				}
				return false;
			}
		});

		if (mUser != null) {
			topCenterText.setText(mUser.getUsername());
		}

	}

	OnClickListener onToggleClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			boolean isChecked = ((CheckBox) v).isChecked();
			switch (v.getId()) {
			case R.id.maleBtn:
				otherEdit.setVisibility(View.GONE);

				if (isChecked) {
					femaleBtn.setChecked(false);
					otherBtn.setChecked(false);
				}

				break;
			case R.id.femaleBtn:
				otherEdit.setVisibility(View.GONE);
				if (isChecked) {
					maleBtn.setChecked(false);
					otherBtn.setChecked(false);
				}

				break;
			case R.id.otherBtn:
				if (isChecked) {
					otherEdit.setVisibility(View.VISIBLE);
					otherEdit.requestFocus();
					maleBtn.setChecked(false);
					femaleBtn.setChecked(false);
				} else {
					otherEdit.setVisibility(View.GONE);
				}

				break;

			}
		}
	};

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

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.createBtn:
			updateProfile();
			break;
		case R.id.profilePic:
			// presentImagePicker();

			new ImageSelectDialog(this).setImageSelectDialogListner(
					new ImageSelectDialogListener() {

						@Override
						public void onGalleryClick(ImageSelectDialog dialog) {
							// TODO Auto-generated method stub
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
							// TODO Auto-generated method stub
							dialog.cancel();

						}

						@Override
						public void onCameraClick(ImageSelectDialog dialog) {
							// TODO Auto-generated method stub
							dialog.cancel();

							fileUri = UploadImageUtility.genarateUri();
							if (fileUri != null) {
								Intent intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								// intent.putExtra(MediaStore.EXTRA_OUTPUT,
								// fileUri);
								startActivityForResult(intent,
										REQUEST_FROM_CAMERA);
							}
						}
					}).show();

			break;

		case R.id.dobBtn:
			// presentDobPicker();
			String dob = dobBtn.getText().toString().trim();
			new DobPickerDialog(ProfileSetupActivity.this)
					.setDobDialogListener(new DatePickerDialogListener() {

						@Override
						public void onDoneClick(DobPickerDialog dialog,
								String dateSelected) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							
							Calendar dbCal=DateTimeUtils.dateToCalendar(dateSelected);
							if(!dbCal.before(Calendar.getInstance())){
								Toast.makeText(ProfileSetupActivity.this, "Can not set future date!", Toast.LENGTH_SHORT).show();
								return;
							}
							
							dobBtn.setText(dateSelected);
							user.setDateOfBirth(dateSelected);
						}

						@Override
						public void onCancelClick(DobPickerDialog dialog) {
							// TODO Auto-generated method stub
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
							// TODO Auto-generated method stub
							dialog.dismiss();
							maritalStatusBtn.setText(maritalStatusSelected);
							user.setMaritalStatus(maritalStatusSelected);

						}

						@Override
						public void onCancelClick(
								MaritalStatusPickerDialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();

						}
					}, maritalStatus.isEmpty() ? null : maritalStatus).show();
			break;
		default:
			break;
		}

	}

	private void updateProfile() {
		if (NetworkUtil.getConnectivityStatus(this) == 0) {
			App.toast("It's seems to be network problem");
			return;
		} else {

			JSONObject params = new JSONObject();
			try {

				if (user.getImage() == null) {
					Toast.makeText(ProfileSetupActivity.this,
							"Please pick user image!", Toast.LENGTH_SHORT)
							.show();

					return;
				}

				if (user.getImage() != null) {
					params.put("image", user.getImage());
					Log.d("Image", user.getImage());
				}

				// gender
				String genderString = null;
				if (maleBtn.isChecked()) {
					genderString = "Male";
				} else if (femaleBtn.isChecked()) {
					genderString = "Female";
				} else if (otherBtn.isChecked()) {
					if (otherEdit.getText().toString().trim().length() > 0) {
						genderString = otherEdit.getText().toString();
					}
				}

				if (genderString == null) {
					Toast.makeText(ProfileSetupActivity.this,
							"Please select gender!", Toast.LENGTH_SHORT).show();
					return;
				}

				if (genderString != null) {
					params.put("gender", genderString);
				}

				// dob date

				if (user.getDateOfBirth() == null) {
					Toast.makeText(ProfileSetupActivity.this,
							"Please enter a date of birth!", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				if (user.getDateOfBirth() != null) {
					params.put("date_of_birth", user.getDateOfBirth());
				}

				if (user.getMaritalStatus() == null) {
					Toast.makeText(ProfileSetupActivity.this,
							"Please select marital status", Toast.LENGTH_SHORT)
							.show();

					return;
				}

				if (user.getMaritalStatus() != null) {
					params.put("marital_status", user.getMaritalStatus());
				}

				// interested in
				String interestString = null;
				if (fellowshippingBtn.isChecked() && datingBtn.isChecked()) {
					/*interestString = EventParams.UPDATE_INTRESTED_IN.Both
							.toString();*/
					interestString="Dating & Fellowshipping";
				} else if (fellowshippingBtn.isChecked()) {
					interestString = EventParams.UPDATE_INTRESTED_IN.Fellowshipping
							.toString();
				} else if (datingBtn.isChecked()) {
					interestString = EventParams.UPDATE_INTRESTED_IN.Dating
							.toString();
				}
				if (interestString != null) {
					params.put("intrested_in", interestString);
				}

				if (interestString == null) {
					Toast.makeText(ProfileSetupActivity.this,
							"Please select interestedin!", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				String aaStory = aaStoryEdit.getText().toString().trim();

				if (aaStory.isEmpty()) {
					Toast.makeText(ProfileSetupActivity.this,
							"Please enter aa Story!", Toast.LENGTH_SHORT)
							.show();

					return;
				}

				// about Story
				if (aaStoryEdit.getText().toString().trim().length() > 0) {
					// updatedUser.setAboutStory(aaStoryEdit.getText().toString());
					params.put("about_story", aaStoryEdit.getText().toString());
				}

				// willing_sponsor
				// if (sponserToggle.isChecked()) {
				// updatedUser.setAboutStory(aaStoryEdit.getText().toString());
				params.put("willing_sponsor", sponserToggle.isChecked() ? "Yes"
						: "No");
				// }

				// Toast.makeText(this, itemName,
				// Toast.LENGTH_SHORT).show();

				// jobj.put("phone", UtilityClass
				// .phoneNumberNormal(strPhoneNumber));

				if (params != null && params.length() > 0) {
					if (!NetworkUtils.isNetworkAvailable(this)) {
						App.toast(getString(R.string.no_internet_connection));
						return;
					}
					socketService.updateAccount(params);
					pd.show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
				params = null;
			}

		}
		// intent = new Intent(this,ProfileSetupMoreActivity.class);
		// startActivity(intent);
		// overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
		// setResult(RESULT_OK);
		// ProfileSetupActivity.this.finish();
		return;
	}

	/**
	 * 
	 */
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
						user.setMaritalStatus((maritalStatuses[np.getValue()]));
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
			user.setDateOfBirth(nowAsISO);
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 22) {
				finish();
			} else if (requestCode == REQUEST_FROM_CAMERA) {
				try {

					if (data.getData() != null) {

						file = new File(data.getData().getPath());
						fullPath = file.getAbsolutePath();

						doScanFile(fullPath);

						File cropFilePath = new File(UploadImageUtility
								.genarateUri().getPath());
						fullPath = cropFilePath.getAbsolutePath();

						Log.d("cropfile::::", fullPath);
						Uri outputUri = Uri.fromFile(cropFilePath);
						new Crop(data.getData()).output(outputUri).asSquare()
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

					}

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

					File cropFilePath = new File(UploadImageUtility
							.genarateUri().getPath());
					fullPath = cropFilePath.getAbsolutePath();

					if (fullPath.toLowerCase().endsWith("gif")) {
						Toast.makeText(ProfileSetupActivity.this,
								"Gif Image file is  not supported!",
								Toast.LENGTH_SHORT).show();
						return;
					}

					Uri outputUri = Uri.fromFile(cropFilePath);
					new Crop(Uri.fromFile(file)).output(outputUri).asSquare()
							.withMaxSize(400, 400)
							.start(this, REQUEST_RESIZE_CROP);
				} catch (Exception e) {

				}

			} else if (requestCode == REQUEST_RESIZE_CROP) {
				newbitmap = fixRotation((Uri) data
						.getParcelableExtra(MediaStore.EXTRA_OUTPUT));

				Bitmap circularBitamp;
				circularBitamp = new PicassoCircularTransform()
						.transform(newbitmap);

				newbitmap = fixRotation((Uri) data
						.getParcelableExtra(MediaStore.EXTRA_OUTPUT));

				Bitmap blurBitmap;
				blurBitmap = new PicassoBlurTransform(
						ProfileSetupActivity.this, 20).transform(newbitmap);

				newbitmap = fixRotation((Uri) data
						.getParcelableExtra(MediaStore.EXTRA_OUTPUT));

				ivBlurBg.setImageBitmap(blurBitmap);
				profilePic.setImageBitmap(circularBitamp);

				file.delete();
				try {
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					newbitmap.compress(CompressFormat.PNG, 100, output);
					byte[] byteArray = output.toByteArray();
					user.setImage(EventParams.BASE64_IMAGE_PNG_STRING
							+ Base64.encodeToString(byteArray, Base64.NO_WRAP));
					byteArray = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
				// image to Base64 string

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

	/**
	 * 
	 */
	private void presentImagePicker() {
		final CharSequence[] options = { "Take Photo", "Choose from Gallery",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Photo!");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();
				if (options[item].equals("Take Photo")) {

					// Toast.makeText(getActivity(), "Button clicked",
					// Toast.LENGTH_SHORT).show();
					// file = getOutputMediaFile(1);
					fileUri = UploadImageUtility.genarateUri();
					if (fileUri != null) {
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						// intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
						startActivityForResult(intent, REQUEST_FROM_CAMERA);
					}
				} else if (options[item].equals("Choose from Gallery")) {
					String state = Environment.getExternalStorageState();
					if (Environment.MEDIA_MOUNTED.equals(state)) {
						file = new File(Environment
								.getExternalStorageDirectory(),
								UploadImageUtility.genarateFileName());
					} else {
						file = new File(getFilesDir(), UploadImageUtility
								.genarateFileName());
					}

					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
					photoPickerIntent.setType("image/jpeg");
					startActivityForResult(photoPickerIntent, 2);

				}
			}
		});
		builder.show();
	}

	@Override
	public void onBackPressed() {
		return;
	}

	@Override
	void onBackendConnected() {

	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		pd.dismiss();
		if (event.equals(EventParams.EVENT_USER_UPDATE)) {
			ProfileSetupActivity.this.finish();
			App.getInstance().globleBitmap = null;
			Intent intent = new Intent(ProfileSetupActivity.this,
					ProfileSetupMoreActivity.class);
			startActivityForResult(intent, 22);

			AppPrefs.getAppPrefs(ProfileSetupActivity.this).saveBooleanPrefs(
					AppPrefs.KEY_PROFILE_SETUP, true);
			overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
		}

	}

	@Override
	public void onSocketResponseFailure(String onEvent, String message) {
		pd.dismiss();
		App.toast(message);
	}
}
