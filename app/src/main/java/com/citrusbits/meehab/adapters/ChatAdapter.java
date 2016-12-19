/**
 * 
 */
package com.citrusbits.meehab.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.model.ChatModel;

/**
 * @author Qamar
 * 
 */
public class ChatAdapter extends ArrayAdapter<ChatModel> {

	// arraylists
	List<ChatModel> chats;

	// context
	Context mContext;

	LayoutInflater inflater;

	private ChatCheckedChangeListener chatCheckedChangeListener;

	boolean edit;

	public ChatAdapter setEdit(boolean eidt) {
		this.edit = eidt;
		return this;
	}

	public ChatAdapter setChatCheckedChangeListener(
			ChatCheckedChangeListener chatCheckChangeLitener) {
		this.chatCheckedChangeListener = chatCheckChangeLitener;
		return this;
	}

	public boolean isEdit() {
		return this.edit;
	}

	public ChatAdapter(Context c, int resource, List<ChatModel> chat) {
		super(c, resource, chat);
		mContext = c;
		chats = chat;
		inflater = LayoutInflater.from(c);

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.chat_list_item, parent,
					false);
			holder.rlReceive = (RelativeLayout) convertView
					.findViewById(R.id.rlReceive);
			holder.tvFromMessage = (TextView) convertView
					.findViewById(R.id.tvFromMessage);
			holder.tvFromDate = (TextView) convertView
					.findViewById(R.id.tvFromDate);

			holder.rlSend = (RelativeLayout) convertView
					.findViewById(R.id.rlSend);
			holder.tvSendMessage = (TextView) convertView
					.findViewById(R.id.tvSendMessage);
			holder.tvSendDate = (TextView) convertView
					.findViewById(R.id.tvSendDate);
			holder.cbFrom = (CheckBox) convertView.findViewById(R.id.cbFrom);
			holder.cbSend = (CheckBox) convertView.findViewById(R.id.cbSend);

			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		final ChatModel chat = chats.get(position);

		if (chat.isSend()) {
			holder.rlSend.setVisibility(View.VISIBLE);
			holder.rlReceive.setVisibility(View.GONE);

			holder.tvSendMessage.setText(chat.getMessage());
			holder.tvSendDate.setText(chat.getDisplayDateTime());
			holder.cbSend.setChecked(chat.isChecked());
			holder.cbSend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					boolean checked = ((CheckBox) v).isChecked();
					chat.setChecked(checked);
					notifyCheckedChange();
				}
			});

			holder.cbSend.setVisibility(edit ? View.VISIBLE : View.GONE);

		} else {

			holder.rlReceive.setVisibility(View.VISIBLE);
			holder.rlSend.setVisibility(View.GONE);
			holder.tvFromMessage.setText(chat.getMessage());
			holder.tvFromDate.setText(chat.getDisplayDateTime());
			holder.cbFrom.setChecked(chat.isChecked());
			holder.cbFrom.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					boolean checked = ((CheckBox) v).isChecked();
					chat.setChecked(checked);
					notifyCheckedChange();
				}
			});

			holder.cbFrom.setVisibility(edit ? View.VISIBLE : View.GONE);
		}

		return convertView;
	}

	public void notifyCheckedChange() {
		if (chatCheckedChangeListener != null) {
			chatCheckedChangeListener.onCheckedChange();
		}
	}

	public interface ChatCheckedChangeListener {
		public void onCheckedChange();
	}

	public static class ViewHolder {
		RelativeLayout rlReceive;
		TextView tvFromMessage;
		TextView tvFromDate;
		RelativeLayout rlSend;
		TextView tvSendMessage;
		TextView tvSendDate;
		CheckBox cbFrom;
		CheckBox cbSend;
	}

}
