package test;

import android.test.FlakyTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import dalvik.annotation.TestTarget;

/**
 * Created by Qamar on 3/29/2017.
 */

public class Test {
    public static final long SECONDS_IN_WEEK = 60 * 60 * 24 * 7;
    public static final long SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long SECONDS_IN_HOUR = 60 * 60;
    public static final long SECONDS_IN_MINUTE = 60;

    public static void test(String[] r){
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        TimeZone mTimeZone = cal.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        long hours = TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS);

        String dateTime = "2017-03-29 12:44:40";
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat newFormat = new SimpleDateFormat("EEE, MMM dd");

        String timeAgo;
        try {
            Date date = oldFormat.parse(dateTime);
            date.setHours(date.getHours() + (int) hours);
            Calendar calendar = Calendar.getInstance();
            long millis = calendar.getTimeInMillis() - date.getTime();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

            String dateMade = oldFormat.format(calendar.getTime());

            Log.e("Date Made", dateMade);

            Log.e("S:" + dateTime, "Second: " + seconds);

            if (seconds >= SECONDS_IN_WEEK) {
                timeAgo = newFormat.format(date);
            } else if (seconds >= SECONDS_IN_DAY) {
                long day = seconds / SECONDS_IN_DAY;
                timeAgo = (day == 1 ? (day + " Day ago") : (day + " Days ago"));
            } else if (seconds > SECONDS_IN_HOUR) {
                long hour = seconds / SECONDS_IN_HOUR;
                timeAgo = (hour == 1 ? (hour + " Hour ago")
                        : (hour + " Hours ago"));
            } else if (seconds > SECONDS_IN_MINUTE) {
                long minute = seconds / SECONDS_IN_MINUTE;
                timeAgo = (minute == 1 ? (minute + " Minute ago")
                        : (minute + " Minutes ago"));
            } else if (seconds < SECONDS_IN_MINUTE) {
                timeAgo = seconds < 10 ? "Just Now" : seconds + " Seconds ago";
            } else {
                timeAgo = newFormat.format(date);
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            timeAgo = dateTime;
        }

        System.out.println("timeAgo: "+timeAgo);
    }

}
