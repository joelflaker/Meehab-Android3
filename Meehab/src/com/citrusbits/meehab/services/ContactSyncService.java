package com.citrusbits.meehab.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.widget.Toast;

public class ContactSyncService extends Service {

	public static final int CONTACT_SERVICE_NOTIFICATION_ID = 42;
	MyObserver contactObserver;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		contactObserver = new MyObserver(new Handler());
		getContentResolver().registerContentObserver(
				ContactsContract.Contacts.CONTENT_URI, true, contactObserver);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getContentResolver().unregisterContentObserver(contactObserver);
		stopForeground(true);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Notification note = new Notification(0, null,
				System.currentTimeMillis());
		note.flags |= Notification.FLAG_NO_CLEAR;
		startForeground(CONTACT_SERVICE_NOTIFICATION_ID, note);

		return (START_NOT_STICKY);
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		// TODO Auto-generated method stub

		Intent restartServiceIntent = new Intent(getApplicationContext(),
				this.getClass());
		restartServiceIntent.setPackage(getPackageName());

		PendingIntent restartServicePendingIntent = PendingIntent.getService(
				getApplicationContext(), 6, restartServiceIntent,
				PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmService = (AlarmManager) getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);
		alarmService.set(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + 1000,
				restartServicePendingIntent);
		
		super.onTaskRemoved(rootIntent);

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	class MyObserver extends ContentObserver {
		public MyObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			this.onChange(selfChange, null);
		}

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			// do s.th.
			// depending on the handler you might be on the UI
			// thread, so be cautious!

			Toast.makeText(ContactSyncService.this, "Contact is changed!",
					Toast.LENGTH_SHORT).show();

		}
	}

}
