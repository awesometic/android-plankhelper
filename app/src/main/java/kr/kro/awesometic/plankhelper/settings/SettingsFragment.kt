package kr.kro.awesometic.plankhelper.settings

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat

import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.settings.custom.NumberPickerPreference
import kr.kro.awesometic.plankhelper.settings.custom.NumberPickerPreferenceDialogFragment
import kr.kro.awesometic.plankhelper.settings.custom.TimePreference
import kr.kro.awesometic.plankhelper.settings.custom.TimePreferenceDialogFragment

import com.google.common.base.Preconditions.checkNotNull

/**
 * Created by Awesometic on 2017-04-17.
 */

class SettingsFragment : PreferenceFragmentCompat(), SettingsContract.View {

    private var mPresenter: SettingsContract.Presenter? = null

    override fun setPresenter(presenter: SettingsContract.Presenter) {
        mPresenter = checkNotNull(presenter)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        addPreferencesFromResource(R.xml.preference)
    }

    /**
     * {@inheritDoc}
     */
    override fun onDisplayPreferenceDialog(preference: Preference) {
        // Try if the preference is one of our custom Preferences
        var dialogFragment: DialogFragment? = null
        if (preference is TimePreference) {
            // Create a new instance of TimePreferenceDialogFragment with the key of the related
            // Preference
            dialogFragment = TimePreferenceDialogFragment.newInstance(preference.getKey())
        } else if (preference is NumberPickerPreference) {
            dialogFragment = NumberPickerPreferenceDialogFragment.newInstance(preference.getKey())
        }

        if (dialogFragment != null) {
            // The dialog was created (it was one of our custom Preferences), show the dialog for it
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(this.fragmentManager, "android.support.v7.preference" + ".PreferenceFragment.DIALOG")
        } else {
            // Dialog creation could not be handled here. Try with the super method.
            super.onDisplayPreferenceDialog(preference)
        }
    }

    companion object {

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}