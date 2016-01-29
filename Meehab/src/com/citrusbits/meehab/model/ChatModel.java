package com.citrusbits.meehab.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class ChatModel implements Serializable {

	private int chatId;

	@SerializedName("timestamp")
	@Expose
	private String timeStamp;

	@SerializedName("message")
	@Expose
	private String message;

	@SerializedName("from_send")
	@Expose
	private int fromSend;

	@SerializedName("to_send")
	@Expose
	private int toSend;

	private String displayDateTime;

	private boolean send;

	private boolean checked;

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getFromSend() {
		return fromSend;
	}

	public void setFromSend(int fromSend) {
		this.fromSend = fromSend;
	}

	public int getToSend() {
		return toSend;
	}

	public void setToSend(int toSend) {
		this.toSend = toSend;
	}

	public String getDisplayDateTime() {
		return displayDateTime;
	}

	public void setDisplayDateTime(String displayDateTime) {
		this.displayDateTime = displayDateTime;
	}

	public boolean isSend() {
		return this.send;
	}

	public void setSend(boolean send) {

		this.send = send;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isChecked() {

		return this.checked;
	}

	public void setChatId(int chatId) {
		this.chatId = chatId;
	}

	public int getChatId() {
		return this.chatId;
	}

	public static final long SECONDS_IN_WEEK = 1 * 60 * 60 * 24 * 7;
	public static final long SECONDS_IN_DAY = 1 * 60 * 60 * 24;
	public static final long SECONDS_IN_HOUR = 1 * 60 * 60;
	public static final long SECONDS_IN_MINUTE = 1 * 60;

	public String convertToDisplayFormat(String dateTime,
			long timeZoneOffsetHours) {
		SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		SimpleDateFormat newFormat = new SimpleDateFormat("EEE, MMM dd");

		try {
			Date date = oldFormat.parse(dateTime);
			date.setHours((int) (date.getHours() + timeZoneOffsetHours));
			Calendar calendar = Calendar.getInstance();
			long millis = calendar.getTimeInMillis() - date.getTime();
			long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

			String dateMade = oldFormat.format(calendar.getTime());

			Log.e("Date Made", dateMade);

			Log.e("S:" + dateTime, "Second: " + seconds);

			if (seconds >= SECONDS_IN_WEEK) {
				return newFormat.format(date);
			} else if (seconds >= SECONDS_IN_DAY) {
				long day = seconds / SECONDS_IN_DAY;
				return (day == 1 ? (day + " Day ago") : (day + " Days ago"));
			} else if (seconds > SECONDS_IN_HOUR) {
				long hour = seconds / SECONDS_IN_HOUR;
				return (hour == 1 ? (hour + " Hour ago")
						: (hour + " Hours ago"));
			} else if (seconds > SECONDS_IN_MINUTE) {
				long minute = seconds / SECONDS_IN_MINUTE;
				return (minute == 1 ? (minute + " Minute ago")
						: (minute + " Minutes ago"));
			} else if (seconds < SECONDS_IN_MINUTE) {
				return seconds < 10 ? "Just Now" : seconds + " Seconds ago";
			} else {
				return newFormat.format(date);
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return dateTime;
		}

	}
}
