package com.citrusbits.meehab.app;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

public class GcmIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	boolean foreground = false;
	Context context;
	Intent intent;
//	Handler handler;
	Gson gson = new Gson();

	public GcmIntentService() {
		super("Meehab Notification");
		context = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
//		handler = new Handler();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (Build.VERSION.SDK_INT >= 14) {
			foreground = MyLifecycleHandler.isApplicationInForeground();
		} else {
			foreground = ForegroundCheckTask.isAppOnForeground(context);
		}
		
		Bundle extras = intent.getExtras();
		
		Log.e("Extras is ", extras.toString());
		for (String key : extras.keySet()) {
		    Object value = extras.get(key);
		  Log.e(key, value.toString());
		}

		String body = extras.getString("gcm.notification.body");
		
		
//		Toast.makeText(this, body, Toast.LENGTH_SHORT).show();
		
		
		if(body==null){
			return;
		}
		Log.e("Body", body);
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				sendNotification(extras.getString("payload"));
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String msg) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// convert the string into object
		// pushData = gson.fromJson(msg, PushData.class);

	}

//	public void showToast(final String message) {
//		handler.post(new Runnable() {
//			public void run() {
//				Toast.makeText(App.getInstance(), message, Toast.LENGTH_LONG)
//						.show();
//			}
//		});
//
//	}

	private String matchNumber(String number) {
		String no = null;

		// ArrayList<Contact> contacts = (ArrayList<Contact>)
		// contactsDatasource.findAll();
		//
		// if(contacts != null && contacts.size() > 0) {
		// for(int i=0; i<contacts.size(); i++) {
		// if(contacts.get(i).getNumber().substring(Math.max(0,
		// contacts.get(i).getNumber().length() - 10)).equals(number)) {
		// no = contacts.get(i).getNumber().substring(Math.max(0,
		// contacts.get(i).getNumber().length() - 10));
		// Log.i("matched number", "matchced number: " + no);
		// }
		// }
		// }

		return no;
	}

}