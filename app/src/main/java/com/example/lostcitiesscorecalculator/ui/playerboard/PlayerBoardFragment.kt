package com.example.lostcitiesscorecalculator.ui.playerboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ToggleButton
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lostcitiesscorecalculator.R
import com.example.lostcitiesscorecalculator.databinding.FragmentPlayerboardBinding

class PlayerBoardFragment : Fragment() {

    private var _binding: FragmentPlayerboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val playerBoardViewModel =
            ViewModelProvider(this).get(PlayerBoardViewModel::class.java)

        _binding = FragmentPlayerboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        addButtonsToGridLayout()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val button_colours = mutableMapOf<Int, String>().apply {
        this[0] = "yellow"
        this[1] = "white"
        this[2] = "blue"
        this[3] = "green"
        this[4] = "red"
        this[5] = "purple"
    }

    private val light_colours = mutableMapOf<Int, String>().apply {
        this[0] = "light_yellow"
        this[1] = "light_white"
        this[2] = "light_blue"
        this[3] = "light_green"
        this[4] = "light_red"
        this[5] = "light_purple"
    }

    private fun findButton(row: Int, column: Int): Button? {
        val gridLayout = binding.boardGrid
        val rowCount = gridLayout.rowCount
        val columnCount = gridLayout.columnCount

        if (row < 0 || row >= rowCount || column < 0 || column >= columnCount) {
            return null // Invalid row or column
        }

        val index = row * columnCount + column

        return if (index != -1) gridLayout.getChildAt(index) as Button else null
    }

    private fun getColorFromString(colorName : String?) : Int {
        var colour = "white"
        if (colorName != null)
            colour = colorName
        val packageName = context?.packageName
        val colorResourceId = resources.getIdentifier(colour, "color", packageName)
        return ContextCompat.getColor(requireContext(), colorResourceId)
    }

    private fun toggleButtonState(button: Button, selectedColour: Int, unselectedColour: Int, selectedTextColour: Int) {
        val isButtonOn = button.tag as? Boolean ?: false

        if (isButtonOn) {
            button.setBackgroundColor(unselectedColour)
            button.setTextColor(selectedColour)
            button.tag = false
        } else {
            button.setBackgroundColor(selectedColour)
            button.setTextColor(selectedTextColour)
            button.tag = true
        }
    }

    private fun addButtonsToGridLayout() {
        val totalRows = 10;
        val totalColumns = 6;

        for (col in 0 until totalColumns)
        {
            val buttonColor = getColorFromString(button_colours[col])
            val unselectedBackgroundColor = getColorFromString("dark_grey")
            val selectedTextColor = getColorFromString("black")

            // Skip the wager row
            for (row in 1 until totalRows)
            {
                val button: Button? = findButton(row, col)

                button?.setTextColor(buttonColor)
                button?.setOnClickListener {
                    toggleButtonState(button, buttonColor, unselectedBackgroundColor, selectedTextColor)
                }
            }
        }
    }
}