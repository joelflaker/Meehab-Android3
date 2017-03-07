/**
 * 
 */
package com.citrusbits.meehab.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
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
import com.citrusbits.meehab.utils.ScreenUtils;
import com.squareup.picasso.Picasso;

/**
 * @author Qamar
 * 
 */
public class FriendsListAdapter extends ArrayAdapter<UserAccount> {

	private final int picSize;
	// arraylists
	List<UserAccount> meetings;

	// context
	Context mContext;
	LayoutInflater inflater;

	public FriendsListAdapter(Context c, int resource, List<UserAccount> m) {
		super(c, resource, m);
		mContext = c;
		meetings = m;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		picSize = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 60, c.getResources()
						.getDisplayMetrics()) * 4;

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
		holder.bind(mContext,userAccount,picSize);
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

		public void bind(Context context, UserAccount userAccount,int cellWidth) {
			tvUserName.setText(userAccount.getUsername());
			tvAge.setText(String.valueOf(userAccount.getAge()));
			tvRelationshipStatus.setText(userAccount.getMaritalStatus());
			tvGender.setText(userAccount.getGender());
			tvOrientation.setText(userAccount.getSexualOrientation());
			ivOnline.setVisibility(userAccount.getCheckinType().equals(
					"online") ? View.VISIBLE : View.GONE);
			String userImage = userAccount.getImage();

			if(!TextUtils.isEmpty(userImage)) {
				Picasso.with(context).load(userImage)
						.placeholder(R.drawable.img_place_holder).resize(cellWidth, cellWidth)
						.error(R.drawable.img_place_holder)
						.transform(new PicassoCircularTransform())
						.into(ivFriend);
			}else {
				Picasso.with(context).load(R.drawable.img_place_holder).resize(cellWidth, cellWidth)
						.transform(new PicassoCircularTransform())
						.into(ivFriend);
			}

			if (userAccount.getUserCheckIn() != null
					&& userAccount.getUserCheckIn() == 1) {
				flUserContainer.setBackgroundResource(R.drawable.circle_bg_blue);
			} else if (userAccount.getRsvpUser() == 1) {
				flUserContainer.setBackgroundResource(R.drawable.circle_bg_maroon);
			} else {
				flUserContainer.setBackgroundColor(Color.TRANSPARENT);
			}
		}
	}

}
