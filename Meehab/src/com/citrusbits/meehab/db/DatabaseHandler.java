package com.citrusbits.meehab.db;

import com.citrusbits.meehab.model.FriendMessageModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Meehab_db_1";

	// Contacts table name
	private static final String TABLE_FRIEND_MESSAGE = "contacts";

	// Contacts Table Columns names
	private static final String KEY_PRIMARY_ID = "id_primary";
	private static final String KEY_FRIEND_ID = "friend_id";
	private static final String KEY_FRIEND_NAME = "friend_name";
	private static final String KEY_UNREAD_MESSAGE_COUNT = "unread_message_count";
	
	public static DatabaseHandler dbHandler;
	public static DatabaseHandler getInstance(Context context){
		if(dbHandler==null){
			dbHandler=new DatabaseHandler(context);
		}
		return dbHandler;
	}

	private DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_FRIEND_MESSAGE_TABLE = "CREATE TABLE " + TABLE_FRIEND_MESSAGE
				+ "(" + KEY_PRIMARY_ID + " INTEGER PRIMARY KEY,"
				+ KEY_FRIEND_ID + " INTEGER," + KEY_FRIEND_NAME + " TEXT,"
				+ KEY_UNREAD_MESSAGE_COUNT + " INTEGER" + ")";
		db.execSQL(CREATE_FRIEND_MESSAGE_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIEND_MESSAGE);

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
		values.put(KEY_FRIEND_ID, friendMessage.getFriendId()); // Contact Name
		values.put(KEY_FRIEND_NAME, friendMessage.getName()); // Contact Phone
		values.put(KEY_UNREAD_MESSAGE_COUNT,
				friendMessage.getUnreadMessageCount());

		// Inserting Row
		long inserted=db.insert(TABLE_FRIEND_MESSAGE, null, values);
		Log.e("Data is inserted at id "+friendMessage.getFriendId(), String.valueOf(inserted));
		db.close(); // Closing database connection
	}
	
	public boolean friendExists(int friendId){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_FRIEND_MESSAGE, new String[] {
				KEY_FRIEND_ID, KEY_FRIEND_NAME, KEY_UNREAD_MESSAGE_COUNT },
				KEY_FRIEND_ID + "=?",
				new String[] { String.valueOf(friendId) }, null, null, null,
				null);
		boolean exists=cursor!=null&&cursor.getCount()>0?true:false;
		try{
			cursor.close();
		}catch(Exception e){
			
		}
		db.close();
		return exists;
	}
	
	public int getAllUnreadMessagesCount(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery("select sum("+KEY_UNREAD_MESSAGE_COUNT+") from "+TABLE_FRIEND_MESSAGE+" ;", null);
		int count=0;
		if(c!=null&&c.moveToFirst()){
			count=c.getInt(0);
		}
		try{
			c.close();
		}catch(Exception e){
			
		}
		db.close();
		return count;
	}

	// Getting single contact
	public FriendMessageModel getFriend(int friendId) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_FRIEND_MESSAGE, new String[] {
				KEY_FRIEND_ID, KEY_FRIEND_NAME, KEY_UNREAD_MESSAGE_COUNT },
				KEY_FRIEND_ID + "=?",
				new String[] { String.valueOf(friendId) }, null, null, null,
				null);
		FriendMessageModel friendMessage = null;

		if (cursor != null && cursor.moveToFirst()) {
            friendMessage=new FriendMessageModel();
			friendMessage.setFriendId(cursor.getInt(cursor
					.getColumnIndex(KEY_FRIEND_ID)));
			friendMessage.setName(cursor.getString(cursor
					.getColumnIndex(KEY_FRIEND_NAME)));
			friendMessage.setUnreadMessageCount(cursor.getInt(cursor
					.getColumnIndex(KEY_UNREAD_MESSAGE_COUNT)));

		}
		
		try{
			cursor.close();
		}catch(Exception e){
			
		}
		db.close();
		// return contact
		return friendMessage;
	}

	public int getUnreadMessageCount(int friendId) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_FRIEND_MESSAGE, null, KEY_FRIEND_ID
				+ "=?", new String[] { String.valueOf(friendId) }, null, null,
				null, null);

		int unreadMessageCount = 0;
		if (cursor != null && cursor.moveToFirst()) {
			
			unreadMessageCount = cursor.getInt(cursor
					.getColumnIndex(KEY_UNREAD_MESSAGE_COUNT));
			
		}
		
		try{
			cursor.close();
		}catch(Exception e){
			
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
		int update= db.update(TABLE_FRIEND_MESSAGE, values, KEY_FRIEND_ID + " = ?",
				new String[] { String.valueOf(friendId) });
		Log.e("Updated", (update+1)+"");
		db.close();
		return update;
	}

	

	// Getting contacts Count
	public int getContactsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_FRIEND_MESSAGE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		int count=cursor.getCount();
		try{
			cursor.close();
		}catch(Exception e){
			
		}
		db.close();
		return count;
	}

}