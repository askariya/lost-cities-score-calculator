package com.example.lostcitiesscorecalculator.ui.playerboard

import PlayerBoardViewModelFactory
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.lostcitiesscorecalculator.R
import com.example.lostcitiesscorecalculator.databinding.FragmentPlayerboardBinding
import com.google.android.material.card.MaterialCardView

class PlayerBoardFragment : Fragment() {

    private var _binding: FragmentPlayerboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PlayerBoardViewModel
    private var playerId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        playerId = arguments?.getInt("playerId") ?: 0
        val factory = PlayerBoardViewModelFactory(playerId)
        viewModel = ViewModelProvider(this, factory).get(PlayerBoardViewModel::class.java)

        _binding = FragmentPlayerboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupGridLayout()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private val totalButtonRows = 10
    private val totalButtonColumns = 6

    private var wButtonTopPadding = 0
    private var wButtonBottomPadding = 0
    private var wButtonLeftPadding = 0
    private var wButtonRightPadding = 0

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

    private fun findImageButton(column: Int): ImageButton? {
        val gridLayout = binding.boardGrid
        val columnCount = gridLayout.columnCount

        if (column < 0 || column >= columnCount) {
            return null // Invalid row or column
        }

        return gridLayout.getChildAt(column) as ImageButton
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
        return if (index != -1) cardView?.getChildAt(0) as TextView else null
    }

    private fun getColorFromString(colorName : String?) : Int {
        var colour = "white"
        if (colorName != null)
            colour = colorName
        val packageName = context?.packageName
        val colorResourceId = resources.getIdentifier(colour, "color", packageName)
        return ContextCompat.getColor(requireContext(), colorResourceId)
    }

    private fun toggleButtonState(row: Int, column: Int, button: Button, selectedColour: Int, unselectedColour: Int, selectedTextColour: Int) {
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

        viewModel.setButtonState(row, column, !isButtonOn)
        checkAndToggleTotalTextViewState(column)
    }

    private fun toggleWagerButtonState(column: Int, wagerButton: ImageButton, selectedColour: Int, unselectedColour: Int, selectedIconColor : Int) {
        val wagerCount = wagerButton.tag as? Int ?: 0

        when (wagerCount)
        {
            0 -> {
                wagerButton.tag = 1
                wagerButton.backgroundTintList = ColorStateList.valueOf(selectedColour)
                wagerButton.setColorFilter(selectedIconColor)
            }
            1 -> {
                wagerButton.tag = 2
                wagerButton.setImageResource(R.drawable.ic_wager2)
                wagerButton.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            2 -> {
                wagerButton.tag = 3
                wagerButton.setImageResource(R.drawable.ic_wager3_alternate)
                wagerButton.setPadding(15,15,15,15)
            }
            3 -> {
                wagerButton.tag = 0
                wagerButton.backgroundTintList = ColorStateList.valueOf(unselectedColour)
                wagerButton.setImageResource(R.drawable.ic_wager)
                wagerButton.setColorFilter(selectedColour)
                wagerButton.scaleType = ImageView.ScaleType.CENTER_INSIDE
                wagerButton.setPadding(wButtonLeftPadding, wButtonTopPadding,wButtonRightPadding,wButtonBottomPadding)
            }
        }

        viewModel.setWagerCount(column, wagerButton.tag as Int)
        checkAndToggleTotalTextViewState(column)
    }

    private fun checkAndToggleTotalTextViewState(column : Int)
    {
        var columnScore = 0
        var selectedButtonCount = 0
        val totalTextView: TextView = findTextView(10, column)!!

        // Calculate the corresponding multiple based on the number of wager cards
        val wagerButton = findImageButton(column)
        val wagerButtonMultiple = (wagerButton?.tag as? Int ?: 0) + 1

        if (wagerButtonMultiple > 1)
            selectedButtonCount++

        // Calculate Score for numbered buttons
        for (row in 1 until totalButtonRows)
        {
            val numberButton = findButton(row, column)
            val buttonIsSelected = numberButton?.tag as? Boolean ?: false

            if (buttonIsSelected) {
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

        // Multiply score based on wager cards
        columnScore *= wagerButtonMultiple

        if (selectedButtonCount >= 8)
            columnScore += 20

        totalTextView.text = columnScore.toString()
        viewModel.setPoints(column, columnScore)
    }

    private fun setupGridLayout() {
        val unselectedBackgroundColor = getColorFromString("dark_grey")
        val selectedTextColor = getColorFromString("black")

        for (col in 0 until totalButtonColumns)
        {
            val buttonColor = getColorFromString(buttonColours[col])

            // Set the OnClickListener and icons for the Wager button
            val wagerButton : ImageButton? = findImageButton(col)
            wButtonTopPadding = wagerButton?.paddingTop!!
            wButtonBottomPadding = wagerButton?.paddingBottom!!
            wButtonLeftPadding = wagerButton?.paddingLeft!!
            wButtonRightPadding = wagerButton?.paddingRight!!
            wagerButton?.tag = viewModel.wagerCounts.value?.get(col) ?: 0
            wagerButton?.setColorFilter(buttonColor)
            wagerButton?.setOnClickListener {
                toggleWagerButtonState(col, wagerButton, buttonColor, unselectedBackgroundColor, selectedTextColor)
            }

            // Set the OnClickListener and colours for each numbered button
            for (row in 1 until totalButtonRows)
            {
                val button : Button? = findButton(row, col)
                button?.tag = viewModel.buttonStates.value?.get(col)?.get(row) ?: false
                button?.setTextColor(buttonColor)
                button?.setOnClickListener {
                    toggleButtonState(row, col, button, buttonColor, unselectedBackgroundColor, selectedTextColor)
                }
            }
        }

        // Observe the ViewModel data and update the UI accordingly
        viewModel.points.observe(viewLifecycleOwner, Observer { points ->
            points.forEach { (col, point) ->
                val totalTextView = findTextView(10, col)
                totalTextView?.text = point.toString()
                if (point > 0) {
                    totalTextView?.setBackgroundColor(getColorFromString(lightColours[col]))
                } else {
                    totalTextView?.setBackgroundColor(getColorFromString("light_grey"))
                }
            }
        })

        viewModel.buttonStates.observe(viewLifecycleOwner, Observer { buttonStates ->
            buttonStates.forEach { (col, states) ->
                states.forEach { (row, state) ->
                    val button = findButton(row, col)
                    button?.tag = state
                    if (state) {
                        button?.setBackgroundColor(getColorFromString(buttonColours[col]))
                        button?.setTextColor(getColorFromString("black"))
                    } else {
                        button?.setBackgroundColor(getColorFromString("dark_grey"))
                        button?.setTextColor(getColorFromString(buttonColours[col]))
                    }
                }
            }
        })

        viewModel.wagerCounts.observe(viewLifecycleOwner, Observer { wagerCounts ->
            wagerCounts.forEach { (col, count) ->
                val wagerButton = findImageButton(col)
                wagerButton?.tag = count
                when (count) {
                    1 -> {
                        wagerButton?.backgroundTintList = ColorStateList.valueOf(getColorFromString(buttonColours[col]))
                        wagerButton?.setColorFilter(getColorFromString("black"))
                    }
                    2 -> {
                        wagerButton?.setImageResource(R.drawable.ic_wager2)
                        wagerButton?.scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                    3 -> {
                        wagerButton?.setImageResource(R.drawable.ic_wager3_alternate)
                        wagerButton?.setPadding(15, 15, 15, 15)
                    }
                    else -> {
                        wagerButton?.backgroundTintList = ColorStateList.valueOf(getColorFromString("dark_grey"))
                        wagerButton?.setImageResource(R.drawable.ic_wager)
                        wagerButton?.setColorFilter(getColorFromString(buttonColours[col]))
                        wagerButton?.scaleType = ImageView.ScaleType.CENTER_INSIDE
                        wagerButton?.setPadding(wButtonLeftPadding, wButtonTopPadding, wButtonRightPadding, wButtonBottomPadding)
                    }
                }
            }
        })
    }
}
