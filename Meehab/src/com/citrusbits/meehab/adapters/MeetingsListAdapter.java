/**
 * 
 */
package com.citrusbits.meehab.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.fragments.FilterResultHolder;
import com.citrusbits.meehab.fragments.FilterResultHolder.FilterTime;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.popup.CodePopup;
import com.citrusbits.meehab.utils.MettingCodes;

/**
 * @author Qamar
 * 
 */
public class MeetingsListAdapter extends ArrayAdapter<MeetingModel> {

	// arraylists
	ArrayList<MeetingModel> meetings;

	ArrayList<MeetingModel> arrayList = new ArrayList<>();

	// context
	Context mContext;

	TextView tvCodes[] = new TextView[10];

	public MeetingsListAdapter(Context c, int resource,
			ArrayList<MeetingModel> m) {
		super(c, resource, m);
		mContext = c;
		meetings = m;
		arrayList.addAll(meetings);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = convertView;
		if (v == null) {
			v = inflater.inflate(R.layout.list_item_meeting, parent, false);
		}
		final MeetingModel m = meetings.get(position);

		TextView txtDayDate = (TextView) v.findViewById(R.id.txtDayDate);

		TextView tvDateHeading = (TextView) v.findViewById(R.id.tvDateHeading);
		tvCodes[0] = (TextView) v.findViewById(R.id.tvCode1);
		tvCodes[1] = (TextView) v.findViewById(R.id.tvCode2);
		tvCodes[2] = (TextView) v.findViewById(R.id.tvCode3);
		tvCodes[3] = (TextView) v.findViewById(R.id.tvCode4);
		tvCodes[4] = (TextView) v.findViewById(R.id.tvCode5);
		tvCodes[5] = (TextView) v.findViewById(R.id.tvCode6);
		tvCodes[6] = (TextView) v.findViewById(R.id.tvCode7);
		tvCodes[7] = (TextView) v.findViewById(R.id.tvCode8);
		tvCodes[8] = (TextView) v.findViewById(R.id.tvCode9);
		tvCodes[9] = (TextView) v.findViewById(R.id.tvCode10);

		TextView txtName = (TextView) v.findViewById(R.id.txtName);
		TextView txtTime = (TextView) v.findViewById(R.id.txtTime);
		TextView txtNumOfReviews = (TextView) v
				.findViewById(R.id.txtNumOfReviews);
		RatingBar rating = (RatingBar) v.findViewById(R.id.rating);
		
		
	//	LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
		//stars.getDrawable(0).setColorFilter(0xFFFF0000, PorterDuff.Mode.SRC_ATOP);
		//stars.getDrawable(1).setColorFilter(0xFFFFFFFF, PorterDuff.Mode.SRC_ATOP);
		//stars.getDrawable(1).setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);
		
		
		//stars.getDrawable(2).setColorFilter(0xFF00FF00, PorterDuff.Mode.SRC_ATOP);
		
		
		TextView txtLocationName = (TextView) v
				.findViewById(R.id.txtLocationName);
		TextView txtAddress = (TextView) v.findViewById(R.id.txtAddress);
		TextView txtDistance = (TextView) v.findViewById(R.id.txtDistance);

		txtDayDate.setText(formateDate(m.getOnDateOrigion()));
		tvDateHeading.setText(m.getOnDate() + " ");
		txtName.setText(m.getName());
		txtTime.setText(m.getOnTime());
		txtNumOfReviews.setText(String.valueOf(m.getReviewsCount())
				+ " Reviews");
		txtLocationName.setText(m.getBuildingType());
		txtAddress.setText(m.getAddress());
		txtDistance.setText(m.getDistanceInMiles() + " miles");

		rating.setRating(m.getReviewsAvg());
		
		//rating.setRating(5);

		tvDateHeading.setVisibility(m.isDateHeaderVisible() ? View.VISIBLE
				: View.GONE);

		String[] codes = m.getCodes().split(",");
		for (int i = 0; i < codes.length; i++) {
			tvCodes[i].setText(codes[i]);
			tvCodes[i].setVisibility(View.VISIBLE);
			tvCodes[i].setTag(codes[i]);
			tvCodes[i].setOnClickListener(onClickListeneer);
		}
		/*
		 * for (String value : codes) {
		 * 
		 * tagsContainer.removeAllViews(); ImageView tag = new
		 * ImageView(getContext());
		 * tag.setImageResource(R.drawable.gray_circle);
		 * tagsContainer.addView(tag); }
		 */
		ToggleButton checkBox = (ToggleButton) v.findViewById(R.id.checkBox);

		if (m.isCheckBoxVisible()) {
			checkBox.setVisibility(View.VISIBLE);
		} else {
			checkBox.setVisibility(View.GONE);
		}

		checkBox.setChecked(m.isChecked());

		return v;
	}

	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		meetings.clear();

		if (charText.length() == 0) {
			meetings.addAll(arrayList);
			// Collections.sort(contactList, new ContactsComparator());

		} else {
			for (MeetingModel wp : arrayList) {
				if (wp.getName().toLowerCase(Locale.getDefault())
						.contains(charText)) {
					meetings.add(wp);

				} else if (wp.getName().toLowerCase(Locale.getDefault())
						.contains(charText)) {
					meetings.add(wp);
				}
			}
		}
		// Collections.sort(contactList, new MeetingComparator());
		notifyDataSetChanged();
	}

	public String formateDate(String date) {
		SimpleDateFormat prevFormate = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat newFormate = new SimpleDateFormat("dd MMM yyyy");
		try {
			Date date2 = prevFormate.parse(date);
			return newFormate.format(date2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return date;
		}
	}

	Location myLocation;

	public void setLocation(Location location) {

		this.myLocation = location;

	}

	public void filter(FilterResultHolder resultHolder) {

		meetings.clear();
		List<String> days = resultHolder.getDays();
		List<FilterTime> timeMapping = resultHolder.applyMapping();

		List<String> typesOrigion = resultHolder.getTypes();
		List<String> types = new ArrayList<String>();
		for (int i = 0; i < typesOrigion.size(); i++) {
			String type = typesOrigion.get(i);
			String typeArray[] = type.split("-");
			if (typeArray.length > 0) {
				types.add(typeArray[0].trim());
				Log.e("Type", typeArray[0]);
			}
		}
		// resultHolder.setTypes(types);

		for (MeetingModel wp : arrayList) {

			String day = "";
			String daysStr[] = wp.getOnDate().split(" ");
			if (daysStr.length > 1) {
				day = daysStr[0];
			}

			Log.e("Day Made ", day);

			// String day = wp.getOnDay();
			// String day = wp.getOnDay();
			String time = wp.getOnTime();
			String zipCode = wp.getZipCode();
			long miles = wp.getDistanceInMiles();
			float revAvg = wp.getReviewsAvg();

			/*
			 * String splitSpace[] = time.split(" "); String splitColon[] =
			 * splitSpace[0].split(":"); time = splitColon[0] + " " +
			 * splitSpace[1];
			 */

			Log.e("Day On", day);

			for (int i = 0; i < days.size(); i++) {
				String ddd = days.get(i);
				Log.e("ddd", ddd);
			}

			boolean isDay = resultHolder.getAnyDay() ? true : days
					.contains(day);
			boolean isTime = resultHolder.getAnyTime() ? true : filterTime(
					timeMapping, time);

			boolean iszipCode = resultHolder.getAnyCode() ? true : zipCode
					.equals(resultHolder.getZipCode());
			boolean isDistance = resultHolder.getAnyDistance() ? true
					: isMiles(resultHolder.getDistance(), wp);
			boolean isStar = resultHolder.getAnyStar() ? true : isStar(
					resultHolder.getRating(), revAvg);

			boolean isFavSatisfy = resultHolder.isFavourites() == wp
					.isFavourite();

			boolean isCode = resultHolder.getAnyType() ? true : satisfyCode(
					types, wp);

			Log.e("isTime", String.valueOf(isTime));

			if (isDay && isTime && iszipCode && isDistance && isStar
					&& isFavSatisfy && isCode) {
				
				Log.e("Contain Days ", "Yes");
				meetings.add(wp);

			} else {
				Log.e("Contain Days ", "No");
			}
		}

		// Collections.sort(contactList, new MeetingComparator());
		notifyDataSetChanged();
	}

	public boolean satisfyCode(List<String> types, MeetingModel wp) {

		String codes = wp.getCodes();
		String codeArray[] = codes.split(",");
		for (String code : codeArray) {
			if (types.contains(code)) {
				return true;
			}
		}

		return false;

	}

	public boolean isStar(String star, float revAvg) {
		star = star.replace(">", "").toLowerCase().replace("stars", "")
				.replace("<", "").replace("star", "").trim();
		Log.e("SSStar", star);
		try {
			int sstar = Integer.parseInt(star);
			return revAvg <= sstar;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	public boolean isMiles(String mile, MeetingModel meeting) {
		mile = mile.replace("miles", "").replace("mile", "").trim();

		/*
		 * Log.e("My Location",
		 * myLocation.getLatitude()+","+myLocation.getLongitude());
		 * 
		 * Log.e("Other Location",
		 * meeting.getLatitude()+","+meeting.getLongitude());
		 */

		long mil = Long.parseLong(mile);
		long distance = meeting.getDistanceInMiles();

		/*
		 * Location pinLocation = new Location("B");
		 * pinLocation.setLatitude(meeting.getLatitude());
		 * pinLocation.setLongitude(meeting.getLongitude()); long distance =
		 * (long) (myLocation.distanceTo(pinLocation) * 0.000621371192f);
		 * 
		 * Log.e("Distance in Miles is ", distance+"");
		 */

		return distance <= mil;
	}

	public boolean filterTime(List<FilterTime> filters, String time) {

		try {
			SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
			final Date dateObj = _12HourSDF.parse(time);
			Calendar calendar = Calendar.getInstance();

			calendar.set(Calendar.HOUR_OF_DAY, dateObj.getHours());
			calendar.set(Calendar.MINUTE, dateObj.getMinutes());
			calendar.set(Calendar.SECOND, 0);

			// Log.e("Calendar Hour:Minute ",calendar.get(Calendar.YEAR)+":"+calendar.get(Calendar.MONTH)+":"+calendar.get(Calendar.DAY_OF_MONTH)+"****"+
			// calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));

			for (FilterTime filter : filters) {
				Calendar start = filter.getStartTime();
				Calendar end = filter.getEndTime();

				// Log.e("Calendar start Hour:Minute ",
				// start.get(Calendar.YEAR)+":"+start.get(Calendar.MONTH)+":"+start.get(Calendar.DAY_OF_MONTH)+"****"+start.get(Calendar.HOUR_OF_DAY)+":"+start.get(Calendar.MINUTE));
				// Log.e("Calendar end Hour:Minute ",end.get(Calendar.YEAR)+":"+end.get(Calendar.MONTH)+":"+end.get(Calendar.DAY_OF_MONTH)+"****"+
				// end.get(Calendar.HOUR_OF_DAY)+":"+end.get(Calendar.MINUTE));

				if (calendar.compareTo(start) == 0
						|| calendar.compareTo(end) == 0) {
					return true;
				}

				if (calendar.after(start) && calendar.before(end)) {
					return true;
				}

			}

		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean containArrayList(List<String> values, String value) {

		for (int i = 0; i < values.size(); i++) {
			if (values.get(i).toLowerCase().equals(value.toLowerCase())) {

				return true;
			}
		}
		return false;

	}

	OnClickListener onClickListeneer = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String code = (String) v.getTag();
			new CodePopup(mContext, MettingCodes.meetingValuesFromCode(code))
					.show(v);
		}
	};

	private class MeetingComparator implements Comparator<MeetingModel> {

		@Override
		public int compare(MeetingModel e1, MeetingModel e2) {
			if (e1.getName().compareTo(e2.getName()) > 0) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}
