package com.citrusbits.meehab.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class StringEncoder {
	public static String Encode(String toEncode) {
		String encoded = toEncode;
		try {
			encoded = URLEncoder.encode(toEncode, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encoded;
	}
}
