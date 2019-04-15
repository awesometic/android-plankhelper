package kr.kro.awesometic.plankhelper.settings;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import kr.kro.awesometic.plankhelper.R;
import kr.kro.awesometic.plankhelper.settings.custom.NumberPickerPreference;
import kr.kro.awesometic.plankhelper.settings.custom.NumberPickerPreferenceDialogFragment;
import kr.kro.awesometic.plankhelper.settings.custom.TimePreference;
import kr.kro.awesometic.plankhelper.settings.custom.TimePreferenceDialogFragment;

import static androidx.core.util.Preconditions.checkNotNull;

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