package com.askariya.lostcitiesscorecalculator.ui.playerboard

import PlayerBoardViewModelFactory
import android.content.res.ColorStateList
import android.graphics.Typeface
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
import com.askariya.lostcitiesscorecalculator.R
import com.askariya.lostcitiesscorecalculator.databinding.FragmentPlayerboardBinding
import com.askariya.lostcitiesscorecalculator.ui.utils.DialogUtils
import com.askariya.lostcitiesscorecalculator.ui.utils.GameStateManager
import com.google.android.material.card.MaterialCardView

class PlayerBoardFragment : Fragment() {

    private var _binding: FragmentPlayerboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PlayerBoardViewModel
    private var playerId: Int = 0

    companion object {
        private const val ARG_PLAYER_ID = "playerId"

        fun newInstance(playerId: Int): PlayerBoardFragment {
            return PlayerBoardFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PLAYER_ID, playerId)
                }
            }
        }
    }

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

        GameStateManager.roundCounter.observe(viewLifecycleOwner, roundCounterObserver)

        if (playerId == 1)
            GameStateManager.player1TotalPoints.observe(viewLifecycleOwner, totalScoreObserver)
        else
            GameStateManager.player2TotalPoints.observe(viewLifecycleOwner, totalScoreObserver)

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

    private fun findTextView(row: Int, column: Int, childIndex: Int): TextView? {
        val gridLayout = binding.boardGrid
        val rowCount = gridLayout.rowCount
        val columnCount = gridLayout.columnCount

        if (row < 0 || row >= rowCount || column < 0 || column >= columnCount) {
            return null // Invalid row or column
        }

        val index = row * columnCount + column

        val cardView = if (index != -1) gridLayout.getChildAt(index) as MaterialCardView else null
        return cardView?.getChildAt(childIndex) as TextView?
    }

    private fun getColorFromString(colorName : String?) : Int {
        val colour = colorName ?: "white"
        val packageName = requireContext().packageName
        val colorResourceId = resources.getIdentifier(colour, "color", packageName)
        return ContextCompat.getColor(requireContext(), colorResourceId)
    }

    private fun setupGridLayout() {
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
                it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
                viewModel.toggleWagerCountCommand(col)
            }

            // Set the OnClickListener and colours for each numbered button
            for (row in 1 until totalButtonRows) {
                val button : Button? = findButton(row, col)
                button?.tag = viewModel.buttonStates.value?.get(col)?.get(row) ?: false
                button?.setTextColor(buttonColor)
                button?.setOnClickListener {
                    it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
                    viewModel.toggleButtonStateCommand(row, col)
                }
            }
        }

        // set the resetButton functionality
        val resetButton : Button = binding.resetButton
        resetButton.setOnClickListener{
            it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            onResetButtonPressed()
        }

        val boardFooter = binding.boardFooter
        if (playerId == 1)
            boardFooter.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.player1_colour))
        else
            boardFooter.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.player2_colour))
    }

    private fun onResetButtonPressed() {
        val currentPlayer = if (playerId == 1) GameStateManager.player1Name.value
                            else GameStateManager.player2Name.value
        // Only prompt if the board has been modified.
        if (viewModel.hasBoardBeenModified) {
            val message = """
            Are you sure you want to reset $currentPlayer's board?<br><br>
            <i>All selected buttons will be unselected and $currentPlayer's 
            current score will be reset.</i>
            """.trimIndent()
            DialogUtils.showConfirmationDialog(requireContext(),
                "Reset Board",
                message,
                "Yes",
                "No")
            {
                viewModel.resetBoardCommand()
            }
        }
        else {
            viewModel.resetBoardCommand()
        }
    }

    private fun setEightCardBonusVisibility(column: Int, visible: Boolean) {
        val eightCardBonusView = findTextView(10, column, 1)
        if (visible)
            eightCardBonusView?.visibility = View.VISIBLE
        else
            eightCardBonusView?.visibility = View.INVISIBLE
    }

    private fun observeViewModel() {
        viewModel.totalPoints.observe(viewLifecycleOwner, scoreObserver)
        viewModel.points.observe(viewLifecycleOwner, pointsObserver)
        viewModel.buttonStates.observe(viewLifecycleOwner, buttonStatesObserver)
        viewModel.eightCardBonusStates.observe(viewLifecycleOwner, eightCardBonusStatesObserver)
        viewModel.wagerCounts.observe(viewLifecycleOwner, wagerCountsObserver)
    }

    private val roundCounterObserver = Observer<Int> { round ->
        viewModel.resetBoardCommand()
    }
    private val totalScoreObserver = Observer<Int> { totalScore ->
        DialogUtils.flashTextColor(binding.totalScoreValue, R.color.white, R.color.color_primary)
        binding.totalScoreValue.text = totalScore.toString()
    }

    private val scoreObserver = Observer<Int> { score ->
        if (playerId == 1) {
            // Update Player 1 score in GameStateManager
            GameStateManager.setPlayer1CurrentPoints(score)
        } else if (playerId == 2) {
            // Update Player 2 score in GameStateManager
            GameStateManager.setPlayer2CurrentPoints(score)
        }

        DialogUtils.flashTextColor(binding.currentScoreValue, R.color.white, R.color.color_primary)
        binding.currentScoreValue.text = score.toString()
    }

    private val pointsObserver = Observer<Map<Int, Int>> { points ->
        val zeroColour = getColorFromString("light_grey")

        points.forEach { (col, point) ->
            val totalTextView = findTextView(10, col, 0)
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
                    button?.setTypeface(Typeface.create("sans-serif-black", Typeface.NORMAL))
                } else {
                    button?.setBackgroundColor(unselectedColour)
                    button?.setTextColor(getColorFromString(buttonColours[col]))
                    button?.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL))
                }
            }
        }
    }

    private val eightCardBonusStatesObserver = Observer<Map<Int, Boolean>> { bonusStates ->
        bonusStates.forEach { (col, state) ->
            setEightCardBonusVisibility(col, state)
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
