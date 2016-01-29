package com.citrusbits.meehab.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.fragments.FilterResultHolder;
import com.citrusbits.meehab.fragments.FilterResultHolder.FilterTime;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.model.RehabModel;
import com.citrusbits.meehab.popup.CodePopup;
import com.citrusbits.meehab.utils.MettingCodes;

public class RehabListAdapter extends ArrayAdapter<RehabModel> {

	public static final int MAX_CODE_SIZE = 8;

	// arraylists
	List<RehabModel> rehabs;

	List<RehabModel> arrayList = new ArrayList<>();

	// context
	Context mContext;

	LayoutInflater inflater;

	public RehabListAdapter(Context c, int resource, List<RehabModel> list) {
		super(c, resource, list);
		mContext = c;
		rehabs = list;
		arrayList.clear();
		arrayList.addAll(list);

		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setRehab(int position, RehabModel rehab) {
		this.arrayList.set(position, rehab);
		this.notifyDataSetChanged();
	}

	public List<RehabModel> getRehabs() {
		return this.rehabs;
	}

	public List<RehabModel> getRehabCache() {
		return this.arrayList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View v = convertView;
		ViewHolder holder;

		if (v == null) {
			holder = new ViewHolder();
			v = inflater.inflate(R.layout.list_rehab_item, parent, false);
			holder.tvRehabName = (TextView) v.findViewById(R.id.tvRehabName);
			holder.tvDistance = (TextView) v.findViewById(R.id.tvDistance);
			holder.tvBuilding = (TextView) v.findViewById(R.id.tvBuilding);
			holder.tvAddress = (TextView) v.findViewById(R.id.tvAddress);
			holder.tvTime = (TextView) v.findViewById(R.id.tvTime);
			holder.tvStatus = (TextView) v.findViewById(R.id.tvStatus);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		final RehabModel rehab = rehabs.get(position);

		holder.tvRehabName.setText(rehab.getName());
		holder.tvDistance.setText(rehab.getDistance() + " miles");
		holder.tvAddress.setText(rehab.getAddress());
		holder.tvTime.setText(rehab.getRehabDays().get(0).getStartTime());

		return v;
	}

	public static class ViewHolder {
		public TextView tvRehabName;
		public TextView tvDistance;
		public TextView tvBuilding;
		public TextView tvAddress;
		public TextView tvTime;
		public TextView tvStatus;
	}

	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase();
		rehabs.clear();
		Log.e("ArrayList Size ","Size "+arrayList.size());
		for (RehabModel wp : arrayList) {
			Log.e("Name ", wp.getName());
			
			if (wp.getName().toLowerCase()
					.contains(charText)) {
				rehabs.add(wp);

			}
		}
		
		
		// }
		// Collections.sort(contactList, new MeetingComparator());
		notifyDataSetChanged();
	}

}
