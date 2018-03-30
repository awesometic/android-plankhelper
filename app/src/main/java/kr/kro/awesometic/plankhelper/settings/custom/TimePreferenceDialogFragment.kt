package kr.kro.awesometic.plankhelper.settings.custom

/**
 * Created by Awesometic on 2017-06-03.
 */

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.support.v7.preference.DialogPreference
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.text.format.DateFormat
import android.view.View
import android.widget.TimePicker
import android.widget.Toast

import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.util.SharedPreferenceManager

/**
 * The Dialog for the [TimePreference].
 *
 * @author Jakob Ulbrich
 */
class TimePreferenceDialogFragment : PreferenceDialogFragmentCompat(), DialogInterface.OnClickListener {

    /**
     * The TimePicker widget
     */
    private var mTimePicker: TimePicker? = null

    /**
     * {@inheritDoc}
     */
    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        mTimePicker = view.findViewById(R.id.settings_pref_dialog_widget_timePicker) as TimePicker

        // Exception: There is no TimePicker with the id 'edit' in the dialog.
        if (mTimePicker == null) {
            throw IllegalStateException("Dialog view must contain a TimePicker with id 'edit'")
        }

        // Get the time from the related Preference
        var minutesAfterMidnight: Int? = null
        val preference = preference
        if (preference is TimePreference) {
            minutesAfterMidnight = preference.time
        }

        // Set the time to the TimePicker
        if (minutesAfterMidnight != null) {
            val hours = minutesAfterMidnight / 60
            val minutes = minutesAfterMidnight % 60
            val is24hour = DateFormat.is24HourFormat(context)

            mTimePicker!!.setIs24HourView(is24hour)
            mTimePicker!!.currentHour = hours
            mTimePicker!!.currentMinute = minutes
        }
    }

    /**
     * Called when the Dialog is closed.
     *
     * @param positiveResult Whether the Dialog was accepted or canceled.
     */
    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {

        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        val preference = preference
        if (preference is TimePreference) {
            val timePreference = getPreference() as TimePreference
            val dialogTitleStartTime = context.resources.getString(R.string.settings_schedule_start_time_title)
            val dialogTitleEndTime = context.resources.getString(R.string.settings_schedule_end_time_title)

            val previousStartTimeMin = SharedPreferenceManager.read(SharedPreferenceManager.PREF_SCHEDULE_START_TIME, 0)
            val previousEndTimeMin = SharedPreferenceManager.read(SharedPreferenceManager.PREF_SCHEDULE_END_TIME, 0)

            val hours: Int
            val minutes: Int
            if (Build.VERSION.SDK_INT >= 23) {
                hours = mTimePicker!!.hour
                minutes = mTimePicker!!.minute
            } else {
                hours = mTimePicker!!.currentHour
                minutes = mTimePicker!!.currentMinute
            }

            val minutesAfterMidnight = hours * 60 + minutes

            if (which == dialog!!.BUTTON_POSITIVE
                    && timePreference.dialogTitle == dialogTitleEndTime
                    && previousStartTimeMin > minutesAfterMidnight) {

                Toast.makeText(
                        context,
                        context.resources.getString(R.string.settings_error_end_time_invalid),
                        Toast.LENGTH_LONG
                ).show()

            } else if (which == dialog.BUTTON_POSITIVE
                    && timePreference.dialogTitle == dialogTitleStartTime
                    && previousEndTimeMin > minutesAfterMidnight) {

                Toast.makeText(
                        context,
                        context.resources.getString(R.string.settings_error_start_earlier_than_end),
                        Toast.LENGTH_LONG
                ).show()

                val endTimePreference = timePreference.preferenceManager.findPreference(SharedPreferenceManager.PREF_SCHEDULE_END_TIME) as TimePreference
                if (endTimePreference.callChangeListener(minutesAfterMidnight + 60)) {
                    endTimePreference.time = minutesAfterMidnight + 60
                }

                if (timePreference.callChangeListener(minutesAfterMidnight)) {
                    timePreference.time = minutesAfterMidnight
                }
            } else {
                if (timePreference.callChangeListener(minutesAfterMidnight)) {
                    timePreference.time = minutesAfterMidnight
                }
            }
        }

        super.onClick(dialog, which)
    }

    companion object {

        /**
         * Creates a new Instance of the TimePreferenceDialogFragment and stores the key of the
         * related Preference
         *
         * @param key The key of the related Preference
         * @return A new Instance of the TimePreferenceDialogFragment
         */
        fun newInstance(key: String): TimePreferenceDialogFragment {
            val fragment = TimePreferenceDialogFragment()
            val b = Bundle(1)
            b.putString(PreferenceDialogFragmentCompat.ARG_KEY, key)
            fragment.arguments = b

            return fragment
        }
    }
}
