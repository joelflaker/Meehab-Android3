package com.citrusbits.meehab.utils;

import android.text.InputType;
import android.text.Spanned;
import android.text.method.NumberKeyListener;

/**
 * Created with IntelliJ IDEA.
 * User: roma86
 * Date: 25/11/13
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class PhoneNumberFilter extends NumberKeyListener {

    /*
    Commented place for different format example
     */

    @Override
    public int getInputType() {
        return InputType.TYPE_CLASS_PHONE;
    }

    @Override
    protected char[] getAcceptedChars() {
//        return new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
        return new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        // Don't let phone numbers start with 1
        if (dstart == 0 && source.equals("1"))
            return "";

        if (end > start) {
            String destTxt = dest.toString();
            String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);

            // Phone number must match xxx-xxx-xxxx
            //remember change s.charAt(start) == ' ' to s.charAt(start) == '-' in PhoneNumberTextWatcher
//                if (!resultingTxt.matches ("^\\d{1,1}(\\d{1,1}(\\d{1,1}(\\-(\\d{1,1}(\\d{1,1}(\\d{1,1}(\\-(\\d{1,1}(\\d{1,1}(\\d{1,1}(\\d{1,1}?)?)?)?)?)?)?)?)?)?)?)?")) {
            //Phone number must match x xxx xxx xx xx
            if (!resultingTxt.matches ("^\\d{1,1}(\\ (\\d{1,1}(\\d{1,1}(\\d{1,1}(\\ (\\d{1,1}(\\d{1,1}(\\d{1,1}(\\ (\\d{1,1}(\\d{1,1}(\\ (\\d{1,1}(\\d{1,1}?)?)?)?)?)?)?)?)?)?)?)?)?)?)?")) {
                return "";
            }
        }
        return null;
    }
}
