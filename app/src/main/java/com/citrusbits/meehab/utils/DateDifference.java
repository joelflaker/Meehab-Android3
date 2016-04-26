package com.citrusbits.meehab.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateDifference {

	public static int dateDifference(String fromDate, String td) {

		//Specify the data format
		Calendar c1, c2;
		Date startDate, endDate;
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String toDate = "";
		long diff = 0;
		int year, month, day;

		try {

			//Convert to Date
			startDate = df.parse(fromDate.replace("-", ""));
			c1 = Calendar.getInstance();
			//Change to Calendar Date
			c1.setTime(startDate);

			if(td == null) {
				//Convert to Date
				//set to current date of the day
				c2 = Calendar.getInstance();
				year = c2.get(Calendar.YEAR);
				month = c2.get(Calendar.MONTH);
				day = c2.get(Calendar.DAY_OF_MONTH);

				if(month < 9) {
					toDate = "" + year + "0" + (month + 1) + "" + day;

				} else {
					toDate = "" + year + "" + (month + 1) + "" + day;

				}

				endDate = df.parse(toDate);
				//Change to Calendar Date
				c2.setTime(endDate);
			} else {
				//Convert to Date
				endDate = df.parse(td.replace("-", ""));
				c2 = Calendar.getInstance();
				//Change to Calendar Date
				c2.setTime(endDate);
			}

			//get Time in milli seconds
			long ms1 = c1.getTimeInMillis();
			long ms2 = c2.getTimeInMillis();

			//get difference in milli seconds
			diff = ms1 - ms2;

		} catch (ParseException e) {
			e.printStackTrace();
		}

		//Find number of days by dividing the mili seconds
		int diffInDays = (int) (diff / (24 * 60 * 60 * 1000));
		return diffInDays;
		//		System.out.println("Number of days difference is: " + diffInDays);

		//To get number of seconds diff/1000
		//To get number of minutes diff/(1000 * 60)
		//To get number of hours diff/(1000 * 60 * 60)

	}

}