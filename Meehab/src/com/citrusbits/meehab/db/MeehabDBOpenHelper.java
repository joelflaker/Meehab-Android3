package com.citrusbits.meehab.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MeehabDBOpenHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "MeehabDB.db";
	private static final int DATABASE_VERSION = 1;

	// Users table
	public static final String TABLE_USER = "Users";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_USER_ID = "user_id";
	public static final String COLUMN_USER_NAME = "user_name";
	public static final String COLUMN_USER_EMAIL = "email";
	public static final String COLUMN_PHONE = "phone";
	public static final String COLUMN_PICTURE = "picture";
	public static final String COLUMN_GENDER = "gender";
	public static final String COLUMN_ACCUPATION = "accupation";
	public static final String COLUMN_DOB = "dob";
	
	public static final String COLUMN_HOME_GROUP = "meeting_home_group";
	
	public static final String COLUMN_MARITAL_STATUS = "marital_status";
	public static final String COLUMN_INTRESTED_IN = "intrested_in";
	public static final String COLUMN_ABOUT_STORY = "about_story";
	public static final String COLUMN_WILLING_SPONSOR = "willing_sponsor";
	public static final String COLUMN_HEIGHT = "height";
	public static final String COLUMN_WEIGHT = "weight";
	public static final String COLUMN_SEXUAL_ORIENTATION = "sexual_orientation";
	public static final String COLUMN_ETHNICITY = "ethnicity";
	public static final String COLUMN_SOBER_DATE = "sober_date";
	public static final String COLUMN_HAVE_KIDS = "have_kids";
	public static final String COLUMN_LAT = "lat";
	public static final String COLUMN_LNG = "lng";
	// public static final String COLUMN_ = "";

	// Meetings table
	public static final String TABLE_MEETINGS = "";
	// public static final String COLUMN_ = "";
	// public static final String COLUMN_ = "";
	// public static final String COLUMN_ = "";
	// public static final String COLUMN_ = "";
	// public static final String COLUMN_ = "";
	// public static final String COLUMN_ = "";
	// public static final String COLUMN_ = "";
	// public static final String COLUMN_ = "";
	// public static final String COLUMN_ = "";
	// public static final String COLUMN_ = "";
	// public static final String COLUMN_ = "";
	// public static final String COLUMN_ = "";

	public static final String TABLE_FILTERS = "Filters";
	public static final String COLUMN_PHOTOS_ONLY = "photos_only";
	public static final String COLUMN_ONLINE_ONLY = "online_only";
	public static final String COLUMN_S_AGE = "s_age";
	public static final String COLUMN_L_AGE = "l_age";
	public static final String COLUMN_BODY_TYPE_REGULAR = "body_type_regular";
	public static final String COLUMN_BODY_TYPE_SLIM = "body_type_slim";
	public static final String COLUMN_BODY_TYPE_STOCKY = "body_type_stocky";
	public static final String COLUMN_BODY_TYPE_ATHLETIC = "body_type_athletic";
	public static final String COLUMN_ETHNICITY_ASIAN = "ethnicity_asian";
	public static final String COLUMN_ETHNICITY_BLACK = "ethnicity_black";
	public static final String COLUMN_ETHNICITY_LATINO = "ethnicity_latino";
	public static final String COLUMN_ETHNICITY_WHITE = "ethnicity_white";
	public static final String COLUMN_ETHNICITY_MIXED = "ethnicity_mixed";
	public static final String COLUMN_ETHNICITY_OTHER = "ethnicity_other";
	public static final String COLUMN_FILTER_LOOKING_FOR_DATING = "looking_for_dating";
	public static final String COLUMN_FILTER_LOOKING_FOR_FRIENDS = "looking_for_friends";
	public static final String COLUMN_FILTER_LOOKING_FOR_RELATIONSHIP = "looking_for_relationship";
	public static final String COLUMN_SINGLE = "single";
	public static final String COLUMN_ENGAGED = "engaged";
	public static final String COLUMN_MARRIED = "married";
	public static final String COLUMN_IN_A_RELATIONSHIP = "in_a_relationship";
	public static final String COLUMN_OPEN_RELATIONSHIP = "open_relationship";

	public static final String TABLE_DIALOGS = "Dialogs";
	public static final String COLUMN_DIALOG_ID = "dialog_id";
	public static final String COLUMN_LAST_MSG = "last_msg";
	public static final String COLUMN_LAST_MSG_TIME = "last_msg_time";
	public static final String COLUMN_OPPONENT_ID = "opponents_id";
	public static final String COLUMN_UNREAD_MESSAGE = "unread_message";
	public static final String COLUMN_CREATOR_ID = "creator_id";

	public static final String TABLE_QB_USERS = "QBUsers";
	public static final String COLUMN_QBID = "qb_id";
	public static final String COLUMN_FULL_NAME = "full_name";
	public static final String COLUMN_IMAGE = "image";
	public static final String COLUMN_LAST_REQUEST_AT = "last_request_at";
	public static final String COLUMN_BLOCK_ID = "block_id";
	public static final String COLUMN_FAVORITE_ID = "favorite_id";
	public static final String COLUMN_USERID = "user_id";
	public static final String COLUMN_CUSTOM_DATA = "custom_data";
	public static final String COLUMN_BLOCKED_BY_ID = "blocked_by_id";
	public static final String COLUMN_BLOCKED_BY = "blocked_by";

	public static final String TABLE_CHATS = "Chats";
	public static final String COLUMN_CHAT_ID_PRIMARY = "chat_id_primary";
	public static final String COLUMN_CHAT_ID = "chat_id";
	public static final String COLUMN_FROM_MSG = "from_msg";
	public static final String COLUMN_TO_MSG = "to_msg";
	public static final String COLUMN_MSG = "message";
	public static final String COLUMN_TIME_STAMP = "time_stamp";

	public static final String TABLE_EVENTS_FILTERS = "Events_Filters";
	public static final String COLUMN_EVENTS_SETTING_ID = "event_setting_id";
	public static final String COLUMN_EVENTS_USER_ID = "user_id";
	public static final String COLUMN_EVENTS_PRICE = "price";
	public static final String COLUMN_EVENTS_DISTANCE = "distance";
	public static final String COLUMN_EVENTS_TIME = "time";
	public static final String COLUMN_EVENTS_ANNIVERSARIES = "anniversaries";
	public static final String COLUMN_EVENTS_CONCERTS = "concerts";
	public static final String COLUMN_EVENTS_DINING = "dining";
	public static final String COLUMN_EVENTS_EDUCATIONAL = "educational";
	public static final String COLUMN_EVENTS_ENTERTAINMENT = "entertainment";
	public static final String COLUMN_EVENTS_FESTIVALS = "festivals";
	public static final String COLUMN_EVENTS_MUSEUM = "museum";
	public static final String COLUMN_EVENTS_OUTDOORS = "outdoors";
	public static final String COLUMN_EVENTS_PARKS = "parks";
	public static final String COLUMN_EVENTS_RELIGIOUS = "religous";
	public static final String COLUMN_EVENTS_SEMINARS = "seminars";
	public static final String COLUMN_EVENTS_SPORTS = "sports";

	public static final String TABLE_EVENTS = "Events";
	public static final String COLUMN_EVENT_ID = "event_id";
	public static final String COLUMN_EVENT_TITLE = "title";
	public static final String COLUMN_EVENT_PRICE = "price";
	public static final String COLUMN_EVENT_DISTANCE = "distance";
	public static final String COLUMN_EVENT_TIME = "time";
	public static final String COLUMN_EVENT_PHONE = "phone";
	public static final String COLUMN_EVENT_PICTURE = "picture";
	public static final String COLUMN_EVENT_CATEGORY = "category";
	public static final String COLUMN_EVENT_LOCATION = "location";
	public static final String COLUMN_EVENT_DESCRIPTION = "description";
	public static final String COLUMN_EVENT_LAT = "lat";
	public static final String COLUMN_EVENT_LNG = "lng";
	public static final String COLUMN_EVENT_URL = "url";

	private static final String TABLE_CREATE_USER = "CREATE TABLE "
			+ TABLE_USER + " (" + COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER_ID
			+ " INTEGER, " + COLUMN_USER_NAME + " TEXT, " + COLUMN_USER_EMAIL
			+ " TEXT, " + COLUMN_PHONE + " TEXT, " + COLUMN_PICTURE + " TEXT, "+ COLUMN_ACCUPATION + " TEXT, "
			+ COLUMN_GENDER + " TEXT, " + COLUMN_DOB + " TEXT, " + COLUMN_HOME_GROUP + " TEXT, "
			+ COLUMN_MARITAL_STATUS + " TEXT, " + COLUMN_INTRESTED_IN
			+ " TEXT, " + COLUMN_ABOUT_STORY + " TEXT, "
			+ COLUMN_WILLING_SPONSOR + " TEXT, " + COLUMN_HEIGHT + " TEXT, "
			+ COLUMN_WEIGHT + " TEXT, " + COLUMN_SEXUAL_ORIENTATION + " TEXT, "
			+ COLUMN_ETHNICITY + " TEXT, " + COLUMN_SOBER_DATE + " TEXT, "
			+ COLUMN_HAVE_KIDS + " TEXT, " + COLUMN_LAT + " TEXT, "
			+ COLUMN_LNG + " TEXT " + ")";

	private static final String TABLE_CREATE_FILTERS = "CREATE TABLE "
			+ TABLE_FILTERS + " (" + COLUMN_PHOTOS_ONLY + " TEXT, "
			+ COLUMN_ONLINE_ONLY + " TEXT, " + COLUMN_S_AGE + " TEXT, "
			+ COLUMN_L_AGE + " TEXT, " + COLUMN_BODY_TYPE_REGULAR + " TEXT, "
			+ COLUMN_BODY_TYPE_SLIM + " TEXT, " + COLUMN_BODY_TYPE_STOCKY
			+ " TEXT, " + COLUMN_BODY_TYPE_ATHLETIC + " TEXT, "
			+ COLUMN_ETHNICITY_ASIAN + " TEXT, " + COLUMN_ETHNICITY_BLACK
			+ " TEXT, " + COLUMN_ETHNICITY_LATINO + " TEXT, "
			+ COLUMN_ETHNICITY_WHITE + " TEXT, " + COLUMN_ETHNICITY_MIXED
			+ " TEXT, " + COLUMN_ETHNICITY_OTHER + " TEXT, "
			+ COLUMN_FILTER_LOOKING_FOR_DATING + " TEXT, "
			+ COLUMN_FILTER_LOOKING_FOR_FRIENDS + " TEXT, "
			+ COLUMN_FILTER_LOOKING_FOR_RELATIONSHIP + " TEXT, "
			+ COLUMN_SINGLE + " TEXT, " + COLUMN_ENGAGED + " TEXT, "
			+ COLUMN_MARRIED + " TEXT, " + COLUMN_IN_A_RELATIONSHIP + " TEXT, "
			+ COLUMN_OPEN_RELATIONSHIP + " TEXT" + ")";

	private static final String TABLE_CREATE_DIALOGS = "CREATE TABLE "
			+ TABLE_DIALOGS + " (" + COLUMN_DIALOG_ID + " TEXT, "
			+ COLUMN_LAST_MSG + " TEXT, " + COLUMN_LAST_MSG_TIME + " TEXT, "
			+ COLUMN_OPPONENT_ID + " TEXT, " + COLUMN_UNREAD_MESSAGE
			+ " TEXT, " + COLUMN_CREATOR_ID + " TEXT" + ")";

	private static final String TABLE_CREATE_QB_USERS = "CREATE TABLE "
			+ TABLE_QB_USERS + " (" + COLUMN_QBID + " TEXT, "
			+ COLUMN_FULL_NAME + " TEXT, " + COLUMN_IMAGE + " TEXT, "
			+ COLUMN_LAST_REQUEST_AT + " TEXT, " + COLUMN_BLOCK_ID + " TEXT, "
			+ COLUMN_FAVORITE_ID + " TEXT, " + COLUMN_USERID + " TEXT, "
			+ COLUMN_CUSTOM_DATA + " TEXT, " + COLUMN_BLOCKED_BY_ID + " TEXT, "
			+ COLUMN_BLOCKED_BY + " TEXT" + ")";

	private static final String TABLE_CREATE_CHATS = "CREATE TABLE "
			+ TABLE_CHATS + " (" + COLUMN_CHAT_ID_PRIMARY
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CHAT_ID + " INTEGER , "  + COLUMN_FROM_MSG + " INTEGER , "
			+ COLUMN_TO_MSG + " INTEGER , " + COLUMN_MSG + " TEXT, "
			+ COLUMN_TIME_STAMP + " TEXT" + ")";
	
	

	public MeehabDBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE_USER);
		db.execSQL(TABLE_CREATE_FILTERS);
		db.execSQL(TABLE_CREATE_DIALOGS);
		db.execSQL(TABLE_CREATE_QB_USERS);
		db.execSQL(TABLE_CREATE_CHATS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIALOGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_QB_USERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS_FILTERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
		onCreate(db);
	}

}
