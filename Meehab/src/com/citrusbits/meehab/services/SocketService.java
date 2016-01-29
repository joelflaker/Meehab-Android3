package com.citrusbits.meehab.services;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import android.util.LruCache;
import android.widget.Toast;

import com.citrusbits.meehab.ChatActivity;
import com.citrusbits.meehab.HomeActivity;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.app.App;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.DatabaseHandler;
import com.citrusbits.meehab.db.UserDatasource;
import com.citrusbits.meehab.fragments.MessagesFragment;
import com.citrusbits.meehab.fragments.OptionsFragment;
import com.citrusbits.meehab.helpers.LogoutHelper;
import com.citrusbits.meehab.model.FriendMessageModel;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.pojo.AddUserResponse;
import com.citrusbits.meehab.pojo.MeetingReviewsResponse;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.receivers.EventReceiver;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.UserAcountUtils;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.emitter.Emitter.Listener;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

public class SocketService extends Service {

	public static final String tag = "NodeJSConnectionService";
	public static final String ACTION_CLEAR_NOTIFICATION = "clear_notification";
	public static final String ACTION_DISABLE_FOREGROUND = "disable_foreground";
	private static final String ACTION_MERGE_PHONE_CONTACTS = "merge_phone_contacts";

	public static final String ACTION_RECONNECT_NODE_JS_SERVER = "reconnect_node_js_server";

	public static final String ACTION_TRY_AGAIN = "try_again";
	public static final String ACTION_DISABLE_ACCOUNT = "disable_account";

	public static final int SOCKET_BACKGROUND_NOTIFICATION_ID = 38;

	// private HashMap<String, Object> cacheResponses = new HashMap<>();

	private final OnBindListener mOnBindListener = new OnBindListener() {

		@Override
		public void onBind(final UserAccount account) {
			updateConversationUi();
		}
	};

	private UserDatasource userDatasource;
	private UserAccount mUserAccount;
	public Socket mSocket;
	private final IBinder mBinder = new SocketServiceConnectionBinder();

	private OnConversationUpdate mOnConversationUpdate = null;

	private OnAccountUpdate mOnAccountUpdate = null;
	private OnStatusChanged statusListener = new OnStatusChanged() {

		@Override
		public void onStatusChanged(UserAccount account) {

		}
	};
	private int socketResponseListenerCount = 0;
	private int accountChangedListenerCount = 0;
	private WakeLock wakeLock;
	private PowerManager pm;
	private LruCache<String, Bitmap> mBitmapCache;
	private Thread mPhoneContactMergerThread;

	private boolean mRestoredFromDatabase = false;
	private boolean RESET_ATTEMPT_COUNT_ON_NETWORK_CHANGE = true;
	private Handler ui;

	private AppPrefs prefs;

	private IO.Options opts;

	public boolean areMessagesInitialized() {
		return this.mRestoredFromDatabase;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(tag, "Service:onStartCommand");

		final String action = intent == null ? null : intent.getAction();
		Notification note = new Notification(0, null,
				System.currentTimeMillis());
		note.flags |= Notification.FLAG_NO_CLEAR;
		startForeground(SOCKET_BACKGROUND_NOTIFICATION_ID, note);

		if (action != null) {
			switch (action) {
			case ConnectivityManager.CONNECTIVITY_ACTION:
				if (hasInternetConnection()
						&& RESET_ATTEMPT_COUNT_ON_NETWORK_CHANGE) {
					resetAllAttemptCounts(true);
				}
				break;
			case ACTION_MERGE_PHONE_CONTACTS:
				if (mRestoredFromDatabase) {
					// PhoneHelper.loadPhoneContacts(getApplicationContext(),new
					// CopyOnWriteArrayList<Bundle>(),this);
				}
				return START_STICKY;
			case Intent.ACTION_SHUTDOWN:
				logoutAndSave();
				return START_NOT_STICKY;
			case ACTION_CLEAR_NOTIFICATION:
				// mNotificationService.clear();
				break;
			case ACTION_DISABLE_FOREGROUND:
				getPreferences().edit()
						.putBoolean("keep_foreground_service", false).commit();
				toggleForegroundService();
				break;
			case ACTION_TRY_AGAIN:
				resetAllAttemptCounts(false);
				break;
			case ACTION_DISABLE_ACCOUNT:
				// String jid = intent.getStringExtra("account");
				// updateAccount(account);
				break;
			case ACTION_RECONNECT_NODE_JS_SERVER:
				// disconnectSocket();
				if (mSocket == null || !mSocket.connected()) {
					connectSocket();
				}

				break;
			}
		}
		this.wakeLock.acquire();

		if (wakeLock.isHeld()) {
			try {
				wakeLock.release();
			} catch (final RuntimeException ignored) {
			}
		}
		return START_STICKY;
	}

	private void resetAllAttemptCounts(boolean reallyAll) {
	}

	public boolean hasInternetConnection() {
		ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnected();
	}

	DatabaseHandler dbHandler;

	private static SocketService mSocketServcie;

	Handler mHandler;

	@SuppressLint("TrulyRandom")
	@Override
	public void onCreate() {
		dbHandler = DatabaseHandler.getInstance(SocketService.this);
		mHandler = new Handler();
		Log.d(tag, "Service:onCreate");
		this.mSocketServcie = this;
		prefs = AppPrefs.getAppPrefs(SocketService.this);
		this.userDatasource = new UserDatasource(this);
		ui = new Handler(Looper.getMainLooper());
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;
		this.mBitmapCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(final String key, final Bitmap bitmap) {
				return bitmap.getByteCount() / 1024;
			}
		};

		connectSocket();

		this.pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		this.wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"MeehabConnectionService");
		toggleForegroundService();
	}

	public static SocketService getInstance() {
		return mSocketServcie;
	}

	/**
	 * connect to nodeJS socket and
	 */
	private void connectSocket() {
		try {

			final String accessToken = getSharedPreferences(
					Consts.APP_PREFS_NAME, Context.MODE_PRIVATE).getString(
					Consts.SERVER_ACCESS_TOKEN, null);
			Log.d("App:connect", "accessToken: " + accessToken);

			opts = new IO.Options();
			opts.forceNew = true;
			// opts.reconnection = false;
			opts.query = "apiKey=@citrusbits&apiSecret=@citrusbits&accessToken=";
			if (accessToken != null) {
				opts.query = "apiKey=@citrusbits&apiSecret=@citrusbits&accessToken="
						+ accessToken;
				Log.d("query", opts.query);
			}

			mSocket = IO.socket(getResources().getString(R.string.url), opts);
			mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
			mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
			mSocket.on(EventParams.EVENT_USER_SIGNUP, onRegister);
			mSocket.on(EventParams.EVENT_USER_FACEBOOK, onFacebookLogin);
			mSocket.on(EventParams.METHOD_USER_LOGIN, onLogin);
			mSocket.on(EventParams.EVENT_USER_FORGOT_PASSWORD, onForgotPassword);
			mSocket.on(EventParams.EVENT_USER_UPDATE, onUpdate);
			mSocket.on(EventParams.EVENT_MEETING_GET_ALL, onMeeting);
			
			mSocket.on(EventParams.EVENT_GET_ALL_REHABS, onAllRehab);
			
			mSocket.on(EventParams.EVENT_ADD_USER_FAVOURITE, onAddUserFavourite);
			mSocket.on(EventParams.EVENT_ADD_MEETING, onAddMeeting);
			mSocket.on(EventParams.EVENT_ADD_REHAB, onAddRehab);
			mSocket.on(EventParams.EVENT_ADD_INSURANCE, onAddInsurance);
			mSocket.on(EventParams.EVENT_FAVOURITE_LIST, onFavList);
			mSocket.on(EventParams.EVENT_MEETING_GET_REVIEWS, onMeetingReviews);
			mSocket.on(EventParams.EVENT_MEETING_ADD_REVIEW, onAddReview);

			mSocket.on(EventParams.EVENT_GET_USER_REVIEWS, onGetUserReviews);
			mSocket.on(EventParams.METHOD_CHAT_SEND_RECEIVE,
					onSendReceiveChatMessage);

			mSocket.on(EventParams.METHOD_DELETE_CHAT_MESSAGE,
					onDeleteChatMessage);

			mSocket.on(EventParams.METHOD_HOME_GROUP_USER, onHomeGroupUser);
			mSocket.on(EventParams.METHOD_RSVP, onRsvp);

			mSocket.on(EventParams.METHOD_GET_CHAT_FRIENDS, onGetChatFriends);

			mSocket.on(EventParams.METHOD_DELETE_CONVERSATION,
					onDeleteConversation);

			mSocket.on(EventParams.METHOD_ACCESS_TOKEN, onAccessToken);

			mSocket.on(EventParams.METHOD_REPORT_USER, onReportUser);

			mSocket.on(EventParams.METHOD_BLOCK_USER_NOTIFY,
					onBlockedUserNotify);

			mSocket.on(EventParams.METHOD_SYNC_PHONE, onSyncUserNotify);

			mSocket.on(EventParams.METHOD_DISCONNECT_SOCKET,
					onDisconnectListener);

			mSocket.on(EventParams.METHOD_CONNECT_SOCKET, onConnectListener);

			mSocket.on(EventParams.METHOD_ERROR_SOCKET, onErrorListner);
			mSocket.on(EventParams.METHOD_RE_CONNECT_SOCKET,
					onReconnectListener);
			mSocket.on(EventParams.METHOD_RECONNECT_ATTEMPT_SOCKET,
					onReconnecAttempt);

			mSocket.on(EventParams.METHOD_CHECK_IN_USER, onCheckInUser);

			mSocket.on(EventParams.METHOD_BLOCK_USER, onBlockUser);
			mSocket.on(EventParams.METHOD_FAVOURITE_USER, onFavouriteUser);

			mSocket.on(EventParams.EVENT_DELETE_USER_REVIEW,
					onDeleteUserReviews);

			mSocket.on(EventParams.METHOD_REPORT_MEETING, onReportMeeting);

			mSocket.on(EventParams.METHOD_CHECK_INFO, onCheckUserInfo);
			mSocket.on(EventParams.METHOD_GET_ALL_FRIENDS, onGetAllFriends);

			mSocket.on(EventParams.METHOD_RSVP_USERS, onGetAllRsvpFriends);

			mSocket.on(EventParams.METHOD_CHECK_IN_MEETING, onCheckInMeeting);

			mSocket.on(EventParams.METHOD_CHAT_PAGINATION, onChatPagination);

			mSocket.connect();

		} catch (URISyntaxException e) {
			Log.d("socket", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public boolean isConnected() {
		return mSocket != null && mSocket.connected();
	}

	private Emitter.Listener onMeeting = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				// toastOnUiThread("Message is "+data.getBoolean("type"));

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					// cacheResponses.put(EventParams.EVENT_MEETING_GET_ALL,
					// data);

					onSocketResponseSuccess(EventParams.EVENT_MEETING_GET_ALL,
							data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};
	
	
	private Emitter.Listener onAllRehab = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				// toastOnUiThread("Message is "+data.getBoolean("type"));

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					// cacheResponses.put(EventParams.EVENT_MEETING_GET_ALL,
					// data);

					onSocketResponseSuccess(EventParams.EVENT_GET_ALL_REHABS,
							data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};
	
	
	private Emitter.Listener onAddInsurance = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(
							EventParams.EVENT_ADD_INSURANCE, data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};
	
	
	private Emitter.Listener onAddRehab = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(
							EventParams.EVENT_ADD_REHAB, data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};
	
	
	
	private Emitter.Listener onAddMeeting = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(
							EventParams.EVENT_ADD_MEETING, data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onAddUserFavourite = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(
							EventParams.EVENT_ADD_USER_FAVOURITE, data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onFavList = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(EventParams.EVENT_FAVOURITE_LIST,
							data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onChatPagination = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					onSocketResponseSuccess(EventParams.METHOD_CHAT_PAGINATION,
							data);

					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onDisconnectListener = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Log.e("Socket service ", "Socket is disconnected!");

			updateAccessToken();
		}
	};

	public void updateAccessToken() {
		final String accessToken = getSharedPreferences(Consts.APP_PREFS_NAME,
				Context.MODE_PRIVATE).getString(Consts.SERVER_ACCESS_TOKEN, "");
		Log.d("App:connect", "accessToken: " + accessToken);

		// opts.query = "apiKey=@citrusbits&apiSecret=@citrusbits&accessToken=";
		String query = opts.query;
		String[] andSplit = query.split("&");
		String[] equalSplit = andSplit[2].split("=");
		String prevAccessToken = equalSplit.length > 1 ? equalSplit[1] : "";
		if (!prevAccessToken.equals(accessToken)) {
			disconnectSocket();
			connectSocket();
		}

	}

	private Emitter.Listener onConnectListener = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Log.e("Socket service ", "Socket is connected!");

			updateAccessToken();
		}
	};

	private Emitter.Listener onErrorListner = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Log.e("Socket service ", "Socket erro is called!");
			updateAccessToken();
		}
	};

	private Emitter.Listener onReconnectListener = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Log.e("Socket service ", "Socket Reconnect is called!");
			updateAccessToken();
		}
	};

	private Emitter.Listener onReconnecAttempt = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Log.e("Socket service ", "Socket Reconnect Attempt is called!");
		}
	};

	private Emitter.Listener onSyncUserNotify = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			/* Gson gson = new Gson(); */
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					onSocketResponseSuccess(EventParams.METHOD_SYNC_PHONE, data);
					Intent i = new Intent(
							ContactSyncService.ACTION_CONTACT_SYNC);
					i.putExtra(ContactSyncService.EXTRA_EVENT,
							EventParams.METHOD_SYNC_PHONE);
					i.putExtra(ContactSyncService.EXTRA_RESULT, data.toString());
					SocketService.this.sendBroadcast(i);

					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onBlockedUserNotify = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					onSocketResponseSuccess(
							EventParams.METHOD_BLOCK_USER_NOTIFY, data);

					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onCheckInUser = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					onSocketResponseSuccess(EventParams.METHOD_CHECK_IN_USER,
							data);

					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onReportUser = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {

			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(EventParams.METHOD_REPORT_USER,
							data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onBlockUser = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {

			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(EventParams.METHOD_BLOCK_USER, data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onFavouriteUser = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {

			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(EventParams.METHOD_FAVOURITE_USER,
							data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onAccessToken = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == false) {

					LogoutHelper logoutHelper = new LogoutHelper(
							SocketService.this);
					logoutHelper.clearLoginCredentails();

					SocketService.this.sendBroadcast(new Intent(
							HomeActivity.ACTION_LOGOUT));

				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onDeleteConversation = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(
							EventParams.METHOD_DELETE_CONVERSATION, data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onGetChatFriends = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(
							EventParams.METHOD_GET_CHAT_FRIENDS, data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onDeleteChatMessage = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(
							EventParams.METHOD_DELETE_CHAT_MESSAGE, data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onSendReceiveChatMessage = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					boolean chatActivityEnable = prefs.getBooleanPrefs(
							AppPrefs.KEY_CHAT_ACTIVITY_OPEN,
							AppPrefs.DEFAULT.CHAT_ACTIVITY_OPEN);

					Log.d("Chat Activity Enable ", chatActivityEnable + "");

					String[] senderIdName = getSenderIdAndName(data);
					int userId = AccountUtils.getUserId(SocketService.this);
					int chatFriendId = AccountUtils
							.getChatFriendId(SocketService.this);
					// String userName=
					if (userId == -1) {
						return;
					}
					int senderId = Integer.parseInt(senderIdName[0]);
					String userName = senderIdName[1];
					if ((chatActivityEnable && senderId == userId)
							|| (chatActivityEnable && senderId == chatFriendId)) {

						onSocketResponseSuccess(
								EventParams.METHOD_CHAT_SEND_RECEIVE, data);
					} else {

						FriendMessageModel friendMessage = dbHandler
								.getFriend(senderId);

						if (friendMessage != null) {
							String name = friendMessage.getName();
							sendNotification(data, userName);
							int unreadMessageCount = friendMessage
									.getUnreadMessageCount() + 1;
							dbHandler.updateMessgeCount(senderId,
									unreadMessageCount);
						} else {

							sendNotification(data, userName);
						}

						SocketService.this.sendBroadcast(new Intent(
								HomeActivity.ACTION_MESSAGE_COUNT_UPDATE));
						boolean conversationOpen = prefs.getBooleanPrefs(
								AppPrefs.KEY_CONVERSATION_FRAG_OPEN,
								AppPrefs.DEFAULT.CONVERSATION_FRAG_OPEN);
						if (conversationOpen) {
							SocketService.this
									.sendBroadcast(new Intent(
											MessagesFragment.ACTION_REFRESH_CONVERSATION));
						}

					}

					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	public String[] getSenderIdAndName(JSONObject data) {
		String fromSend = "-1";
		JSONObject sendReceiveObject;
		String name = "";
		try {
			sendReceiveObject = data.getJSONObject("sendReciveMessages");
			fromSend = sendReceiveObject.getString("fromID");
			name = sendReceiveObject.getString("from_username");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String[] { fromSend, name };

	}

	public void sendNotification(JSONObject data, String name) {

		JSONObject sendReceiveObject;
		Log.e("Notification block is called!", "Yes");
		try {
			sendReceiveObject = data.getJSONObject("sendReciveMessages");
			String message;
			message = sendReceiveObject.getString("message");
			String timeStamp = sendReceiveObject.getString("datetime_added");
			int fromSend = sendReceiveObject.getInt("fromID");
			int toSend = sendReceiveObject.getInt("toID");
			String fromName = sendReceiveObject.getString("from_username");

			// if (AccountUtils.getUserId(SocketService.this) == fromSend) {
			// return;
			// }

			String tittle = "Mehab";
			String subject = name;
			String body = message;

			NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notify = new Notification(
					R.drawable.ic_chat_notification_icon, tittle,
					System.currentTimeMillis());
			Intent notificationIntent = new Intent(SocketService.this,
					ChatActivity.class);
			notificationIntent.putExtra(ChatActivity.KEY_FRIEND_ID, fromSend);
			notificationIntent.putExtra(ChatActivity.KEY_FRIEND_NAME, fromName);
			PendingIntent pending = PendingIntent.getActivity(
					getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			

			notify.setLatestEventInfo(getApplicationContext(), subject, body,
					pending);
			notify.flags |= Notification.FLAG_AUTO_CANCEL;
			notif.notify(fromSend, notify);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Emitter.Listener onGetUserReviews = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(EventParams.EVENT_GET_USER_REVIEWS,
							data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onReportMeeting = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(EventParams.METHOD_REPORT_MEETING,
							data);

					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onRsvp = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				final JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(EventParams.METHOD_RSVP, data);

					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onHomeGroupUser = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(EventParams.METHOD_HOME_GROUP_USER,
							data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onDeleteUserReviews = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(
							EventParams.EVENT_DELETE_USER_REVIEW, data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onCheckUserInfo = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(EventParams.METHOD_CHECK_INFO, data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onCheckInMeeting = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {

			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(
							EventParams.METHOD_CHECK_IN_MEETING, data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				// onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onGetAllRsvpFriends = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(EventParams.METHOD_RSVP_USERS, data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onGetAllFriends = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];

				Log.d(tag, data.toString());

				if (data.getBoolean("type") == true) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(EventParams.METHOD_GET_ALL_FRIENDS,
							data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onRegister = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Log.d("signup:", args.toString());
			Gson gson = new Gson();
			try {
				JSONObject data = (JSONObject) args[0];
				Log.e("signup:", data.toString());
				AddUserResponse response = gson.fromJson(data.toString(),
						AddUserResponse.class);
				Log.e("Type", response.getType() + "");
				if (response.getType()) {
					userDatasource.add(response.getUser());
					// save accessToken
					AccountUtils.saveUserId(SocketService.this, response
							.getUser().getId());
					getPreferences()
							.edit()
							.putString(Consts.SERVER_ACCESS_TOKEN,
									response.getAccessToken().getAccessToken())
							.commit();
					onSocketResponseSuccess(EventParams.EVENT_USER_SIGNUP, "");
					// disconnectSocket();
					// connectSocket();

					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}

		}
	};

	private Emitter.Listener onLogin = new Emitter.Listener() {

		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();

			try {
				JSONObject data = (JSONObject) args[0];
				Log.d(tag, data.toString());
				AddUserResponse response = gson.fromJson(data.toString(),
						AddUserResponse.class);

				if (response.getType()) {

					getPreferences()
							.edit()
							.putString(Consts.SERVER_ACCESS_TOKEN,
									response.getAccessToken().getAccessToken())
							.commit();

					int userId = AccountUtils.getUserId(SocketService.this);
					if (response.getUser().getId() == userId) {
						userDatasource.update(response.getUser());
					} else {
						userDatasource.add(response.getUser());
					}

					AccountUtils.saveUserId(SocketService.this, response
							.getUser().getId());

					onSocketResponseSuccess(EventParams.METHOD_USER_LOGIN, "");

				} else {
					onSocketResponseFailure(response.getMessage());
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}

		}

	};

	private Emitter.Listener onFacebookLogin = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {
				JSONObject data = (JSONObject) args[0];
				Log.d(tag, data.toString());
				AddUserResponse response = gson.fromJson(data.toString(),
						AddUserResponse.class);

				if (response.getType()) {
					userDatasource.add(response.getUser());
					getPreferences()
							.edit()
							.putString(Consts.SERVER_ACCESS_TOKEN,
									response.getAccessToken().getAccessToken())
							.commit();
					onSocketResponseSuccess("", "");
					// disconnectSocket();
					// connectSocket();

					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(response.getMessage());
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}

		}
	};

	private Emitter.Listener onForgotPassword = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {
				JSONObject data = (JSONObject) args[0];
				Log.d(tag, data.toString());
				AddUserResponse response = gson.fromJson(data.toString(),
						AddUserResponse.class);
				if (response.getType()) {
					onSocketResponseSuccess(
							EventParams.EVENT_USER_FORGOT_PASSWORD, response);
				} else {
					onSocketResponseFailure(response.getMessage());
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onUpdate = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];
				Log.d(tag, data.toString());
				AddUserResponse response = gson.fromJson(data.toString(),
						AddUserResponse.class);
				if (response.getType()) {
					userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(EventParams.EVENT_USER_UPDATE,
							response);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(response.getMessage());
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}
		}
	};

	private Emitter.Listener onMeetingReviews = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];
				Log.d(tag, data.toString());
				MeetingReviewsResponse response = gson.fromJson(
						data.toString(), MeetingReviewsResponse.class);
				if (data.getBoolean("type")) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(
							EventParams.EVENT_MEETING_GET_REVIEWS, response);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}

		}
	};

	private Emitter.Listener onAddReview = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Gson gson = new Gson();
			try {

				JSONObject data = (JSONObject) args[0];
				Log.d(tag, data.toString());
				// AddUserResponse response = gson.fromJson(data.toString(),
				// AddUserResponse.class);
				if (data.getBoolean("type")) {
					// userDatasource.update(response.getUser());
					//
					onSocketResponseSuccess(
							EventParams.EVENT_MEETING_ADD_REVIEW, data);
					// App.getInstance().connectNodeJS();
				} else {
					onSocketResponseFailure(data.getString("message"));
				}
			} catch (Exception e) {
				onSocketResponseFailure(getString(R.string.server_response_error));
			}

		}
	};
	private Emitter.Listener onConnectError = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			Log.d(tag, "disconnected" + args.toString());
		}
	};
	private List<Conversation> conversations;
	private OnSocketResponseListener mOnSocketResponseListener;

	public void toggleForegroundService() {
		if (getPreferences().getBoolean("keep_foreground_service", false)) {
			// startForeground(NotificationService.FOREGROUND_NOTIFICATION_ID,
			// this.mNotificationService.createForegroundNotification());
		} else {
			stopForeground(true);
		}
	}

	protected void onSocketResponseFailure(final String message) {
		if (mOnSocketResponseListener != null) {
			ui.post(new Runnable() {
				@Override
				public void run() {
					mOnSocketResponseListener.onSocketResponseFailure(message);
				}
			});
		}
	}

	protected void onSocketResponseSuccess(final String event, final Object obj) {
		if (mOnSocketResponseListener != null) {
			ui.post(new Runnable() {
				@Override
				public void run() {
					mOnSocketResponseListener.onSocketResponseSuccess(event,
							obj);
				}
			});
		}
	}

	@Override
	public void onTaskRemoved(final Intent rootIntent) {

		if (!getPreferences().getBoolean("keep_foreground_service", false)) {
			this.logoutAndSave();
		} else {

		}

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

	private void logoutAndSave() {
		// logout
		// clear prefs
		// delete db
		// triger logout event

		// Context context = getApplicationContext();
		// AlarmManager alarmManager = (AlarmManager) context
		// .getSystemService(Context.ALARM_SERVICE);
		// Intent intent = new Intent(context, EventReceiver.class);
		// alarmManager.cancel(PendingIntent.getBroadcast(context, 0, intent,
		// 0));
		Log.d(tag, "good bye");
		stopSelf();
	}

	protected void scheduleWakeUpCall(int seconds, int requestCode) {
		final long timeToWake = SystemClock.elapsedRealtime()
				+ (seconds < 0 ? 1 : seconds + 1) * 1000;

		Context context = getApplicationContext();
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, EventReceiver.class);
		intent.setAction("ping");
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context,
				requestCode, intent, 0);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeToWake,
				alarmIntent);
	}

	/**
	 * @param event
	 * @param params
	 * @param listener
	 * @return
	 */
	public boolean emit(final String event, JSONObject params, Listener listener) {

		// cache
		/*
		 * if (cacheResponses.get(event) != null) { ui.postAtTime(new Runnable()
		 * {
		 * 
		 * @Override public void run() { onSocketResponseSuccess(event,
		 * cacheResponses.get(event)); } }, 50); return false; }
		 */

		mSocket.emit(event, params, listener);
		if (event.equals(EventParams.EVENT_USER_LOGOUT)) {
			stopSelf();
		}
		return true;
	}

	/**
	 * @param params
	 *            username, email, password
	 */
	public boolean registerAccount(final JSONObject params) {
		return emit(EventParams.EVENT_USER_SIGNUP, params, onRegister);
	}

	/**
	 * @param params
	 *            Facebook id
	 */
	public boolean loginWithFacebook(JSONObject params) {
		return emit(EventParams.EVENT_USER_FACEBOOK, params, onFacebookLogin);
	}

	public boolean login(final JSONObject params) {
		return emit(EventParams.METHOD_USER_LOGIN, params, onLogin);
	}

	public boolean forgotPassword(final JSONObject params) {
		return emit(EventParams.EVENT_USER_FORGOT_PASSWORD, params,
				onForgotPassword);
	}

	public boolean updateAccount(final JSONObject params) {
		return emit(EventParams.EVENT_USER_UPDATE, params, onUpdate);
	}

	/**
	 * get Meeting list
	 */
	public boolean getMeeting(final JSONObject params) {
		return emit(EventParams.EVENT_MEETING_GET_ALL, params, onMeeting);
	}
	
	public boolean getAllRehab(final JSONObject params) {
		return emit(EventParams.EVENT_GET_ALL_REHABS, params, onAllRehab);
	}

	public boolean favMeetingsList(final JSONObject params) {
		return emit(EventParams.EVENT_FAVOURITE_LIST, params, onFavList);
	}

	public boolean addUserFavourite(final JSONObject params) {
		return emit(EventParams.EVENT_ADD_USER_FAVOURITE, params,
				onAddUserFavourite);
	}
	
	
	public boolean addMeeting(final JSONObject params) {
		return emit(EventParams.EVENT_ADD_MEETING, params,
				onAddMeeting);
	}

	public boolean addRehab(final JSONObject params) {
		return emit(EventParams.EVENT_ADD_REHAB, params,
				onAddRehab);
	}
	
	public boolean addInsurance(final JSONObject params) {
		return emit(EventParams.EVENT_ADD_INSURANCE, params,
				onAddInsurance);
	}

	public boolean getUserReviews(final JSONObject params) {
		return emit(EventParams.EVENT_GET_USER_REVIEWS, params,
				onGetUserReviews);
	}

	public boolean sendReceiveChatMessage(final JSONObject params) {
		return emit(EventParams.METHOD_CHAT_SEND_RECEIVE, params,
				onSendReceiveChatMessage);
	}

	public boolean reportMeeting(final JSONObject params) {
		return emit(EventParams.METHOD_REPORT_MEETING, params, onReportMeeting);
	}

	public boolean deleteChatMessages(final JSONObject params) {
		return emit(EventParams.METHOD_DELETE_CHAT_MESSAGE, params,
				onDeleteChatMessage);
	}

	public boolean homeGroupUser(final JSONObject params) {
		return emit(EventParams.METHOD_HOME_GROUP_USER, params, onHomeGroupUser);
	}

	public boolean rsvpMeeting(final JSONObject params) {
		return emit(EventParams.METHOD_RSVP, params, onRsvp);
	}

	public boolean deleteConversation(final JSONObject params) {
		return emit(EventParams.METHOD_DELETE_CONVERSATION, params,
				onDeleteConversation);
	}

	public boolean favourteUser(final JSONObject params) {
		return emit(EventParams.METHOD_FAVOURITE_USER, params, onFavouriteUser);
	}

	public boolean blockUser(final JSONObject params) {
		return emit(EventParams.METHOD_BLOCK_USER, params, onBlockUser);
	}

	public boolean syncPhone(final JSONObject params) {
		return emit(EventParams.METHOD_SYNC_PHONE, params, onSyncUserNotify);
	}

	public boolean reportUser(final JSONObject params) {
		return emit(EventParams.METHOD_REPORT_USER, params, onReportUser);
	}

	public boolean getChatFriends(final JSONObject params) {
		return emit(EventParams.METHOD_GET_CHAT_FRIENDS, params,
				onGetChatFriends);
	}

	public boolean getChatPagination(final JSONObject params) {
		return emit(EventParams.METHOD_CHAT_PAGINATION, params,
				onChatPagination);
	}

	public boolean deleteUserReviews(final JSONObject params) {
		return emit(EventParams.EVENT_DELETE_USER_REVIEW, params,
				onDeleteUserReviews);
	}

	public boolean checkUserInfo(final JSONObject params) {
		return emit(EventParams.METHOD_CHECK_INFO, params, onCheckUserInfo);
	}

	public boolean getAllFriends(final JSONObject params) {
		return emit(EventParams.METHOD_GET_ALL_FRIENDS, params, onGetAllFriends);
	}

	public boolean getAllRSVPUsers(final JSONObject params) {
		return emit(EventParams.METHOD_RSVP_USERS, params, onGetAllRsvpFriends);
	}

	public boolean checkInMeeting(final JSONObject params) {
		return emit(EventParams.METHOD_CHECK_IN_MEETING, params,
				onCheckInMeeting);
	}

	/**
	 * @param id
	 */
	public boolean getMeetingReviews(final JSONObject params) {
		return emit(EventParams.EVENT_MEETING_GET_REVIEWS, params,
				onMeetingReviews);
	}

	public boolean addReview(JSONObject params) {
		return emit(EventParams.EVENT_MEETING_ADD_REVIEW, params, onAddReview);
	}

	public void sendChatState(int conversation) {
		if (sendChatStates()) {
			// MessagePacket packet =
			// mMessageGenerator.generateChatState(conversation);
			// sendMessagePacket(conversation.getAccount(), packet);
		}
	}

	public List<Conversation> getConversations() {
		return this.conversations;
	}

	public UserAccount getUserAccount() {
		return this.mUserAccount;
	}

	public void saveAccountInDb(final UserAccount account) {
		// databaseBackend.createAccount(account);
		// this.reconnectAccountInBackground(account);
		updateAccountUi();
	}

	public void setOnConversationListChangedListener(
			OnConversationUpdate listener) {
		synchronized (this) {
			if (checkListeners()) {
				switchToForeground();
			}
			this.mOnConversationUpdate = listener;
		}
	}

	public void removeOnConversationListChangedListener() {
	}

	public void setOnAccountListChangedListener(OnAccountUpdate listener) {
		synchronized (this) {
			if (checkListeners()) {
				switchToForeground();
			}
			this.mOnAccountUpdate = listener;
			if (this.accountChangedListenerCount < 2) {
				this.accountChangedListenerCount++;
			}
		}
	}

	public void removeOnAccountListChangedListener() {
		synchronized (this) {
			this.accountChangedListenerCount--;
			if (this.accountChangedListenerCount <= 0) {
				this.mOnAccountUpdate = null;
				this.accountChangedListenerCount = 0;
				if (checkListeners()) {
					switchToBackground();
				}
			}
		}
	}

	private boolean checkListeners() {
		return (this.mOnAccountUpdate == null
				&& this.mOnConversationUpdate == null && this.mOnSocketResponseListener == null);
	}

	private void switchToForeground() {
	}

	private void switchToBackground() {
	}

	public void disconnect(UserAccount account, boolean force) {

	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void updateConversation(Conversation conversation) {
		// this.databaseBackend.updateConversation(conversation);
	}

	public void reconnectAccountInBackground() {
		// reconnect nodejs
	}

	public SharedPreferences getPreferences() {
		return getSharedPreferences(Consts.APP_PREFS_NAME, Context.MODE_PRIVATE);
	}

	public boolean forceEncryption() {
		return getPreferences().getBoolean("force_encryption", false);
	}

	public boolean confirmMessages() {
		return getPreferences().getBoolean("confirm_messages", true);
	}

	public boolean sendChatStates() {
		return getPreferences().getBoolean("chat_states", false);
	}

	public boolean indicateReceived() {
		return getPreferences().getBoolean("indicate_received", false);
	}

	public int unreadCount() {
		// int count = 0;
		// for(Conversation conversation : getConversations()) {
		// count += conversation.unreadCount();
		// }
		return 0;
	}

	public void updateConversationUi() {
		if (mOnConversationUpdate != null) {
			mOnConversationUpdate.onConversationUpdate();
		}
	}

	public void updateAccountUi() {
		if (mOnAccountUpdate != null) {

			this.mUserAccount = userDatasource.findUser(AccountUtils
					.getUserId(this));
			mOnAccountUpdate.onAccountUpdate(mUserAccount);
		}
	}

	public PowerManager getPowerManager() {
		return this.pm;
	}

	public LruCache<String, Bitmap> getBitmapCache() {
		return this.mBitmapCache;
	}

	public void sendPresence(final UserAccount account) {
		// sendPresencePacket(account,
		// mPresenceGenerator.sendPresence(account));
	}

	public void sendOfflinePresence(final UserAccount account) {
		// sendPresencePacket(account,
		// mPresenceGenerator.sendOfflinePresence(account));
	}

	// public List<Contact> findContacts(int jid) {
	// ArrayList<Contact> contacts = new ArrayList<>();
	// for (UserAccount account : getAccounts()) {
	// if (!account.isOptionSet(UserAccount.OPTION_DISABLED)) {
	// Contact contact = account.getRoster().getContactFromRoster(jid);
	// if (contact != null) {
	// contacts.add(contact);
	// }
	// }
	// }
	// return contacts;
	// }

	// public NotificationService getNotificationService() {
	// return this.mNotificationService;
	// }
	//
	// public HttpConnectionManager getHttpConnectionManager() {
	// return this.mHttpConnectionManager;
	// }

	public interface OnMoreMessagesLoaded {
		public void onMoreMessagesLoaded(int count, Conversation conversation);

		public void informUser(int r);
	}

	// tmpcode
	public class Conversation {
	}

	public interface OnConversationUpdate {
		public void onConversationUpdate();
	}

	public interface OnAccountUpdate {
		public void onAccountUpdate(UserAccount mUserAccount);
	}

	public class SocketServiceConnectionBinder extends Binder {
		public SocketService getService() {
			return SocketService.this;
		}
	}

	public interface OnBindListener {
		public void onBind(UserAccount account);
	}

	public interface OnStatusChanged {
		public void onStatusChanged(UserAccount account);
	}

	public void setOnSocketResponseListener(OnSocketResponseListener listener) {
		synchronized (this) {
			if (checkListeners()) {
				switchToForeground();
			}
			this.mOnSocketResponseListener = listener;
			if (this.socketResponseListenerCount < 2) {
				this.socketResponseListenerCount++;
			}
		}
	}

	public void removeOnSocketResponseListener() {
		synchronized (this) {
			this.socketResponseListenerCount--;
			if (this.socketResponseListenerCount <= 0) {
				this.mOnSocketResponseListener = null;
				this.socketResponseListenerCount = 0;
				if (checkListeners()) {
					switchToBackground();
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(tag, "Service:onDestroy");
		// App.alert("Service:onDestroy");

		// disconnectSocket();
		stopForeground(true);
	}

	public void disconnectSocket() {
		try {
			mSocket.disconnect();
			// mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
			// mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
			// mSocket.off(EventParams.EVENT_USER_SIGNUP, onRegister);
			// mSocket.off(EventParams.EVENT_USER_FACEBOOK, onFacebookLogin);
			// mSocket.off(EventParams.METHOD_USER_LOGIN, onLogin);
			// mSocket.off(EventParams.EVENT_USER_FORGOT_PASSWORD,
			// onForgotPassword);
			// mSocket.off(EventParams.EVENT_USER_UPDATE, onUpdate);
			// mSocket.off(EventParams.EVENT_MEETING_GET_ALL, onMeeting);
			mSocket.off();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void toastOnUiThread(final String message) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(SocketService.this, message, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

}
