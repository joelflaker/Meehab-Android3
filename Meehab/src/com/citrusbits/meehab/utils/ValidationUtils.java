package com.citrusbits.meehab.utils;

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

}
