package com.citrusbits.meehab.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.model.FriendModel;

public class FriendsAdapter extends ArrayAdapter<FriendModel> {

	// arraylists
	List<FriendModel> useraccount;

	// context
	Context mContext;

	public FriendsAdapter(Context c, int resource, List<FriendModel> userAccount) {
		super(c, resource, userAccount);
		mContext = c;
		this.useraccount=userAccount;
	}

	@Override
	public View getView(final int position, View convertView,ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.grid_item_friend, parent, false);

		

		return rowView;
	}

}
