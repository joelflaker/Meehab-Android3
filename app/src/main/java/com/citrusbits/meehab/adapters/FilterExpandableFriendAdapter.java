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
					// TODO Auto-generated method stub
					onlineNow = ((CheckBox) v).isChecked();
				}
			});
			CheckBox cbWillingToSponsor = (CheckBox) convertView
					.findViewById(R.id.cbWillingToSponsor);
			cbWillingToSponsor.setChecked(willingToSponsor);
			cbWillingToSponsor.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					willingToSponsor = ((CheckBox) v).isChecked();
				}
			});
			CheckBox cbHasKids = (CheckBox) convertView
					.findViewById(R.id.cbHasKids);
			cbHasKids.setChecked(hasKids);
			cbHasKids.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
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
			rightcheck.setImageResource(R.drawable.down_arrow);
		} else {
			rightcheck.setImageResource(R.drawable.up_arrow);
		}
		return convertView;
	}

	public FriendFilterResultHolder getFilterResultHolder() {
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
							cacheSelection(groupPosition, childs.get(i)
									.getName());
						}
						childs.get(0).setName("Deselect All");

						setUnSetAny(groupPosition, true);
						notifyDataSetChanged();
					} else {
						List<ExpChild> childs = categories.get(groupPosition)
								.getChildren();

						boolean allChecked = true;
						String name = childs.get(childPosition).getName();

						if (groupPosition == 1) {
							fFilterResultHolder.setAnyFriendType(false);
							fFilterResultHolder.addFriendType(name);
						} else if (groupPosition == 2) {
							fFilterResultHolder.setAnyGender(false);
							fFilterResultHolder.addGender(name);
						} else if (groupPosition == 3) {
							fFilterResultHolder.setAnyAge(false);
							fFilterResultHolder.addAge(name);
						} else if (groupPosition == 4) {
							fFilterResultHolder.setAnyEthenticity(false);
							fFilterResultHolder.addEthenticity(name);
						} else if (groupPosition == 5) {
							fFilterResultHolder.setAnyMaterialStatus(false);
							fFilterResultHolder.addMaterialStatus(name);
						} else if (groupPosition == 6) {
							fFilterResultHolder.setAnyinterestedIn(false);
							fFilterResultHolder.addInterestedIn(name);
						} else if (groupPosition == 7) {
							fFilterResultHolder.setAnyTimeSober(false);
							fFilterResultHolder.addSober(name);
						} else if (groupPosition == 8) {
							fFilterResultHolder.setAnyHeight(false);
							fFilterResultHolder.addHeight(name);
						} else if (groupPosition == 9) {
							fFilterResultHolder.setAnyWeight(false);
							fFilterResultHolder.addWeight(name);
						}

						for (int i = 1; i < childs.size(); i++) {

							Log.e(" " + i, childs.get(i).isChecked() + "");

							if (!childs.get(i).isChecked()) {
								allChecked = false;
								break;
							}
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

						if (groupPosition == 1) {
							fFilterResultHolder.clearFriendType();
						} else if (groupPosition == 2) {
							fFilterResultHolder.clearGender();
						} else if (groupPosition == 3) {
							fFilterResultHolder.clearAge();
						} else if (groupPosition == 4) {
							fFilterResultHolder.clearEhtenticity();
						} else if (groupPosition == 5) {
							fFilterResultHolder.clearMaterialStatus();
						} else if (groupPosition == 6) {
							fFilterResultHolder.clearInterestedIn();
						} else if (groupPosition == 7) {
							fFilterResultHolder.clearSober();
						} else if (groupPosition == 8) {
							fFilterResultHolder.clearHeight();
						} else if (groupPosition == 9) {
							fFilterResultHolder.clearWeight();
						}

						notifyDataSetChanged();
					} else {

						List<ExpChild> childs = categories.get(groupPosition)
								.getChildren();

						if (childs.get(0).isChecked()) {
							childs.get(0).setChecked(false);
							childs.get(0).setName("Select All");
						}

						String name = childs.get(childPosition).getName();
						if (groupPosition == 1) {
							fFilterResultHolder.setAnyFriendType(false);
							fFilterResultHolder.removeFriendType(name);
						} else if (groupPosition == 2) {
							fFilterResultHolder.setAnyGender(false);
							fFilterResultHolder.removeGender(name);
						} else if (groupPosition == 3) {
							fFilterResultHolder.setAnyAge(false);
							fFilterResultHolder.removeAge(name);
						} else if (groupPosition == 4) {
							fFilterResultHolder.setAnyEthenticity(false);
							fFilterResultHolder.removeEthenticiy(name);
						} else if (groupPosition == 5) {
							fFilterResultHolder.setAnyMaterialStatus(false);
							fFilterResultHolder.removeMaterialStatus(name);
						} else if (groupPosition == 6) {
							fFilterResultHolder.setAnyinterestedIn(false);
							fFilterResultHolder.removeInterestedIn(name);
						} else if (groupPosition == 7) {
							fFilterResultHolder.setAnyTimeSober(false);
							fFilterResultHolder.removeSober(name);
						} else if (groupPosition == 8) {
							fFilterResultHolder.setAnyHeight(false);
							fFilterResultHolder.removeHeight(name);
						} else if (groupPosition == 9) {
							fFilterResultHolder.setAnyWeight(false);
							fFilterResultHolder.removeWeight(name);
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

						if (allunChecked) {
							childs.get(0).setName("Select All");
							setUnSetAny(groupPosition, true);
						}

						notifyDataSetChanged();

					}
				}
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

	public void setUnSetAny(int groupPosition, boolean any) {
		if (groupPosition == 1) {
			fFilterResultHolder.setAnyFriendType(any);
		} else if (groupPosition == 2) {
			fFilterResultHolder.setAnyGender(any);
		} else if (groupPosition == 3) {
			fFilterResultHolder.setAnyAge(any);
		} else if (groupPosition == 4) {
			fFilterResultHolder.setAnyEthenticity(any);
		} else if (groupPosition == 5) {
			fFilterResultHolder.setAnyMaterialStatus(any);
		} else if (groupPosition == 6) {
			fFilterResultHolder.setAnyinterestedIn(any);
		} else if (groupPosition == 7) {
			fFilterResultHolder.setAnyTimeSober(any);
		} else if (groupPosition == 8) {
			fFilterResultHolder.setAnyHeight(any);
		} else if (groupPosition == 9) {
			fFilterResultHolder.setAnyWeight(any);
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
