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
		LayoutInflater inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		convertView = inflater.inflate(R.layout.list_exp_item_filter_parent,
				parent, false);

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
			rightcheck.setImageResource(R.drawable.down_arrow);
		} else {
			rightcheck.setImageResource(R.drawable.up_arrow);
		}
		return convertView;
	}

	public RehaabFilterResultHolder getFilterResultHolder() {
		return this.fFilterResultHolder;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.list_exp_item_filter_child,
				parent, false);

		final ExpChild child = categories.get(groupPosition).getChildren()
				.get(childPosition);
		TextView text = (TextView) convertView.findViewById(R.id.filter_value);
		text.setText(child.getName());

		final CheckBox check = (CheckBox) convertView
				.findViewById(R.id.filter_check);
		check.setChecked(child.isChecked());

		check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				child.setChecked(isChecked);
				String childName = child.getName();
				if (isChecked) {
					if (childName.toLowerCase().equals("select all")) {
						List<ExpChild> childs = categories.get(groupPosition)
								.getChildren();
						for (int i = 0; i < childs.size(); i++) {
							childs.get(i).setChecked(true);
//							cacheSelection(groupPosition, childs.get(i)
//									.getName());
						}
						childs.get(0).setName("Deselect All");

						if (groupPosition == 0) {
							fFilterResultHolder.selectAllType(true);
						} else if (groupPosition == 1) {
							fFilterResultHolder.selectAllInsurance(true);
						}
						
						setUnSetAny(groupPosition, true);
						notifyDataSetChanged();
					} else {
						List<ExpChild> childs = categories.get(groupPosition)
								.getChildren();

						boolean allChecked = true;
						String name = childs.get(childPosition).getName();

						if (groupPosition == 0) {
							fFilterResultHolder.setanyType(false);
							fFilterResultHolder.addRehabType(name);
						} else if (groupPosition == 1) {
							fFilterResultHolder.setanyInsuranceAccepted(false);
							fFilterResultHolder.addInsuranceAccepted(name);
						}
						for (int i = 1; i < childs.size(); i++) {

							Log.e(" " + i, childs.get(i).isChecked() + "");

							if (!childs.get(i).isChecked()) {
								allChecked = false;
								break;
							}
						}

						if (groupPosition == 0) {
							fFilterResultHolder.selectAllType(allChecked);
						} else if (groupPosition == 1) {
							fFilterResultHolder.selectAllInsurance(allChecked);
						}
						if (allChecked) {

							childs.get(0).setChecked(true);
							childs.get(0).setName("Deselect All");
							setUnSetAny(groupPosition, true);
							notifyDataSetChanged();
							
						}

					}
				} else {
					if (childName.toLowerCase().equals("deselect all")) {
						List<ExpChild> childs = categories.get(groupPosition)
								.getChildren();
						for (int i = 0; i < childs.size(); i++) {
							childs.get(i).setChecked(false);
						}

						childs.get(0).setName("Select All");

						if (groupPosition == 0) {
							fFilterResultHolder.clearRehabType();
							fFilterResultHolder.selectAllType(false);
							fFilterResultHolder.setanyType(true);
						} else if (groupPosition == 1) {
							fFilterResultHolder.selectAllInsurance(false);
							fFilterResultHolder.setanyInsuranceAccepted(true);
							fFilterResultHolder.clearInsuranceAccepted();
						}

						notifyDataSetChanged();
					} else {

						List<ExpChild> childs = categories.get(groupPosition)
								.getChildren();

						if (childs.get(0).isChecked()) {
							childs.get(0).setChecked(false);
							childs.get(0).setName("Select All");
						}

						boolean allunChecked = true;

						for (int i = 1; i < childs.size(); i++) {
							Log.e("Uncheck " + i, childs.get(i).isChecked()
									+ "");
							if (childs.get(i).isChecked()) {
								allunChecked = false;
								break;
							}
						}
						
						String name = childs.get(childPosition).getName();
						if (groupPosition == 0) {
							fFilterResultHolder.removeRehabType(name);
							fFilterResultHolder.setanyType(fFilterResultHolder.getRehabType().size() == 0);
						} else if (groupPosition == 1) {
							fFilterResultHolder.removeInsuranceAccepted(name);
							fFilterResultHolder.setanyInsuranceAccepted(fFilterResultHolder.getInsuranceAccepted().size() == 0);
						}
						

						if (allunChecked) {
							childs.get(0).setName("Select All");
							setUnSetAny(groupPosition, true);
							
						}
						
						if (groupPosition == 0) {
							fFilterResultHolder.selectAllType(allunChecked);
						} else if (groupPosition == 1) {
							fFilterResultHolder.selectAllInsurance(allunChecked);
						}

						notifyDataSetChanged();

					}
				}
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

	public void setUnSetAny(int groupPosition, boolean any) {
		if (groupPosition == 0) {
			fFilterResultHolder.setanyType(any);
		} else if (groupPosition == 1) {
			fFilterResultHolder.setanyInsuranceAccepted(any);
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
