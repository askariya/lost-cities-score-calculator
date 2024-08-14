package com.askariya.lostcitiesscorecalculator.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.askariya.lostcitiesscorecalculator.R

class SettingsDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullscreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_dialog_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()

        // Set up the toolbar
        val toolbar = view.findViewById<Toolbar>(R.id.settings_toolbar)
        toolbar.apply {
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material) // Built-in back icon
            setNavigationOnClickListener {
                dismiss()
            }
            title = getString(R.string.title_settings)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { d ->
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(d.window?.attributes)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
            d.window?.attributes = layoutParams
        }
    }
}
