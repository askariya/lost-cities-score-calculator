package com.askariya.lostcitiesscorecalculator.ui.settings

import android.os.Bundle
import android.text.InputFilter
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.askariya.lostcitiesscorecalculator.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val player1NamePreference = findPreference<EditTextPreference>("player1name")
        val player2NamePreference = findPreference<EditTextPreference>("player2name")

        // Set character limit (e.g., 10 characters) for player1name and player2name
        val maxLength = 20
        val filterArray = arrayOf(InputFilter.LengthFilter(maxLength))

        player1NamePreference?.setOnBindEditTextListener { editText ->
            editText.filters = filterArray
        }

        player2NamePreference?.setOnBindEditTextListener { editText ->
            editText.filters = filterArray
        }
    }
}
