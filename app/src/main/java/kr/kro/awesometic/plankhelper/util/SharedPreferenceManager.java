package kr.kro.awesometic.plankhelper.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import kr.kro.awesometic.plankhelper.R;

/**
 * Created by Awesometic on 2017-06-04.
 */

// https://stackoverflow.com/questions/19612993/writing-singleton-class-to-manage-android-sharedpreferences
public class SharedPreferenceManager {

    private static SharedPreferences mSharedPref;

    public static String PREF_SCHEDULE_SWITCH;
    public static String PREF_SCHEDULE_START_TIME;
    public static String PREF_SCHEDULE_END_TIME;
    
    private SharedPreferenceManager() {

    }

    public static void init(Context context) {
        if(mSharedPref == null) {
            // get default shared preference instance to get values set by settings(Preference)
            mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);

            // get preference keys from R.values.preference_keys.xml
            PREF_SCHEDULE_SWITCH = context.getResources().getString(R.string.pref_schedule_switch);
            PREF_SCHEDULE_START_TIME = context.getResources().getString(R.string.pref_schedule_start_time);
            PREF_SCHEDULE_END_TIME = context.getResources().getString(R.string.pref_schedule_end_time);
        }
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).apply();
    }
}
