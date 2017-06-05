package kr.kro.awesometic.plankhelper.settings.time;

/**
 * Created by Awesometic on 2017-06-03.
 */

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.util.SharedPreferenceManager;

/**
 * The Dialog for the {@link TimePreference}.
 *
 * @author Jakob Ulbrich
 */
public class TimePreferenceDialogFragment extends PreferenceDialogFragmentCompat
        implements DialogInterface.OnClickListener {

    /**
     * The TimePicker widget
     */
    private TimePicker mTimePicker;

    /**
     * Creates a new Instance of the TimePreferenceDialogFragment and stores the key of the
     * related Preference
     *
     * @param key The key of the related Preference
     * @return A new Instance of the TimePreferenceDialogFragment
     */
    public static TimePreferenceDialogFragment newInstance(String key) {
        final TimePreferenceDialogFragment
                fragment = new TimePreferenceDialogFragment();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);

        return fragment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mTimePicker = (TimePicker) view.findViewById(R.id.settings_pref_dialog_widget_timePicker);

        // Exception: There is no TimePicker with the id 'edit' in the dialog.
        if (mTimePicker == null) {
            throw new IllegalStateException("Dialog view must contain a TimePicker with id 'edit'");
        }

        // Get the time from the related Preference
        Integer minutesAfterMidnight = null;
        DialogPreference preference = getPreference();
        if (preference instanceof TimePreference) {
            minutesAfterMidnight = ((TimePreference) preference).getTime();
        }

        // Set the time to the TimePicker
        if (minutesAfterMidnight != null) {
            int hours = minutesAfterMidnight / 60;
            int minutes = minutesAfterMidnight % 60;
            boolean is24hour = DateFormat.is24HourFormat(getContext());

            mTimePicker.setIs24HourView(is24hour);
            mTimePicker.setCurrentHour(hours);
            mTimePicker.setCurrentMinute(minutes);
        }
    }

    /**
     * Called when the Dialog is closed.
     *
     * @param positiveResult Whether the Dialog was accepted or canceled.
     */
    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {

        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        DialogPreference preference = getPreference();
        if (preference instanceof TimePreference) {
            TimePreference timePreference = (TimePreference) getPreference();
            String dialogTitleStartTime = getContext().getResources().getString(R.string.settings_schedule_start_time_title);
            String dialogTitleEndTime = getContext().getResources().getString(R.string.settings_schedule_end_time_title);

            int previousStartTimeMin = SharedPreferenceManager.read(SharedPreferenceManager.PREF_SCHEDULE_START_TIME, 0);
            int previousEndTimeMin = SharedPreferenceManager.read(SharedPreferenceManager.PREF_SCHEDULE_END_TIME, 0);

            int hours;
            int minutes;
            if (Build.VERSION.SDK_INT >= 23) {
                hours = mTimePicker.getHour();
                minutes = mTimePicker.getMinute();
            } else {
                hours = mTimePicker.getCurrentHour();
                minutes = mTimePicker.getCurrentMinute();
            }

            int minutesAfterMidnight = (hours * 60) + minutes;

            if (which == dialog.BUTTON_POSITIVE
                    && timePreference.getDialogTitle().equals(dialogTitleEndTime)
                    && previousStartTimeMin > minutesAfterMidnight) {

                Toast.makeText(
                        getContext(),
                        getContext().getResources().getString(R.string.settings_error_end_time_invalid),
                        Toast.LENGTH_LONG
                ).show();

            } else if (which == dialog.BUTTON_POSITIVE
                    && timePreference.getDialogTitle().equals(dialogTitleStartTime)
                    && previousEndTimeMin > minutesAfterMidnight) {

                Toast.makeText(
                        getContext(),
                        getContext().getResources().getString(R.string.settings_error_start_earlier_than_end),
                        Toast.LENGTH_LONG
                ).show();

                TimePreference endTimePreference = (TimePreference) timePreference.getPreferenceManager().findPreference(SharedPreferenceManager.PREF_SCHEDULE_END_TIME);
                if (endTimePreference.callChangeListener(minutesAfterMidnight + 60)) {
                    endTimePreference.setTime(minutesAfterMidnight + 60);
                }

                if (timePreference.callChangeListener(minutesAfterMidnight)) {
                    timePreference.setTime(minutesAfterMidnight);
                }
            } else {
                if (timePreference.callChangeListener(minutesAfterMidnight)) {
                    timePreference.setTime(minutesAfterMidnight);
                }
            }
        }

        super.onClick(dialog, which);
    }
}
