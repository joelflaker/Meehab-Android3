package com.citrusbits.meehab.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.citrusbits.meehab.ChatActivity;
import com.citrusbits.meehab.HomeActivity;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.adapters.MessagesAdapter;
import com.citrusbits.meehab.constants.EventParams;
import com.citrusbits.meehab.model.ChatModel;
import com.citrusbits.meehab.model.MessageModel;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.services.OnBackendConnectListener;
import com.citrusbits.meehab.services.OnSocketResponseListener;
import com.citrusbits.meehab.utils.AccountUtils;
import com.citrusbits.meehab.utils.DeviceUtils;
import com.citrusbits.meehab.utils.UtilityClass;
import com.google.gson.Gson;

public class MessagesFragment extends Fragment implements
		OnSocketResponseListener, OnBackendConnectListener,
		ListView.OnItemClickListener, View.OnClickListener {
	public static final String TAG = MessagesFragment.class.getSimpleName();
	private ListView list;
	private HomeActivity homeActivity;

	private ImageButton ibEdit;
	private ProgressDialog pd;
	private MessagesAdapter adapter;
	private List<MessageModel> messages = new ArrayList<MessageModel>();

	public MessagesFragment() {
	}

	public MessagesFragment(HomeActivity homeActivity) {
		this.homeActivity = homeActivity;
		pd = UtilityClass.getProgressDialog(homeActivity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_conversation, container,
				false);

		v.findViewById(R.id.ivMenu).setOnClickListener(this);
		ibEdit = (ImageButton) v.findViewById(R.id.ibEdit);
		ibEdit.setOnClickListener(this);
		list = (ListView) v.findViewById(R.id.lvConversation);

		adapter = new MessagesAdapter(getActivity(),
				R.layout.list_item_messages, messages);

		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		getChatFriends();

		return v;
	}

	public void getChatFriends() {
		if (homeActivity.socketService != null) {
			pd.show();
			JSONObject object = new JSONObject();

			try {
				object.put("user_id", AccountUtils.getUserId(getActivity()));

				Log.e("json send ", object.toString());
				homeActivity.socketService.getChatFriends(object);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
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
		case R.id.ibEdit:
			// edit

			boolean edit = adapter.isEdit();
			int resource = edit ? R.drawable.edit_btn
					: R.drawable.delete_msg_btn;
			ibEdit.setImageResource(resource);
			if (!edit) {
				adapter.setEdit(true);
				adapter.notifyDataSetChanged();
			} else {
				deleteConversation();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * @param string
	 */
	protected void toast(String string) {
		Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
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
		// TODO Auto-generated method stub
		MessageModel message = messages.get(position);

		if (adapter.isEdit()) {
			message.setChecked(!message.isChecked());
			adapter.notifyDataSetChanged();
			return;
		}

		UserAccount user = new UserAccount();

		int userId = AccountUtils.getUserId(getActivity());
		int friendId = message.getFromID() == userId ? message.getToID()
				: message.getFromID();
		user.setUsername(message.getUsername());
		user.setId(friendId);
		Intent i = new Intent(getActivity(), ChatActivity.class);
		i.putExtra(ChatActivity.EXTRA_CHAT, user);
		getActivity().startActivity(i);
		getActivity().overridePendingTransition(R.anim.activity_in,
				R.anim.activity_out);
	}

	@Override
	public void onBackendConnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSocketResponseSuccess(String event, Object obj) {
		if (pd != null) {
			pd.dismiss();
		}

		if (event.equals(EventParams.METHOD_GET_CHAT_FRIENDS)) {

			JSONObject data = ((JSONObject) obj);

			try {
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

					messages.add(message);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			adapter.notifyDataSetChanged();
			// Toast.makeText(getActivity(), "Message size is " +
			// messages.size(),
			// Toast.LENGTH_SHORT).show();
		} else if (event.equals(EventParams.METHOD_DELETE_CONVERSATION)) {

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			adapter.setEdit(!adapter.isEdit());
			adapter.notifyDataSetChanged();
			// Toast.makeText(getActivity(), "Message size is " +
			// messages.size(),
			// Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onSocketResponseFailure(String message) {
		// TODO Auto-generated method stub
		if (pd != null) {
			pd.dismiss();
		}

		Toast.makeText(homeActivity, message, Toast.LENGTH_LONG).show();
	}

}
