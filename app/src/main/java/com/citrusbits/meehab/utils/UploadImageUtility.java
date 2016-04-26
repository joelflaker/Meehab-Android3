package com.citrusbits.meehab.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

public class UploadImageUtility {
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static int serverResponseCode;
	
	static Context context;
	
	public static File getPhotoDirectory() {
		File outputDir = null;
		String externalStorageState = Environment.getExternalStorageState();
		if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
			File pictureDir = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			outputDir = new File(pictureDir, "Meehab");
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
		}
		return outputDir;
	}

	@SuppressLint("SimpleDateFormat")
	public static Uri genarateUri() {
		Uri photoFileUri = null;
		File outDir = getPhotoDirectory();
		if (outDir != null) {
			String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new Date());
			String photoFileName = timeStamp + ".png";
			File outputFile = new File(outDir, photoFileName);
			photoFileUri = Uri.fromFile(outputFile);
		}
		return photoFileUri;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String genarateFileName() {
		File outDir = getPhotoDirectory();
		String photoFileName = "";
		if (outDir != null) {
			String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new Date());
			photoFileName = timeStamp + ".png";
		}
		return photoFileName;
	}
}
