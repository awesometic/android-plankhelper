package kr.kro.awesometic.plankhelper.settings.custom

import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.preference.DialogPreference
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker

import java.util.Locale

import butterknife.BindView
import butterknife.ButterKnife
import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.util.TimeUtils

/**
 * Created by Awesometic on 2017-06-05.
 */

class NumberPickerPreferenceDialogFragment : PreferenceDialogFragmentCompat(), DialogInterface.OnClickListener {

    /**
     * The NumberPicker widget
     */
    @BindView(R.id.settings_pref_dialog_widget_numberPicker_hour)
    internal var mNumberPickerHour: NumberPicker? = null

    @BindView(R.id.settings_pref_dialog_widget_numberPicker_min)
    internal var mNumberPickerMin: NumberPicker? = null

    @BindView(R.id.settings_pref_dialog_widget_numberPicker_sec)
    internal var mNumberPickerSec: NumberPicker? = null

    override fun onCreateDialogView(context: Context): View {
        val inflater = getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        return inflater.inflate(R.layout.settings_pref_dialog_numberpicker, null)
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        ButterKnife.bind(this, view)

        var pickedSeconds: Int? = null
        val preference = preference
        if (preference is NumberPickerPreference) {
            pickedSeconds = preference.time
        }

        // Init and set the time to the NumberPickers
        if (pickedSeconds != null) {
            val previousSelectedTime = TimeUtils.secToTimeFormat(pickedSeconds)
            val hour = Integer.parseInt(previousSelectedTime.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
            val min = Integer.parseInt(previousSelectedTime.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
            val sec = Integer.parseInt(previousSelectedTime.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2])

            mNumberPickerHour!!.maxValue = 10
            mNumberPickerMin!!.maxValue = 59
            mNumberPickerSec!!.maxValue = 59

            setDividerColor(mNumberPickerHour, ContextCompat.getColor(context, R.color.numberPickerDivider))
            setDividerColor(mNumberPickerMin, ContextCompat.getColor(context, R.color.numberPickerDivider))
            setDividerColor(mNumberPickerSec, ContextCompat.getColor(context, R.color.numberPickerDivider))

            mNumberPickerHour!!.setFormatter { value -> String.format(Locale.getDefault(), "%02d", value) }
            mNumberPickerMin!!.setFormatter { value -> String.format(Locale.getDefault(), "%02d", value) }
            mNumberPickerSec!!.setFormatter { value -> String.format(Locale.getDefault(), "%02d", value) }

            mNumberPickerHour!!.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            mNumberPickerMin!!.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            mNumberPickerSec!!.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            mNumberPickerHour!!.value = hour
            mNumberPickerMin!!.value = min
            mNumberPickerSec!!.value = sec
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {

    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        super.onClick(dialog, which)

        val preference = preference
        if (preference is NumberPickerPreference) {
            val numberPickerPreference = getPreference() as NumberPickerPreference

            val hour = if (mNumberPickerHour!!.value < 10) "0" + mNumberPickerHour!!.value else mNumberPickerHour!!.value.toString()
            val min = if (mNumberPickerMin!!.value < 10) "0" + mNumberPickerMin!!.value else mNumberPickerMin!!.value.toString()
            val sec = if (mNumberPickerSec!!.value < 10) "0" + mNumberPickerSec!!.value else mNumberPickerSec!!.value.toString()

            val resultSec = TimeUtils.timeFormatToMSec("$hour:$min:$sec.000").toInt() / 1000

            if (numberPickerPreference.callChangeListener(resultSec)) {
                numberPickerPreference.time = resultSec
            }
        }
    }

    /** https://stackoverflow.com/questions/24233556/changing-numberpicker-divider-color
     */
    private fun setDividerColor(picker: NumberPicker, color: Int) {

        val pickerFields = NumberPicker::class.java.declaredFields
        for (pf in pickerFields) {
            if (pf.name == "mSelectionDivider") {
                pf.isAccessible = true
                try {
                    val colorDrawable = ColorDrawable(color)
                    pf.set(picker, colorDrawable)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: Resources.NotFoundException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }

                break
            }
        }
    }

    companion object {

        fun newInstance(key: String): NumberPickerPreferenceDialogFragment {
            val fragment = NumberPickerPreferenceDialogFragment()
            val b = Bundle(1)
            b.putString(PreferenceDialogFragmentCompat.ARG_KEY, key)
            fragment.arguments = b

            return fragment
        }
    }
}
