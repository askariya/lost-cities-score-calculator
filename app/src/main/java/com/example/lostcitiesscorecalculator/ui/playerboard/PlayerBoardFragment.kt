package com.example.lostcitiesscorecalculator.ui.playerboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lostcitiesscorecalculator.databinding.FragmentPlayerboardBinding
import com.google.android.material.card.MaterialCardView

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

        setupGridLayout()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val totalButtonRows = 10;
    private val totalButtonColumns = 6;

    private val buttonColours = mutableMapOf<Int, String>().apply {
        this[0] = "yellow"
        this[1] = "white"
        this[2] = "blue"
        this[3] = "green"
        this[4] = "red"
        this[5] = "purple"
    }

    private val lightColours = mutableMapOf<Int, String>().apply {
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

    private fun findTextView(row: Int, column: Int): TextView? {
        val gridLayout = binding.boardGrid
        val rowCount = gridLayout.rowCount
        val columnCount = gridLayout.columnCount

        if (row < 0 || row >= rowCount || column < 0 || column >= columnCount) {
            return null // Invalid row or column
        }

        val index = row * columnCount + column

        val cardView = if (index != -1) gridLayout.getChildAt(index) as MaterialCardView else null
        return if (index != -1) cardView?.getChildAt(0) as TextView else null;
    }

    private fun getColorFromString(colorName : String?) : Int {
        var colour = "white"
        if (colorName != null)
            colour = colorName
        val packageName = context?.packageName
        val colorResourceId = resources.getIdentifier(colour, "color", packageName)
        return ContextCompat.getColor(requireContext(), colorResourceId)
    }

    private fun toggleButtonState(column : Int, button: Button, selectedColour: Int, unselectedColour: Int, selectedTextColour: Int) {
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

        checkAndToggleTotalTextViewState(column)
    }

    private fun checkAndToggleTotalTextViewState(column : Int)
    {
        var columnScore = 0
        var selectedButtonCount = 0
        val totalTextView: TextView = findTextView(10, column)!!

        //TODO Calculate score for wager buttons

        // Calculate Score for numbered buttons
        for (row in 1 until totalButtonRows)
        {
            val rowButton = findButton(row, column)
            val buttonIsSelected = rowButton?.tag as? Boolean ?: false

            if (buttonIsSelected)
            {
                columnScore += (row + 1)
                selectedButtonCount++
            }
        }

        if (selectedButtonCount > 0) {
            columnScore -= 20
            totalTextView.setBackgroundColor(getColorFromString(lightColours[column]))
        } else {
            totalTextView.setBackgroundColor(getColorFromString("light_grey"))
        }

        if (selectedButtonCount >= 8)
            columnScore += 20

        totalTextView.text = columnScore.toString()
    }

    private fun setupGridLayout() {
        for (col in 0 until totalButtonColumns)
        {
            val buttonColor = getColorFromString(buttonColours[col])
            val unselectedBackgroundColor = getColorFromString("dark_grey")
            val selectedTextColor = getColorFromString("black")

            // Set the OnClickListener and colours for each button
            // Skip the wager row
            for (row in 1 until totalButtonRows)
            {
                val button: Button? = findButton(row, col)

                button?.setTextColor(buttonColor)
                button?.setOnClickListener {
                    toggleButtonState(col, button, buttonColor, unselectedBackgroundColor, selectedTextColor)
                }
            }
        }
    }
}