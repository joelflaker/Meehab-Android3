/**
 * 
 */
package com.citrusbits.meehab.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.model.TMeeting;

/**
 * @author Qamar
 *
 */
public class RehabsListAdapter extends ArrayAdapter<TMeeting> {

	// arraylists
	ArrayList<TMeeting> arrayList;

	// context
	Context mContext;

	public RehabsListAdapter(Context c, int resource, ArrayList<TMeeting> m) {
		super(c, resource, m);
		mContext = c;
		arrayList = m;
		
	}

	@Override
	public View getView(final int position, View convertView,ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_item_rehab, parent, false);

		

		return rowView;
	}

}
