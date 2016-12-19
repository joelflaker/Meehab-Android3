/**
 * 
 */
package com.citrusbits.meehab.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.UserAccount;
import com.squareup.picasso.Picasso;

/**
 * @author Qamar
 * 
 */
public class FriendsListAdapter extends ArrayAdapter<UserAccount> {

	// arraylists
	List<UserAccount> meetings;

	// context
	Context mContext;
	LayoutInflater inflater;

	String baseUrl;

	int circleBlueBgRes;
	int circleMaroonBgRes;
	
	

	public FriendsListAdapter(Context c, int resource, List<UserAccount> m) {
		super(c, resource, m);
		mContext = c;
		meetings = m;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		baseUrl = Consts.SOCKET_URL;

		circleBlueBgRes = R.drawable.circle_bg_blue;
		circleMaroonBgRes = R.drawable.circle_bg_maroon;
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
			holder.flUserContainer = (FrameLayout) convertView
					.findViewById(R.id.flUserContainer);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		UserAccount userAccount = meetings.get(position);

		holder.tvUserName.setText(userAccount.getUsername());
		holder.tvAge.setText(String.valueOf(userAccount.getAge()));
		holder.tvRelationshipStatus.setText(userAccount.getMaritalStatus());
		holder.tvGender.setText(userAccount.getGender());
		holder.tvOrientation.setText(userAccount.getSexualOrientation());
		holder.ivOnline.setVisibility(userAccount.getCheckinType().equals(
				"online") ? View.VISIBLE : View.GONE);
		String userImage = baseUrl + userAccount.getImage();

		Picasso.with(mContext).load(userImage)
				.placeholder(R.drawable.profile_pic_border).resize(60, 60)
				.error(R.drawable.profile_pic_border)
				.transform(new PicassoCircularTransform())
				.into(holder.ivFriend);

		if (userAccount.getUserCheckIn() != null
				&& userAccount.getUserCheckIn() == 1) {
			holder.flUserContainer.setBackgroundResource(circleBlueBgRes);
		} else if (userAccount.getRsvpUser() == 1) {
			holder.flUserContainer.setBackgroundResource(circleMaroonBgRes);
		} else {
			holder.flUserContainer.setBackgroundColor(Color.TRANSPARENT);
		}

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

		FrameLayout flUserContainer;
	}

}
