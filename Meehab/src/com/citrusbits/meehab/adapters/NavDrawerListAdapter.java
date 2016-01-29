package com.citrusbits.meehab.adapters;

import java.util.ArrayList;

import com.citrusbits.meehab.NavDrawerItem;
import com.citrusbits.meehab.R;
import com.citrusbits.meehab.db.DatabaseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class NavDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<NavDrawerItem> navDrawerItems;
	
	private int unreadMessageCount;

	public NavDrawerListAdapter(Context context,
			ArrayList<NavDrawerItem> navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;
		unreadMessageCount=DatabaseHandler.getInstance(context).getAllUnreadMessagesCount();
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}
	
	public void updateUnreadMessageCount(){
		unreadMessageCount=DatabaseHandler.getInstance(context).getAllUnreadMessagesCount();
		this.notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {

		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			holder.tvCounter = (TextView) convertView
					.findViewById(R.id.tvCounter);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		NavDrawerItem drawerItem = navDrawerItems.get(position);
		holder.tvTitle.setText(drawerItem.getTitle());
		holder.tvCounter = (TextView) convertView.findViewById(R.id.tvCounter);
		holder.tvCounter
				.setVisibility(position == ViewHolder.MESSAGE_ITEM_POSITION &&unreadMessageCount>0? View.VISIBLE
						: View.GONE);
		holder.tvCounter.setText(String.valueOf(unreadMessageCount));

		return convertView;
	}

	public static class ViewHolder {
		public static final int MESSAGE_ITEM_POSITION = 2;
		public TextView tvTitle;
		public TextView tvCounter;
	}

}