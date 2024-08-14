package com.askariya.lostcitiesscorecalculator.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.askariya.lostcitiesscorecalculator.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}