package kr.kro.awesometic.plankhelper.settings.custom

/**
 * Created by Awesometic on 2017-06-03.
 */

import android.content.Context
import android.content.res.TypedArray
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet

import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.util.TimeUtils

/**
 * A Preference to select a specific Time with a [android.widget.TimePicker].
 *
 * @author Jakob Ulbrich
 */
class TimePreference @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.preferenceStyle, defStyleRes: Int = defStyleAttr)// Du custom stuff here
// ...
// read attributes etc.
    : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * In Minutes after midnight
     */
    /**
     * Gets the time from the Shared Preferences
     *
     * @return The current preference value
     */
    /**
     * Saves the time to the SharedPreferences
     *
     * @param time The time to save
     */
    // Save to SharedPreference
    var time: Int = 0
        set(time) {
            field = time
            persistInt(time)

            summary = TimeUtils.minToTimeFormat(time)
        }

    /**
     * Resource of the dialog layout
     */
    private val mDialogLayoutResId = R.layout.settings_pref_dialog_time

    //

    /**
     * Called when a Preference is being inflated and the default value attribute needs to be read
     */
    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        // The type of this preference is Int, so we read the default value from the attributes
        // as Int. Fallback value is set to 0.
        return a!!.getInt(index, 0)
    }

    //

    /**
     * Returns the layout resource that is used as the content View for the dialog
     */
    override fun getDialogLayoutResource(): Int {
        return mDialogLayoutResId
    }


    //

    /**
     * Implement this to set the initial value of the Preference.
     */
    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        // If the value can be restored, do it. If not, use the default value.
        time = if (restorePersistedValue)
            getPersistedInt(time)
        else
            defaultValue as Int
    }
}// Delegate to other constructor
// Delegate to other constructor
// Use the preferenceStyle as the default style
// Delegate to other constructor