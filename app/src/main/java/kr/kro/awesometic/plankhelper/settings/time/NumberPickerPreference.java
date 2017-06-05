package kr.kro.awesometic.plankhelper.settings.time;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.util.TimeUtils;

/**
 * Created by Awesometic on 2017-06-05.
 */

public class NumberPickerPreference extends DialogPreference {

    /**
     * In Seconds
     */
    private int mTime;

    /**
     * Resource of the dialog layout
     */
    private int mDialogLayoutResId = R.layout.settings_pref_dialog_time;

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.preferenceStyle);
    }

    public NumberPickerPreference(Context context) {
        super(context);
    }

    public int getTime() {
        return mTime;
    }

    public void setTime(int time) {
        mTime = time;

        persistInt(time);

        setSummary(TimeUtils.secToTimeFormat(time));
    }

    @Override
    public int getDialogLayoutResource() {
        return super.getDialogLayoutResource();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return mDialogLayoutResId;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setTime(restorePersistedValue ?
                getPersistedInt(mTime) : (int) defaultValue);
    }
}
