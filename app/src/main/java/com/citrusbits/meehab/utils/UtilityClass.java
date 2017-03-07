package com.citrusbits.meehab.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormatSymbols;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;

import com.citrusbits.meehab.app.MeehabApp;
import com.citrusbits.meehab.ui.users.LoginAndRegisterActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.db.UserDatasource;
import com.google.android.gms.maps.model.LatLng;

public class UtilityClass {

	//db source
	private UserDatasource userDatasource;
	
	public static String getUserToken(Context context) {
		String token = null;
		SharedPreferences settings = context.getSharedPreferences(Consts.APP_PREFS_NAME, 0);
		if (token == null) {
			token = MeehabApp.regid;

			SharedPreferences.Editor editor = settings.edit();
			editor.putString(Consts.GCM_TOKEN, token);
			editor.commit();
		}

		return token;
	}


	public static int getResourceId(Context context, String pVariableName, String pResourcename, String pPackageName) 	{
		try {
			return context.getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} 
	}
	
	public final static boolean isValidEmail(String target) {
		if (target == null) {
			return false;
		} else {
			final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);;
            Matcher matcher = pattern.matcher(target);
            return  matcher.matches();
		}
	}
	
	public static Pair isPhoneValid(String phone) {
		Pair pair = new Pair();
		if (phone.length() >= 14) {
			pair.valid = true;
			pair.message = "valid";
		} else {
			pair.valid = false;
			pair.message = "Please Enter valid Phone number";
		}

		return pair;
	}

	public static String phoneNumberNormal(String phone) {
		phone = phone.replace("(", "");
		phone = phone.replace(")", "");
		phone = phone.replace(" ", "");
		phone = phone.replace("-", "");
		phone = phone.replace("+", "");
		return phone;
	}

	public static String phoneNumberUsFormat(String phone) {
		if (phone.length() == 10) {
			String s1 = phone.substring(0, 3);
			String s2 = phone.substring(3, 6);
			String s3 = phone.substring(6);
			phone = "(" + s1 + ") " + s2 + "-" + s3;
		}
		return phone;
	}

	public static Pair isPasswordValid(String password) {
		Pair pair = new Pair();
		if (containAlphanumeric(password) && password.length() >= 8
				&& password.length() <= 12) {
			pair.valid = true;
			pair.message = "valid";
		} else if (!containAlphanumeric(password)) {
			pair.valid = false;
			pair.message = "Password must be 8-12 characters long, numbers and letters";
		} else if (password.length() < 8) {
			pair.valid = false;
			pair.message = "Password must be 8-12 characters long, numbers and letters";
		} else if (password.length() > 12) {
			pair.valid = false;
			pair.message = "Password must be 8-12 characters long, numbers and letters";
		}

		return pair;
	}

	public static boolean containAlphanumeric(final String str) {
		byte counter = 0;
		boolean checkdigit = false, checkchar = false;
		for (int i = 0; i < str.length() && counter < 2; i++) {
			// If we find a non-digit character we return false.
			if (!checkdigit && Character.isDigit(str.charAt(i))) {
				checkdigit = true;
				counter++;
			}
			String a = String.valueOf(str.charAt(i));
			if (!checkchar && a.matches("[a-z]*")) {
				checkchar = true;
				counter++;
			}
		}
		if (checkdigit && checkchar) {
			return true;
		}
		return false;
	}

	

	//private method of your class
	public static int getIndex(Spinner spinner, String myString) {
		int index = 0;

		for (int i=0;i<spinner.getCount();i++){
			if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
				index = i;
				i=spinner.getCount();//will stop the loop, kind of break, by making condition false
			}
		}
		return index;
	} 

	public static int getIndex(String[] array, String myString) {
		int index = 0;

		for (int i=0;i<array.length;i++){
			if (array[i].toString().equalsIgnoreCase(myString)){
				index = i;
				i=array.length;//will stop the loop, kind of break, by making condition false
			}
		}
		return index;
	} 
	
	public static boolean isAppInstalled(Context context, String packageName) {
	    PackageManager pm = context.getPackageManager();
	    boolean installed = false;
	    try {
	       pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
	       installed = true;
	    } catch (PackageManager.NameNotFoundException e) {
	       installed = false;
	    }
	    return installed;
	}

	public static int nDigitRandomNo(int digits){
	    int max = (int) Math.pow(10,(digits)) - 1; //for digits =7, max will be 9999999
	    int min = (int) Math.pow(10, digits-1); //for digits = 7, min will be 1000000
	    int range = max-min; //This is 8999999
	    Random r = new Random(); 
	    int x = r.nextInt(range);// This will generate random integers in range 0 - 8999999
	    int nDigitRandomNo = x+min; //Our random rumber will be any random number x + min
	    return nDigitRandomNo;
	}
	
	public static int randInt(int min, int max) {

	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	public static String encryptPassword(String password)
	{
		MessageDigest crypt;
		String sha1 = "";
		try {
			crypt = MessageDigest.getInstance("SHA-1");
	    crypt.reset();
	    crypt.update(password.getBytes("UTF-8"));

	    sha1 = new BigInteger(1, crypt.digest()).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		/////
//		String sha1 = "";
//	    try
//	    {
//	        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
//	        crypt.reset();
//	        crypt.update(password.getBytes("UTF-8"));
//	        sha1 = byteToHex(crypt.digest());
//	    }
//	    catch(NoSuchAlgorithmException e)
//	    {
//	        e.printStackTrace();
//	    }
//	    catch(UnsupportedEncodingException e)
//	    {
//	        e.printStackTrace();
//	    }
//	    return sha1;
	    ///////////
		return sha1;
	}

	private static String byteToHex(final byte[] hash)
	{
	    Formatter formatter = new Formatter();
	    for (byte b : hash)
	    {
	        formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}
	public static String getUserId(Context ctx){
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
		return sharedPrefs.getString(Consts.USER_ID, null);
	}
	
	public static Boolean isLoggedIn(Context ctx){
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
		return sharedPrefs.getBoolean(Consts.IS_LOGGED_IN, false);
	}

	public static Boolean isRating(Context ctx){
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
		return sharedPrefs.getBoolean(Consts.IS_RATING, false);
	}
	
	public static Boolean isContactSync(Context ctx){
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
		return sharedPrefs.getBoolean(Consts.IS_CONTACT_SYNC, false);
	}
	
	public static void setImageName(Context ctx, String name){
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString(Consts.IMAGE_NAME, name);
		editor.commit();
	}

	public static String getImageName(Context ctx){
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
		return sharedPrefs.getString(Consts.IMAGE_NAME, null);
	}
	
	public static boolean isStringNumeric( String str ) {
	    DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
	    char localeMinusSign = currentLocaleSymbols.getMinusSign();

	    if ( !Character.isDigit( str.charAt( 0 ) ) && str.charAt( 0 ) != localeMinusSign ) return false;

	    boolean isDecimalSeparatorFound = false;
	    char localeDecimalSeparator = currentLocaleSymbols.getDecimalSeparator();

	    for ( char c : str.substring( 1 ).toCharArray() ) {
	        if ( !Character.isDigit( c ) ) {
	            if ( c == localeDecimalSeparator && !isDecimalSeparatorFound ) {
	                isDecimalSeparatorFound = true;
	                continue;
	            }
	            return false;
	        }
	    }
	    
	    return true;
	}
	
	public static String removeExtension(String s) {

	    String separator = System.getProperty("file.separator");
	    String filename;

	    // Remove the path upto the filename.
	    int lastSeparatorIndex = s.lastIndexOf(separator);
	    if (lastSeparatorIndex == -1) {
	        filename = s;
	    } else {
	        filename = s.substring(lastSeparatorIndex + 1);
	    }

	    // Remove the extension.
	    int extensionIndex = filename.lastIndexOf(".");
	    if (extensionIndex == -1)
	        return filename;

	    return filename.substring(0, extensionIndex);
	}
	
	public static int pxToDp(Context context, int px) {
	    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
	    int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	    return dp;
	}
	
	public static int dpToPx(Context context, int dp) {
	    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}
	
	public void logout(Context ctx){

		//initialize userDatasource
//		userDatasource = new UserDatasource(ctx);
//		userDatasource.open();

		if(UtilityClass.getUserId(ctx) != null) {
//			userDatasource.removeAllUsers();
		}
		
		//clear shared pref
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
		Editor e = sharedPrefs.edit();
		e.clear();
		e.commit();

		//take the user to signin screen
		Intent intent = new Intent(ctx, LoginAndRegisterActivity.class);
		ctx.startActivity(intent);
		((Activity)ctx).finish();

	}

	/**
	 * @param c
	 * @return
	 */
	public static Dialog getProgressDialog(Context c) {
//		ProgressDialog pd = new ProgressDialog(c);
//		pd.setCanceledOnTouchOutside(false);
//		pd.setMessage("Loading...");
//		pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
		
		Dialog pd = new Dialog(c,c.getApplicationInfo().theme);
		pd.requestWindowFeature (Window.FEATURE_NO_TITLE);
		pd.setContentView (R.layout.progress_dialog);
		pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(150,0,0,0)));
        
		return pd;
	}
	
	/**
	 * @param dist
	 * @return
	 */
	public static String calculatDistance(LatLng dist) {
		String distance = "";
//		LatLng dist = new LatLng(meeting.getLatitude(), meeting.getLongitude());
		
//		float[] results = new float[1];
//		Location.distanceBetween(meeting.getLatitude(), meeting.getLongitude(),
//				MeehabApp.getMyPosition().latitude, MeehabApp.getMyPosition().longitude,
//		                         results);
		
		Location locationA = new Location("");
		locationA.setLatitude(dist.latitude);
		locationA.setLongitude( dist.longitude);
		Location locationB = new Location("");
		locationB.setLatitude(MeehabApp.getMyPosition().latitude);
		locationB.setLongitude(MeehabApp.getMyPosition().longitude);
		distance = (int)(locationA.distanceTo(locationB)/1000) + " miles";
		
		return distance;
	}


	public static Bitmap snapFromUrl(String url) {
            Bitmap bitmap = null;
            MediaMetadataRetriever mediaMetadataRetriever = null;
            try{
                mediaMetadataRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14) {
					mediaMetadataRetriever.setDataSource(url, new HashMap<String, String>());
				}else {
					mediaMetadataRetriever.setDataSource(url);
				}
//				bitmap = ThumbnailUtils.createVideoThumbnail(url, MediaStore.Video.Thumbnails.MICRO_KIND);
//                bitmap = mediaMetadataRetriever.getFrameAtTime();
				String duration= mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
				long time = Long.valueOf(duration)/2;
				bitmap = mediaMetadataRetriever.getFrameAtTime(time,MediaMetadataRetriever.OPTION_NEXT_SYNC);
			}catch (Exception e){
                e.printStackTrace();
            }finally{
                if (mediaMetadataRetriever != null){
                    mediaMetadataRetriever.release();
                }
            }
            return bitmap;
	}

	public static void hideSoftKeyboard(Context context, View view){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static Bitmap resize(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);
		// RECREATE THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;
	}
}
