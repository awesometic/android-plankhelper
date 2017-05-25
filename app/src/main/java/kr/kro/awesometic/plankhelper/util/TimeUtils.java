package kr.kro.awesometic.plankhelper.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Awesometic on 2017-04-22.
 */

public class TimeUtils {

    public static int PARSE_ERROR = -1;

    public static String getCurrentDateFormatted() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());

        return simpleDateFormat.format(new Date());
    }

    public static long timeFormatToMSec(String time) {
        // HH:mm:ss.SSS

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());

        Date defaultDate = null;
        Date date = null;
        try {
            defaultDate = simpleDateFormat.parse("1970-01-02 00:00:00.000");
            date = simpleDateFormat.parse("1970-01-02 " + time);

        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return ((date == null) ? PARSE_ERROR : (int) date.getTime() - (int) defaultDate.getTime());
    }

    public static String mSecToTimeFormat(long mSec) {
        long second = (mSec / 1000) % 60;
        long minute = (mSec / (1000 * 60)) % 60;
        long hour = (mSec / (1000 * 60 * 60)) % 24;

        return String.format(Locale.getDefault(), "%02d:%02d:%02d.%03d", hour, minute, second, mSec % 1000);
    }
}
