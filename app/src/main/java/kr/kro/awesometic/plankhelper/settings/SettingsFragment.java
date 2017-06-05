package kr.kro.awesometic.plankhelper.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.settings.time.NumberPickerPreference;
import kr.kro.awesometic.plankhelper.settings.time.NumberPickerPreferenceDialogFragment;
import kr.kro.awesometic.plankhelper.settings.time.TimePreference;
import kr.kro.awesometic.plankhelper.settings.time.TimePreferenceDialogFragment;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-04-17.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SettingsContract.View {

    private SettingsContract.Presenter mPresenter;

    public SettingsFragment() {

    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void setPresenter(@NonNull SettingsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        // Try if the preference is one of our custom Preferences
        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference) {
            // Create a new instance of TimePreferenceDialogFragment with the key of the related
            // Preference
            dialogFragment = TimePreferenceDialogFragment.newInstance(preference.getKey());
        } else if (preference instanceof NumberPickerPreference) {
            dialogFragment = NumberPickerPreferenceDialogFragment.newInstance(preference.getKey());
        }

        if (dialogFragment != null) {
            // The dialog was created (it was one of our custom Preferences), show the dialog for it
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference" +
                    ".PreferenceFragment.DIALOG");
        } else {
            // Dialog creation could not be handled here. Try with the super method.
            super.onDisplayPreferenceDialog(preference);
        }
    }
}