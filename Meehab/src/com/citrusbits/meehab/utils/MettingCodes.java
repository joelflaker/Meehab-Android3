package com.citrusbits.meehab.utils;

import java.util.HashMap;

public class MettingCodes {
	public static final HashMap<String, String> maps = new HashMap<>();

	public static HashMap<String, String> getCodes() {

		if (maps.isEmpty()) {
			maps.put("B", "Beginners");
			maps.put("BS", "Book Study");
			maps.put("C", "Closed Meeting (Alcoholics Only)");
			maps.put("CC", "Child Care Availabe");
			maps.put("D", "Sign Language Available");
			maps.put("G", "Lesbian, Gay, Bisexual, Transgender");
			maps.put("H", "Handicapped Accessible");
			maps.put("M", "Men Stag");
			maps.put("O", "Open");
			maps.put("P", "Participation");
			maps.put("S", "Speakers");
			maps.put("W", "Women Stag");
			maps.put("Y", "Young People");
		}
		return maps;

	}

	public static String meetingValuesFromCode(String code) {
		return getCodes().get(code);
	}
}
