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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.lostcitiesscorecalculator.R
import com.example.lostcitiesscorecalculator.databinding.FragmentPlayerboardBinding
import com.example.lostcitiesscorecalculator.ui.scoreboard.SharedScoreViewModel
import com.google.android.material.card.MaterialCardView

class PlayerBoardFragment : Fragment() {

    private var _binding: FragmentPlayerboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PlayerBoardViewModel
    private var playerId: Int = 0
    private lateinit var sharedScoreViewModel: SharedScoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        playerId = requireArguments().getInt("playerId", 0)
        val factory = PlayerBoardViewModelFactory(playerId)
        viewModel = ViewModelProvider(this, factory).get(PlayerBoardViewModel::class.java)

        sharedScoreViewModel = ViewModelProvider(requireActivity()).get()

        if (playerId == 1)
            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.title_player1)
        else
            (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.title_player2)

        setupGridLayout()
        observeViewModel()

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

    private val buttonColours = mapOf(
        0 to "yellow",
        1 to "white",
        2 to "blue",
        3 to "green",
        4 to "red",
        5 to "purple"
    )

    private val lightColours = mapOf(
        0 to "light_yellow",
        1 to "light_white",
        2 to "light_blue",
        3 to "light_green",
        4 to "light_red",
        5 to "light_purple"
    )

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
        val colour = colorName ?: "white"
        val packageName = requireContext().packageName
        val colorResourceId = resources.getIdentifier(colour, "color", packageName)
        return ContextCompat.getColor(requireContext(), colorResourceId)
    }

    private fun setupGridLayout() {
        val unselectedBackgroundColor = getColorFromString("dark_grey")
        val selectedTextColor = getColorFromString("black")

        for (col in 0 until totalButtonColumns) {
            val buttonColor = getColorFromString(buttonColours[col])

            // Set the OnClickListener and icons for the Wager button
            val wagerButton : ImageButton? = findImageButton(col)
            wButtonTopPadding = wagerButton?.paddingTop ?: 0
            wButtonBottomPadding = wagerButton?.paddingBottom ?: 0
            wButtonLeftPadding = wagerButton?.paddingLeft ?: 0
            wButtonRightPadding = wagerButton?.paddingRight ?: 0

            wagerButton?.tag = viewModel.wagerCounts.value?.get(col) ?: 0
            wagerButton?.setColorFilter(buttonColor)
            wagerButton?.setOnClickListener {
                viewModel.toggleWagerCountCommand(col)
            }

            // Set the OnClickListener and colours for each numbered button
            for (row in 1 until totalButtonRows) {
                val button : Button? = findButton(row, col)
                button?.tag = viewModel.buttonStates.value?.get(col)?.get(row) ?: false
                button?.setTextColor(buttonColor)
                button?.setOnClickListener {
                    viewModel.toggleButtonStateCommand(row, col)
                }
            }
        }
    }

    private fun resetBoard()
    {
        viewModel.resetBoardCommand()
    }

    private fun observeViewModel() {
        viewModel.totalPoints.observe(viewLifecycleOwner, totalScoreObserver)
        viewModel.points.observe(viewLifecycleOwner, pointsObserver)
        viewModel.buttonStates.observe(viewLifecycleOwner, buttonStatesObserver)
        viewModel.wagerCounts.observe(viewLifecycleOwner, wagerCountsObserver)
    }

    private val totalScoreObserver = Observer<Int> { score ->
        if (playerId == 1) {
            // Update Player 1 score in SharedScoreViewModel
            sharedScoreViewModel.setPlayer1TotalPoints(score)
        } else if (playerId == 2) {
            // Update Player 2 score in SharedScoreViewModel
            sharedScoreViewModel.setPlayer2TotalPoints(score)
        }
    }

    private val pointsObserver = Observer<Map<Int, Int>> { points ->
        val zeroColour = getColorFromString("light_grey")

        points.forEach { (col, point) ->
            val totalTextView = findTextView(10, col)
            totalTextView?.text = point.toString()
            if (point != 0) {
                totalTextView?.setBackgroundColor(getColorFromString(lightColours[col]))
            } else {
                totalTextView?.setBackgroundColor(zeroColour)
            }
        }
    }

    private val buttonStatesObserver = Observer<Map<Int, Map<Int, Boolean>>> { buttonStates ->
        val unselectedColour = getColorFromString("dark_grey")
        val textSelectedColour = getColorFromString("black")

        buttonStates.forEach { (col, states) ->
            states.forEach { (row, state) ->
                val button = findButton(row, col)
                button?.tag = state
                if (state) {
                    button?.setBackgroundColor(getColorFromString(buttonColours[col]))
                    button?.setTextColor(textSelectedColour)
                } else {
                    button?.setBackgroundColor(unselectedColour)
                    button?.setTextColor(getColorFromString(buttonColours[col]))
                }
            }
        }
    }

    private val wagerCountsObserver = Observer<Map<Int, Int>> { wagerCounts ->
        val unselectedColour = getColorFromString("dark_grey")
        val iconSelectedColour = getColorFromString("black")

        wagerCounts.forEach { (col, count) ->
            val wagerButton = findImageButton(col)
            val selectedColour = getColorFromString(buttonColours[col])
            when (count) {
                0 -> {
                    wagerButton?.tag = 0
                    wagerButton?.backgroundTintList = ColorStateList.valueOf(unselectedColour)
                    wagerButton?.setImageResource(R.drawable.ic_wager)
                    wagerButton?.setColorFilter(selectedColour)
                    wagerButton?.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    wagerButton?.setPadding(wButtonLeftPadding, wButtonTopPadding, wButtonRightPadding, wButtonBottomPadding)
                }
                1 -> {
                    wagerButton?.tag = 1
                    wagerButton?.backgroundTintList = ColorStateList.valueOf(selectedColour)
                    wagerButton?.setColorFilter(iconSelectedColour)
                }
                2 -> {
                    wagerButton?.tag = 2
                    wagerButton?.setImageResource(R.drawable.ic_wager2)
                    wagerButton?.scaleType = ImageView.ScaleType.CENTER_CROP
                }
                3 -> {
                    wagerButton?.tag = 3
                    wagerButton?.setImageResource(R.drawable.ic_wager3_alternate)
                    wagerButton?.setPadding(15, 15, 15, 15)
                }
            }
        }
    }
}
