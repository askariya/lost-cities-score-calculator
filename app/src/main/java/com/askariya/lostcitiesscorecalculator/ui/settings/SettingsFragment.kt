package com.askariya.lostcitiesscorecalculator.ui.settings

import android.os.Bundle
import android.text.InputFilter
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
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

        // Set up validation to prevent empty strings or whitespace
        val nonEmptyStringValidator = Preference.OnPreferenceChangeListener { preference, newValue ->
            val newName = newValue as? String
            if (newName.isNullOrBlank()) {
                // Show a message to the user (e.g., Toast or Dialog)
                Toast.makeText(requireContext(), "Name cannot be empty or whitespace", Toast.LENGTH_SHORT).show()

                // Reset the value to an empty string
                if (preference is EditTextPreference) {
                    preference.text = ""
                }

                false // Reject the change to prevent it from being saved
            } else {
                true // Accept the change
            }
        }

        player1NamePreference?.onPreferenceChangeListener = nonEmptyStringValidator
        player2NamePreference?.onPreferenceChangeListener = nonEmptyStringValidator
    }
}
