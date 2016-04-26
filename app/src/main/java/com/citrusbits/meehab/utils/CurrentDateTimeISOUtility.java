package com.citrusbits.meehab.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentDateTimeISOUtility {
	public static String getCurrentDateTimeISO() {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZZ");
		Date now = new Date();
		String timestamp = sdf.format(now.getTime());
		return timestamp;
	}
}
