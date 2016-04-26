package com.citrusbits.meehab.utils;

public class EmailPasswordValidationUtil {

	public static boolean containAlphanumeric(final String str) {
		byte counter = 0;
		boolean checkdigit = false, checkchar = false;
		for (int i = 0; i < str.length() && counter < 2; i++) {
			// If we find a non-digit character we return false.
			if (!checkdigit && Character.isDigit(str.charAt(i))) {
				checkdigit = true;
				counter++;
			}
			String a = String.valueOf(str.charAt(i));
			if (!checkchar && a.matches("[a-z]*")) {
				checkchar = true;
				counter++;
			}
		}
		if (checkdigit && checkchar) {
			return true;
		}
		return false;
	}

	public final static boolean isValidEmail(String target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}
}
