package com.citrusbits.meehab.utils;

import java.util.Date;

public class TimeDifferenceUtil {
	int years;
	int months;
	int days;
	int hours;
	int minutes;
	int seconds;
	String differenceString;

	public TimeDifferenceUtil(Date curdate, Date olddate) {

		float diff = curdate.getTime() - olddate.getTime();
		if (diff >= 0) {
			int yearDiff = Math
					.round((diff / (365l * 2592000000f)) >= 1 ? (diff / (365l * 2592000000f))
							: 0);
			if (yearDiff > 0) {
				years = yearDiff;
				setDifferenceString(years + (years == 1 ? " year" : " years")
						+ " ago");
			} else {
				int monthDiff = Math
						.round((diff / 2592000000f) >= 1 ? (diff / 2592000000f)
								: 0);
				if (monthDiff > 0) {
					if (monthDiff > 11)
						monthDiff = 11;

					months = monthDiff;
					setDifferenceString(months
							+ (months == 1 ? " month" : " months") + " ago");
				} else {
					int dayDiff = Math
							.round((diff / (86400000f)) >= 1 ? (diff / (86400000f))
									: 0);
					if (dayDiff > 0) {
						days = dayDiff;
						if (days == 30)
							days = 29;
						setDifferenceString(days
								+ (days == 1 ? " day" : " days") + " ago");
					} else {
						int hourDiff = Math
								.round((diff / (3600000f)) >= 1 ? (diff / (3600000f))
										: 0);
						if (hourDiff > 0) {
							hours = hourDiff;
							setDifferenceString(hours
									+ (hours == 1 ? " hour" : " hours")
									+ " ago");
						} else {
							int minuteDiff = Math
									.round((diff / (60000f)) >= 1 ? (diff / (60000f))
											: 0);
							if (minuteDiff > 0) {
								minutes = minuteDiff;
								setDifferenceString(minutes
										+ (minutes == 1 ? " minute"
												: " minutes") + " ago");
							} else {
								int secondDiff = Math
										.round((diff / (1000f)) >= 1 ? (diff / (1000f))
												: 0);
								if (secondDiff > 0)
									seconds = secondDiff;
								else
									seconds = 1;
								setDifferenceString(seconds
										+ (seconds == 1 ? " second"
												: " seconds") + " ago");
							}
						}
					}

				}
			}

		}

	}

	public String getDifferenceString() {
		return differenceString;
	}

	public void setDifferenceString(String differenceString) {
		this.differenceString = differenceString;
	}

	public int getYears() {
		return years;
	}

	public void setYears(int years) {
		this.years = years;
	}

	public int getMonths() {
		return months;
	}

	public void setMonths(int months) {
		this.months = months;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

}