/**
 * 
 */
package com.citrusbits.meehab.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.images.PicassoCircularTransform;
import com.citrusbits.meehab.model.FriendModel;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.utils.ScreenUtils;
import com.squareup.picasso.Picasso;

/**
 * @author Qamar
 * 
 */
public class FriendsGridAdapter extends ArrayAdapter<UserAccount> {

	// arraylists
	List<UserAccount> useraccount;

	// context
	Context mContext;
	int grid_spacing;
	final int cellWidthHeight;

	public FriendsGridAdapter(Context c, int resource,
			List<UserAccount> userAccount) {
		super(c, resource, userAccount);
		mContext = c;
		this.useraccount = userAccount;
		grid_spacing = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 10, c.getResources()
						.getDisplayMetrics()) * 4;
		cellWidthHeight = (ScreenUtils.screenWidthHeigh(c)[0] - grid_spacing) / 3;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.grid_item_friend, parent,false);
			holder.ivFriend = (ImageView) convertView
					.findViewById(R.id.ivFriend);
			holder.ivOnline = (ImageView) convertView
					.findViewById(R.id.ivOnline);
			holder.ivFavourite = (ImageView) convertView
					.findViewById(R.id.ivFavourite);

			holder.ivBlockIcon = (ImageView) convertView
					.findViewById(R.id.ivBlockIcon);

			holder.flCell = (FrameLayout) convertView.findViewById(R.id.flCell);

			FrameLayout.LayoutParams flParams = (android.widget.FrameLayout.LayoutParams) holder.flCell
					.getLayoutParams();
			flParams.width = cellWidthHeight;
			flParams.height = cellWidthHeight;

			holder.flCell.setLayoutParams(flParams);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		UserAccount account = useraccount.get(position);

		holder.bind(mContext,account,cellWidthHeight);
		return convertView;
	}

	public static class ViewHolder {

		ImageView ivFriend;
		ImageView ivOnline;
		ImageView ivFavourite;
		ImageView ivBlockIcon;
		FrameLayout flCell;

		public void bind(Context context, UserAccount account, int cellWidthHeight) {

			ivOnline
					.setVisibility(account.getCheckinType().equals("online") ? View.VISIBLE
							: View.GONE);
			if (ivFavourite != null) {
				ivFavourite.setVisibility(account.isFavourite() == 1
						&& account.isBlocked() == 0 ? View.VISIBLE : View.GONE);
			}

			ivBlockIcon
					.setVisibility(account.isBlocked() == 1 ? View.VISIBLE
							: View.GONE);

			String url = account.getImage();

			if(!TextUtils.isEmpty(url)) {
				Picasso.with(context).load(url)
						.placeholder(R.drawable.img_place_holder)
						.resize(cellWidthHeight + 20, cellWidthHeight + 20)
						.error(R.drawable.img_place_holder)
						.into(ivFriend);
			}else {
				Picasso.with(context).load(R.drawable.img_place_holder)
						.resize(cellWidthHeight + 20, cellWidthHeight + 20)
						.into(ivFriend);
			}

			if (account.getUserCheckIn() != null && account.getUserCheckIn() == 1) {
				flCell.setBackgroundResource(R.drawable.rectangle_bg_blue);
			} else if (account.getRsvpUser() == 1) {
				flCell.setBackgroundResource(R.drawable.rectangle_bg_maroon);
			} else {
				flCell.setBackgroundColor(Color.TRANSPARENT);
			}

		}
	}

}
