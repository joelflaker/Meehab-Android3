package com.citrusbits.meehab.ui.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.citrusbits.meehab.app.MeehabApp;
import com.citrusbits.meehab.ui.users.ChatActivity;
import com.citrusbits.meehab.ui.HomeActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.ui.users.UserProfileActivity;
import com.citrusbits.meehab.adapters.MessagesAdapter;
import com.citrusbits.meehab.adapters.MessagesAdapter.MessageCheckedChangeListener;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.ui.dialog.BlockFrindConfirmationDialog;
import com.citrusbits.meehab.ui.dialog.BlockFrindConfirmationDialog.BlockFrindConfirmationDialogClickListener;
import com.citrusbits.meehab.model.MessageModel;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.prefrences.AppPrefs;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.NetworkUtil;
import com.citrusbits.meehab.utils.NetworkUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.gson.Gson;

public class MessagesFragment extends Fragment implements
		OnSocketResponseListener,OnBackendConnectListener, ListView.OnItemClickListener,
		View.OnClickListener {

	public static final String TAG = MessagesFragment.class.getSimpleName();

	public static final int REQUEST_CODE_CHAT = 5;
	public static final int RESULT_CODE_CHANGES_HAPPEN = 5;

	public static final String ACTION_REFRESH_CONVERSATION = "com.citrusbits.meehab.refresh.conversation";
    private static final int USER_DETAIL_REQUEST = 32;

    private ListView list;
	private HomeActivity homeActivity;

	private ImageButton ibEdit;
	private Dialog pd;
	private MessagesAdapter adapter;
	private List<MessageModel> messages = new ArrayList<>();

	private AppPrefs prefs;
	private ImageButton ibBlockUser;
	private ImageButton ibDelete;

	private RelativeLayout bottomContainer;

	private int mAccountPosition = -1;
	
	//extra from message push
	
	private long timezone=0;
	private boolean isThisUserFavorite;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = AppPrefs.getAppPrefs(getActivity());
		prefs.saveBooleanPrefs(AppPrefs.KEY_CONVERSATION_FRAG_OPEN, true);
		timezone = getTimeZoneOffset();
	}
	
	public long getTimeZoneOffset() {
		Calendar mCalendar = new GregorianCalendar();
		TimeZone mTimeZone = mCalendar.getTimeZone();
		int mGMTOffset = mTimeZone.getRawOffset();
		long hours = TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
		System.out.printf("GMT offset is %s hours", hours);
		return hours;
	}

	public MessagesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_conversation, container,
				false);

		this.homeActivity = (HomeActivity) getActivity();
		pd = UtilityClass.getProgressDialog(homeActivity);

		v.findViewById(R.id.ivMenu).setOnClickListener(this);
		ibEdit = (ImageButton) v.findViewById(R.id.ibEdit);

		ibBlockUser = (ImageButton) v.findViewById(R.id.ibBlockUser);
		ibDelete = (ImageButton) v.findViewById(R.id.ibDelete);
		bottomContainer = (RelativeLayout) v.findViewById(R.id.bottomContainer);

		ibEdit.setOnClickListener(this);
		list = (ListView) v.findViewById(R.id.lvConversation);

		adapter = new MessagesAdapter(getActivity(),
				R.layout.list_item_messages, messages)
				.setMessageCheckedChangeListener(messageCheckChangeListener);
		adapter.setOnViewClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				MessageModel messageModel = (MessageModel) v.getTag();

				if (adapter.isEdit()) {
					messageModel.setChecked(!messageModel.isChecked());
					adapter.notifyDataSetChanged();
					messageCheckChangeListener.onMessageCheckedChange();
					return;
				}

				if(NetworkUtil.isConnected(getContext())){
					pd.show();
					isThisUserFavorite = messageModel.getFavourite() == 1;
					homeActivity.socketService.getUserById(messageModel.getId());
				}else {
					MeehabApp.toast(getResources().getString(R.string.no_internet_connection));
				}
			}
		});
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		ibBlockUser.setOnClickListener(this);
		ibDelete.setOnClickListener(this);
		
		getChatFriends(true);

		getActivity().registerReceiver(refreshReceiver,
				new IntentFilter(ACTION_REFRESH_CONVERSATION));

		return v;
	}

	MessageCheckedChangeListener messageCheckChangeListener = new MessageCheckedChangeListener() {

		@Override
		public void onMessageCheckedChange() {
			int count = getCheckedCount();
			if (count == 0 || count == 1) {
				notifyButtons(count);
			}
		}
	};

	private boolean ifMessagesUpdated;

	private boolean isWatingMessages;

	public void notifyButtons(int count) {

		int blockResId = count == 0 ? R.drawable.block_user_gray
				: R.drawable.block_user_yellow;
		int deleteResId = count == 0 ? R.drawable.delete_btn_gray
				: R.drawable.delete_btn;

		ibBlockUser.setImageResource(blockResId);
		ibDelete.setImageResource(deleteResId);
	}

	public int getCheckedCount() {
		int count = 0;
		for (MessageModel message : messages) {
			if (message.isChecked()) {
				count++;
			}
		}

		return count;
	}

	public void deselectAllMessages() {
		for (MessageModel message : messages) {
			if (message.isChecked()) {
				message.setChecked(false);
			}
		}
	}

	public void getChatFriends(boolean showProgress) {
		
//		if(ifMessagesUpdated || isWatingMessages) return;
		
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			MeehabApp.toast(getString(R.string.no_internet_connection));
			return;
		}
		if (homeActivity.socketService != null) {
			if (showProgress) {
				pd.show();
			}
			JSONObject object = new JSONObject();

			try {
				object.put("user_id", AccountUtils.getUserId(getActivity()));

				Log.e("json send ", object.toString());
				
				isWatingMessages = true;
				homeActivity.socketService.getChatFriends(object);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivMenu:
			if (homeActivity.isDrawerOpen()) {
				homeActivity.changeDrawerVisibility(false);
			} else {
				homeActivity.changeDrawerVisibility(true);
			}
			break;
		case R.id.ibDelete:
			if (getCheckedCount() > 0) {
				if (!NetworkUtils.isNetworkAvailable(getActivity())) {
					MeehabApp.toast(getActivity().getString(
							R.string.no_internet_connection));
					return;
				}
				deleteConversation();
			}

			break;
		case R.id.ibBlockUser:
			if (getCheckedCount() > 0) {

				new BlockFrindConfirmationDialog(getActivity())
						.setBlockFrindConfirmationDialogClickListener(
								new BlockFrindConfirmationDialogClickListener() {

									@Override
									public void onYesClick(
											BlockFrindConfirmationDialog dialog) {
										dialog.dismiss();
										blockUser();
									}

									@Override
									public void onNoClick(
											BlockFrindConfirmationDialog dialog) {
										dialog.dismiss();
									}
								},
								getCheckedCount() > 1 ? BlockFrindConfirmationDialog.STATUS_MULTIPLE_BLOCK
										: BlockFrindConfirmationDialog.STATUS_SINGLE_BLOCK)
						.show();

			}
			break;
		case R.id.ibEdit:
			// edit

			boolean edit = adapter.isEdit();
			int resource = edit ? R.drawable.edit_btn
					: R.drawable.cancel_btn_small;
			ibEdit.setImageResource(resource);
			if (!edit) {
				adapter.setEdit(true);
				adapter.notifyDataSetChanged();
				bottomContainer.setVisibility(View.VISIBLE);
			} else {
				adapter.setEdit(false);
				deselectAllMessages();
				adapter.notifyDataSetChanged();
				bottomContainer.setVisibility(View.GONE);
			}
			break;

		default:
			break;
		}
	}

	public void deleteConversation() {
		if (homeActivity.socketService != null) {

			JSONObject object = new JSONObject();

			try {

				pd.show();

				JSONArray deleteArray = new JSONArray();
				object.put("user_id", AccountUtils.getUserId(getActivity()));

				for (MessageModel chat : messages) {
					if (chat.isChecked()) {
						JSONObject obj = new JSONObject();

						int friendId = chat.getFromID() == AccountUtils
								.getUserId(getActivity()) ? chat.getToID()
								: chat.getFromID();
						obj.put("friend_id", friendId);
						// obj.put("message_id", chat.getChatId());
						// obj.put("message_from", chat.isSend() ? 1 : 0);
						deleteArray.put(obj);
					}

				}

				object.put("delete_messages", deleteArray);
				Log.e("delete json send ", object.toString());

				homeActivity.socketService.deleteConversation(object);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	protected void toast(String string) {
		Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStart() {
		super.onStart();
		
	}

	@Override
	public void onBackendConnected() {
		if(messages.size() == 0){
			getChatFriends(true);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
//		if( friendPushExtra != null){
//			Intent i = new Intent(getActivity(), ChatActivity.class);
//			i.putExtras(friendPushExtra);
//			this.startActivityForResult(i, REQUEST_CODE_CHAT);
//		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		MessageModel message = messages.get(position);
		if (adapter.isEdit()) {
			message.setChecked(!message.isChecked());
			adapter.notifyDataSetChanged();
			messageCheckChangeListener.onMessageCheckedChange();
			return;
		}

		mAccountPosition = position;

		UserAccount user = new UserAccount();

		int userId = AccountUtils.getUserId(getActivity());
		int friendId = message.getFromID() == userId ? message.getToID()
				: message.getFromID();
		user.setUsername(message.getUsername());
		user.setId(friendId);
		Intent i = new Intent(getActivity(), ChatActivity.class);
		i.putExtra(ChatActivity.KEY_FRIEND_ID, user.getId());
		i.putExtra(ChatActivity.KEY_FRIEND_NAME, user.getUsername());
		this.startActivityForResult(i, REQUEST_CODE_CHAT);
		getActivity().overridePendingTransition(R.anim.activity_in,
				R.anim.activity_out);
	}

	private Date getMessageDate(String messageDateTime) {
		SimpleDateFormat simpleDateFormate = new SimpleDateFormat(
				"yyy-MM-dd HH:mm:ss", Locale.US);
		try {
			Date dd = simpleDateFormate.parse(messageDateTime);
			dd.setHours((int) (dd.getHours()+timezone));
			return dd;
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
	}

	public void blockUser() {
		if (homeActivity.socketService != null) {
			JSONObject object = new JSONObject();
			String friendsId = "";
			for (MessageModel chat : messages) {
				if (chat.isChecked()) {
					int friendId = chat.getFromID() == AccountUtils
							.getUserId(getActivity()) ? chat.getToID() : chat
							.getFromID();
					if (!friendsId.isEmpty()) {
						friendsId = friendsId + ",";
					}

					friendsId = friendsId + friendId;

				}

			}
			try {
				pd.show();
				object.put("user_id", AccountUtils.getUserId(getActivity()));
				object.put("friend_ids", friendsId);
				object.put("block", 1);
				Log.e("json send ", object.toString());
				homeActivity.socketService.blockUser(object);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode == REQUEST_CODE_CHAT
				&& resultCode == RESULT_CODE_CHANGES_HAPPEN)
                || (requestCode == USER_DETAIL_REQUEST && resultCode == Activity.RESULT_OK)) {
			messages.clear();
			getChatFriends(false);
		} else if (requestCode == REQUEST_CODE_CHAT
				&& resultCode == UserProfileActivity.RESULT_CODE_BLOCKED) {
			if (mAccountPosition != -1) {

				messages.remove(mAccountPosition);

				adapter.notifyDataSetChanged();

			}
		}
	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		if (pd != null) {
			pd.dismiss();
		}

		if(EventParams.METHOD_USER_BY_ID.equals(event)){
			pd.dismiss();
			JSONObject data = (JSONObject) obj;

			UserAccount account = new Gson().fromJson(data.optJSONObject("user").toString(),
					UserAccount.class); ;
//
			account.setFavourite(isThisUserFavorite? 1:0);
			Intent intent = new Intent(getActivity(), UserProfileActivity.class);
			intent.putExtra(UserProfileActivity.EXTRA_USER_ACCOUNT, account);

			// put friend
			startActivityForResult(intent,USER_DETAIL_REQUEST);
			getActivity().overridePendingTransition(R.anim.activity_in,
					R.anim.activity_out);
		}

		if (event.equals(EventParams.METHOD_GET_CHAT_FRIENDS)) {
			isWatingMessages = false;
			JSONObject data = ((JSONObject) obj);

			try {
				
				messages.clear();
				JSONArray friendArray = data.getJSONArray("getChatFrinds");
				Log.e("Data", data.toString());

				for (int i = 0; i < friendArray.length(); i++) {
					JSONObject jsonObj = friendArray.getJSONObject(i);
					
					MessageModel message = new MessageModel();
					message.setFromID(jsonObj.getInt("fromID"));
					message.setId(jsonObj.getInt("id"));
					message.setImage(jsonObj.getString("image"));
					message.setMessage(jsonObj.getString("message"));
					message.setToID(jsonObj.getInt("toID"));
					message.setUsername(jsonObj.getString("username"));
					message.setCheckInType(jsonObj.getString("checkin_type"));
					message.setFavourite(jsonObj.getInt("favorite_user"));
					message.setUserCheckIn(jsonObj.getInt("user_checkin"));
					message.setRsvpUser(jsonObj.getInt("rsvp_user"));
					String messageTimee = jsonObj.getString("message_time");
					
					message.setMessageTime(messageTimee);
					message.setMessageDate(getMessageDate(messageTimee));

					messages.add(message);
				}

				Collections.sort(messages, new Comparator<MessageModel>() {
					public int compare(MessageModel lhs, MessageModel rhs) {

						/*return lhs.getMessageDate().compareTo(
								rhs.getMessageDate());*/
						
						
						return rhs.getMessageDate().compareTo(
								lhs.getMessageDate());
						
					}
				});
				ifMessagesUpdated = true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			adapter.notifyDataSetChanged();
			// Toast.makeText(getActivity(), "Message size is " +
			// messages.size(),
			// Toast.LENGTH_SHORT).show();
		} else if (event.equals(EventParams.METHOD_CHECK_IN_USER)) {
			JSONObject data = ((JSONObject) obj);
			try {

				String message = data.getString("message");

				int friendId = data.getInt("friend_id");
				String checkInType = data.getString("checkin_type");

				for (int i = 0; i < messages.size(); i++) {

					if (friendId == messages.get(i).getId()) {
						messages.get(i).setCheckInType(checkInType);
					}
				}

				adapter.notifyDataSetChanged();

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		else if (event.equals(EventParams.METHOD_DELETE_CONVERSATION)) {

			JSONObject data = ((JSONObject) obj);
			Log.e(TAG, data.toString());

			try {
				String message = data.getString("message");
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT)
						.show();
				for (int i = 0; i < messages.size(); i++) {
					MessageModel msg = messages.get(i);
					if (msg.isChecked()) {
						messages.remove(i);
						i--;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// adapter.setEdit(!adapter.isEdit());
			adapter.notifyDataSetChanged();
			// Toast.makeText(getActivity(), "Message size is " +
			// messages.size(),
			// Toast.LENGTH_SHORT).show();
		} else if (event.equals(EventParams.METHOD_BLOCK_USER)) {

			JSONObject data = ((JSONObject) obj);
			Log.e(TAG, data.toString());

			try {
				String message = data.getString("message");
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT)
						.show();
				for (int i = 0; i < messages.size(); i++) {
					MessageModel msg = messages.get(i);
					if (msg.isChecked()) {
						messages.remove(i);
						i--;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// adapter.setEdit(!adapter.isEdit());
			adapter.notifyDataSetChanged();
			// Toast.makeText(getActivity(), "Message size is " +
			// messages.size(),
			// Toast.LENGTH_SHORT).show();
		} else if (event.equals(EventParams.METHOD_BLOCK_USER_NOTIFY)) {
			JSONObject data = ((JSONObject) obj);
			try {

				String message = data.getString("message");

				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT)
						.show();

				int userId = data.getInt("user_id");

				for (int i = 0; i < messages.size(); i++) {

					if (userId == messages.get(i).getId()) {
						messages.remove(i);
					}
				}
				adapter.notifyDataSetChanged();

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(refreshReceiver);
		prefs.saveBooleanPrefs(AppPrefs.KEY_CONVERSATION_FRAG_OPEN, true);
		homeActivity = null;
	}

	@Override
	public void onSocketResponseFailure(String event, String message) {
		if (pd != null) {
			pd.dismiss();
		}
		
		if (event.equals(EventParams.METHOD_GET_CHAT_FRIENDS)) {
			ifMessagesUpdated = false;
			isWatingMessages = false;
		}

		Toast.makeText(homeActivity, message, Toast.LENGTH_LONG).show();
	}

	BroadcastReceiver refreshReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			messages.clear();
			getChatFriends(false);
		}
	};


//	public void setPushExtra(Bundle extra) {
//		friendPushExtra = extra;
//	}

}
