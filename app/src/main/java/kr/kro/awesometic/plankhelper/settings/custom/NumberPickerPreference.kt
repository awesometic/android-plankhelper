package kr.kro.awesometic.plankhelper.settings.custom

import android.content.Context
import android.content.res.TypedArray
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet

import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.util.TimeUtils

/**
 * Created by Awesometic on 2017-06-05.
 */

class NumberPickerPreference : DialogPreference {

    /**
     * In Seconds
     */
    var time: Int = 0
        set(time) {
            field = time

            persistInt(time)

            summary = TimeUtils.secToTimeFormat(time)
        }

    /**
     * Resource of the dialog layout
     */
    private val mDialogLayoutResId = R.layout.settings_pref_dialog_time

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, R.attr.preferenceStyle) {}

    constructor(context: Context) : super(context) {}

    override fun getDialogLayoutResource(): Int {
        return super.getDialogLayoutResource()
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        return mDialogLayoutResId
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        time = if (restorePersistedValue)
            getPersistedInt(time)
        else
            defaultValue as Int
    }
}
