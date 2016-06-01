package com.citrusbits.meehab.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.citrusbits.meehab.model.UserAccount;

public class UserDatasource {

	SQLiteOpenHelper helper;
	SQLiteDatabase database;
	Context context;

	private static final String[] allColumns = { MeehabDBOpenHelper.COLUMN_ID,
			MeehabDBOpenHelper.COLUMN_USERID,
			MeehabDBOpenHelper.COLUMN_USER_NAME,
			MeehabDBOpenHelper.COLUMN_USER_EMAIL,
			MeehabDBOpenHelper.COLUMN_USER_CHECK_IN,
			MeehabDBOpenHelper.COLUMN_USER_FACEBOOK_ID,
			MeehabDBOpenHelper.COLUMN_USER_NOTIFICATION,
			MeehabDBOpenHelper.COLUMN_PHONE,
			MeehabDBOpenHelper.COLUMN_INSURANCE,
			MeehabDBOpenHelper.COLUMN_PICTURE,
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

	public UserDatasource(Context context) {
		helper = new MeehabDBOpenHelper(context);
		this.context = context;
	}

	private UserDatasource open() {
		database = helper.getWritableDatabase();
		return this;
	}

	private void close() {
		helper.close();
	}

	public UserAccount add(UserAccount user) {
		if (user == null)
			return null;

		open();

		ContentValues values = new ContentValues();
		int userId = user.getId();

		values.put(MeehabDBOpenHelper.COLUMN_USER_ID, user.getId());
		values.put(MeehabDBOpenHelper.COLUMN_USER_NAME, user.getUsername());
		values.put(MeehabDBOpenHelper.COLUMN_USER_EMAIL, user.getEmail());
		values.put(MeehabDBOpenHelper.COLUMN_USER_FACEBOOK_ID, user.getSocailId());
		values.put(MeehabDBOpenHelper.COLUMN_USER_CHECK_IN, user.getCheckinType());
		values.put(MeehabDBOpenHelper.COLUMN_USER_NOTIFICATION, user.getNotification());
		values.put(MeehabDBOpenHelper.COLUMN_PHONE, user.getPhone());
		values.put(MeehabDBOpenHelper.COLUMN_PICTURE, user.getImage());
		values.put(MeehabDBOpenHelper.COLUMN_GENDER, user.getGender());
		values.put(MeehabDBOpenHelper.COLUMN_INSURANCE, user.getInsurance());
		values.put(MeehabDBOpenHelper.COLUMN_DOB, user.getDateOfBirth());
		values.put(MeehabDBOpenHelper.COLUMN_HOME_GROUP, user.getMeetingHomeGroup());
		values.put(MeehabDBOpenHelper.COLUMN_MARITAL_STATUS,
				user.getMaritalStatus());
		values.put(MeehabDBOpenHelper.COLUMN_INTRESTED_IN,
				user.getIntrestedIn());
		values.put(MeehabDBOpenHelper.COLUMN_ACCUPATION, user.getAccupation());
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

		database.insert(MeehabDBOpenHelper.TABLE_USER, null, values);
		// user.setId(id);

		close();
		return user;
	}

	public List<UserAccount> findAll() {

		open();

		List<UserAccount> users = new ArrayList<UserAccount>();
		Cursor c = database.query(MeehabDBOpenHelper.TABLE_USER, allColumns,
				null, null, null, null, null);

		if (c.getCount() > 0) {
			while (c.moveToNext()) {
				UserAccount user = new UserAccount();
				user.setId(c.getInt(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_USER_ID)));
				user.setUsername(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_USER_NAME)));
				user.setEmail(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_USER_EMAIL)));
				user.setSocailId(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_USER_FACEBOOK_ID)));
				user.setCheckinType(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_USER_CHECK_IN)));
				user.setNotification(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_USER_NOTIFICATION)));
				user.setPhone(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_PHONE)));
				user.setInsurance(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_INSURANCE)));
				user.setImage(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_PICTURE)));
				user.setGender(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_GENDER)));
				user.setDateOfBirth(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_DOB)));
				user.setMaritalStatus(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_MARITAL_STATUS)));
				user.setIntrestedIn(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_INTRESTED_IN)));
				user.setAboutStory(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_ABOUT_STORY)));
				user.setWillingSponsor(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_WILLING_SPONSOR)));
				user.setHeight(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_HEIGHT)));
				user.setWeight(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_WEIGHT)));
				user.setSexualOrientation(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_SEXUAL_ORIENTATION)));
				user.setEthnicity(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_ETHNICITY)));
				user.setLatitude(c.getInt(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_LAT)));
				user.setLongitude(c.getInt(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_LNG)));
				user.setSoberSence(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_SOBER_DATE)));
				user.setHaveKids(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_HAVE_KIDS)));
				user.setAccupation(c.getString(c
						.getColumnIndex(MeehabDBOpenHelper.COLUMN_ACCUPATION)));
				users.add(user);
			}
		}

		close();
		return users;
	}

	public UserAccount findUser(int userId) {
		UserAccount user = null;
		open();
		Cursor c = database.query(MeehabDBOpenHelper.TABLE_USER, null,
				MeehabDBOpenHelper.COLUMN_USER_ID + "=?",
				new String[] { String.valueOf(userId) }, null, null, null);

		if (c.getCount() > 0 && c.moveToFirst()) {
			user = new UserAccount();
			// while (c.moveToNext()) {
			user.setId(c.getInt(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_USER_ID)));
			user.setUsername(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_USER_NAME)));
			user.setEmail(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_USER_EMAIL)));
			
			user.setSocailId(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_USER_FACEBOOK_ID)));
			user.setCheckinType(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_USER_CHECK_IN)));
			user.setNotification(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_USER_NOTIFICATION)));
			user.setPhone(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_PHONE)));
			user.setInsurance(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_INSURANCE)));
			user.setImage(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_PICTURE)));
			user.setGender(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_GENDER)));
			user.setDateOfBirth(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_DOB)));

			user.setMeetingHomeGroup(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_HOME_GROUP)));

			user.setMaritalStatus(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_MARITAL_STATUS)));
			user.setIntrestedIn(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_INTRESTED_IN)));
			user.setAboutStory(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_ABOUT_STORY)));
			user.setWillingSponsor(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_WILLING_SPONSOR)));
			user.setHeight(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_HEIGHT)));
			user.setWeight(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_WEIGHT)));
			user.setSexualOrientation(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_SEXUAL_ORIENTATION)));
			user.setEthnicity(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_ETHNICITY)));
			user.setLatitude(c.getInt(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_LAT)));
			user.setLongitude(c.getInt(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_LNG)));
			user.setSoberSence(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_SOBER_DATE)));
			user.setHaveKids(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_HAVE_KIDS)));
			user.setAccupation(c.getString(c
					.getColumnIndex(MeehabDBOpenHelper.COLUMN_ACCUPATION)));
			// }
		}
		c.close();
		close();
		return (user != null ? user : null);
	}

	public boolean remove(UserAccount user) {
		open();
		String where = MeehabDBOpenHelper.COLUMN_USER_ID + "='" + user.getId()
				+ "'";

		int result = database
				.delete(MeehabDBOpenHelper.TABLE_USER, where, null);

		close();
		return (result == 1);
	}

	public boolean update(UserAccount user) {
		if (user == null)
			return false;
		open();
		ContentValues values = new ContentValues();
		values.put(MeehabDBOpenHelper.COLUMN_USER_ID, user.getId());
		values.put(MeehabDBOpenHelper.COLUMN_USER_NAME, user.getUsername());
		values.put(MeehabDBOpenHelper.COLUMN_USER_EMAIL, user.getEmail());
		values.put(MeehabDBOpenHelper.COLUMN_USER_FACEBOOK_ID, user.getSocailId());
		values.put(MeehabDBOpenHelper.COLUMN_USER_CHECK_IN, user.getCheckinType());
		values.put(MeehabDBOpenHelper.COLUMN_USER_NOTIFICATION, user.getNotification());
		values.put(MeehabDBOpenHelper.COLUMN_PHONE, user.getPhone());
		values.put(MeehabDBOpenHelper.COLUMN_PICTURE, user.getImage());
		values.put(MeehabDBOpenHelper.COLUMN_GENDER, user.getGender());

		values.put(MeehabDBOpenHelper.COLUMN_ACCUPATION, user.getAccupation());
		values.put(MeehabDBOpenHelper.COLUMN_INSURANCE, user.getInsurance());
		values.put(MeehabDBOpenHelper.COLUMN_DOB, user.getDateOfBirth());
		
		values.put(MeehabDBOpenHelper.COLUMN_HOME_GROUP, user.getMeetingHomeGroup());
		
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
		close();

		return (result == 1);
	}
	
	
	public boolean updateHomeGroup(UserAccount user) {
		if (user == null)
			return false;
		open();
		ContentValues values = new ContentValues();
		
		
		values.put(MeehabDBOpenHelper.COLUMN_HOME_GROUP, user.getMeetingHomeGroup());
		


		String where = MeehabDBOpenHelper.COLUMN_USER_ID + "='" + user.getId()
				+ "'";
		// String where = "id=?";
		// String[] whereArgs = new String[] {String.valueOf(meeting.getId())};

		int result = database.update(MeehabDBOpenHelper.TABLE_USER, values,
				where, null);
		close();

		return (result == 1);
	}

	public boolean removeAllUsers() {
		open();
		database.execSQL("delete from " + MeehabDBOpenHelper.TABLE_USER);
		close();
		return true;
	}

	/**
	 * @return
	 */
	public boolean hasAccounts() {
		open();
		Cursor c = database.query(MeehabDBOpenHelper.TABLE_USER, allColumns,
				null, null, null, null, null);

		if (c.getCount() > 0) {
			return true;
		}
		c.close();
		close();
		return false;

	}

}
