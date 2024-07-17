package com.example.lostcitiesscorecalculator.ui.scoreboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.lostcitiesscorecalculator.R
import com.example.lostcitiesscorecalculator.databinding.FragmentScoreboardBinding

class ScoreboardFragment : Fragment() {

    private var _binding: FragmentScoreboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedScoreViewModel: SharedScoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scoreboardViewModel =
            ViewModelProvider(this).get(ScoreboardViewModel::class.java)

        _binding = FragmentScoreboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // set the resetButton functionality
        val submitScoreButton : Button = binding.submitScoreButton
        submitScoreButton.setOnClickListener{
            //TODO display a warning here and ask user to confirm (or do this in SharedScoreViewModel)
            sharedScoreViewModel.submitCurrentPointsToTotal()
        }

        sharedScoreViewModel = ViewModelProvider(requireActivity()).get()

        sharedScoreViewModel.player1CurrentPoints.observe(viewLifecycleOwner, player1CurrentScoreObserver)
        sharedScoreViewModel.player2CurrentPoints.observe(viewLifecycleOwner, player2CurrentScoreObserver)
        sharedScoreViewModel.roundCounter.observe(viewLifecycleOwner, roundCounterObserver)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addNewRound(roundCount: Int) {
        // Create TextView for Round number
        val player1RoundScore = sharedScoreViewModel.player1RoundScore.value ?: 0
        val player2RoundScore = sharedScoreViewModel.player2RoundScore.value ?: 0
        var textColor = R.color.yellow

        if (player1RoundScore > player2RoundScore)
            textColor = R.color.player1_colour
        else if (player2RoundScore > player1RoundScore)
            textColor = R.color.player2_colour

        val roundColor = ContextCompat.getColor(requireContext(), textColor)
        val roundNumber = TextView(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            text = roundCount.toString()
            setBackgroundColor(roundColor)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
        }

        // Create TextView for Player 1 Score
        val player1ScoreView = TextView(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            text = player1RoundScore.toString()
            setBackgroundColor(roundColor)
            gravity = android.view.Gravity.CENTER
        }

        // Create TextView for Player 2 Score
        val player2ScoreView = TextView(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            text = player2RoundScore.toString()
            setBackgroundColor(roundColor)
            gravity = android.view.Gravity.CENTER
        }

        // Add the views to the GridLayout
        val gridLayout = binding.scoreGridLayout
        gridLayout.addView(roundNumber)
        gridLayout.addView(player1ScoreView)
        gridLayout.addView(player2ScoreView)
        checkEmptyViewVisibility()
    }

    private fun resetScoreboard() {
        // Clear children of scoreboard gridlayout and toggle emptyview
        val scoreGrid = binding.scoreGridLayout
        for (i in scoreGrid.childCount - 1 downTo 0) {
            val child = scoreGrid.getChildAt(i)
            scoreGrid.removeViewAt(i)
        }
        checkEmptyViewVisibility()
    }

    // Function to check and update empty view visibility
    private fun checkEmptyViewVisibility() {
        binding.emptyView.visibility = if (binding.scoreGridLayout.childCount <= 0) View.VISIBLE else View.GONE
    }

    private val player1CurrentScoreObserver = Observer<Int> { score ->
//        val player1Score = binding.player1ScoreTextView
//        player1Score.text = "Score: $score"
    }
    private val player2CurrentScoreObserver = Observer<Int> { score ->
//        val player2Score = binding.player2ScoreTextView
//        player2Score.text = "Score: $score"
    }
    private val roundCounterObserver = Observer<Int> { round ->
        if (round > 1) {
            addNewRound(round - 1)
        }
        else {
            // If round is set to 1, reset the scoreboard
            resetScoreboard()
        }
    }
}