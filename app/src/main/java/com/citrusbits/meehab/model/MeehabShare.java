package com.citrusbits.meehab.model;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.widget.Toast;

import com.citrusbits.meehab.R;

public class MeehabShare {

	private static String getDownloadLink(Context context) {
		final String appPackageName = context.getPackageName(); // getPackageName()
		return "https://play.google.com/store/apps/details?id="
				+ appPackageName;
	}

	public static void shareBySms(Context context) {
		String url = getDownloadLink(context);
		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		sendIntent.setData(Uri.parse("sms:"));

		sendIntent.putExtra("sms_body",
				context.getString(R.string.share_body_text)+url);

		context.startActivity(sendIntent);
	}

	public static void shareByEmail(Context context) {
		String url = getDownloadLink(context);
		Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "",
				null));
		i.putExtra(Intent.EXTRA_TEXT,
				context.getString(R.string.share_body_text)+url);
		try {
			context.startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(context, "There are no email clients installed.",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	public static void shareByFacebook(Context context) {
		String url = getDownloadLink(context);
		String type = "image/*";
		try {
			Uri mehabUri = getMeehabUri(context);
			String packageName = "com.facebook.katana";
			Intent intent = createSocialIntent(context, type, mehabUri, packageName);
			context.startActivity(Intent.createChooser(intent , "Share to"));
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
		}

	}
	
	
	public static void shareByTwitter(Context context) {
		String url = getDownloadLink(context);
		String type = "image/*";
		try {
//			Uri mehabUri = getMeehabUri(context);
			String socialPackage = "com.twitter.android";

			Intent share = new Intent(Intent.ACTION_SEND);
			// Set the MIME type
			share.setType(type);
			// Create the URI from the media
			// File media = new File(mediaPath);
			// Uri uri = Uri.fromFile(media);
			// Add the URI to the Intent.
//			share.putExtra(Intent.EXTRA_STREAM, uri);
			share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_twitter_body_text));
			share.setPackage(socialPackage);
			// Broadcast the Intent.
			context.startActivity(Intent.createChooser(share, "Share to"));

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
		}

	}
	

	public static void shareByInstragram(Context context) {
		String type = "image/*";
		try {
			Uri mehabUri = getMeehabUri(context);
			String instagramPackage = "com.instagram.android";
			createSocialIntent(context, type, mehabUri, instagramPackage);

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
		}

	}

	private static Uri getMeehabUri(Context context) {
		InputStream bitmap;
		Uri mehabUri = null;
		try {
			bitmap = context.getAssets().open("ic_app.png");
			Bitmap bit = BitmapFactory.decodeStream(bitmap);
			String path = Images.Media.insertImage(
					context.getContentResolver(), bit, "title", null);
			mehabUri = Uri.parse(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mehabUri;

	}

	private static Intent createSocialIntent(Context context, String type,
											 Uri uri, String socialPackage) {

		// Create the new Intent using the 'Send' action.
		Intent share = new Intent(Intent.ACTION_SEND);
		// Set the MIME type
		share.setType(type);
		// Create the URI from the media
		// File media = new File(mediaPath);
		// Uri uri = Uri.fromFile(media);
		// Add the URI to the Intent.
		share.putExtra(Intent.EXTRA_STREAM, uri);
		share.putExtra(Intent.EXTRA_TEXT,
				context.getString(R.string.share_body_text));
		share.setPackage(socialPackage);
		// Broadcast the Intent.
		return share;
	}
}
