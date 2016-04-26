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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.TextView;

import com.citrusbits.meehab.R;
import com.citrusbits.meehab.fragments.FilterResultHolder.FilterTime;
import com.citrusbits.meehab.model.MeetingModel;
import com.citrusbits.meehab.popup.CodePopup;
import com.citrusbits.meehab.utils.MettingCodes;

public class FavMeetingListAdapter extends ArrayAdapter<MeetingModel> {
	
	public static final int MAX_CODE_SIZE = 8;

	// arraylists
	ArrayList<MeetingModel> meetings;

	ArrayList<MeetingModel> arrayList = new ArrayList<>();

	// context
	Context mContext;

	TextView tvCodes[] = new TextView[8];

	private boolean edit;

	public FavMeetingListAdapter(Context c, int resource,
			ArrayList<MeetingModel> m) {
		super(c, resource, m);
		mContext = c;
		meetings = m;
		arrayList.addAll(meetings);
	}

	public void setMeeting(int position, MeetingModel meeting) {
		this.arrayList.set(position, meeting);
		this.notifyDataSetChanged();
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public boolean getEdit() {
		return this.edit;
	}
	
	public ArrayList<MeetingModel> getMeetingArray() {
		return this.arrayList;
	}
	

	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = convertView;
		if (v == null) {
			v = inflater.inflate(R.layout.fav_list_item_meeting, parent, false);
		}
		final MeetingModel m = meetings.get(position);

		TextView txtDayDate = (TextView) v.findViewById(R.id.txtDayDate);
		CheckBox cbMeeting = (CheckBox) v.findViewById(R.id.cbMeeting);

		TextView tvDateHeading = (TextView) v.findViewById(R.id.tvDateHeading);
		tvCodes[0] = (TextView) v.findViewById(R.id.tvCode1);
		tvCodes[1] = (TextView) v.findViewById(R.id.tvCode2);
		tvCodes[2] = (TextView) v.findViewById(R.id.tvCode3);
		tvCodes[3] = (TextView) v.findViewById(R.id.tvCode4);
		tvCodes[4] = (TextView) v.findViewById(R.id.tvCode5);
		tvCodes[5] = (TextView) v.findViewById(R.id.tvCode6);
		tvCodes[6] = (TextView) v.findViewById(R.id.tvCode7);
		tvCodes[7] = (TextView) v.findViewById(R.id.tvCode8);
		

		TextView txtName = (TextView) v.findViewById(R.id.txtName);
		TextView txtTime = (TextView) v.findViewById(R.id.txtTime);
		TextView txtNumOfReviews = (TextView) v
				.findViewById(R.id.txtNumOfReviews);
		RatingBar rating = (RatingBar) v.findViewById(R.id.rating);

		TextView txtLocationName = (TextView) v
				.findViewById(R.id.txtLocationName);
		TextView txtAddress = (TextView) v.findViewById(R.id.txtAddress);
		TextView txtDistance = (TextView) v.findViewById(R.id.txtDistance);

		txtDayDate.setText(formateDate(m.getOnDateOrigion()));
		tvDateHeading.setText(m.getOnDate() + " ");
		txtName.setText(m.getName());
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

		cbMeeting.setVisibility(edit ? View.VISIBLE : View.GONE);

		cbMeeting.setChecked(m.isChecked());

		/*cbMeeting.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				m.setChecked(isChecked);
				arrayList.get(position).setChecked(isChecked);
			}
		});*/

		return v;
	}

	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		meetings.clear();
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
			e.printStackTrace();
			return date;
		}
	}

	Location myLocation;

	public void setLocation(Location location) {

		this.myLocation = location;

	}

	public boolean satisfyCode(List<String> types, MeetingModel wp) {

		String codes = wp.getCodes();
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

	public boolean filterTime(List<FilterTime> filters, String time) {

		if (time.equals("nomeeting")) {
			return false;
		}

		try {
			SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
			SimpleDateFormat dateHourFormate = new SimpleDateFormat(
					"yyy-MMM-dd hh:mm a");
			final Date dateObj = _12HourSDF.parse(time);
			Calendar calendar = Calendar.getInstance();

			calendar.set(Calendar.HOUR_OF_DAY, dateObj.getHours());
			calendar.set(Calendar.MINUTE, dateObj.getMinutes());
			calendar.set(Calendar.SECOND, 0);

			Log.i("Origional Calendar",
					dateHourFormate.format(calendar.getTime()));

			for (FilterTime filter : filters) {
				Calendar start = filter.getStartTime();

				Calendar end = filter.getEndTime();

				start.set(Calendar.SECOND, 0);
				end.set(Calendar.SECOND, 0);

				Log.i("Start Calendar", dateHourFormate.format(start.getTime()));
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
