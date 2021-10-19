package xyz.alexschubi.ttimer

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import xyz.alexschubi.ttimer.R


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}