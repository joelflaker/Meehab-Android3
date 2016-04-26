package com.citrusbits.meehab.utils;

import java.net.URL;
import java.util.regex.Pattern;

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

public class ValidationUtils {
	public static boolean validatePhoneNumber(String phoneNumber) {

		return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);

	}

	public final static boolean isValidEmail(CharSequence target) {
		if (TextUtils.isEmpty(target)) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}
	
	public final static boolean isSpecialChar(CharSequence target) {
		if (TextUtils.isEmpty(target)) {
			return false;
		} else {
			return Pattern.compile("[^A-Za-z0-9 ]").matcher(target).find();
		}
	}
	public final static boolean isValidUrl(CharSequence target) {
		if (TextUtils.isEmpty(target)) {
			return false;
		} else {
			return (target.toString().startsWith("https://") || target.toString().startsWith("http://")) && Pattern.compile("[.]?.*[.x][a-z]{2,3}").matcher(target).find();
		}
	}

}
