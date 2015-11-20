package com.citrusbits.meehab.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.citrusbits.meehab.model.ChatModel;
import com.citrusbits.meehab.model.UserAccount;

public class ChatDataSource {

	SQLiteOpenHelper helper;
	SQLiteDatabase database;
	Context context;
	private boolean KeepOpen = false;

	private static final String[] allColumns = { MeehabDBOpenHelper.COLUMN_ID,
			MeehabDBOpenHelper.COLUMN_USERID,
			MeehabDBOpenHelper.COLUMN_USER_NAME,
			MeehabDBOpenHelper.COLUMN_USER_EMAIL,
			MeehabDBOpenHelper.COLUMN_PHONE, MeehabDBOpenHelper.COLUMN_PICTURE,
			MeehabDBOpenHelper.COLUMN_GENDER, MeehabDBOpenHelper.COLUMN_DOB,
			MeehabDBOpenHelper.COLUMN_MARITAL_STATUS,
			MeehabDBOpenHelper.COLUMN_INTRESTED_IN,
			MeehabDBOpenHelper.COLUMN_ABOUT_STORY,
			MeehabDBOpenHelper.COLUMN_WILLING_SPONSOR,
			MeehabDBOpenHelper.COLUMN_HEIGHT, MeehabDBOpenHelper.COLUMN_WEIGHT,
			MeehabDBOpenHelper.COLUMN_SEXUAL_ORIENTATION,
			MeehabDBOpenHelper.COLUMN_ETHNICITY,
			MeehabDBOpenHelper.COLUMN_HAVE_KIDS,
			MeehabDBOpenHelper.COLUMN_SOBER_DATE,
			MeehabDBOpenHelper.COLUMN_LNG, MeehabDBOpenHelper.COLUMN_LAT };

	public ChatDataSource(Context context) {
		helper = new MeehabDBOpenHelper(context);
		this.context = context;
	}

	public ChatDataSource open() {
		database = helper.getWritableDatabase();
		KeepOpen = true;
		return this;
	}

	public void close() {
		helper.close();
		KeepOpen = false;
	}

	public ChatModel addChat(ChatModel chat) {
		if (chat == null)
			return null;

		openIfNot();

		ContentValues values = new ContentValues();
		values.put(MeehabDBOpenHelper.COLUMN_CHAT_ID, chat.getChatId());
		values.put(MeehabDBOpenHelper.COLUMN_FROM_MSG, chat.getFromSend());
		values.put(MeehabDBOpenHelper.COLUMN_TO_MSG, chat.getToSend());
		values.put(MeehabDBOpenHelper.COLUMN_MSG, chat.getMessage());
		values.put(MeehabDBOpenHelper.COLUMN_TIME_STAMP, chat.getTimeStamp());

		database.insert(MeehabDBOpenHelper.TABLE_CHATS, null, values);
		// user.setId(id);

		closeIfNot();
		return chat;
	}

	public List<ChatModel> findAll(int fromId, int toId) {

		openIfNot();

		List<ChatModel> chats = new ArrayList<ChatModel>();
		// Cursor c = database.query(MeehabDBOpenHelper.TABLE_CHATS, null,
		// MeehabDBOpenHelper.COLUMN_FROM_MSG+"=? and "+MeehabDBOpenHelper.COLUMN_TO_MSG+"=?",
		// new String[]{String.valueOf(fromId),String.valueOf(toId)}, null,
		// null, null);
		String selectQuery = "Select* from " + MeehabDBOpenHelper.TABLE_CHATS
				+ " where " + MeehabDBOpenHelper.COLUMN_FROM_MSG + "=" + fromId
				+ " and " + MeehabDBOpenHelper.COLUMN_TO_MSG + "=" + toId
				+ " or " + MeehabDBOpenHelper.COLUMN_FROM_MSG + "=" + toId
				+ " and " + MeehabDBOpenHelper.COLUMN_TO_MSG + "=" + fromId;
		
		
		Cursor c = database.rawQuery(selectQuery, null);
		if (c.getCount() > 0 && c.moveToFirst()) {
			do {

				ChatModel chat = new ChatModel();
				chat.setFromSend(c.getInt(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_FROM_MSG)));
				chat.setChatId(c.getInt(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_CHAT_ID)));
				chat.setToSend(c.getInt(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_TO_MSG)));
				chat.setMessage(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_MSG)));

				chat.setTimeStamp(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_TIME_STAMP)));

				chats.add(chat);
			} while (c.moveToNext());
		}

		closeIfNot();
		return chats;
	}

	

	public boolean removeChat(int chatId) {
		openIfNot();
		String where = MeehabDBOpenHelper.COLUMN_CHAT_ID + "='" + chatId
				+ "'";

		int result = database
				.delete(MeehabDBOpenHelper.TABLE_CHATS, where, null);

		closeIfNot();
		return (result == 1);
	}

	public boolean update(UserAccount user) {
		if (user == null)
			return false;
		openIfNot();
		ContentValues values = new ContentValues();
		values.put(MeehabDBOpenHelper.COLUMN_USER_ID, user.getId());
		values.put(MeehabDBOpenHelper.COLUMN_USER_NAME, user.getUsername());
		values.put(MeehabDBOpenHelper.COLUMN_USER_EMAIL, user.getEmail());
		values.put(MeehabDBOpenHelper.COLUMN_PHONE, user.getPhone());
		values.put(MeehabDBOpenHelper.COLUMN_PICTURE, user.getImage());
		values.put(MeehabDBOpenHelper.COLUMN_GENDER, user.getGender());
		values.put(MeehabDBOpenHelper.COLUMN_DOB, user.getDateOfBirth());
		values.put(MeehabDBOpenHelper.COLUMN_MARITAL_STATUS,
				user.getMaritalStatus());
		values.put(MeehabDBOpenHelper.COLUMN_INTRESTED_IN,
				user.getIntrestedIn());
		values.put(MeehabDBOpenHelper.COLUMN_ABOUT_STORY, user.getAboutStory());
		values.put(MeehabDBOpenHelper.COLUMN_WILLING_SPONSOR,
				user.getWillingSponsor());
		values.put(MeehabDBOpenHelper.COLUMN_HEIGHT, user.getHeight());
		values.put(MeehabDBOpenHelper.COLUMN_WEIGHT, user.getWeight());
		values.put(MeehabDBOpenHelper.COLUMN_SEXUAL_ORIENTATION,
				user.getSexualOrientation());
		values.put(MeehabDBOpenHelper.COLUMN_ETHNICITY, user.getEthnicity());
		values.put(MeehabDBOpenHelper.COLUMN_LAT, user.getLatitude());
		values.put(MeehabDBOpenHelper.COLUMN_LNG, user.getLongitude());
		values.put(MeehabDBOpenHelper.COLUMN_SOBER_DATE, user.getSoberSence());
		values.put(MeehabDBOpenHelper.COLUMN_HAVE_KIDS, user.getHaveKids());

		String where = MeehabDBOpenHelper.COLUMN_USER_ID + "='" + user.getId()
				+ "'";
		// String where = "id=?";
		// String[] whereArgs = new String[] {String.valueOf(meeting.getId())};

		int result = database.update(MeehabDBOpenHelper.TABLE_USER, values,
				where, null);
		closeIfNot();

		return (result == 1);
	}

	public boolean removeAllUsers() {
		openIfNot();
		database.execSQL("delete from " + MeehabDBOpenHelper.TABLE_USER);
		closeIfNot();
		return true;
	}

	/**
	 * @return
	 */
	public boolean hasAccounts() {
		openIfNot();
		Cursor c = database.query(MeehabDBOpenHelper.TABLE_USER, allColumns,
				null, null, null, null, null);

		if (c.getCount() > 0) {
			return true;
		}
		closeIfNot();
		return false;

	}

	private void closeIfNot() {
		if (!KeepOpen) {
			close();
		}
	}

	private void openIfNot() {
		if (!KeepOpen) {
			open();
		}
	}

}
