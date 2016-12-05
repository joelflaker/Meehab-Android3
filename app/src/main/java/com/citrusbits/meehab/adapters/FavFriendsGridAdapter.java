package com.citrusbits.meehab.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.constants.Consts;
import com.citrusbits.meehab.model.UserAccount;
import com.citrusbits.meehab.utils.ScreenUtils;
import com.squareup.picasso.Picasso;

public class FavFriendsGridAdapter  extends ArrayAdapter<UserAccount> {

	// arraylists
	List<UserAccount> useraccount;

	// context
	Context mContext;
	int grid_spacing;
	String baseUrl;
	int cellWidthHeight;

	int rectBlueBgRes;
	int rectMaroonBgRes;
	
	private boolean edit;

	public FavFriendsGridAdapter(Context c, int resource,
			List<UserAccount> userAccount) {
		super(c, resource, userAccount);
		mContext = c;
		this.useraccount = userAccount;
		grid_spacing = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 10, c.getResources()
						.getDisplayMetrics())*4;
		baseUrl = Consts.SOCKET_URL;
		cellWidthHeight = (ScreenUtils.screenWidthHeigh(c)[0] - grid_spacing) / 3;
		rectBlueBgRes = R.drawable.rectangle_bg_blue;
		rectMaroonBgRes = R.drawable.rectangle_bg_maroon;

	}
	
	public void setEdit(boolean edit){
		this.edit=edit;
	}
	
	public boolean isEdit(){
		return this.edit;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.grid_item_friend, parent,
					false);
			holder.ivFriend = (ImageView) convertView
					.findViewById(R.id.ivFriend);
			holder.ivOnline = (ImageView) convertView
					.findViewById(R.id.ivOnline);
			holder.ivFavourite = (ImageView) convertView
					.findViewById(R.id.ivFavourite);

			holder.ivBlockIcon = (ImageView) convertView
					.findViewById(R.id.ivBlockIcon);
			
			holder.ivCheck=(ImageView) convertView.findViewById(R.id.ivCheck);

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
		String url = baseUrl + account.getImage();

		Picasso.with(mContext).load(url).placeholder(R.drawable.profile_pic)
				.resize(cellWidthHeight + 20, cellWidthHeight + 20)
				.error(R.drawable.profile_pic)

				.into(holder.ivFriend);

		holder.ivOnline
				.setVisibility(account.getCheckinType().equals("online") ? View.VISIBLE
						: View.GONE);
		holder.ivFavourite
				.setVisibility(account.isFavourite() == 1 ? View.VISIBLE
						: View.GONE);
		holder.ivBlockIcon
				.setVisibility(account.isBlocked() == 1 ? View.VISIBLE
						: View.GONE);
		holder.ivCheck.setVisibility(account.isChecked()?View.VISIBLE:View.GONE);

		if (account.getUserCheckIn() != null && account.getUserCheckIn() == 1) {
			holder.flCell.setBackgroundResource(rectBlueBgRes);
		} else if (account.getRsvpUser() == 1) {
			holder.flCell.setBackgroundResource(rectMaroonBgRes);
		} else {
			holder.flCell.setBackgroundColor(Color.TRANSPARENT);
		}

		return convertView;
	}

	public static class ViewHolder {

		ImageView ivFriend;
		ImageView ivOnline;
		ImageView ivFavourite;
		ImageView ivBlockIcon;
		FrameLayout flCell;
		ImageView ivCheck;
	}

}
