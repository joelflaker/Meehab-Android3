package com.citrusbits.meehab.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.MessageModel;
import com.squareup.picasso.Picasso;

public class MessagesAdapter extends ArrayAdapter<MessageModel> {

	// arraylists
	List<MessageModel> messages;

	// context
	Context mContext;
	LayoutInflater inflater;

	String baseUrl;

	private boolean edit;

	public MessagesAdapter(Context c, int resource, List<MessageModel> m) {
		super(c, resource, m);
		mContext = c;
		messages = m;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		baseUrl = c.getString(R.string.url);
	}

	public MessagesAdapter setEdit(boolean edit) {
		this.edit = edit;
		return this;
	}

	public boolean isEdit() {
		return this.edit;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item_messages, parent,
					false);
			holder.ivFriend = (ImageView) convertView
					.findViewById(R.id.ivFriend);
			holder.ivOnline = (ImageView) convertView
					.findViewById(R.id.ivOnline);
			holder.tvUserName = (TextView) convertView
					.findViewById(R.id.tvUserName);
			holder.tvMessage = (TextView) convertView
					.findViewById(R.id.tvMessage);
			holder.cbSelected = (CheckBox) convertView
					.findViewById(R.id.cbSelected);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final MessageModel message = messages.get(position);

		holder.tvUserName.setText(message.getUsername());
		holder.tvMessage.setText(message.getMessage());
		holder.cbSelected.setChecked(message.isChecked());
		holder.cbSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean checked = ((CheckBox) v).isChecked();
				message.setChecked(checked);
			}
		});

		holder.cbSelected.setVisibility(edit ? View.VISIBLE : View.GONE);

		String userImage = baseUrl + message.getImage();
		Picasso.with(mContext).load(userImage)
				.placeholder(R.drawable.profile_pic).resize(60, 60)
				.error(R.drawable.profile_pic)
				.transform(new PicassoCircularTransform())
				.into(holder.ivFriend);

		return convertView;
	}

	public static class ViewHolder {
		ImageView ivFriend;
		ImageView ivOnline;
		TextView tvUserName;
		TextView tvMessage;
		CheckBox cbSelected;

	}

}
