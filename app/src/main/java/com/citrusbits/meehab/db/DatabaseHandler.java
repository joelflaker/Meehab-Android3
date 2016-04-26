package com.citrusbits.meehab.db;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import com.citrusbits.meehab.model.FriendMessageModel;
import com.citrusbits.meehab.model.RSVPModel;
import com.citrusbits.meehab.services.DeviceContact;
import com.citrusbits.meehab.utils.AccountUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Meehab_db_1";

	// Friends message table name
	private static final String TABLE_FRIEND_MESSAGE = "friend_message";

	// Contact table name table name
	private static final String TABLE_CONTACT = "contact";

	// RSVP table name table name
	private static final String TABLE_RSVP = "rsvp";

	private static final String KEY_USER_ID = "user_id";

	// Message Table Columns names
	private static final String KEY_PRIMARY_ID = "id_primary";
	private static final String KEY_FRIEND_ID = "friend_id";
	private static final String KEY_FRIEND_NAME = "friend_name";
	private static final String KEY_UNREAD_MESSAGE_COUNT = "unread_message_count";

	// Contacts Table Columns names

	private static final String KEY_CONTACT_NAME = "contact_name";
	private static final String KEY_PHONE_NUMBER = "phone_number";
	private static final String KEY_CONTACT_ID = "contact_id";

	private static final String KEY_VERSION = "version";

	private static final String KEY_CONTACT_ACTION = "contact_action";
	private static final String KEY_SYNC_FLAG = "sync_flag";

	// Contacts Table Columns names

	private static final String KEY_MEETING_ID = "meeting_id";
	private static final String KEY_MEETING_DAY = "meeting_day";
	private static final String KEY_CHECK_IN = "check_in";
	private static final String KEY_RSVP = "rsvp";
	private static final String KEY_EVENT_URI = "event_uri";

	public static DatabaseHandler dbHandler;

	public Context mContext;

	public static DatabaseHandler getInstance(Context context) {
		if (dbHandler == null) {
			dbHandler = new DatabaseHandler(context);
		}
		return dbHandler;
	}

	private DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.mContext = context;
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_FRIEND_MESSAGE_TABLE = "CREATE TABLE "
				+ TABLE_FRIEND_MESSAGE + "(" + KEY_PRIMARY_ID
				+ " INTEGER PRIMARY KEY," + KEY_USER_ID + " INTEGER,"
				+ KEY_FRIEND_ID + " INTEGER," + KEY_FRIEND_NAME + " TEXT,"
				+ KEY_UNREAD_MESSAGE_COUNT + " INTEGER" + ")";

		String CREATE_RSVP_TABLE = "CREATE TABLE " + TABLE_RSVP + "("
				+ KEY_PRIMARY_ID + " INTEGER PRIMARY KEY," + KEY_USER_ID
				+ " INTEGER," + KEY_MEETING_ID + " INTEGER," + KEY_CHECK_IN
				+ " INTEGER," + KEY_RSVP + " INTEGER," + KEY_EVENT_URI
				+ " TEXT," + KEY_MEETING_DAY + " TEXT" + ")";

		String CREATE_CONTACT_TABLE = "CREATE TABLE " + TABLE_CONTACT + "("
				+ KEY_PRIMARY_ID + " INTEGER PRIMARY KEY," + KEY_USER_ID
				+ " INTEGER," + KEY_CONTACT_ID + " INTEGER," + KEY_CONTACT_NAME
				+ " TEXT," + KEY_PHONE_NUMBER + " TEXT," + KEY_VERSION
				+ " INTEGER," + KEY_CONTACT_ACTION + " TEXT," + KEY_SYNC_FLAG
				+ " INTEGER" + ")";

		db.execSQL(CREATE_FRIEND_MESSAGE_TABLE);
		db.execSQL(CREATE_CONTACT_TABLE);
		db.execSQL(CREATE_RSVP_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIEND_MESSAGE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RSVP);

		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new contact
	public void addFriend(FriendMessageModel friendMessage) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_USER_ID, getUserId());
		values.put(KEY_FRIEND_ID, friendMessage.getFriendId()); // Contact Name
		values.put(KEY_FRIEND_NAME, friendMessage.getName()); // Contact Phone
		values.put(KEY_UNREAD_MESSAGE_COUNT,
				friendMessage.getUnreadMessageCount());

		// Inserting Row
		long inserted = db.insert(TABLE_FRIEND_MESSAGE, null, values);
		Log.e("Data is inserted at id " + friendMessage.getFriendId(),
				String.valueOf(inserted));
		db.close(); // Closing database connection
	}

	private int getUserId() {
		return AccountUtils.getUserId(mContext);
	}

	public void addRsvp(RSVPModel rsvp) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_USER_ID, getUserId());
		values.put(KEY_RSVP, rsvp.getRsvp());
		values.put(KEY_MEETING_ID, rsvp.getMeetingId()); // Meeting Id
		values.put(KEY_EVENT_URI, rsvp.getCalendarUri()); // Calendar Uri
		values.put(KEY_CHECK_IN, rsvp.getCheckIn());
		values.put(KEY_MEETING_DAY, rsvp.getMeetingDay()); // Meeting Day

		// Inserting Row
		long inserted = db.insert(TABLE_RSVP, null, values);
		Log.e("Data is inserted at id " + inserted, String.valueOf(inserted));
		db.close(); // Closing database connection
	}

	public void updateRsvp(RSVPModel rsvp) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_RSVP, rsvp.getRsvp()); // Calendar Uri

		// Inserting Row
		// long inserted = db.insert(TABLE_RSVP,
		// KEY_USER_ID+"=? and "+KEY_MEETING_ID+"=? and "+KEY_MEETING_DAY+"=?",
		// new
		// String{String.valueOf(getUserId()),rsvp.getMeetingId(),rsvp.getMeetingDay()});
		int value = db.update(
				TABLE_RSVP,
				values,
				KEY_USER_ID + "=? and " + KEY_MEETING_ID + "=? and "
						+ KEY_MEETING_DAY + "=?",
				new String[] { String.valueOf(getUserId()),
						String.valueOf(rsvp.getMeetingId()),
						rsvp.getMeetingDay() });
		Log.e("Data updated at " + value, "Yes");
		db.close(); // Closing database connection
	}

	public void updatedRsvpEventUri(RSVPModel rsvp) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_EVENT_URI, rsvp.getCalendarUri()); // Calendar Uri

		// Inserting Row
		// long inserted = db.insert(TABLE_RSVP,
		// KEY_USER_ID+"=? and "+KEY_MEETING_ID+"=? and "+KEY_MEETING_DAY+"=?",
		// new
		// String{String.valueOf(getUserId()),rsvp.getMeetingId(),rsvp.getMeetingDay()});
		int value = db.update(
				TABLE_RSVP,
				values,
				KEY_USER_ID + "=? and " + KEY_MEETING_ID + "=? and "
						+ KEY_MEETING_DAY + "=?",
				new String[] { String.valueOf(getUserId()),
						String.valueOf(rsvp.getMeetingId()),
						rsvp.getMeetingDay() });
		Log.e("Data updated at " + value, "Yes");
		db.close(); // Closing database connection
	}

	public void addCheckIn(RSVPModel rsvp) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_USER_ID, getUserId());
		values.put(KEY_MEETING_ID, rsvp.getMeetingId()); // Meeting Id
		values.put(KEY_CHECK_IN, rsvp.getCheckIn()); // Calendar Uri
		values.put(KEY_EVENT_URI, "");
		values.put(KEY_MEETING_DAY, rsvp.getMeetingDay()); // Meeting Day

		// Inserting Row
		long inserted = db.insert(TABLE_RSVP, null, values);
		Log.e("Data is inserted at id " + inserted, String.valueOf(inserted));
		db.close(); // Closing database connection
	}

	public void addContact(DeviceContact contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_USER_ID, getUserId());
		values.put(KEY_CONTACT_ID, contact.getContactId()); // Contact Name
		values.put(KEY_CONTACT_NAME, contact.getContactName()); // Contact Phone
		values.put(KEY_PHONE_NUMBER, contact.getPhoneNumber());
		values.put(KEY_VERSION, contact.getVersion());
		values.put(KEY_CONTACT_ACTION, contact.getContactAction());
		values.put(KEY_SYNC_FLAG, contact.getSyncFlag());
		// Inserting Row
		long inserted = db.insert(TABLE_CONTACT, null, values);
		Log.e("Data is inserted at id " + contact.getContactId(),
				String.valueOf(inserted));
		db.close(); // Closing database connection
	}

	public boolean friendExists(int friendId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(
				TABLE_FRIEND_MESSAGE,
				new String[] { KEY_USER_ID, KEY_FRIEND_ID, KEY_FRIEND_NAME,
						KEY_UNREAD_MESSAGE_COUNT },
				KEY_USER_ID + "=? and " + KEY_FRIEND_ID + "=?",
				new String[] { String.valueOf(getUserId()),
						String.valueOf(friendId) }, null, null, null, null);
		boolean exists = cursor != null && cursor.getCount() > 0 ? true : false;
		try {
			cursor.close();
		} catch (Exception e) {

		}
		db.close();
		return exists;
	}

	public boolean ContactExists(String phoneNumber) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CONTACT, null, KEY_USER_ID + "=? and "
				+ KEY_PHONE_NUMBER + "=?",
				new String[] { String.valueOf(getUserId()), phoneNumber },
				null, null, null, null);
		boolean exists = cursor != null && cursor.getCount() > 0 ? true : false;
		try {
			cursor.close();
		} catch (Exception e) {

		}
		db.close();
		return exists;
	}

	public int getAllUnreadMessagesCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery("select sum(" + KEY_UNREAD_MESSAGE_COUNT
				+ ") from " + TABLE_FRIEND_MESSAGE + " where " + KEY_USER_ID
				+ "=" + getUserId() + " ;", null);
		int count = 0;
		if (c != null && c.moveToFirst()) {
			count = c.getInt(0);
		}
		try {
			c.close();
		} catch (Exception e) {

		}
		db.close();
		return count;
	}

	// Getting single Friend Message
	public FriendMessageModel getFriend(int friendId) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(
				TABLE_FRIEND_MESSAGE,
				new String[] { KEY_USER_ID, KEY_FRIEND_ID, KEY_FRIEND_NAME,
						KEY_UNREAD_MESSAGE_COUNT },
				KEY_USER_ID + "=? and " + KEY_FRIEND_ID + "=?",
				new String[] { String.valueOf(getUserId()),
						String.valueOf(friendId) }, null, null, null, null);
		FriendMessageModel friendMessage = null;

		if (cursor != null && cursor.moveToFirst()) {
			friendMessage = new FriendMessageModel();
			friendMessage.setFriendId(cursor.getInt(cursor
					.getColumnIndex(KEY_FRIEND_ID)));
			friendMessage.setName(cursor.getString(cursor
					.getColumnIndex(KEY_FRIEND_NAME)));
			friendMessage.setUnreadMessageCount(cursor.getInt(cursor
					.getColumnIndex(KEY_UNREAD_MESSAGE_COUNT)));

		}

		try {
			cursor.close();
		} catch (Exception e) {

		}
		db.close();
		// return contact
		return friendMessage;
	}

	// Getting single Friend Message
	public List<DeviceContact> getAllContacts() {
		SQLiteDatabase db = this.getReadableDatabase();
		List<DeviceContact> contacts = new ArrayList<DeviceContact>();
		Cursor cursor = db.query(TABLE_CONTACT, null, KEY_USER_ID + "=?",
				new String[] { String.valueOf(getUserId()) }, null, null, null,
				null);

		if (cursor != null && cursor.moveToFirst()) {
			do {
				DeviceContact contact = new DeviceContact();
				contact.setContactId(cursor.getInt(cursor
						.getColumnIndex(KEY_CONTACT_ID)));
				contact.setContactName(cursor.getString(cursor
						.getColumnIndex(KEY_CONTACT_NAME)));
				contact.setPhoneNumber(cursor.getString(cursor
						.getColumnIndex(KEY_PHONE_NUMBER)));
				contact.setVersion(cursor.getInt(cursor
						.getColumnIndex(KEY_VERSION)));
				contact.setContactAction(cursor.getString(cursor
						.getColumnIndex(KEY_CONTACT_ACTION)));
				contact.setSyncFlag(cursor.getInt(cursor
						.getColumnIndex(KEY_SYNC_FLAG)));
				contacts.add(contact);

			} while (cursor.moveToNext());

		}

		try {
			cursor.close();
		} catch (Exception e) {

		}
		db.close();
		// return contact
		return contacts;
	}

	// Getting single Friend Message
	public List<DeviceContact> getAllUnsyncDeleteContacts() {
		SQLiteDatabase db = this.getReadableDatabase();
		List<DeviceContact> contacts = new ArrayList<DeviceContact>();
		Cursor cursor = db.query(TABLE_CONTACT, null, KEY_USER_ID + "=? AND "
				+ KEY_CONTACT_ACTION + "=? AND " + KEY_SYNC_FLAG + "=?",
				new String[] { String.valueOf(getUserId()),
						DeviceContact.ContactAction.DELETE.name(), "1" }, null,
				null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				DeviceContact contact = new DeviceContact();
				contact.setContactId(cursor.getInt(cursor
						.getColumnIndex(KEY_CONTACT_ID)));
				contact.setContactName(cursor.getString(cursor
						.getColumnIndex(KEY_CONTACT_NAME)));
				contact.setPhoneNumber(cursor.getString(cursor
						.getColumnIndex(KEY_PHONE_NUMBER)));
				contact.setVersion(cursor.getInt(cursor
						.getColumnIndex(KEY_VERSION)));
				contact.setContactAction(cursor.getString(cursor
						.getColumnIndex(KEY_CONTACT_ACTION)));
				contact.setSyncFlag(cursor.getInt(cursor
						.getColumnIndex(KEY_SYNC_FLAG)));
				contacts.add(contact);

			} while (cursor.moveToNext());

		}

		try {
			cursor.close();
		} catch (Exception e) {

		}
		db.close();
		// return contact
		return contacts;
	}

	// Getting single Friend Message
	public List<DeviceContact> getAllUnsyncAddContacts() {
		SQLiteDatabase db = this.getReadableDatabase();
		List<DeviceContact> contacts = new ArrayList<DeviceContact>();
		Cursor cursor = db.query(TABLE_CONTACT, null, KEY_USER_ID + "=? AND "
				+ KEY_CONTACT_ACTION + "=? AND " + KEY_SYNC_FLAG + "=?",
				new String[] { String.valueOf(getUserId()),
						DeviceContact.ContactAction.ADD.name(), "1" }, null,
				null, null, null);

		if (cursor != null && cursor.moveToFirst()) {
			do {
				DeviceContact contact = new DeviceContact();
				contact.setContactId(cursor.getInt(cursor
						.getColumnIndex(KEY_CONTACT_ID)));
				contact.setContactName(cursor.getString(cursor
						.getColumnIndex(KEY_CONTACT_NAME)));
				contact.setPhoneNumber(cursor.getString(cursor
						.getColumnIndex(KEY_PHONE_NUMBER)));
				contact.setVersion(cursor.getInt(cursor
						.getColumnIndex(KEY_VERSION)));
				contact.setContactAction(cursor.getString(cursor
						.getColumnIndex(KEY_CONTACT_ACTION)));
				contact.setSyncFlag(cursor.getInt(cursor
						.getColumnIndex(KEY_SYNC_FLAG)));
				contacts.add(contact);

			} while (cursor.moveToNext());

		}

		try {
			cursor.close();
		} catch (Exception e) {

		}
		db.close();
		// return contact
		return contacts;
	}

	public int getUnreadMessageCount(int friendId) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(
				TABLE_FRIEND_MESSAGE,
				null,
				KEY_USER_ID + "=? AND " + KEY_FRIEND_ID + "=?",
				new String[] { String.valueOf(getUserId()),
						String.valueOf(friendId) }, null, null, null, null);

		int unreadMessageCount = 0;
		if (cursor != null && cursor.moveToFirst()) {

			unreadMessageCount = cursor.getInt(cursor
					.getColumnIndex(KEY_UNREAD_MESSAGE_COUNT));

		}

		try {
			cursor.close();
		} catch (Exception e) {

		}

		db.close();
		// return contact
		return unreadMessageCount;
	}

	// Updating single contact
	public int updateMessgeCount(int friendId, int messageCount) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_UNREAD_MESSAGE_COUNT, messageCount);

		// updating row
		int update = db.update(
				TABLE_FRIEND_MESSAGE,
				values,
				KEY_USER_ID + "=? AND " + KEY_FRIEND_ID + " = ?",
				new String[] { String.valueOf(getUserId()),
						String.valueOf(friendId) });
		Log.e("Updated", (update + 1) + "");
		db.close();
		return update;
	}

	public void updateContactActionAndFlag(DeviceContact contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_CONTACT_ACTION, contact.getContactAction());
		values.put(KEY_SYNC_FLAG, contact.getSyncFlag());
		// update Row

		int updated = db.update(
				TABLE_CONTACT,
				values,
				KEY_USER_ID + "=? AND " + KEY_CONTACT_ID + "=?",
				new String[] { String.valueOf(getUserId()),
						String.valueOf(contact.getContactId()) });

		Log.e("Data is update status ", updated + "");
		db.close(); // Closing database connection
	}

	public void updateAndActionSyncFlagZero() {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_SYNC_FLAG, 0);
		// update Row

		int updated = db.update(TABLE_CONTACT, values, KEY_USER_ID + "=? AND "
				+ KEY_CONTACT_ACTION + "=?",
				new String[] { DeviceContact.ContactAction.ADD.name() });

		Log.e("Data is update status ", updated + "");
		db.close(); // Closing database connection
	}

	public void deleteAllContactsWithDeleteAction() {
		SQLiteDatabase db = this.getWritableDatabase();
		int delete = db.delete(TABLE_CONTACT, KEY_USER_ID + "=? AND "
				+ KEY_CONTACT_ACTION + "=?",
				new String[] { String.valueOf(getUserId()),
						DeviceContact.ContactAction.DELETE.name() });
		Log.e("deleted", String.valueOf(delete));
		db.close();
	}

	public void updateContact(DeviceContact contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_CONTACT_ID, contact.getContactId()); // Contact Name
		values.put(KEY_CONTACT_NAME, contact.getContactName()); // Contact Phone
		values.put(KEY_PHONE_NUMBER, contact.getPhoneNumber());
		values.put(KEY_VERSION, contact.getVersion());
		// update Row

		int updated = db.update(
				TABLE_CONTACT,
				values,
				KEY_USER_ID + "=? AND " + KEY_CONTACT_ID + "=?",
				new String[] { String.valueOf(getUserId()),
						String.valueOf(contact.getContactId()) });

		Log.e("Data is update status ", updated + "");
		db.close(); // Closing database connection
	}

	public void deleteContact(String phoneNumber) {
		SQLiteDatabase db = this.getWritableDatabase();
		int delete = db.delete(TABLE_CONTACT, KEY_USER_ID + "=? AND "
				+ KEY_PHONE_NUMBER + "=?",
				new String[] { String.valueOf(getUserId()), phoneNumber });
		Log.e("deleted", String.valueOf(delete));
		db.close();
	}

	public void deleteRsvp(String uri) {
		SQLiteDatabase db = this.getWritableDatabase();
		int delete = db.delete(TABLE_RSVP, KEY_USER_ID + "=? AND "
				+ KEY_EVENT_URI + "=?",
				new String[] { String.valueOf(getUserId()), uri });
		Log.e("deleted", String.valueOf(delete));
		db.close();
	}

	public void removeAllCheckIn(String meetingId) {
		SQLiteDatabase db = this.getWritableDatabase();
		int delete = db.delete(TABLE_RSVP, KEY_USER_ID + "=? AND "
				+ KEY_MEETING_ID + "=? AND " + KEY_CHECK_IN + "=?",
				new String[] { String.valueOf(getUserId()), meetingId, "1" });
		Log.e("deleted", String.valueOf(delete));
		db.close();
	}

	public void deleteCheckIn(int meetingId, String meetingDay) {
		SQLiteDatabase db = this.getWritableDatabase();
		int delete = db.delete(
				TABLE_RSVP,
				KEY_USER_ID + "=? AND " + KEY_MEETING_ID + "=? and "
						+ KEY_MEETING_DAY + "=? and " + KEY_CHECK_IN + "=?",
				new String[] { String.valueOf(getUserId()),
						String.valueOf(meetingId), meetingDay, "1" });
		Log.e("deleted", String.valueOf(delete));
		db.close();
	}

	public boolean isMeetingRSVP(int meetingId, String meetingDay) {
		String countQuery = "SELECT  * FROM " + TABLE_RSVP + " where "
				+ KEY_USER_ID + "=" + getUserId() + " and " + KEY_MEETING_ID
				+ "=" + meetingId + " and " + KEY_RSVP + "=" + 1 + " and "
				+ KEY_MEETING_DAY + "='" + meetingDay + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		boolean flag = false;

		if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
			flag = true;
		}

		try {
			cursor.close();
		} catch (Exception e) {

		}
		db.close();
		return flag;
	}

	public boolean isCheckIn(int meetingId, String meetingDay) {
		String countQuery = "SELECT  * FROM " + TABLE_RSVP + " where "
				+ KEY_USER_ID + "=" + getUserId() + " and " + KEY_MEETING_ID
				+ "=" + meetingId + " and " + KEY_CHECK_IN + "=" + 1 + " and "
				+ KEY_MEETING_DAY + "='" + meetingDay + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		boolean flag = false;

		if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
			flag = true;
		}

		try {
			cursor.close();
		} catch (Exception e) {

		}
		db.close();
		return flag;
	}

	public boolean rsvpFlagExists(int meetingId, String meetingDay) {
		String countQuery = "SELECT  * FROM " + TABLE_RSVP + " where "
				+ KEY_USER_ID + "=" + getUserId() + " and " + KEY_MEETING_ID
				+ "=" + meetingId + " and " + KEY_RSVP + "=" + 1 + " and "
				+ KEY_MEETING_DAY + "='" + meetingDay + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		boolean rsvpFalgExists = false;

		if (cursor != null && cursor.moveToFirst()) {
			rsvpFalgExists = true;
		}

		try {
			cursor.close();
		} catch (Exception e) {

		}
		db.close();
		return rsvpFalgExists;
	}

	public String getEventUri(int meetingId, String meetingDay) {
		String countQuery = "SELECT  * FROM " + TABLE_RSVP + " where "
				+ KEY_USER_ID + "=" + getUserId() + " and " + KEY_MEETING_ID
				+ "=" + meetingId + " and " + KEY_CHECK_IN + "!=" + 1 + " and "
				+ KEY_MEETING_DAY + "='" + meetingDay + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		String eventUri = null;

		if (cursor != null && cursor.moveToFirst()) {
			eventUri = cursor.getString(cursor.getColumnIndex(KEY_EVENT_URI));
		}

		try {
			cursor.close();
		} catch (Exception e) {

		}
		db.close();
		return eventUri;
	}

	// Getting contacts Count
	public int getFriendMessageCount() {
		String countQuery = "SELECT  * FROM " + TABLE_FRIEND_MESSAGE
				+ " where " + KEY_USER_ID + "=" + getUserId();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		int count = cursor.getCount();
		try {
			cursor.close();
		} catch (Exception e) {

		}
		db.close();
		return count;
	}

}