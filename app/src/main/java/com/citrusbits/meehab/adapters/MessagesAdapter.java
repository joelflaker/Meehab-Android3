package com.citrusbits.meehab.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.db.DatabaseHandler;
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
	MessageCheckedChangeListener messageCheckedChangeListener;
	
	int circleBlueBgRes;
	int circleMaroonBgRes;
	private OnClickListener onClickListener;


	public MessagesAdapter(Context c, int resource, List<MessageModel> m) {
		super(c, resource, m);
		mContext = c;
		messages = m;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		baseUrl = Consts.SOCKET_URL;

		circleBlueBgRes = R.drawable.circle_bg_blue;
		circleMaroonBgRes = R.drawable.circle_bg_maroon;
	}

	public MessagesAdapter setMessageCheckedChangeListener(
			MessageCheckedChangeListener messageCheckedChangeListener) {
		this.messageCheckedChangeListener = messageCheckedChangeListener;
		return this;
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
			holder.ivFavourite=(ImageView) convertView.findViewById(R.id.ivFavourite);
			holder.tvUserName = (TextView) convertView
					.findViewById(R.id.tvUserName);
			holder.tvMessage = (TextView) convertView
					.findViewById(R.id.tvMessage);
			holder.cbSelected = (CheckBox) convertView
					.findViewById(R.id.cbSelected);
			holder.tvMessageTime=(TextView) convertView.findViewById(R.id.tvMessageTime);
			holder.flUserContainer=(FrameLayout) convertView.findViewById(R.id.flUserContainer);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final MessageModel message = messages.get(position);

		holder.tvUserName.setText(message.getUsername());
//		if(message.is
		holder.tvMessage.setText(message.getMessage());
		holder.cbSelected.setChecked(message.isChecked());
		holder.ivFavourite.setVisibility(message.getFavourite()==1?View.VISIBLE:View.GONE);
		holder.tvMessageTime.setText(getMessageTime(message.getMessageDate()));
		holder.cbSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean checked = ((CheckBox) v).isChecked();
				message.setChecked(checked);
				if (messageCheckedChangeListener != null) {
					messageCheckedChangeListener.onMessageCheckedChange();
				}
			}
		});
		
		if(DatabaseHandler.getInstance(mContext).getUnreadMessageCount(message.getFromID()) > 0){
			holder.tvMessage.setTextColor(ContextCompat.getColor(mContext,R.color.black));
			holder.tvMessage.setTypeface(null, Typeface.BOLD);
		}else{
			holder.tvMessage.setTextColor(ContextCompat.getColor(mContext,R.color.text_color_gray));
			holder.tvMessage.setTypeface(null, Typeface.NORMAL);
		}

		holder.cbSelected.setVisibility(edit ? View.VISIBLE : View.GONE);
		holder.ivOnline
				.setVisibility(message.getCheckInType().equals("online") ? View.VISIBLE
						: View.GONE);
		holder.ivFriend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setTag(message);
				onClickListener.onClick(v);
			}
		});
		String userImage = message.getImage();
		if(!TextUtils.isEmpty(userImage)) {
			Picasso.with(mContext).load(userImage)
					.placeholder(R.drawable.img_place_holder)
					.error(R.drawable.img_place_holder)
					.transform(new PicassoCircularTransform())
					.into(holder.ivFriend);
		}else {
			Picasso.with(mContext).load(R.drawable.img_place_holder)
					.into(holder.ivFriend);
		}
		if (message.getUserCheckIn() == 1) {
			holder.flUserContainer.setBackgroundResource(circleBlueBgRes);
		} else if (message.getRsvpUser() == 1) {
			holder.flUserContainer.setBackgroundResource(circleMaroonBgRes);
		} else {
			holder.flUserContainer.setBackgroundColor(Color.TRANSPARENT);
		}

		return convertView;
	}
	
	private String getMessageTime(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a\nMM-dd-yy");
		return dateFormat.format(date);
	}

	public void setOnViewClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public static class ViewHolder {
		
		ImageView ivFavourite;
		ImageView ivFriend;
		ImageView ivOnline;
		TextView tvUserName;
		TextView tvMessage;
		
		TextView tvMessageTime;
		CheckBox cbSelected;
		
		FrameLayout flUserContainer;

	}

	public interface MessageCheckedChangeListener {
		public void onMessageCheckedChange();
	}

}
