package kr.kro.awesometic.plankhelper.settings.time;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.util.Constants;
import kr.kro.awesometic.plankhelper.util.TimeUtils;

/**
 * Created by Awesometic on 2017-06-05.
 */

public class NumberPickerPreferenceDialogFragment extends PreferenceDialogFragmentCompat
        implements DialogInterface.OnClickListener {

    /**
     * The NumberPicker widget
     */
    @BindView(R.id.settings_pref_dialog_widget_numberPicker_hour)
    NumberPicker mNumberPickerHour;

    @BindView(R.id.settings_pref_dialog_widget_numberPicker_min)
    NumberPicker mNumberPickerMin;

    @BindView(R.id.settings_pref_dialog_widget_numberPicker_sec)
    NumberPicker mNumberPickerSec;

    public static NumberPickerPreferenceDialogFragment newInstance(String key) {
        final NumberPickerPreferenceDialogFragment
                fragment = new NumberPickerPreferenceDialogFragment();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    protected View onCreateDialogView(Context context) {
        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return inflater.inflate(R.layout.settings_pref_dialog_numberpicker, null);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        ButterKnife.bind(this, view);

        Integer pickedSeconds = null;
        DialogPreference preference = getPreference();
        if (preference instanceof NumberPickerPreference) {
            pickedSeconds = ((NumberPickerPreference) preference).getTime();
        }

        // Init and set the time to the NumberPickers
        if (pickedSeconds != null) {
            String previousSelectedTime = TimeUtils.secToTimeFormat(pickedSeconds);
            int hour = Integer.parseInt(previousSelectedTime.split(":")[0]);
            int min = Integer.parseInt(previousSelectedTime.split(":")[1]);
            int sec = Integer.parseInt(previousSelectedTime.split(":")[2]);
            
            mNumberPickerHour.setMaxValue(10);
            mNumberPickerMin.setMaxValue(59);
            mNumberPickerSec.setMaxValue(59);
            
            setDividerColor(mNumberPickerHour, ContextCompat.getColor(getContext(), R.color.numberPickerDivider));
            setDividerColor(mNumberPickerMin, ContextCompat.getColor(getContext(), R.color.numberPickerDivider));
            setDividerColor(mNumberPickerSec, ContextCompat.getColor(getContext(), R.color.numberPickerDivider));

            mNumberPickerHour.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return String.format(Locale.getDefault(), "%02d", value);
                }
            });
            mNumberPickerMin.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return String.format(Locale.getDefault(), "%02d", value);
                }
            });
            mNumberPickerSec.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return String.format(Locale.getDefault(), "%02d", value);
                }
            });

            mNumberPickerHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            mNumberPickerMin.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            mNumberPickerSec.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

            mNumberPickerHour.setValue(hour);
            mNumberPickerMin.setValue(min);
            mNumberPickerSec.setValue(sec);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);

        DialogPreference preference = getPreference();
        if (preference instanceof NumberPickerPreference) {
            NumberPickerPreference numberPickerPreference = (NumberPickerPreference) getPreference();
            
            String hour = (mNumberPickerHour.getValue() < 10) ? "0" + mNumberPickerHour.getValue() : String.valueOf(mNumberPickerHour.getValue());
            String min = (mNumberPickerMin.getValue() < 10) ? "0" + mNumberPickerMin.getValue() : String.valueOf(mNumberPickerMin.getValue());
            String sec = (mNumberPickerSec.getValue() < 10) ? "0" + mNumberPickerSec.getValue() : String.valueOf(mNumberPickerSec.getValue());
            
            int resultSec = ((int) TimeUtils.timeFormatToMSec(hour + ":" + min + ":" + sec + ".000")) / 1000;

            if (numberPickerPreference.callChangeListener(resultSec)) {
                numberPickerPreference.setTime(resultSec);
            }
        }
    }

    /** https://stackoverflow.com/questions/24233556/changing-numberpicker-divider-color
     * */
    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
