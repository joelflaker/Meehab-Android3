/**
 * 
 */
package com.citrusbits.meehab.adapters;

import java.util.ArrayList;
import java.util.List;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.model.ExpCategory;
import com.citrusbits.meehab.model.ExpChild;
import com.citrusbits.meehab.model.FriendFilterResultHolder;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author Qamar
 * 
 */
public class FilterExpandableFriendAdapter extends BaseExpandableListAdapter {

	private final ArrayList<ExpCategory> categories;
	public Activity context;
	private int lastExpandedGroupPosition;
	ExpandableListView list;

	private OnClickListener mOnClickListener;

	private FriendFilterResultHolder fFilterResultHolder = new FriendFilterResultHolder();

	private boolean onlineNow;
	private boolean willingToSponsor;
	private boolean hasKids;

	public FilterExpandableFriendAdapter(Activity act, ExpandableListView l,
			ArrayList<ExpCategory> g) {
		context = act;
		list = l;
		categories = g;
	}

	public FilterExpandableFriendAdapter setFilterExpandAdapter(
			OnClickListener onClickListener) {
		this.mOnClickListener = onClickListener;
		return this;
	}

	public boolean isOnline() {
		return this.onlineNow;
	}

	public FilterExpandableFriendAdapter setFriendFilterResultHolder(
			FriendFilterResultHolder friendFilterResultHolder) {
		
		this.fFilterResultHolder = friendFilterResultHolder;
		onlineNow=fFilterResultHolder.isOnlineNow();
		willingToSponsor=friendFilterResultHolder.isWillingToSponsor();
		hasKids=friendFilterResultHolder.isHasKids();
		
		
		return this;
	}

	public boolean isWillingtoSponosr() {
		return this.willingToSponsor;
	}

	public boolean isHasKids() {
		return this.hasKids;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ExpCategory group = categories.get(groupPosition);
		if (groupPosition == 0) {
			convertView = inflater.inflate(R.layout.friend_filter_header,
					parent, false);
			CheckBox cbOnlineNow = (CheckBox) convertView
					.findViewById(R.id.cbOnlineNow);
			cbOnlineNow.setChecked(onlineNow);
			cbOnlineNow.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onlineNow = ((CheckBox) v).isChecked();
				}
			});
			CheckBox cbWillingToSponsor = (CheckBox) convertView
					.findViewById(R.id.cbWillingToSponsor);
			cbWillingToSponsor.setChecked(willingToSponsor);
			cbWillingToSponsor.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					willingToSponsor = ((CheckBox) v).isChecked();
				}
			});
			CheckBox cbHasKids = (CheckBox) convertView
					.findViewById(R.id.cbHasKids);
			cbHasKids.setChecked(hasKids);
			cbHasKids.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					hasKids = ((CheckBox) v).isChecked();
				}
			});

			return convertView;
		} else {
			convertView = inflater.inflate(
					R.layout.list_exp_item_filter_parent, parent, false);
		}

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

	public FriendFilterResultHolder getFilterResultHolder() {
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


				List<ExpChild> childs = categories.get(groupPosition)
						.getChildren();


				String name = childs.get(childPosition).getName();

				boolean allChecked = true;

				for (int i = 1; i < childs.size(); i++) {
					if (!childs.get(i).isChecked()) {
						allChecked = false;
						break;
					}
				}

				if (groupPosition == 1) {
					if (child.isChecked()) {
						fFilterResultHolder.addFriendType(name);
					}else {
						fFilterResultHolder.removeFriendType(name);
					}
					fFilterResultHolder.setAnyFriendType(allChecked || fFilterResultHolder.getFriendType().size() == 0);

				} else if (groupPosition == 2) {
					if (child.isChecked()) {
						fFilterResultHolder.addGender(name);
					}else {
						fFilterResultHolder.removeGender(name);
					}
					fFilterResultHolder.setAnyGender(allChecked || fFilterResultHolder.getGender().size() == 0);
				} else if (groupPosition == 3) {
					if (child.isChecked()) {
						fFilterResultHolder.addAge(name);
					}else {
						fFilterResultHolder.removeAge(name);
					}
					fFilterResultHolder.setAnyAge(allChecked || fFilterResultHolder.getAge().size() == 0);
				} else if (groupPosition == 4) {
					if (child.isChecked()) {
						fFilterResultHolder.addEthenticity(name);
					}else {
						fFilterResultHolder.removeEthenticiy(name);
					}
					fFilterResultHolder.setAnyEthenticity(allChecked || fFilterResultHolder.getEthenticity().size() == 0);
				} else if (groupPosition == 5) {
					if (child.isChecked()) {
						fFilterResultHolder.addMaterialStatus(name);
					}else {
						fFilterResultHolder.removeMaterialStatus(name);
					}
					fFilterResultHolder.setAnyMaterialStatus(allChecked || fFilterResultHolder.getMaterialStatus().size() == 0);
				} else if (groupPosition == 6) {
					if (child.isChecked()) {
						fFilterResultHolder.addInterestedIn(name);
					}else {
						fFilterResultHolder.removeInterestedIn(name);
					}
					if (allChecked) {
						fFilterResultHolder.setAnyinterestedIn(allChecked || fFilterResultHolder.getInterestedIn().size() == 0);
					}else if(child.isChecked()) {
						fFilterResultHolder.setAnyinterestedIn(child.getName().equalsIgnoreCase("both"));
					}
				} else if (groupPosition == 7) {
					if (child.isChecked()) {
						fFilterResultHolder.addSober(name);
					}else {
						fFilterResultHolder.removeSober(name);
					}
					fFilterResultHolder.setAnyTimeSober(allChecked || fFilterResultHolder.getSober().size() == 0);
				} else if (groupPosition == 8) {
					if (child.isChecked()) {
						fFilterResultHolder.addHeight(name);
					}else {
						fFilterResultHolder.removeHeight(name);
					}
					fFilterResultHolder.setAnyHeight(allChecked || fFilterResultHolder.getHeight().size() == 0);

				} else if (groupPosition == 9) {
					if (child.isChecked()) {
						fFilterResultHolder.addWeight(name);
					}else {
						fFilterResultHolder.removeWeight(name);
					}
					fFilterResultHolder.setAnyWeight(allChecked || fFilterResultHolder.getWeight().size() == 0);
				}

				notifyDataSetChanged();

			}
		});

		return convertView;
	}

	public void cacheSelection(int groupPosition, String selectedValue) {
		if (groupPosition == 1) {
			fFilterResultHolder.addFriendType(selectedValue);
		} else if (groupPosition == 2) {
			fFilterResultHolder.addGender(selectedValue);
		} else if (groupPosition == 3) {
			fFilterResultHolder.addAge(selectedValue);
		} else if (groupPosition == 4) {
			fFilterResultHolder.addEthenticity(selectedValue);
		} else if (groupPosition == 5) {
			fFilterResultHolder.addMaterialStatus(selectedValue);
		} else if (groupPosition == 6) {
			fFilterResultHolder.addInterestedIn(selectedValue);
		} else if (groupPosition == 7) {
			fFilterResultHolder.addSober(selectedValue);
		} else if (groupPosition == 8) {
			fFilterResultHolder.addHeight(selectedValue);
		} else if (groupPosition == 9) {
			fFilterResultHolder.addWeight(selectedValue);
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
