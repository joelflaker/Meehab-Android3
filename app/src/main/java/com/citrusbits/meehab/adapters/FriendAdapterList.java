package com.citrusbits.meehab.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.adapters.FriendsListAdapter.ViewHolder;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.FriendModel;
import com.citrusbits.meehab.model.UserAccount;
import com.squareup.picasso.Picasso;

public class FriendAdapterList extends ArrayAdapter<FriendModel> {

	// arraylists
	List<FriendModel> meetings;

	// context
	Context mContext;
	LayoutInflater inflater;

	String baseUrl;

	public FriendAdapterList(Context c, int resource, List<FriendModel> m) {
		super(c, resource, m);
		mContext = c;
		meetings = m;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		baseUrl = Consts.SOCKET_URL;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item_friend, parent,
					false);
			holder.ivFriend = (ImageView) convertView
					.findViewById(R.id.ivFriend);
			holder.ivOnline = (ImageView) convertView
					.findViewById(R.id.ivOnline);
			holder.tvUserName = (TextView) convertView
					.findViewById(R.id.tvUserName);
			holder.tvAge = (TextView) convertView.findViewById(R.id.tvAge);
			holder.tvRelationshipStatus = (TextView) convertView
					.findViewById(R.id.tvRelationshipStatus);
			holder.tvGender = (TextView) convertView
					.findViewById(R.id.tvGender);
			holder.tvOrientation = (TextView) convertView
					.findViewById(R.id.tvOrientation);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/*
		 * UserAccount userAccount = meetings.get(position);
		 * 
		 * holder.tvUserName.setText(userAccount.getUsername());
		 * holder.tvWeight.setText(userAccount.getWeight());
		 * holder.tvRelationshipStatus.setText(userAccount.getMaritalStatus());
		 * holder.tvGender.setText(userAccount.getGender());
		 * holder.tvOrientation.setText(userAccount.getSexualOrientation());
		 * 
		 * String userImage = baseUrl + userAccount.getImage();
		 * 
		 * Picasso.with(mContext).load(userImage)
		 * .placeholder(R.drawable.profile_pic).resize(60, 60)
		 * .error(R.drawable.profile_pic) .transform(new
		 * PicassoCircularTransform()) .into(holder.ivFriend);
		 */

		return convertView;
	}

	public static class ViewHolder {
		ImageView ivFriend;
		ImageView ivOnline;
		TextView tvUserName;
		TextView tvAge;
		TextView tvRelationshipStatus;
		TextView tvGender;
		TextView tvOrientation;
	}

}
