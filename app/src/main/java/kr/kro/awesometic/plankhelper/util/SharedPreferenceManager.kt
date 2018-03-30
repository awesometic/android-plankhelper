package kr.kro.awesometic.plankhelper.util

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager

import kr.kro.awesometic.plankhelper.R

/**
 * Created by Awesometic on 2017-06-04.
 */

// https://stackoverflow.com/questions/19612993/writing-singleton-class-to-manage-android-sharedpreferences
object SharedPreferenceManager {

    private var mSharedPref: SharedPreferences? = null

    var PREF_SCHEDULE_SWITCH: String
    var PREF_SCHEDULE_START_TIME: String
    var PREF_SCHEDULE_END_TIME: String

    fun init(context: Context) {
        if (mSharedPref == null) {
            // get default shared preference instance to get values set by settings(Preference)
            mSharedPref = PreferenceManager.getDefaultSharedPreferences(context)

            // get preference keys from R.values.preference_keys.xml
            PREF_SCHEDULE_SWITCH = context.resources.getString(R.string.pref_schedule_switch)
            PREF_SCHEDULE_START_TIME = context.resources.getString(R.string.pref_schedule_start_time)
            PREF_SCHEDULE_END_TIME = context.resources.getString(R.string.pref_schedule_end_time)
        }
    }

    fun read(key: String, defValue: String): String? {
        return mSharedPref!!.getString(key, defValue)
    }

    fun write(key: String, value: String) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }

    fun read(key: String, defValue: Boolean): Boolean {
        return mSharedPref!!.getBoolean(key, defValue)
    }

    fun write(key: String, value: Boolean) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putBoolean(key, value)
        prefsEditor.apply()
    }

    fun read(key: String, defValue: Int): Int {
        return mSharedPref!!.getInt(key, defValue)
    }

    fun write(key: String, value: Int?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putInt(key, value!!).apply()
    }
}
