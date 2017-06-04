package kr.kro.awesometic.plankhelper.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Awesometic on 2017-04-22.
 */

public class TimeUtils {

    public static int PARSE_ERROR = -1;

    public static String getCurrentDateFormatted() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());

        return simpleDateFormat.format(new Date());
    }

    public static String getCurrentDatetimeFormatted() {
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

    public static String minToTimeFormat(int min) {
        return String.format(Locale.getDefault(), "%02d:%02d", min / 60, min % 60);
    }

    public static List<Integer> getDaysOfCurrentWeek(int startOfTheWeek) {
        List<Integer> resultDays = new ArrayList<Integer>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd", Locale.getDefault());

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.DAY_OF_WEEK, startOfTheWeek);

        for (int i = 0; i < 7; i++) {
            if (i == 0)
                resultDays.add(Integer.parseInt(simpleDateFormat.format(calendar.getTime())));
            else
                resultDays.add(resultDays.get(i - 1) + 1);
        }

        return resultDays;
    }
}
