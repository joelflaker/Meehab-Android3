package com.citrusbits.meehab.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.citrusbits.meehab.LoginActivity;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.contacts.PhoneContacts;
import com.citrusbits.meehab.db.DatabaseHandler;
import com.citrusbits.meehab.services.DeviceContact.ContactAction;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.NetworkUtils;

public class ContactSyncService extends Service {

	public static final String ACTION_CONTACT_SYNC = "com.citrusbits.meehab.contactsync";

	public static final String ACTION_CONTACTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	public static final String ACTION_WIFI_STATE_CHANGE = "android.net.wifi.WIFI_STATE_CHANGED";

	public static final String EXTRA_EVENT = "event";
	public static final String EXTRA_RESULT = "result";

	public static final int CONTACT_SERVICE_NOTIFICATION_ID = 42;
	public static final long THREASHHOLD_TIME = 2000; // ms

	MyObserver contactObserver;

	private long prevTime;

	DatabaseHandler dbHandler;

	PhoneContacts phoneContacts;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		contactObserver = new MyObserver(new Handler());

		getContentResolver().registerContentObserver(
				ContactsContract.Contacts.CONTENT_URI, true, contactObserver);
		dbHandler = DatabaseHandler.getInstance(ContactSyncService.this);
		phoneContacts = new PhoneContacts(ContactSyncService.this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_CONTACT_SYNC);
		filter.addAction(ACTION_CONTACTIVITY_CHANGE);
		filter.addAction(ACTION_WIFI_STATE_CHANGE);
		this.registerReceiver(receiver, filter);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getContentResolver().unregisterContentObserver(contactObserver);
		stopForeground(true);
		this.unregisterReceiver(receiver);
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

			long currentTime = System.currentTimeMillis();
			if ((currentTime - prevTime) > THREASHHOLD_TIME) {

				Log.e("Threashhold time diff is ",
						String.valueOf(currentTime - prevTime));
				prevTime = currentTime;

				List<DeviceContact> storedContacts = dbHandler.getAllContacts();
				List<DeviceContact> deviceContacts = phoneContacts
						.getPhoneContacts();

				if (deviceContacts.size() > storedContacts.size()) {
					Toast.makeText(ContactSyncService.this,
							"Contact is added!", Toast.LENGTH_SHORT).show();
					for (int i = 0; i < deviceContacts.size(); i++) {
						DeviceContact deviceContact = deviceContacts.get(i);
						boolean contactPresent = false;
						for (int j = 0; j < storedContacts.size(); j++) {
							DeviceContact storedContact = storedContacts.get(j);
							if (deviceContact.getPhoneNumber().equals(
									storedContact.getPhoneNumber())) {
								contactPresent = true;
							}
						}
						if (!contactPresent) {
							Log.i("Device Contact added",
									deviceContact.getPhoneNumber() + " contact");

							Log.e("Device Contact added",
									deviceContact.getPhoneNumber());
							deviceContact.setSyncFlag(1);
							deviceContact
									.setContactAction(DeviceContact.ContactAction.ADD
											.name());
							dbHandler.addContact(deviceContact);

							List<DeviceContact> addContactsss = new ArrayList<DeviceContact>();
							addContactsss.add(deviceContacts.get(i));
							addContact(addContactsss);
							break;
						}

					}
				} else if (deviceContacts.size() == storedContacts.size()) {
					Toast.makeText(ContactSyncService.this,
							"Contact is updated!", Toast.LENGTH_SHORT).show();
					sortContact(deviceContacts);
					sortContact(storedContacts);
					for (int i = 0; i < deviceContacts.size(); i++) {

						if (!deviceContacts.get(i).getPhoneNumber()
								.equals(storedContacts.get(i).getPhoneNumber())) {

							Log.e("Old contact ", storedContacts.get(i)
									.getPhoneNumber());

							Log.e("New contact ", deviceContacts.get(i)
									.getPhoneNumber());
							//dbHandler.updateContact(deviceContacts.get(i));

							List<DeviceContact> contacts = new ArrayList<DeviceContact>();
							storedContacts.get(i).setSyncFlag(1);
							storedContacts.get(i).setContactAction(
									ContactAction.DELETE.name());
							dbHandler.updateContactActionAndFlag(storedContacts
									.get(i));
							contacts.add(storedContacts.get(i));
							removeConatct(contacts);

							List<DeviceContact> addContactsss = new ArrayList<DeviceContact>();

							deviceContacts.get(i).setSyncFlag(1);
							deviceContacts.get(i).setContactAction(
									DeviceContact.ContactAction.ADD.name());
							dbHandler.addContact(deviceContacts.get(i));
							addContactsss.add(deviceContacts.get(i));
							addContact(addContactsss);

							break;
						}

					}

				} else if (deviceContacts.size() < storedContacts.size()) {

					List<DeviceContact> contacts = new ArrayList<DeviceContact>();
					for (int i = 0; i < storedContacts.size(); i++) {
						DeviceContact storedContact = storedContacts.get(i);
						boolean contactPresent = false;
						for (int j = 0; j < deviceContacts.size(); j++) {
							DeviceContact deviceContact = deviceContacts.get(j);
							if (deviceContact.getPhoneNumber().equals(
									storedContact.getPhoneNumber())) {
								contactPresent = true;
							}
						}
						if (!contactPresent) {
							Log.e("Device Contact deleted",
									storedContact.getPhoneNumber());
							storedContact.setSyncFlag(1);
							storedContact.setContactAction(ContactAction.DELETE
									.name());
							dbHandler.updateContactActionAndFlag(storedContact);
							/*
							 * dbHandler.deleteContact(storedContact
							 * .getPhoneNumber());
							 */

							contacts.add(storedContact);

							// break;
						}

					}

					if (contacts.size() > 0) {
						removeConatct(contacts);
					}

				}

			}
		}
	}

	private JSONArray getPhoneBookArray(List<DeviceContact> contacts) {

		JSONArray phoneBook = new JSONArray();
		for (DeviceContact contac : contacts) {
			JSONObject phone = new JSONObject();
			try {

				phone.put("phone", contac.getPhoneNumber());
				phone.put("name", contac.getContactName());

				phoneBook.put(phone);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return phoneBook;

	}

	public void sortContact(List<DeviceContact> contactList) {
		Collections.sort(contactList, new Comparator<DeviceContact>() {
			public int compare(DeviceContact contact1, DeviceContact contact2) {
				return (contact1.getContactId() + "")
						.compareToIgnoreCase(contact2.getContactId() + "");
			}
		});
	}

	public void addContact(List<DeviceContact> contacts) {
		JSONObject params = new JSONObject();
		try {
			// Toast.makeText(this, itemName,
			// Toast.LENGTH_SHORT).show();

			params.put("sync", "add");
			params.put("device_id",
					DeviceUtils.getDeviceId(ContactSyncService.this));
			params.put("phonebook", getPhoneBookArray(contacts));

			if (SocketService.getInstance() != null) {
				SocketService socketService = SocketService.getInstance();
				if (NetworkUtils.isNetworkAvailable(ContactSyncService.this)) {
					socketService.syncPhone(params);
				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
			params = null;
		}
	}

	public void removeConatct(List<DeviceContact> contacts) {
		JSONObject params = new JSONObject();
		try {
			// Toast.makeText(this, itemName,
			// Toast.LENGTH_SHORT).show();

			params.put("sync", "remove");
			params.put("device_id",
					DeviceUtils.getDeviceId(ContactSyncService.this));
			params.put("phonebook", getPhoneBookArray(contacts));

			if (SocketService.getInstance() != null) {
				SocketService socketService = SocketService.getInstance();
				if (NetworkUtils.isNetworkAvailable(ContactSyncService.this)) {
					socketService.syncPhone(params);
				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
			params = null;
		}
	}

	public void onSocketResponse(String event, String result) {

		Log.i("Data ", result);

		if (event.equals(EventParams.METHOD_SYNC_PHONE)) {
			try {
				JSONObject data = new JSONObject(result);
				String forKey = data.getString("forkey");
				if (forKey.equals("remove")) {
					dbHandler.deleteAllContactsWithDeleteAction();
				} else if (forKey.equals("add")) {
					dbHandler.updateAndActionSyncFlagZero();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(ACTION_CONTACT_SYNC)) {
				String event = intent.getStringExtra(EXTRA_EVENT);
				String result = intent.getStringExtra(EXTRA_RESULT);
				onSocketResponse(event, result);
			} else if (intent.getAction().equals(ACTION_CONTACTIVITY_CHANGE)
					|| intent.getAction().equals(ACTION_WIFI_STATE_CHANGE)) {

				if (NetworkUtils.isNetworkAvailable(ContactSyncService.this)) {
					
					List<DeviceContact> unsyncDeleteContacts = dbHandler
							.getAllUnsyncDeleteContacts();
					List<DeviceContact> unsyncAddContacts = dbHandler
							.getAllUnsyncAddContacts();
					
					//Toast.makeText(context, "Network On! "+unsyncDeleteContacts.size()+":"+unsyncAddContacts.size(), Toast.LENGTH_SHORT)
					//.show();
					
					if (unsyncDeleteContacts.size() > 0) {
						Log.i("Unsync Deleted Number ", unsyncDeleteContacts.get(0).getPhoneNumber());
						removeConatct(unsyncDeleteContacts);
					}

					if (unsyncAddContacts.size() > 0) {
						Log.i("Unsync Added Number ", unsyncAddContacts.get(0).getPhoneNumber());
						addContact(unsyncAddContacts);
					}
				} else {
					//Toast.makeText(context, "Network Off!", Toast.LENGTH_SHORT)
						//	.show();
				}

			}
		}
	};

}
