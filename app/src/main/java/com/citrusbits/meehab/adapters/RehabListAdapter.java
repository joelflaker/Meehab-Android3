package com.citrusbits.meehab.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.model.RehaabFilterResultHolder;
import com.citrusbits.meehab.model.RehabModel;
import com.citrusbits.meehab.model.RehabResponseModel;

public class RehabListAdapter extends ArrayAdapter<RehabModel> {

	public static final int MAX_CODE_SIZE = 8;

	// arraylists
	List<RehabModel> rehabs;

	List<RehabModel> allRehabs;
	List<RehabModel> filteredRehabs = new ArrayList<>();

	// context
	Context mContext;

	LayoutInflater inflater;

	private RehabResponseModel rehabResponse;

	public RehabListAdapter(Context c, int resource, RehabResponseModel rrm) {
		super(c, resource);
		mContext = c;
		this.rehabResponse = rrm;
		rehabs = new ArrayList<>(rrm.getInsuranceRehabs());
		filteredRehabs.clear();
		filteredRehabs.addAll(rehabs);
		allRehabs = rrm.getRehabs();

		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return rehabs.size();
	}

	@Nullable
	@Override
	public RehabModel getItem(int position) {
		return rehabs.get(position);
	}

	public List<RehabModel> getRehabs() {
		return this.rehabs;
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
//		List<String> insss = rehab.getRehabInsurances();
//		String insurances = "";
//		for(int i = 0; i < insss.size(); i++){
//			insurances = insss.get(i) + (i < insss.size() ? "," : "");
//		}
		holder.tvBuilding.setText(rehab.getTypeName());
		holder.tvDistance.setText(rehab.getDistance() + " miles");
		holder.tvAddress.setText(rehab.getAddress() +", "+ rehab.getState() +" "+ rehab.getZipCode());
		holder.tvTime.setText(rehab.getHours());
		
		if(RehabResponseModel.getTodayRehabTiming(rehab.getRehabDays()) == null){
			holder.tvStatus.setVisibility(View.GONE);
		}else if(rehabResponse.isOpenNow(rehab.getRehabDays())){
			holder.tvStatus.setVisibility(View.VISIBLE);
			holder.tvStatus.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.hours_bg_green));
			holder.tvStatus.setText(R.string.rehab_open_now_);
		}else{
			holder.tvStatus.setVisibility(View.VISIBLE);
			holder.tvStatus.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.yellow_round_corners));
			holder.tvStatus.setText(R.string.rehab_close_now_);
		}
		
//		holder.tvTime.setText(rehab.getRehabDays().get(0).getStartTime());

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

	// applyFilter Class
	public void filter(String charText) {
		charText = charText.toLowerCase();
		rehabs.clear();
//		Log.e("ArrayList Size ","Size "+allRehabs.size());
		for (RehabModel wp : filteredRehabs) {
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

	public void filter(RehaabFilterResultHolder resultHolder) {
		rehabs.clear();
		filteredRehabs.clear();

		//scanning for rehab type
//		List<String> typesOrigion = resultHolder.getRehabType();
//		List<String> types = new ArrayList<String>();
//		for (int i = 0; i < typesOrigion.size(); i++) {
//			String type = typesOrigion.get(i);
//			String typeArray[] = type.split("-");
//			if (typeArray.length > 0) {
//				types.add(typeArray[0].trim());
//				Log.e("Type", typeArray[0]);
//			}
//		}
		// resultHolder.setTypes(types);
		List<String>  typesLocal = Arrays.asList(mContext.getResources().getStringArray(
				R.array.rehab_facility_arr));

		int k = 0;
		for (RehabModel wp : allRehabs) {

			String zipCode = wp.getZipCode();
			double miles = wp.getDistance();

			/*
			 * String splitSpace[] = time.split(" "); String splitColon[] =
			 * splitSpace[0].split(":"); time = splitColon[0] + " " +
			 * splitSpace[1];
			 */
			
			//open now?
			boolean isStatuMatch = RehabResponseModel.isOpenNow(wp.getRehabDays()) == resultHolder.isOpenNow();

			//type
			ArrayList<String> types = (ArrayList<String>) resultHolder.getRehabType();
			
			boolean isTypeMatch;
			
			//if then all others types of
			if(types.contains("Other")){
				isTypeMatch = types.contains(wp.getTypeName()) || TextUtils.isEmpty(wp.getTypeName()) && !typesLocal.contains(wp.getTypeName());
			}else{
				isTypeMatch = resultHolder.isanyType() ? true : types.contains(wp.getTypeName());
			}
			
			
			//insurances
			boolean isInsuracesMatch = resultHolder.isanyInsuranceAccepted();
			for(String insA: wp.getRehabInsurances()){
				if(resultHolder.getInsuranceAccepted().contains(insA)
						|| wp.getPackageName().equalsIgnoreCase(RehabResponseModel.PLATINUM_PACKAGE)){
					isInsuracesMatch = true;
					break;
				}
			}
			
			//ZIP-code
			boolean iszipCode = resultHolder.isanyZipCode() ? true : zipCode
					.equals(resultHolder.getZipCode());
			
			//distance
			boolean isDistance =  true;
			if(resultHolder.is50Distance()){
				isDistance = true;//miles > 50;
			}else{
				isDistance = isMiles(resultHolder.getDistance(), miles);
			} 
			
			
			
			Log.i("Is Zipcode " + k, String.valueOf(iszipCode));
			Log.i("Is Distance " + k, String.valueOf(isDistance));

			if (isStatuMatch && iszipCode && isDistance && isTypeMatch && isInsuracesMatch) {
				rehabs.add(wp);
				filteredRehabs.add(wp);
			}

			k++;
		}

		// Collections.sort(contactList, new MeetingComparator());
		notifyDataSetChanged();
	}
	public boolean isMiles(String mile, double rehabDistance) {
		mile = mile.toLowerCase().replace("miles", "").replace("mile", "").trim();
		/*
		 * Log.e("My Location",
		 * myLocation.getLatitude()+","+myLocation.getLongitude());
		 * 
		 * Log.e("Other Location",
		 * meeting.getLatitude()+","+meeting.getLongitude());
		 */

		long mil = Long.parseLong(mile);

		/*
		 * Location pinLocation = new Location("B");
		 * pinLocation.setLatitude(meeting.getLatitude());
		 * pinLocation.setLongitude(meeting.getLongitude()); long distance =
		 * (long) (myLocation.distanceTo(pinLocation) * 0.000621371192f);
		 * 
		 * Log.e("Distance in Miles is ", distance+"");
		 */

		return rehabDistance <= mil;
	}
//	Location myLocation;
//
//	public void setLocation(Location location) {
//
//		this.myLocation = location;
//
//	}

	public void clearFilters() {
		rehabs.clear();
		rehabs.addAll(rehabResponse.getInsuranceRehabs());
		filteredRehabs.clear();
		filteredRehabs.addAll(rehabs);
		notifyDataSetChanged();
	}

}
