package com.citrusbits.meehab.ui.users;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.adapters.ChatAdapter;
import com.citrusbits.meehab.adapters.ChatAdapter.ChatCheckedChangeListener;
import com.citrusbits.meehab.app.MeehabApp;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.db.ChatDataSource;
import com.citrusbits.meehab.db.DatabaseHandler;
import com.citrusbits.meehab.ui.dialog.BlockFrindConfirmationDialog;
import com.citrusbits.meehab.ui.dialog.BlockUserDialog;
import com.citrusbits.meehab.ui.dialog.BlockFrindConfirmationDialog.BlockFrindConfirmationDialogClickListener;
import com.citrusbits.meehab.ui.dialog.BlockUserDialog.BlockUserDialogClickListener;
import com.citrusbits.meehab.ui.fragments.MessagesFragment;
import com.citrusbits.meehab.model.ChatModel;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.ui.HomeActivity;
import com.citrusbits.meehab.ui.SocketActivity;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.TimestampUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.gson.Gson;

public class ChatActivity extends SocketActivity implements OnClickListener,
		OnItemClickListener, OnSocketResponseListener {

	public static final String TAG = ChatActivity.class.getSimpleName();

	public static final String KEY_FRIEND_ID = "friend_id";
	public static final String KEY_FRIEND_NAME = "friend_name";
	public static final String KEY_PUSH_CHAT = "meehab.chat";
	
	public static final long TIME_REFRESH_RATE = 10 * 1000;

	// public static final String EXTRA_CHAT = "chat";

	ImageView ivBack;
	TextView tvUserName;
	ImageView ivActionProfile;
	ImageButton ibEdit;
	ListView lvChat;
	EditText etMessage;
	ImageButton ibSend;

	List<ChatModel> chatMessages = new ArrayList<ChatModel>();

	private Dialog pd;

	private int appUserId;

	private ChatAdapter adapter;

	private ChatDataSource chatDatasource;

	int paginationIndex = 1;

	SwipeRefreshLayout mSwipeRefreshLayout;

	AppPrefs prefs;

	private long timeZoneOffset = 0;

	Handler refreshHandler;
	public static int friendId;
	private ImageButton ibDelete;
	private LinearLayout llMessage;

	private boolean fromPushChat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		ibDelete = (ImageButton) findViewById(R.id.ibDelete);
		llMessage = (LinearLayout) findViewById(R.id.llMessage);
		refreshHandler = new Handler();
		prefs = AppPrefs.getAppPrefs(ChatActivity.this);
		
		fromPushChat = getIntent().hasExtra(KEY_PUSH_CHAT);
		
		friendId = getIntent().getIntExtra(KEY_FRIEND_ID, -1);
		String friendName = getIntent().getStringExtra(KEY_FRIEND_NAME);
		prefs.saveIntegerPrefs(AppPrefs.KEY_CHAT_FRIEND_ID, friendId);
		appUserId = AppPrefs.getAppPrefs(this).getIntegerPrefs(
				AppPrefs.KEY_USER_ID, AppPrefs.DEFAULT.USER_ID);
		ivBack = (ImageView) findViewById(R.id.ivBack);
		tvUserName = (TextView) findViewById(R.id.tvUserName);
		ivActionProfile = (ImageView) findViewById(R.id.ivActionOnProfile);
		ibEdit = (ImageButton) findViewById(R.id.ibEdit);
		lvChat = (ListView) findViewById(R.id.lvChat);
		etMessage = (EditText) findViewById(R.id.etMessage);
		ibSend = (ImageButton) findViewById(R.id.ibSend);
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

		mSwipeRefreshLayout.setColorSchemeResources(R.color.orange,
				R.color.green, R.color.blue);

		tvUserName.setText(friendName);

		ivBack.setOnClickListener(this);
		ibSend.setOnClickListener(this);
		ivActionProfile.setOnClickListener(this);
		ibEdit.setOnClickListener(this);
		ibDelete.setOnClickListener(this);

		chatDatasource = new ChatDataSource(this);
		chatDatasource.open();

		adapter = new ChatAdapter(this, R.layout.chat_list_item, chatMessages)
				.setChatCheckedChangeListener(chatCheckChangeListener);

		lvChat.setAdapter(adapter);
		lvChat.setOnItemClickListener(this);
		scrollChatListToBottom();

		// chatPagination(paginationIndex);

		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				chatPagination(paginationIndex);
			}
		});

		prefs.saveBooleanPrefs(AppPrefs.KEY_CHAT_ACTIVITY_OPEN, true);

		timeZoneOffset = TimestampUtils.getTimeZoneOffset();

	}

	ChatCheckedChangeListener chatCheckChangeListener = new ChatCheckedChangeListener() {

		@Override
		public void onCheckedChange() {
			// TODO Auto-generated method stub
			int count = getCheckedCount();
			if (count == 0 || count == 1) {
				notifyDeleteButton(count);
			}
		}
	};

	public void notifyDeleteButton(int count) {
		int resourceId = count == 0 ? R.drawable.delete_btn_gray
				: R.drawable.delete_btn;
		ibDelete.setImageResource(resourceId);
	}

	private int getCheckedCount() {
		int checkCount = 0;
		for (ChatModel chat : chatMessages) {
			if (chat.isChecked()) {
				checkCount++;
			}
		}
		return checkCount;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.ivBack:
			onBackPressed();
			break;
		case R.id.ibSend:
			String message = etMessage.getText().toString().trim();

			if (message.trim().isEmpty()) {
				DeviceUtils.showSoftKeyboard(ChatActivity.this);
				etMessage.requestFocus();
				return;
			}
			sendChatMessage(message);
//			DeviceUtils.hideSoftKeyboard(ChatActivity.this);

			break;
		case R.id.ivActionOnProfile:
			// performAction();

			new BlockUserDialog(this).setBlockUserDialogListener(
					new BlockUserDialogClickListener() {

						@Override
						public void onReportUser(BlockUserDialog dialog) {
							dialog.dismiss();
							Intent i = new Intent(ChatActivity.this,
									ReportFriendActivity.class);
							i.putExtra(ReportFriendActivity.KEY_FRIEND_ID,
									friendId);
							startActivity(i);
						}

						@Override
						public void onCancelClick(BlockUserDialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}

						@Override
						public void onBlockUser(BlockUserDialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							
							new BlockFrindConfirmationDialog(ChatActivity.this).setBlockFrindConfirmationDialogClickListener(new BlockFrindConfirmationDialogClickListener() {
								
								@Override
								public void onYesClick(BlockFrindConfirmationDialog dialog) {
									// TODO Auto-generated method stub
									dialog.dismiss();
									blockUser();
								}
								
								@Override
								public void onNoClick(BlockFrindConfirmationDialog dialog) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							},  BlockFrindConfirmationDialog.STATUS_SINGLE_BLOCK).show();
						
						}
					}).show();

			break;
		case R.id.ibEdit:

			boolean edit = adapter.isEdit();
			int resource = edit ? R.drawable.edit_btn
					: R.drawable.cancel_btn_small;
			ibEdit.setImageResource(resource);
			if (!edit) {
				ibDelete.setVisibility(View.VISIBLE);
				llMessage.setVisibility(View.GONE);
				adapter.setEdit(true);
				adapter.notifyDataSetChanged();
			} else {
				// deleteChatMessages();
				deselectAllMessages();
				checkDeleteButton();
				ibDelete.setVisibility(View.GONE);
				llMessage.setVisibility(View.VISIBLE);
				deselectAllMessages();
				adapter.setEdit(false);
				adapter.notifyDataSetChanged();
			}
			// Toast.makeText(this, "Edit button is called! "+edit,
			// Toast.LENGTH_SHORT).show();

			break;
		case R.id.ibDelete:
			deleteChatMessages();
			break;
		}

	}

	public void deselectAllMessages() {
		for (ChatModel chat : chatMessages) {
			if (chat.isChecked()) {
				chat.setChecked(false);
			}

		}

	}

	public void deleteChatMessages() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			MeehabApp.toast(getString(R.string.no_internet_connection));
			return;
		}

		if (socketService != null) {
			pd = UtilityClass.getProgressDialog(this);
			JSONObject object = new JSONObject();

			try {

				object.put("user_id", AccountUtils.getUserId(this));

				JSONArray deleteArray = new JSONArray();

				for (ChatModel chat : chatMessages) {
					if (chat.isChecked()) {
						JSONObject obj = new JSONObject();
						obj.put("message_id", chat.getChatId());
						obj.put("message_from", chat.isSend() ? 1 : 0);
						deleteArray.put(obj);
					}

				}

				object.put("delete_message", deleteArray);
				Log.e("delete json send ", object.toString());

				if (deleteArray.length() > 0) {
					pd.show();
					socketService.deleteChatMessages(object);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void chatPagination(int index) {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			MeehabApp.toast(getString(R.string.no_internet_connection));
			return;
		}

		if (socketService != null) {

			JSONObject object = new JSONObject();

			try {

				object.put("index", index);
				object.put("user_id", AccountUtils.getUserId(this));
				object.put("friend_id", friendId);

				Log.e("json send ", object.toString());
				socketService.getChatPagination(object);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void sendChatMessage(String message) {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			MeehabApp.toast(getString(R.string.no_internet_connection));
			return;
		}

		if (socketService != null) {
			// pd = UtilityClass.getProgressDialog(this);

			JSONObject object = new JSONObject();

			try {
				// pd.show();
				object.put("to_send", friendId);
				object.put("message", message);

				Log.e("json send ", object.toString());
				socketService.sendReceiveChatMessage(object);
				etMessage.setText("");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void blockUser() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			MeehabApp.toast(getString(R.string.no_internet_connection));
			return;
		}

		if (socketService != null) {
			pd = UtilityClass.getProgressDialog(this);
			pd.show();
			JSONObject object = new JSONObject();

			try {
				object.put("user_id", AccountUtils.getUserId(this));
				object.put("friend_ids", friendId);
				object.put("block", 1);
				Log.e("json send ", object.toString());
				socketService.blockUser(object);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (adapter.isEdit()) {
			ChatModel message = chatMessages.get(position);
			message.setChecked(!message.isChecked());
			adapter.notifyDataSetChanged();
			int count = getCheckedCount();
			if (count == 0 || count == 1) {
				notifyDeleteButton(count);
			}
		}

	}

	@Override
	public void onBackendConnected() {
		Log.e("Chat OnBackend called", "Yes");
		super.onBackendConnected();
		chatPagination(paginationIndex);

	}

	long prevTime;


	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		if (pd != null) {
			pd.dismiss();
		}

		mSwipeRefreshLayout.setRefreshing(false);
		if (event.equals(EventParams.METHOD_CHAT_SEND_RECEIVE)) {

			JSONObject data = ((JSONObject) obj);
			Gson gson = new Gson();

			long currentTime = System.currentTimeMillis();
			Log.e("Time Diff", String.valueOf((currentTime - prevTime)));

			prevTime = currentTime;

			try {
				JSONObject sendReceiveObject = data
						.getJSONObject("sendReciveMessages");
				String message;
				message = sendReceiveObject.getString("message");
				String timeStamp = sendReceiveObject
						.getString("datetime_added");
				int fromSend = sendReceiveObject.getInt("fromID");
				int toSend = sendReceiveObject.getInt("toID");
				int id = sendReceiveObject.getInt("message_id");

				ChatModel chat = new ChatModel();
				chat.setMessage(message);
				chat.setTimeStamp(timeStamp);
				chat.setFromSend(fromSend);
				chat.setToSend(toSend);
				chat.setChatId(id);
				chat.setDisplayDateTime(chat.convertToDisplayFormat(
						chat.getTimeStamp(), timeZoneOffset));
				Log.e("Chat data is ", data.toString());
				chat.setSend(chat.getFromSend() == appUserId);

				chatDatasource.addChat(chat);

				chatMessages.add(chat);

				Log.e(TAG, data.toString());

				adapter.notifyDataSetChanged();
				scrollChatListToBottom();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (event.equals(EventParams.METHOD_BLOCK_USER)) {
			JSONObject data = ((JSONObject) obj);

			try {
				String message = data.getString("message");

				Toast.makeText(ChatActivity.this, "User has been blocked!",
						Toast.LENGTH_SHORT).show();
				etMessage.setEnabled(false);
				setResult(UserProfileActivity.RESULT_CODE_BLOCKED, new Intent());
				ChatActivity.this.finish();
				overridePendingTransition(R.anim.activity_back_in,
						R.anim.activity_back_out);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		else if (event.equals(EventParams.METHOD_CHAT_PAGINATION)) {
			mSwipeRefreshLayout.setRefreshing(false);
			JSONObject data = ((JSONObject) obj);
			DatabaseHandler.getInstance(ChatActivity.this).updateMessgeCount(
					friendId, 0);

			List<ChatModel> paginationMessages = new ArrayList<ChatModel>();

			try {
				JSONArray chatPagination = data.getJSONArray("chatPagination");

				refreshHandler.removeCallbacks(timeRefreshRunnable);

				for (int i = 0; i < chatPagination.length(); i++) {
					JSONObject sendReceiveObject = chatPagination
							.getJSONObject(i);
					String message;
					message = sendReceiveObject.getString("message");
					String timeStamp = sendReceiveObject
							.getString("datetime_added");
					int fromSend = sendReceiveObject.getInt("fromID");
					int toSend = sendReceiveObject.getInt("toID");
					int id = sendReceiveObject.getInt("id");

					ChatModel chat = new ChatModel();
					chat.setMessage(message);
					chat.setTimeStamp(timeStamp);
					chat.setFromSend(fromSend);
					chat.setToSend(toSend);
					chat.setChatId(id);
					chat.setDisplayDateTime(chat.convertToDisplayFormat(
							chat.getTimeStamp(), timeZoneOffset));
					Log.e("Chat data is ", data.toString());
					chat.setSend(chat.getFromSend() == appUserId);

					paginationMessages.add(chat);

				}

				Collections.reverse(paginationMessages);

				chatMessages.addAll(paginationMessages);

				refreshHandler.postDelayed(timeRefreshRunnable,
						TIME_REFRESH_RATE);

				Log.e(TAG, data.toString());

				

				adapter.notifyDataSetChanged();
				
				if(paginationIndex<20){
					scrollChatListToBottom();
				}
				
				
				if (chatPagination.length() > 0) {
					paginationIndex = paginationIndex + 20;
				}

				ChatActivity.this.sendBroadcast(new Intent(
						HomeActivity.ACTION_MESSAGE_COUNT_UPDATE));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (event.equals(EventParams.METHOD_DELETE_CHAT_MESSAGE)) {
			JSONObject data = ((JSONObject) obj);
			Log.e("Delete Message Response", data.toString());
			for (int i = 0; i < chatMessages.size(); i++) {
				ChatModel chat = chatMessages.get(i);
				if (chat.isChecked()) {
					chatMessages.remove(chat);
					chatDatasource.removeChat(chat.getChatId());
					i--;
				}
			}
            checkDeleteButton();
			scrollChatListToBottom();
		} else if (event.equals(EventParams.METHOD_BLOCK_USER_NOTIFY)) {
			JSONObject data = ((JSONObject) obj);
			try {
				int blocked = data.getInt("blocked");
				String message = data.getString("message");
				etMessage.setEnabled(blocked == 0 ? true : false);
				Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT)
						.show();
				
				setResult(UserProfileActivity.RESULT_CODE_BLOCKED, new Intent());
				ChatActivity.this.finish();
				overridePendingTransition(R.anim.activity_back_in,
						R.anim.activity_back_out);


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void checkDeleteButton() {
		int count = getCheckedCount();
		if (count == 0 || count == 1) {
			notifyDeleteButton(count);
		}
	}

	public void refreshTime() {
		for (int i = 0; i < chatMessages.size(); i++) {
			ChatModel chat = chatMessages.get(i);
			chat.setDisplayDateTime(chat.convertToDisplayFormat(
					chat.getTimeStamp(), timeZoneOffset));
		}
	}

	@Override
	public void onSocketResponseFailure(String onEvent,String message) {
		// TODO Auto-generated method stub
		if (pd != null) {
			pd.dismiss();
		}

		if (message.equals("This User Blocked You")) {
			etMessage.setEnabled(false);
		}
		Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();

		mSwipeRefreshLayout.setRefreshing(false);
	}

	private void scrollChatListToBottom() {
		lvChat.post(new Runnable() {
			@Override
			public void run() {
				// Select the last row so it will scroll into view...
				lvChat.setSelection(adapter.getCount() - 1);
			}
		});
	}

	@Override
	public void onBackPressed() {
		setResult(MessagesFragment.RESULT_CODE_CHANGES_HAPPEN);
		
		if(fromPushChat){
			Intent intent = new Intent(this,
					HomeActivity.class);
			
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(KEY_PUSH_CHAT, true);
			startActivity(intent);
			finish();
		}else{
			super.onBackPressed();
			overridePendingTransition(R.anim.activity_back_in,
					R.anim.activity_back_out);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		prefs.saveBooleanPrefs(AppPrefs.KEY_CHAT_ACTIVITY_OPEN, true);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		prefs.saveBooleanPrefs(AppPrefs.KEY_CHAT_ACTIVITY_OPEN, false);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		chatDatasource.close();
		friendId = 0;
		prefs.saveIntegerPrefs(AppPrefs.KEY_CHAT_FRIEND_ID, -1);
		refreshHandler.removeCallbacks(timeRefreshRunnable);
	}

	Runnable timeRefreshRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			refreshTime();
			adapter.notifyDataSetChanged();
			refreshHandler.postDelayed(this, TIME_REFRESH_RATE);
		}
	};
}
