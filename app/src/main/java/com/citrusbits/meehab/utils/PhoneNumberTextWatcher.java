package com.citrusbits.meehab.utils;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;

/**
 * Created with IntelliJ IDEA.
 * User: roma86
 * Date: 25/11/13
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class PhoneNumberTextWatcher implements TextWatcher {

    /*
    Commented place for different format example
     */

    private boolean isFormatting;
    private boolean deletingHyphen;
    private int hyphenStart;
    private boolean deletingBackward;

    @Override
    public void afterTextChanged(Editable text) {
        if (isFormatting)
            return;

        isFormatting = true;

        // If deleting hyphen, also delete character before or after it
        if (deletingHyphen && hyphenStart > 0) {
            if (deletingBackward) {
                if (hyphenStart - 1 < text.length()) {
                    text.delete(hyphenStart - 1, hyphenStart);
                }
            } else if (hyphenStart < text.length()) {
                text.delete(hyphenStart, hyphenStart + 1);
            }
        }

        //first character should be al8
//        if (text.length() == 1){
//            if(!text.toString().equalsIgnoreCase("8")) {
//                text.replace(0, 1, "8");
//            }
//        }

//        if (text.length() == 3 || text.length() == 7) {
//            text.append('-');
//        }

        if (text.length() == 1 || text.length() == 5 || text.length() == 9 || text.length() == 92) {
            text.append(' ');
        }


        isFormatting = false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (isFormatting)
            return;

        // Make sure user is deleting one char, without a selection
        final int selStart = Selection.getSelectionStart(s);
        final int selEnd = Selection.getSelectionEnd(s);
        if (s.length() > 1 // Can delete another character
                && count == 1 // Deleting only one character
                && after == 0 // Deleting
                && s.charAt(start) == ' ' // a hyphen
                && selStart == selEnd) { // no selection
            deletingHyphen = true;
            hyphenStart = start;
            // Check if the user is deleting forward or backward
            if (selStart == start + 1) {
                deletingBackward = true;
            } else {
                deletingBackward = false;
            }
        } else {
            deletingHyphen = false;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}
