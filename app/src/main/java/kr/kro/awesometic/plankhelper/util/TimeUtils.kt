package kr.kro.awesometic.plankhelper.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Created by Awesometic on 2017-04-22.
 */

object TimeUtils {

    var PARSE_ERROR = -1

    val currentDateFormatted: String
        get() {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            simpleDateFormat.timeZone = TimeZone.getDefault()

            return simpleDateFormat.format(Date())
        }

    val currentDatetimeFormatted: String
        get() {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            simpleDateFormat.timeZone = TimeZone.getDefault()

            return simpleDateFormat.format(Date())
        }

    fun timeFormatToMSec(time: String): Long {
        // HH:mm:ss.SSS

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getDefault()

        var defaultDate: Date? = null
        var date: Date? = null
        try {
            defaultDate = simpleDateFormat.parse("1970-01-02 00:00:00.000")
            date = simpleDateFormat.parse("1970-01-02 $time")

        } catch (ex: ParseException) {
            ex.printStackTrace()
        }

        return (if (date == null) PARSE_ERROR else date.time.toInt() - defaultDate!!.time.toInt()).toLong()
    }

    fun mSecToTimeFormat(mSec: Long): String {
        val second = mSec / 1000 % 60
        val minute = mSec / (1000 * 60) % 60
        val hour = mSec / (1000 * 60 * 60) % 24

        return String.format(Locale.getDefault(), "%02d:%02d:%02d.%03d", hour, minute, second, mSec % 1000)
    }

    fun secToTimeFormat(sec: Int): String {
        val second = sec % 60
        val minute = sec % (60 * 60) / 60
        val hour = sec / (60 * 60)

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second)
    }

    fun minToTimeFormat(min: Int): String {
        return String.format(Locale.getDefault(), "%02d:%02d", min / 60, min % 60)
    }

    fun getDaysOfCurrentWeek(startOfTheWeek: Int): List<Int> {
        val resultDays = ArrayList<Int>()
        val simpleDateFormat = SimpleDateFormat("dd", Locale.getDefault())

        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.set(Calendar.DAY_OF_WEEK, startOfTheWeek)

        for (i in 0..6) {
            if (i == 0)
                resultDays.add(Integer.parseInt(simpleDateFormat.format(calendar.time)))
            else
                resultDays.add(resultDays[i - 1] + 1)
        }

        return resultDays
    }
}
