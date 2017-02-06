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
import com.citrusbits.meehab.ui.fragments.FilterResultHolder;
import com.citrusbits.meehab.ui.fragments.FilterResultHolder.FilterTime;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.ui.popup.CodePopup;
import com.citrusbits.meehab.utils.MettingCodes;

/**
 * @author Yasir
 * 
 */
public class MeetingsListAdapter extends ArrayAdapter<MeetingModel> {

	public static final int MAX_CODE_SIZE = 8;

	// arraylists
	ArrayList<MeetingModel> meetings;

	ArrayList<MeetingModel> arrayList = new ArrayList<>();

	// context
	Context mContext;

	TextView tvCodes[] = new TextView[8];

	public MeetingsListAdapter(Context c, int resource,
			ArrayList<MeetingModel> m) {
		super(c, resource, m);
		mContext = c;
		meetings = m;
		arrayList.clear();
		arrayList.addAll(meetings);
	}

	public void setMeeting(int position, MeetingModel meeting) {
		this.arrayList.set(position, meeting);
		this.notifyDataSetChanged();
	}

	public ArrayList<MeetingModel> getMeetings() {
		return this.meetings;
	}

	public ArrayList<MeetingModel> getMeetingCache() {
		return this.arrayList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = convertView;
		if (v == null) {
			v = inflater.inflate(R.layout.list_item_meeting, parent, false);
		}
		final MeetingModel m = meetings.get(position);

		final TextView txtDayDate = (TextView) v.findViewById(R.id.txtDayDate);

		final TextView tvDateHeading = (TextView) v.findViewById(R.id.tvDateHeading);
		tvCodes[0] = (TextView) v.findViewById(R.id.tvCode1);
		tvCodes[1] = (TextView) v.findViewById(R.id.tvCode2);
		tvCodes[2] = (TextView) v.findViewById(R.id.tvCode3);
		tvCodes[3] = (TextView) v.findViewById(R.id.tvCode4);
		tvCodes[4] = (TextView) v.findViewById(R.id.tvCode5);
		tvCodes[5] = (TextView) v.findViewById(R.id.tvCode6);
		tvCodes[6] = (TextView) v.findViewById(R.id.tvCode7);
		tvCodes[7] = (TextView) v.findViewById(R.id.tvCode8);

		final TextView txtName = (TextView) v.findViewById(R.id.txtName);
		final TextView txtTime = (TextView) v.findViewById(R.id.txtTime);
		final TextView txtNumOfReviews = (TextView) v
				.findViewById(R.id.txtNumOfReviews);
		final RatingBar rating = (RatingBar) v.findViewById(R.id.rating);

		// LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
		// stars.getDrawable(0).setColorFilter(0xFFFF0000,
		// PorterDuff.Mode.SRC_ATOP);
		// stars.getDrawable(1).setColorFilter(0xFFFFFFFF,
		// PorterDuff.Mode.SRC_ATOP);
		// stars.getDrawable(1).setColorFilter(0xFFFFFFFF,
		// PorterDuff.Mode.MULTIPLY);

		// stars.getDrawable(2).setColorFilter(0xFF00FF00,
		// PorterDuff.Mode.SRC_ATOP);

		final TextView txtLocationName = (TextView) v
				.findViewById(R.id.txtLocationName);
		final TextView txtAddress = (TextView) v.findViewById(R.id.txtAddress);
		final TextView txtDistance = (TextView) v.findViewById(R.id.txtDistance);

		txtDayDate.setText(formateDate(m.getOnDateOrigion()));
		tvDateHeading.setText(m.getOnDate() + " ");
		txtName.setText(m.getName());
		// txtTime.setText(m.getOnTime());
		txtTime.setText(m.getNearestTime());
		txtNumOfReviews.setText(String.valueOf(m.getReviewsCount())
				+ " Reviews");
		txtLocationName.setText(m.getBuildingType());
		txtAddress.setText(m.getAddress());
		txtDistance.setText(m.getDistanceInMiles() + " miles");

		rating.setRating(m.getReviewsAvg());

		// rating.setRating(5);

		tvDateHeading.setVisibility(m.isDateHeaderVisible() ? View.VISIBLE
				: View.GONE);

		Log.i(m.getName(), m.getCodes());

		String[] codes = m.getCodes().split(",");
		int i = 0;
		for (; i < codes.length && i < MAX_CODE_SIZE; i++) {
			tvCodes[i].setText(codes[i]);
			tvCodes[i].setVisibility(View.VISIBLE);
			tvCodes[i].setTag(codes[i]);
			tvCodes[i].setOnClickListener(onClickListeneer);
		}

		for (; i < tvCodes.length; i++) {
			tvCodes[i].setVisibility(View.GONE);
		}
		/*
		 * for (String value : codes) {
		 * 
		 * tagsContainer.removeAllViews(); ImageView tag = new
		 * ImageView(getContext());
		 * tag.setImageResource(R.drawable.gray_circle);
		 * tagsContainer.addView(tag); }
		 */
		final ToggleButton checkBox = (ToggleButton) v.findViewById(R.id.checkBox);

		if (m.isCheckBoxVisible()) {
			checkBox.setVisibility(View.VISIBLE);
		} else {
			checkBox.setVisibility(View.GONE);
		}

		checkBox.setChecked(m.isChecked());

		return v;
	}

	// applyFilter Class
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		meetings.clear();

		/*
		 * if (charText.length() == 0) { meetings.addAll(allRehabs); //
		 * Collections.sort(contactList, new ContactsComparator());
		 * 
		 * } else {
		 */

		String prevDate = "";

		for (MeetingModel wp : arrayList) {
			if (wp.getName().toLowerCase(Locale.getDefault())
					.contains(charText)) {
				meetings.add(wp);

				if (!prevDate.equals(wp.getOnDateOrigion())) {
					wp.setDateHeaderVisibility(true);
					prevDate = wp.getOnDateOrigion();
				} else {
					wp.setDateHeaderVisibility(false);
				}

			}/*
			 * else if (wp.getName().toLowerCase(Locale.getDefault())
			 * .contains(charText)) { meetings.add(wp); }
			 */
		}
		// }
		// Collections.sort(contactList, new MeetingComparator());
		notifyDataSetChanged();
	}

	public String formateDate(String date) {
		final SimpleDateFormat prevFormate = new SimpleDateFormat("dd/MM/yyyy");
		final SimpleDateFormat newFormate = new SimpleDateFormat("MMM dd yyyy");
		try {
			Date date2 = prevFormate.parse(date);
			return newFormate.format(date2);
		} catch (Exception e) {
			e.printStackTrace();
			return date;
		}
	}

//	Location myLocation;
//
//	public void setLocation(Location location) {
//
//		this.myLocation = location;
//
//	}

	public void filter(FilterResultHolder resultHolder) {

		meetings.clear();
		final List<String> days = resultHolder.getDays();
		final List<FilterTime> timeMapping = resultHolder.applyMapping();

		final List<String> typesOrigion = resultHolder.getTypes();
		final List<String> types = new ArrayList<String>();
		for (int i = 0; i < typesOrigion.size(); i++) {
			String type = typesOrigion.get(i);
			String typeArray[] = type.split("-");
			if (typeArray.length > 0) {
				types.add(typeArray[0].trim());
				Log.e("Type", typeArray[0]);
			}
		}
		// resultHolder.setTypes(types);
		int k = 0;
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
			double miles = wp.getDistanceInMiles();
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

			boolean isDay = resultHolder.getAnyDay() ? true : satisfyDays(days,
					wp);
			boolean isTime = resultHolder.getAnyTime() ? true : filterTime(
					timeMapping, time);

			boolean iszipCode = resultHolder.getAnyCode() ? true : zipCode
					.equals(resultHolder.getZipCode());
			boolean isDistance = resultHolder.getAnyDistance() ? true
					: isMiles(resultHolder.getDistance(), wp);
			boolean isStar = resultHolder.getAnyStar() ? (revAvg >= resultHolder.getRating()) : true;

			boolean isFavSatisfy = !resultHolder.isFavourites() ? true
					: resultHolder.isFavourites() == wp.isFavourite();

			boolean isCode = resultHolder.getAnyType() ? true : satisfyCode(
					types, wp);

			String log = "->Is Day:" + String.valueOf(isDay)
			+ "\nIs Time " + String.valueOf(isTime)
			+ "\nIs Zipcode " + String.valueOf(iszipCode)
					+ "\nIs Distance " + String.valueOf(isDistance)
					+ "\nIs Star " + String.valueOf(isStar)
					+ "\nIs Fave " + String.valueOf(isFavSatisfy)
					+ "\nIs Code " + String.valueOf(isCode);
			Log.i("MeetingFilter","Meeting name:" + String.valueOf(wp.getName()) + log);

			if (isDay && isTime && iszipCode && isDistance && isStar
					&& isFavSatisfy && isCode) {

				Log.e("Contain Days ", "Yes");
				meetings.add(wp);

			} else {
				Log.e("Contain Days ", "No");
			}

			k = k + 1;
		}

		String prevDate = "";
		for (int i = 0; i < meetings.size(); i++) {
			MeetingModel m = meetings.get(i);
			if (m.getOnDateOrigion() == null) {
				continue;
			}
			if (!prevDate.equals(m.getOnDateOrigion())) {
				m.setDateHeaderVisibility(true);
				prevDate = m.getOnDateOrigion();
			} else {
				m.setDateHeaderVisibility(false);
			}
		}

		// Collections.sort(contactList, new MeetingComparator());
		notifyDataSetChanged();
	}

	public boolean satisfyCode(List<String> types, MeetingModel wp) {

		final String codes = wp.getCodes();
		Log.e("Types ", types.toString());
		Log.e("code", codes);
		String codeArray[] = codes.split(",");
		for (String code : codeArray) {
			if (types.contains(code)) {
				return true;
			}
		}

		return false;

	}

	public boolean satisfyDays(List<String> daysList, MeetingModel wp) {

		String days = wp.getOnDay();

		String dayArray[] = days.split(",");
		for (String day : dayArray) {
			if (daysList.contains(day)) {
				return true;
			}
		}

		return false;

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

		long mil = 50;
		try {
			mil = Long.parseLong(mile);
		}catch (Exception e){
			e.printStackTrace();
		}
		double distance = meeting.getDistanceInMiles();

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

	public boolean filterTime(List<FilterTime> filters, String times) {

		final String timeArray[] = times.split(",");

		for (int k = 0; k < timeArray.length; k++) {
			String time = timeArray[k];
			try {
				SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
				SimpleDateFormat dateHourFormate = new SimpleDateFormat(
						"yyy-MMM-dd hh:mm a");
				final Date dateObj = _12HourSDF.parse(time);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dateObj);
//				calendar.set(Calendar.HOUR_OF_DAY, dateObj.getHours());
//				calendar.set(Calendar.MINUTE, dateObj.getMinutes());
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);

				Log.i("Origional Calendar",
						dateHourFormate.format(calendar.getTime()));

				for (FilterTime filter : filters) {
					Calendar start = filter.getStartTime();

					Calendar end = filter.getEndTime();

					start.set(Calendar.SECOND, 0);
					end.set(Calendar.SECOND, 0);
					start.set(Calendar.MILLISECOND, 0);
					end.set(Calendar.MILLISECOND, 0);

					Log.i("Start Calendar",
							dateHourFormate.format(start.getTime()));
					Log.i("End Calendar", dateHourFormate.format(end.getTime()));

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
			final String code = (String) v.getTag();
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
