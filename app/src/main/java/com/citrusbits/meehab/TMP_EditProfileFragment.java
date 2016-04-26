package com.citrusbits.meehab;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class TMP_EditProfileFragment extends Fragment implements OnClickListener,
		OnValueChangeListener, OnFocusChangeListener, Observer {

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object data) {
		
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		
	}

	/* (non-Javadoc)
	 * @see android.widget.NumberPicker.OnValueChangeListener#onValueChange(android.widget.NumberPicker, int, int)
	 */
	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		
	}
	//
	//	Button btnAge, btnHeight, btnWeight;
	//	ImageButton btnRegular, btnSlim, btnStocky, btnAthletic, btnAsian,
	//			btnBlack, btnLatino, btnWhite, btnMixed, btnOther, btnDating,
	//			btnFriends, btnRelationship, btnSingle, btnEngaged, btnMarried,
	//			btnInRelationship, btnOpenRelationship, btnSave;
	//	EditText etBio;
	//	TextView txtBio;
	//	ImageView imgEdit;
	//	static NetworkImageView imgProfileBig;
	//	public static CircularNetworkImageView imgProfile;
	//	public static boolean isImageChanged = false;
	//
	//	boolean isDating = false, isFriends = false, isRelationship = false,
	//			isRegular = false, isSlim = false, isStocky = false,
	//			isAthletic = false, isAsian = false, isBlack = false,
	//			isLatino = false, isWhite = false, isMixed = false,
	//			isOther = false, isSingle = false, isEngaged = false,
	//			isMarried = false, isInRelationship = false,
	//			isOpenRelation = false;
	//
	//	String bodyType = "", ethnicity = "", status = "", lookingForDating = "0",
	//			lookingForFriends = "0", lookingForRelationship = "0";
	//
	//	Uri fileUri;
	//	static String lastMediaFilePath = null;
	//	static Uri lastMediaFileUri = null;
	//	static File file;
	//
	//	static Bitmap newbitmap;
	//	static String filePath;
	//	static String fullPath;
	//
	//	static int height, width, bigHeight, bigWidth;
	//	ProgressDialog pd;
	//	public static String picture = "";
	//	UserDatasource user;
	//
	//	UpdateProfileService updateProfile;
	//	UpdateProfileService updateBio;
	//	static AddUserData userModel;
	//
	//	@Override
	//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	//			Bundle savedInstanceState) {
	//		View v = inflater.inflate(R.layout.edit_profile, container, false);
	//
	//		user = new UserDatasource(getActivity());
	//		updateProfile = new UpdateProfileService();
	//		updateProfile.addObserver(this);
	//		updateBio = new UpdateProfileService();
	//		updateBio.addObserver(this);
	//
	//		etBio = (EditText) v.findViewById(R.id.etBio);
	//
	//		BitmapDrawable bd = (BitmapDrawable) this.getResources().getDrawable(
	//				R.drawable.profile_img_circle);
	//		height = bd.getBitmap().getHeight();
	//		width = bd.getBitmap().getWidth();
	//
	//		BitmapDrawable bdBig = (BitmapDrawable) this.getResources()
	//				.getDrawable(R.drawable.edit_blue_bg_big);
	//		bigHeight = bdBig.getBitmap().getHeight();
	//		bigWidth = bdBig.getBitmap().getWidth();
	//
	//		etBio.setOnFocusChangeListener(this);
	//
	//		txtBio = (TextView) v.findViewById(R.id.txtBio);
	//
	//		imgEdit = (ImageView) v.findViewById(R.id.imgEditBio);
	//		imgProfile = (CircularNetworkImageView) v.findViewById(R.id.imgProfile);
	//		imgProfile.setOnClickListener(this);
	//		imgProfileBig = (NetworkImageView) v.findViewById(R.id.imgProfileBig);
	//
	//		android.view.ViewGroup.LayoutParams layoutParams = imgProfile
	//				.getLayoutParams();
	//		layoutParams.width = width;
	//		layoutParams.height = height;
	//		imgProfile.setLayoutParams(layoutParams);
	//		imgProfile.setDefaultImageResId(R.drawable.profile_img_circle);
	//
	//		android.view.ViewGroup.LayoutParams layoutParamsBig = imgProfileBig
	//				.getLayoutParams();
	//		layoutParamsBig.width = bigWidth;
	//		layoutParamsBig.height = bigHeight;
	//		imgProfileBig.setLayoutParams(layoutParamsBig);
	//
	//		btnAge = (Button) v.findViewById(R.id.btnAge);
	//		btnAge.setOnClickListener(this);
	//		btnHeight = (Button) v.findViewById(R.id.btnHeight);
	//		btnHeight.setOnClickListener(this);
	//		btnWeight = (Button) v.findViewById(R.id.btnWeight);
	//		btnWeight.setOnClickListener(this);
	//		btnRegular = (ImageButton) v.findViewById(R.id.btnRegular);
	//		btnRegular.setOnClickListener(this);
	//		btnSlim = (ImageButton) v.findViewById(R.id.btnSlim);
	//		btnSlim.setOnClickListener(this);
	//		btnStocky = (ImageButton) v.findViewById(R.id.btnStocky);
	//		btnStocky.setOnClickListener(this);
	//		btnAthletic = (ImageButton) v.findViewById(R.id.btnAthletic);
	//		btnAthletic.setOnClickListener(this);
	//		btnAsian = (ImageButton) v.findViewById(R.id.btnAsian);
	//		btnAsian.setOnClickListener(this);
	//		btnBlack = (ImageButton) v.findViewById(R.id.btnBlack);
	//		btnBlack.setOnClickListener(this);
	//		btnLatino = (ImageButton) v.findViewById(R.id.btnLatino);
	//		btnLatino.setOnClickListener(this);
	//		btnWhite = (ImageButton) v.findViewById(R.id.btnWhite);
	//		btnWhite.setOnClickListener(this);
	//		btnMixed = (ImageButton) v.findViewById(R.id.btnMixed);
	//		btnMixed.setOnClickListener(this);
	//		btnOther = (ImageButton) v.findViewById(R.id.btnOther);
	//		btnOther.setOnClickListener(this);
	//		btnDating = (ImageButton) v.findViewById(R.id.btnDating);
	//		btnDating.setOnClickListener(this);
	//		btnFriends = (ImageButton) v.findViewById(R.id.btnFriends);
	//		btnFriends.setOnClickListener(this);
	//		btnRelationship = (ImageButton) v.findViewById(R.id.btnRelationship);
	//		btnRelationship.setOnClickListener(this);
	//		btnSingle = (ImageButton) v.findViewById(R.id.btnSingle);
	//		btnSingle.setOnClickListener(this);
	//		btnEngaged = (ImageButton) v.findViewById(R.id.btnEngaged);
	//		btnEngaged.setOnClickListener(this);
	//		btnMarried = (ImageButton) v.findViewById(R.id.btnMarried);
	//		btnMarried.setOnClickListener(this);
	//		btnInRelationship = (ImageButton) v
	//				.findViewById(R.id.btnInRelationship);
	//		btnInRelationship.setOnClickListener(this);
	//		btnOpenRelationship = (ImageButton) v
	//				.findViewById(R.id.btnOpenRelationship);
	//		btnOpenRelationship.setOnClickListener(this);
	//		btnSave = (ImageButton) v.findViewById(R.id.btnSave);
	//		btnSave.setOnClickListener(this);
	//
	//		user.open();
	//		userModel = new AddUserData();
	//		userModel = user.findUser();
	//		picture = userModel.getPicture();
	//		imgProfile.setDefaultImageResId(R.drawable.profile_img_circle);
	//		imgProfile.setErrorImageResId(R.drawable.profile_img_circle);
	//
	//		if (userModel.getPicture().compareTo("") == 0) {
	//			imgProfile.setDefaultImageResId(R.drawable.menu_profile_img_circle);
	//			// imgProfile.setImageUrl(null, ImageCacheManager.getInstance()
	//			// .getImageLoader());
	//		} else {
	//			imgProfile.setImageUrl(getResources().getString(R.string.image_url)
	//					+ userModel.getPicture(), ImageCacheManager.getInstance()
	//					.getImageLoader(), width, height, 1);
	//
	//			imgProfileBig.setImageUrl(
	//					getResources().getString(R.string.image_url)
	//							+ userModel.getPicture(), ImageCacheManager
	//							.getInstance().getImageLoader(), bigWidth,
	//					bigHeight, 0);
	//		}
	//
	//		bodyType = userModel.getBodyType();
	//		ethnicity = userModel.getEthnicity();
	//		lookingForDating = userModel.getLookingForDating();
	//		lookingForFriends = userModel.getLookingForFriends();
	//		lookingForRelationship = userModel.getLookingForRelationship();
	//		status = userModel.getStatus();
	//
	//		if (userModel.getAge().compareTo("") != 0) {
	//			btnAge.setText(userModel.getAge());
	//		}
	//		if (userModel.getHeight().compareTo("") != 0) {
	//			btnHeight.setText(userModel.getHeight());
	//		}
	//		if (userModel.getWeight().compareTo("") != 0) {
	//			btnWeight.setText(userModel.getWeight());
	//		}
	//		if (userModel.getBodyType().compareTo("regular") == 0) {
	//			btnRegular.setBackgroundResource(R.drawable.regular_btn_sellected);
	//			bodyType = "regular";
	//			isRegular = true;
	//		} else if (userModel.getBodyType().compareTo("slim") == 0) {
	//			btnSlim.setBackgroundResource(R.drawable.slim_btn_sellected);
	//			bodyType = "slim";
	//			isSlim = true;
	//		} else if (userModel.getBodyType().compareTo("stocky") == 0) {
	//			btnStocky.setBackgroundResource(R.drawable.stocky_btn_sellected);
	//			bodyType = "stocky";
	//			isStocky = true;
	//		} else if (userModel.getBodyType().compareTo("athletic") == 0) {
	//			btnAthletic
	//					.setBackgroundResource(R.drawable.athletic_btn_sellected);
	//			bodyType = "athletic";
	//			isAthletic = true;
	//		}
	//
	//		if (userModel.getEthnicity().compareTo("asian") == 0) {
	//			btnAsian.setBackgroundResource(R.drawable.asian_btn_sellected);
	//			ethnicity = "asian";
	//			isAsian = true;
	//		} else if (userModel.getEthnicity().compareTo("black") == 0) {
	//			btnBlack.setBackgroundResource(R.drawable.black_btn_sellected);
	//			ethnicity = "black";
	//			isBlack = true;
	//		} else if (userModel.getEthnicity().compareTo("latino") == 0) {
	//			btnLatino.setBackgroundResource(R.drawable.latino_btn_sellected);
	//			ethnicity = "latino";
	//			isLatino = true;
	//		} else if (userModel.getEthnicity().compareTo("white") == 0) {
	//			btnWhite.setBackgroundResource(R.drawable.white_btn_sellected);
	//			ethnicity = "white";
	//			isWhite = true;
	//		} else if (userModel.getEthnicity().compareTo("mixed") == 0) {
	//			btnMixed.setBackgroundResource(R.drawable.mixed_btn_sellected);
	//			ethnicity = "mixed";
	//			isMixed = true;
	//		} else if (userModel.getEthnicity().compareTo("other") == 0) {
	//			btnOther.setBackgroundResource(R.drawable.other_btn_sellected);
	//			ethnicity = "other";
	//			isOther = true;
	//		}
	//
	//		if (userModel.getLookingForDating().compareTo("1") == 0) {
	//			btnDating.setBackgroundResource(R.drawable.dating_btn_sellected);
	//			lookingForDating = "1";
	//			isDating = true;
	//		}
	//		if (userModel.getLookingForFriends().compareTo("1") == 0) {
	//			btnFriends.setBackgroundResource(R.drawable.friends_btn_sellected);
	//			lookingForFriends = "1";
	//			isFriends = true;
	//		}
	//		if (userModel.getLookingForRelationship().compareTo("1") == 0) {
	//			btnRelationship
	//					.setBackgroundResource(R.drawable.relationship_btn_sellected);
	//			lookingForRelationship = "1";
	//			isRelationship = true;
	//		}
	//
	//		if (userModel.getStatus().compareTo("single") == 0) {
	//			btnSingle.setBackgroundResource(R.drawable.single_btn_sellected);
	//			status = "single";
	//			isSingle = true;
	//		} else if (userModel.getStatus().compareTo("engaged") == 0) {
	//			btnEngaged.setBackgroundResource(R.drawable.engaged_btn_sellected);
	//			status = "engaged";
	//			isEngaged = true;
	//		} else if (userModel.getStatus().compareTo("married") == 0) {
	//			btnMarried.setBackgroundResource(R.drawable.married_btn_sellected);
	//			status = "married";
	//			isMarried = true;
	//		} else if (userModel.getStatus().compareTo("in_a_relationship") == 0) {
	//			btnInRelationship
	//					.setBackgroundResource(R.drawable.in_a_relationship_btn_sellected);
	//			status = "in_a_relationship";
	//			isInRelationship = true;
	//		} else if (userModel.getStatus().compareTo("open_relationship") == 0) {
	//			btnOpenRelationship
	//					.setBackgroundResource(R.drawable.open_relationship_btn_sellected);
	//			status = "open_relationship";
	//			isOpenRelation = true;
	//		}
	//		if (userModel.getBio().compareTo("") != 0) {
	//			etBio.setText(userModel.getBio());
	//			txtBio.setVisibility(View.GONE);
	//			imgEdit.setVisibility(View.GONE);
	//		}
	//
	//		return v;
	//	}
	//
	//	@Override
	//	public void onClick(View v) {
	//		if (v.getId() == R.id.btnAge) {
	//			showAgePicker();
	//		}
	//		if (v.getId() == R.id.btnHeight) {
	//			showHeightPicker();
	//		}
	//		if (v.getId() == R.id.btnWeight) {
	//			showWeightPicker();
	//		}
	//		if (v.getId() == R.id.btnRegular) {
	//			if (isRegular) {
	//				isRegular = false;
	//				bodyType = "";
	//				btnRegular.setBackgroundResource(R.drawable.regular_btn);
	//			} else {
	//				isRegular = true;
	//				isSlim = false;
	//				isStocky = false;
	//				isAthletic = false;
	//				bodyType = "regular";
	//
	//				btnRegular
	//						.setBackgroundResource(R.drawable.regular_btn_sellected);
	//				btnSlim.setBackgroundResource(R.drawable.slim_btn);
	//				btnStocky.setBackgroundResource(R.drawable.stocky_btn);
	//				btnAthletic.setBackgroundResource(R.drawable.athletic_btn);
	//			}
	//		}
	//		if (v.getId() == R.id.btnSlim) {
	//			if (isSlim) {
	//				isSlim = false;
	//				bodyType = "";
	//				btnSlim.setBackgroundResource(R.drawable.slim_btn);
	//			} else {
	//				isRegular = false;
	//				isSlim = true;
	//				isStocky = false;
	//				isAthletic = false;
	//				bodyType = "slim";
	//
	//				btnRegular.setBackgroundResource(R.drawable.regular_btn);
	//				btnSlim.setBackgroundResource(R.drawable.slim_btn_sellected);
	//				btnStocky.setBackgroundResource(R.drawable.stocky_btn);
	//				btnAthletic.setBackgroundResource(R.drawable.athletic_btn);
	//			}
	//		}
	//		if (v.getId() == R.id.btnStocky) {
	//			if (isStocky) {
	//				isStocky = false;
	//				bodyType = "";
	//				btnStocky.setBackgroundResource(R.drawable.stocky_btn);
	//			} else {
	//				isRegular = false;
	//				isSlim = false;
	//				isStocky = true;
	//				isAthletic = false;
	//				bodyType = "stocky";
	//
	//				btnRegular.setBackgroundResource(R.drawable.regular_btn);
	//				btnSlim.setBackgroundResource(R.drawable.slim_btn);
	//				btnStocky
	//						.setBackgroundResource(R.drawable.stocky_btn_sellected);
	//				btnAthletic.setBackgroundResource(R.drawable.athletic_btn);
	//			}
	//		}
	//		if (v.getId() == R.id.btnAthletic) {
	//			if (isAthletic) {
	//				isAthletic = false;
	//				bodyType = "";
	//				btnAthletic.setBackgroundResource(R.drawable.athletic_btn);
	//			} else {
	//				isRegular = false;
	//				isSlim = false;
	//				isStocky = false;
	//				isAthletic = true;
	//				bodyType = "athletic";
	//
	//				btnRegular.setBackgroundResource(R.drawable.regular_btn);
	//				btnSlim.setBackgroundResource(R.drawable.slim_btn);
	//				btnStocky.setBackgroundResource(R.drawable.stocky_btn);
	//				btnAthletic
	//						.setBackgroundResource(R.drawable.athletic_btn_sellected);
	//			}
	//		}
	//		if (v.getId() == R.id.btnAsian) {
	//			if (isAsian) {
	//				isAsian = false;
	//				ethnicity = "";
	//				btnAsian.setBackgroundResource(R.drawable.asian_btn);
	//			} else {
	//				isAsian = true;
	//				isBlack = false;
	//				isLatino = false;
	//				isWhite = false;
	//				isMixed = false;
	//				isOther = false;
	//				ethnicity = "asian";
	//
	//				btnAsian.setBackgroundResource(R.drawable.asian_btn_sellected);
	//				btnBlack.setBackgroundResource(R.drawable.black_btn);
	//				btnLatino.setBackgroundResource(R.drawable.latino_btn);
	//				btnWhite.setBackgroundResource(R.drawable.white_btn);
	//				btnMixed.setBackgroundResource(R.drawable.mixed_btn);
	//				btnOther.setBackgroundResource(R.drawable.other_btn);
	//			}
	//		}
	//		if (v.getId() == R.id.btnBlack) {
	//			if (isBlack) {
	//				isBlack = false;
	//				ethnicity = "";
	//				btnBlack.setBackgroundResource(R.drawable.black_btn);
	//			} else {
	//				isAsian = false;
	//				isBlack = true;
	//				isLatino = false;
	//				isWhite = false;
	//				isMixed = false;
	//				isOther = false;
	//				ethnicity = "black";
	//
	//				btnAsian.setBackgroundResource(R.drawable.asian_btn);
	//				btnBlack.setBackgroundResource(R.drawable.black_btn_sellected);
	//				btnLatino.setBackgroundResource(R.drawable.latino_btn);
	//				btnWhite.setBackgroundResource(R.drawable.white_btn);
	//				btnMixed.setBackgroundResource(R.drawable.mixed_btn);
	//				btnOther.setBackgroundResource(R.drawable.other_btn);
	//			}
	//		}
	//		if (v.getId() == R.id.btnLatino) {
	//			if (isLatino) {
	//				isLatino = false;
	//				ethnicity = "";
	//				btnLatino.setBackgroundResource(R.drawable.latino_btn);
	//			} else {
	//				isAsian = false;
	//				isBlack = false;
	//				isLatino = true;
	//				isWhite = false;
	//				isMixed = false;
	//				isOther = false;
	//				ethnicity = "latino";
	//
	//				btnAsian.setBackgroundResource(R.drawable.asian_btn);
	//				btnBlack.setBackgroundResource(R.drawable.black_btn);
	//				btnLatino
	//						.setBackgroundResource(R.drawable.latino_btn_sellected);
	//				btnWhite.setBackgroundResource(R.drawable.white_btn);
	//				btnMixed.setBackgroundResource(R.drawable.mixed_btn);
	//				btnOther.setBackgroundResource(R.drawable.other_btn);
	//			}
	//		}
	//		if (v.getId() == R.id.btnWhite) {
	//			if (isWhite) {
	//				isWhite = false;
	//				ethnicity = "";
	//				btnWhite.setBackgroundResource(R.drawable.white_btn);
	//			} else {
	//				isAsian = false;
	//				isBlack = false;
	//				isLatino = false;
	//				isWhite = true;
	//				isMixed = false;
	//				isOther = false;
	//				ethnicity = "white";
	//
	//				btnAsian.setBackgroundResource(R.drawable.asian_btn);
	//				btnBlack.setBackgroundResource(R.drawable.black_btn);
	//				btnLatino.setBackgroundResource(R.drawable.latino_btn);
	//				btnWhite.setBackgroundResource(R.drawable.white_btn_sellected);
	//				btnMixed.setBackgroundResource(R.drawable.mixed_btn);
	//				btnOther.setBackgroundResource(R.drawable.other_btn);
	//			}
	//		}
	//		if (v.getId() == R.id.btnMixed) {
	//			if (isMixed) {
	//				isMixed = false;
	//				ethnicity = "";
	//				btnMixed.setBackgroundResource(R.drawable.mixed_btn);
	//			} else {
	//				isAsian = false;
	//				isBlack = false;
	//				isLatino = false;
	//				isWhite = false;
	//				isMixed = true;
	//				isOther = false;
	//				ethnicity = "mixed";
	//
	//				btnAsian.setBackgroundResource(R.drawable.asian_btn);
	//				btnBlack.setBackgroundResource(R.drawable.black_btn);
	//				btnLatino.setBackgroundResource(R.drawable.latino_btn);
	//				btnWhite.setBackgroundResource(R.drawable.white_btn);
	//				btnMixed.setBackgroundResource(R.drawable.mixed_btn_sellected);
	//				btnOther.setBackgroundResource(R.drawable.other_btn);
	//			}
	//		}
	//		if (v.getId() == R.id.btnOther) {
	//			if (isOther) {
	//				isOther = false;
	//				ethnicity = "";
	//				btnOther.setBackgroundResource(R.drawable.other_btn);
	//			} else {
	//				isAsian = false;
	//				isBlack = false;
	//				isLatino = false;
	//				isWhite = false;
	//				isMixed = false;
	//				isOther = true;
	//				ethnicity = "other";
	//
	//				btnAsian.setBackgroundResource(R.drawable.asian_btn);
	//				btnBlack.setBackgroundResource(R.drawable.black_btn);
	//				btnLatino.setBackgroundResource(R.drawable.latino_btn);
	//				btnWhite.setBackgroundResource(R.drawable.white_btn);
	//				btnMixed.setBackgroundResource(R.drawable.mixed_btn);
	//				btnOther.setBackgroundResource(R.drawable.other_btn_sellected);
	//			}
	//		}
	//		if (v.getId() == R.id.btnDating) {
	//			if (isDating) {
	//				lookingForDating = "0";
	//				isDating = false;
	//				btnDating.setBackgroundResource(R.drawable.dating_btn);
	//			} else {
	//				lookingForDating = "1";
	//				isDating = true;
	//				btnDating
	//						.setBackgroundResource(R.drawable.dating_btn_sellected);
	//			}
	//		}
	//		if (v.getId() == R.id.btnFriends) {
	//			if (isFriends) {
	//				lookingForFriends = "0";
	//				isFriends = false;
	//				btnFriends.setBackgroundResource(R.drawable.friends_btn);
	//			} else {
	//				lookingForFriends = "1";
	//				isFriends = true;
	//				btnFriends
	//						.setBackgroundResource(R.drawable.friends_btn_sellected);
	//			}
	//		}
	//		if (v.getId() == R.id.btnRelationship) {
	//			if (isRelationship) {
	//				lookingForRelationship = "0";
	//				isRelationship = false;
	//				btnRelationship
	//						.setBackgroundResource(R.drawable.relationship_btn);
	//			} else {
	//				lookingForRelationship = "1";
	//				isRelationship = true;
	//				btnRelationship
	//						.setBackgroundResource(R.drawable.relationship_btn_sellected);
	//			}
	//		}
	//		if (v.getId() == R.id.btnSingle) {
	//			if (isSingle) {
	//				isSingle = false;
	//				status = "";
	//				btnSingle.setBackgroundResource(R.drawable.single_btn);
	//			} else {
	//				isSingle = true;
	//				isEngaged = false;
	//				isMarried = false;
	//				isInRelationship = false;
	//				isOpenRelation = false;
	//				status = "single";
	//
	//				btnSingle
	//						.setBackgroundResource(R.drawable.single_btn_sellected);
	//				btnEngaged.setBackgroundResource(R.drawable.engaged_btn);
	//				btnMarried.setBackgroundResource(R.drawable.married_btn);
	//				btnInRelationship
	//						.setBackgroundResource(R.drawable.in_a_relationship_btn);
	//				btnOpenRelationship
	//						.setBackgroundResource(R.drawable.open_relationship_btn);
	//			}
	//		}
	//		if (v.getId() == R.id.btnEngaged) {
	//			if (isEngaged) {
	//				isEngaged = false;
	//				status = "";
	//				btnEngaged.setBackgroundResource(R.drawable.engaged_btn);
	//			} else {
	//				isSingle = false;
	//				isEngaged = true;
	//				isMarried = false;
	//				isInRelationship = false;
	//				isOpenRelation = false;
	//				status = "engaged";
	//
	//				btnSingle.setBackgroundResource(R.drawable.single_btn);
	//				btnEngaged
	//						.setBackgroundResource(R.drawable.engaged_btn_sellected);
	//				btnMarried.setBackgroundResource(R.drawable.married_btn);
	//				btnInRelationship
	//						.setBackgroundResource(R.drawable.in_a_relationship_btn);
	//				btnOpenRelationship
	//						.setBackgroundResource(R.drawable.open_relationship_btn);
	//			}
	//		}
	//		if (v.getId() == R.id.btnMarried) {
	//			if (isMarried) {
	//				isMarried = false;
	//				status = "";
	//				btnMarried.setBackgroundResource(R.drawable.married_btn);
	//			} else {
	//				isSingle = false;
	//				isEngaged = false;
	//				isMarried = true;
	//				isInRelationship = false;
	//				isOpenRelation = false;
	//				status = "married";
	//
	//				btnSingle.setBackgroundResource(R.drawable.single_btn);
	//				btnEngaged.setBackgroundResource(R.drawable.engaged_btn);
	//				btnMarried
	//						.setBackgroundResource(R.drawable.married_btn_sellected);
	//				btnInRelationship
	//						.setBackgroundResource(R.drawable.in_a_relationship_btn);
	//				btnOpenRelationship
	//						.setBackgroundResource(R.drawable.open_relationship_btn);
	//			}
	//		}
	//		if (v.getId() == R.id.btnInRelationship) {
	//			if (isInRelationship) {
	//				isInRelationship = false;
	//				status = "";
	//				btnInRelationship
	//						.setBackgroundResource(R.drawable.in_a_relationship_btn);
	//			} else {
	//				isSingle = false;
	//				isEngaged = false;
	//				isMarried = false;
	//				isInRelationship = true;
	//				isOpenRelation = false;
	//				status = "in_a_relationship";
	//
	//				btnSingle.setBackgroundResource(R.drawable.single_btn);
	//				btnEngaged.setBackgroundResource(R.drawable.engaged_btn);
	//				btnMarried.setBackgroundResource(R.drawable.married_btn);
	//				btnInRelationship
	//						.setBackgroundResource(R.drawable.in_a_relationship_btn_sellected);
	//				btnOpenRelationship
	//						.setBackgroundResource(R.drawable.open_relationship_btn);
	//			}
	//		}
	//		if (v.getId() == R.id.btnOpenRelationship) {
	//			if (isOpenRelation) {
	//				isOpenRelation = false;
	//				status = "";
	//				btnOpenRelationship
	//						.setBackgroundResource(R.drawable.open_relationship_btn);
	//			} else {
	//				isSingle = false;
	//				isEngaged = false;
	//				isMarried = false;
	//				isInRelationship = false;
	//				isOpenRelation = true;
	//				status = "open_relationship";
	//
	//				btnSingle.setBackgroundResource(R.drawable.single_btn);
	//				btnEngaged.setBackgroundResource(R.drawable.engaged_btn);
	//				btnMarried.setBackgroundResource(R.drawable.married_btn);
	//				btnInRelationship
	//						.setBackgroundResource(R.drawable.in_a_relationship_btn);
	//				btnOpenRelationship
	//						.setBackgroundResource(R.drawable.open_relationship_btn_sellected);
	//			}
	//		}
	//		if (v.getId() == R.id.imgProfile) {
	//			selectImage();
	//		}
	//		if (v.getId() == R.id.btnSave) {
	//			etBio.clearFocus();
	//
	//			pd = new ProgressDialog(getActivity(), R.style.MyTheme);
	//			pd.setCancelable(false);
	//			pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
	//			pd.show();
	//
	//			updateProfile.UpdateProfile(getActivity(), userModel.getUserId(),
	//					btnWeight.getText().toString(), bodyType, ethnicity,
	//					status,
	//					StringEncoder.Encode(btnHeight.getText().toString()),
	//					btnAge.getText().toString(), lookingForDating,
	//					lookingForFriends, lookingForRelationship);
	//
	//			if (etBio.getText().toString().compareTo("") != 0) {
	//				updateBio.UpdateBio(getActivity(), userModel.getUserId(),
	//						StringEncoder.Encode(etBio.getText().toString()));
	//			}
	//		}
	//	}
	//
	//	public void showAgePicker() {
	//
	//		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
	//				getActivity());
	//		LayoutInflater inflater = getActivity().getLayoutInflater();
	//		View dialogView = inflater.inflate(R.layout.age_picker, null);
	//		dialogBuilder.setView(dialogView);
	//		dialogBuilder.setTitle("Select Age");
	//
	//		final NumberPicker np = (NumberPicker) dialogView
	//				.findViewById(R.id.agePicker);
	//		np.setMaxValue(100);
	//		np.setMinValue(18);
	//		if (btnAge.getText().toString().compareTo("Select") != 0) {
	//			np.setValue(Integer.parseInt(btnAge.getText().toString()));
	//		}
	//		np.setWrapSelectorWheel(false);
	//		np.setOnValueChangedListener(this);
	//
	//		dialogBuilder.setPositiveButton("Set",
	//				new DialogInterface.OnClickListener() {
	//
	//					@Override
	//					public void onClick(DialogInterface dialog, int which) {
	//						btnAge.setText(String.valueOf(np.getValue()));
	//					}
	//				});
	//
	//		dialogBuilder.setNegativeButton("Cancel", null);
	//
	//		dialogBuilder.show();
	//	}
	//
	//	public void showHeightPicker() {
	//
	//		final String[] values = new String[] { "3'", "4'", "5'", "6'", "7'" };
	//		final String[] valuesInches = new String[] { "0''", "1''", "2''",
	//				"3''", "4''", "5''", "6''", "7''", "8''", "9''", "10''", "11''" };
	//
	//		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
	//				getActivity());
	//		LayoutInflater inflater = getActivity().getLayoutInflater();
	//		View dialogView = inflater.inflate(R.layout.height_picker, null);
	//		dialogBuilder.setView(dialogView);
	//		dialogBuilder.setTitle("Select Height");
	//
	//		final NumberPicker np = (NumberPicker) dialogView
	//				.findViewById(R.id.heightPickerFeet);
	//		np.setMaxValue(4);
	//		np.setMinValue(0);
	//		np.setDisplayedValues(values);
	//		// String index = (String) btnHeight.getTag();
	//		// if (index != null) {
	//		// np.setValue(values[Integer.parseInt(index)]);
	//		// }
	//		np.setWrapSelectorWheel(false);
	//		np.setOnValueChangedListener(this);
	//
	//		final NumberPicker npInches = (NumberPicker) dialogView
	//				.findViewById(R.id.heightPickerInches);
	//		npInches.setMaxValue(11);
	//		npInches.setMinValue(0);
	//		npInches.setDisplayedValues(valuesInches);
	//		// String index = (String) btnHeight.getTag();
	//		// if (index != null) {
	//		// np.setValue(values[Integer.parseInt(index)]);
	//		// }
	//		npInches.setWrapSelectorWheel(false);
	//		npInches.setOnValueChangedListener(this);
	//
	//		dialogBuilder.setPositiveButton("Set",
	//				new DialogInterface.OnClickListener() {
	//
	//					@Override
	//					public void onClick(DialogInterface dialog, int which) {
	//						// btnHeight.setTag(np.getValue());
	//						btnHeight.setText(values[np.getValue()] + " "
	//								+ valuesInches[npInches.getValue()]);
	//					}
	//				});
	//
	//		dialogBuilder.setNegativeButton("Cancel", null);
	//
	//		dialogBuilder.show();
	//	}
	//
	//	public void showWeightPicker() {
	//
	//		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
	//				getActivity());
	//		LayoutInflater inflater = getActivity().getLayoutInflater();
	//		View dialogView = inflater.inflate(R.layout.weight_picker, null);
	//		dialogBuilder.setView(dialogView);
	//		dialogBuilder.setTitle("Select Weight");
	//
	//		final NumberPicker np = (NumberPicker) dialogView
	//				.findViewById(R.id.agePicker);
	//		np.setMaxValue(500);
	//		np.setMinValue(50);
	//		String str = btnWeight.getText().toString();
	//		if (str.compareTo("Select") != 0) {
	//			String weight = str.substring(0, str.indexOf(" "));
	//			np.setValue(Integer.parseInt(weight));
	//		}
	//		np.setWrapSelectorWheel(false);
	//		np.setOnValueChangedListener(this);
	//
	//		dialogBuilder.setPositiveButton("Set",
	//				new DialogInterface.OnClickListener() {
	//
	//					@Override
	//					public void onClick(DialogInterface dialog, int which) {
	//						btnWeight.setText(String.valueOf(np.getValue())
	//								+ " lbs");
	//					}
	//				});
	//
	//		dialogBuilder.setNegativeButton("Cancel", null);
	//
	//		dialogBuilder.show();
	//	}
	//
	//	@Override
	//	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
	//		// TODO Auto-generated method stub
	//
	//	}
	//
	//	@Override
	//	public void onFocusChange(View v, boolean hasFocus) {
	//		if (v.getId() == R.id.etBio) {
	//			if (hasFocus) {
	//				txtBio.setVisibility(View.GONE);
	//				imgEdit.setVisibility(View.GONE);
	//			} else {
	//				if (etBio.getText().toString().compareTo("") == 0) {
	//					txtBio.setVisibility(View.VISIBLE);
	//					imgEdit.setVisibility(View.VISIBLE);
	//				}
	//			}
	//		}
	//	}
	//
	//	private void selectImage() {
	//
	//		final CharSequence[] options = { "Take Photo", "Choose from Gallery",
	//				"Cancel" };
	//
	//		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	//		builder.setTitle("Change Picture!");
	//		builder.setItems(options, new DialogInterface.OnClickListener() {
	//			@Override
	//			public void onClick(DialogInterface dialog, int item) {
	//				if (options[item].equals("Take Photo")) {
	//
	//					// Toast.makeText(getActivity(), "Button clicked",
	//					// Toast.LENGTH_SHORT).show();
	//					// file = getOutputMediaFile(1);
	//
	//					HomeActivity.fileUri = UploadImageUtility.genarateUri();
	//					if (HomeActivity.fileUri != null) {
	//						Intent intent = new Intent(
	//								MediaStore.ACTION_IMAGE_CAPTURE);
	//						intent.putExtra(MediaStore.EXTRA_OUTPUT,
	//								HomeActivity.fileUri);
	//						getActivity().startActivityForResult(intent, 1);
	//					}
	//				} else if (options[item].equals("Choose from Gallery")) {
	//					String state = Environment.getExternalStorageState();
	//					if (Environment.MEDIA_MOUNTED.equals(state)) {
	//						file = new File(Environment
	//								.getExternalStorageDirectory(),
	//								UploadImageUtility.genarateFileName());
	//					} else {
	//						file = new File(getActivity().getFilesDir(),
	//								UploadImageUtility.genarateFileName());
	//					}
	//
	//					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
	//					photoPickerIntent.setType("image/*");
	//					getActivity().startActivityForResult(photoPickerIntent, 2);
	//				} else if (options[item].equals("Cancel")) {
	//					dialog.dismiss();
	//				}
	//			}
	//		});
	//		builder.show();
	//	}
	//
	//	@Override
	//	public void onStart() {
	//		super.onStart();
	//		updateProfile.addObserver(this);
	//		updateBio.addObserver(this);
	//	}
	//
	//	@Override
	//	public void onResume() {
	//		super.onResume();
	//		user.open();
	//
	//		updateProfile.addObserver(this);
	//		updateBio.addObserver(this);
	//	}
	//
	//	@Override
	//	public void onPause() {
	//		super.onPause();
	//		user.close();
	//		updateProfile.deleteObserver(this);
	//		updateBio.deleteObserver(this);
	//	}
	//
	//	@Override
	//	public void onStop() {
	//		super.onStop();
	//		updateProfile.deleteObserver(this);
	//		updateBio.deleteObserver(this);
	//	}
	//
	//	@Override
	//	public void update(Observable observable, Object data) {
	//		if (observable == updateProfile) {
	//			pd.dismiss();
	//
	//			if (updateProfile.getResponse().getStatus().compareTo("200") == 0) {
	//				if (btnAge.getText().toString().compareTo("Select") != 0) {
	//					userModel.setAge(btnAge.getText().toString());
	//				}
	//				if (btnHeight.getText().toString().compareTo("Select") != 0) {
	//					userModel.setHeight(btnHeight.getText().toString());
	//				}
	//				if (btnWeight.getText().toString().compareTo("Select") != 0) {
	//					userModel.setWeight(btnWeight.getText().toString());
	//				}
	//				userModel.setBodyType(bodyType);
	//				userModel.setEthnicity(ethnicity);
	//				userModel.setLookingForDating(lookingForDating);
	//				userModel.setLookingForFriends(lookingForFriends);
	//				userModel.setLookingForRelationship(lookingForRelationship);
	//				userModel.setStatus(status);
	//
	//				user.update(userModel);
	//
	//				Toast.makeText(getActivity(),
	//						updateProfile.getResponse().getMessage(),
	//						Toast.LENGTH_SHORT).show();
	//
	//				// getFragmentManager().popBackStackImmediate();
	//			}
	//		}
	//		if (observable == updateBio) {
	//			if (updateBio.getResponse().getStatus().compareTo("200") == 0) {
	//				userModel.setBio(etBio.getText().toString());
	//
	//				user.update(userModel);
	//			}
	//		}
	//	}

}
