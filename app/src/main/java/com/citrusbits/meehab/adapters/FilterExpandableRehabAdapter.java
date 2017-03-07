package com.citrusbits.meehab.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.model.ExpCategory;
import com.citrusbits.meehab.model.ExpChild;
import com.citrusbits.meehab.model.FriendFilterResultHolder;
import com.citrusbits.meehab.model.RehaabFilterResultHolder;

public class FilterExpandableRehabAdapter extends BaseExpandableListAdapter {

	private final ArrayList<ExpCategory> categories;
	public Activity context;
	private int lastExpandedGroupPosition;
	ExpandableListView list;

	private RehaabFilterResultHolder fFilterResultHolder;

	public FilterExpandableRehabAdapter(Activity act, ExpandableListView l,
			ArrayList<ExpCategory> g, RehaabFilterResultHolder filterModel) {
		this.context = act;
		this.list = l;
		this.fFilterResultHolder = filterModel;
		this.categories = g;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.list_exp_item_filter_parent,
					parent, false);
		}

		ExpCategory group = categories.get(groupPosition);
		// Get grouprow.xml file elements and set values
		TextView txtType = (TextView) convertView
				.findViewById(R.id.txtTypeName);
		TextView txtRight = (TextView) convertView.findViewById(R.id.txtRight);
		ImageButton rightcheck = (ImageButton) convertView
				.findViewById(R.id.imgBtnRight);

		txtType.setText(group.getName());
		txtRight.setText(group.getValue());

		txtType.setFocusable(false);
		rightcheck.setFocusable(false);

		// Change right check image on parent at runtime
		if (isExpanded) {
			rightcheck.setImageResource(R.drawable.up_arrow);
		} else {
			rightcheck.setImageResource(R.drawable.down_arrow);
		}
		return convertView;
	}

	public RehaabFilterResultHolder getFilterResultHolder() {
		return this.fFilterResultHolder;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_exp_item_filter_child,
					parent, false);
		}

		final ExpChild child = categories.get(groupPosition).getChildren()
				.get(childPosition);
		TextView text = (TextView) convertView.findViewById(R.id.filter_value);
		text.setText(child.getName());

		final CheckBox check = (CheckBox) convertView
				.findViewById(R.id.filter_check);
		check.setChecked(child.isChecked());

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				child.setChecked(!child.isChecked());
				check.setChecked(child.isChecked());
				String childName = child.getName();
				List<ExpChild> childs = categories.get(groupPosition)
						.getChildren();

				boolean allChecked = true;
				String name = childs.get(childPosition).getName();

				for (int i = 1; i < childs.size(); i++) {
					if (!childs.get(i).isChecked()) {
						allChecked = false;
						break;
					}
				}

				if (groupPosition == 0) {
					if (child.isChecked()) {
						fFilterResultHolder.addRehabType(name);
					}else {
						fFilterResultHolder.removeRehabType(name);
					}
					fFilterResultHolder.selectAllType(allChecked);
					fFilterResultHolder.setanyType(allChecked || fFilterResultHolder.getRehabType().size() == 0);
				} else if (groupPosition == 1) {
					if (child.isChecked()) {
						fFilterResultHolder.addInsuranceAccepted(name);
					}else {
						fFilterResultHolder.removeInsuranceAccepted(name);
					}
					fFilterResultHolder.selectAllInsurance(allChecked);
					fFilterResultHolder.setanyInsuranceAccepted(allChecked || fFilterResultHolder.getInsuranceAccepted().size() == 0);
				}

				notifyDataSetChanged();

			}
		});

		return convertView;
	}

	public void cacheSelection(int groupPosition, String selectedValue) {
		if (groupPosition == 0) {
			fFilterResultHolder.addRehabType(selectedValue);
		} else if (groupPosition == 1) {
			fFilterResultHolder.addInsuranceAccepted(selectedValue);
		}
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return categories.get(groupPosition).getChildren().size();
	}

	@Override
	public ExpChild getChild(int groupPosition, int childPosition) {
		return categories.get(groupPosition).getChildren().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public ExpCategory getGroup(int groupPosition) {
		return categories.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return categories.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	/**
	 * automatically collapse last expanded group
	 * @see http://stackoverflow.com/questions/4314777/programmatically-collapse-a-group-in-expandablelistview
	 */
	public void onGroupExpanded(int groupPosition) {

		if (groupPosition != lastExpandedGroupPosition) {
			list.collapseGroup(lastExpandedGroupPosition);
		}

		super.onGroupExpanded(groupPosition);

		lastExpandedGroupPosition = groupPosition;

	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
}
