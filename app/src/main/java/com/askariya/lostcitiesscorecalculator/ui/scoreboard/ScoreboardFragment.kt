package com.askariya.lostcitiesscorecalculator.ui.scoreboard

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.askariya.lostcitiesscorecalculator.R
import com.askariya.lostcitiesscorecalculator.databinding.FragmentScoreboardBinding
import com.askariya.lostcitiesscorecalculator.ui.utils.DialogUtils
import com.askariya.lostcitiesscorecalculator.ui.utils.GameStateManager

class ScoreboardFragment : Fragment() {

    private var _binding: FragmentScoreboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scoreboardViewModel =
            ViewModelProvider(this).get(ScoreboardViewModel::class.java)

        _binding = FragmentScoreboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // set the submitButton functionality
        val submitScoreButton : Button = binding.submitScoreButton
        submitScoreButton.setOnClickListener{
            it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            onSubmitButtonPressed()
        }

        // set the restartButton functionality
        val restartGameButton : Button = binding.restartGameButton
        restartGameButton.setOnClickListener{
            it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            onRestartGameButtonPressed()
        }

        // set the endGameButton functionality
        val endGameButton : Button = binding.endGameButton
        endGameButton.setOnClickListener{
            it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            onEndGameButtonPressed()
        }

        GameStateManager.gameOver.observe(viewLifecycleOwner, endGameObserver)
        GameStateManager.player1CurrentPoints.observe(viewLifecycleOwner, player1CurrentScoreObserver)
        GameStateManager.player2CurrentPoints.observe(viewLifecycleOwner, player2CurrentScoreObserver)
        GameStateManager.player1TotalPoints.observe(viewLifecycleOwner, player1TotalScoreObserver)
        GameStateManager.player2TotalPoints.observe(viewLifecycleOwner, player2TotalScoreObserver)
        GameStateManager.roundScores.observe(viewLifecycleOwner, roundScoresObserver)
        GameStateManager.submittedRoundScore.observe(viewLifecycleOwner, submittedRoundScoreObserver)
        GameStateManager.player1Name.observe(viewLifecycleOwner, player1NameObserver)
        GameStateManager.player2Name.observe(viewLifecycleOwner, player2NameObserver)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onSubmitButtonPressed() {
        GameStateManager.submitScore(requireContext())
    }
    private fun onRestartGameButtonPressed() {
        GameStateManager.restartGame(requireContext())
    }
    private fun onEndGameButtonPressed() {
        val message = """
            Are you sure you want to end the game?<br><br>
            <i>All player scores and round history will be finalized.</i>
            """.trimIndent()
        DialogUtils.showConfirmationDialog(requireContext(),
            "End Game",
            message,
            "Yes",
            "No")
        {
            GameStateManager.endGame(requireContext())
        }
    }

    private fun addNewRound(roundCount: Int, player1RoundScore: Int, player2RoundScore: Int) {
        // Create TextView for Round number
        val textSize = 20f
        val textColor = ContextCompat.getColor(requireContext(), R.color.white)
        val colorPrimaryVariant = ContextCompat.getColor(requireContext(), R.color.color_primary_variant)
        var backgroundColor = ContextCompat.getColor(requireContext(), R.color.background_color)

        val spacingInDp = 10 // Desired spacing in dp
        val spacingInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            spacingInDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        if (player1RoundScore > player2RoundScore)
            backgroundColor = ContextCompat.getColor(requireContext(), R.color.player1_colour)
        else if (player2RoundScore > player1RoundScore)
            backgroundColor = ContextCompat.getColor(requireContext(), R.color.player2_colour)

        val roundNumber = TextView(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 0.3f)
                bottomMargin = spacingInPx
            }
            text = roundCount.toString()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            setTextColor(textColor)
            setBackgroundColor(colorPrimaryVariant)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(12, 12, 12, 12)
            gravity = android.view.Gravity.CENTER

            // Initial state for animation
            alpha = 0f
            translationY = 50f
        }

        // Create TextView for Player 1 Score
        val player1ScoreView = TextView(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 0.35f)
                bottomMargin = spacingInPx
            }
            text = player1RoundScore.toString()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            setTextColor(textColor)
            setBackgroundColor(backgroundColor)
            setPadding(12, 12, 12, 12)
            gravity = android.view.Gravity.CENTER

            // Initial state for animation
            alpha = 0f
            translationY = 50f
        }

        // Create TextView for Player 2 Score
        val player2ScoreView = TextView(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 0.35f)
                bottomMargin = spacingInPx
            }
            text = player2RoundScore.toString()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            setTextColor(textColor)
            setBackgroundColor(backgroundColor)
            setPadding(12, 12, 12, 12)
            gravity = android.view.Gravity.CENTER

            // Initial state for animation
            alpha = 0f
            translationY = 50f
        }

        // Add the views to the GridLayout
        val gridLayout = binding.scoreGridLayout
        gridLayout.addView(roundNumber)
        gridLayout.addView(player1ScoreView)
        gridLayout.addView(player2ScoreView)

        // Animate the views into place with a 1-second delay
        val delay = 350L // 1 second in milliseconds

        roundNumber.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(delay)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .start()

        player1ScoreView.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(delay)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .start()

        player2ScoreView.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(delay)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .start()

        checkEmptyViewVisibility()
    }

    // Function to check and update empty view visibility
    private fun checkEmptyViewVisibility() {
        binding.emptyView.visibility = if (binding.scoreGridLayout.childCount <= 0) View.VISIBLE else View.GONE
    }

    private val endGameObserver = Observer<Boolean> { gameOver ->
        if (gameOver)
        {
            binding.scoreBoardContent.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
            binding.scoreCurrentHeader.visibility = View.GONE
            binding.scoreboardFooter.visibility = View.GONE
            binding.totalScoreLabel.text = getString(R.string.final_label)
            binding.totalScoreLabel.textSize = 25F
            binding.totalScoreLabel.setPadding(15,15,15,15)
            binding.player1TotalScoreView.textSize = 25F
            binding.player1TotalScoreView.setPadding(15,15,15,15)
            binding.player2TotalScoreView.textSize = 25F
            binding.player2TotalScoreView.setPadding(15,15,15,15)
        }
        else
        {
            binding.scoreBoardContent.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background_color_dark))
            binding.scoreCurrentHeader.visibility = View.VISIBLE
            binding.scoreboardFooter.visibility = View.VISIBLE
            binding.totalScoreLabel.text = getString(R.string.total_label)
            binding.totalScoreLabel.textSize = 20F
            binding.totalScoreLabel.setPadding(10,10,10,10)
            binding.player1TotalScoreView.textSize = 20F
            binding.player1TotalScoreView.setPadding(10,10,10,10)
            binding.player2TotalScoreView.textSize = 20F
            binding.player2TotalScoreView.setPadding(10,10,10,10)
        }
    }

    private val player1CurrentScoreObserver = Observer<Int> { score ->
        val player1Score = binding.player1CurrentScoreView
        player1Score.text = score.toString()
    }
    private val player2CurrentScoreObserver = Observer<Int> { score ->
        val player2Score = binding.player2CurrentScoreView
        player2Score.text = score.toString()
    }

    private val player1TotalScoreObserver = Observer<Int> { totalScore ->
        val player1Score = binding.player1TotalScoreView
        player1Score.text = totalScore.toString()
    }
    private val player2TotalScoreObserver = Observer<Int> { totalScore ->
        val player2Score = binding.player2TotalScoreView
        player2Score.text = totalScore.toString()
    }

    private val roundScoresObserver = Observer<MutableMap<Int, Pair<Int, Int>>> { roundScores ->
        val roundNumbers = roundScores.keys.sorted()
        val scoreGrid = binding.scoreGridLayout
        scoreGrid.removeAllViews()

        for (round in roundNumbers) {
            val player1Score = roundScores[round]?.first ?: 0
            val player2Score = roundScores[round]?.second ?: 0
            addNewRound(round, player1Score, player2Score)
        }

        checkEmptyViewVisibility()
    }

    private val submittedRoundScoreObserver = Observer<Pair<Int, Int>> { roundScores ->
        val player1Score = roundScores.first
        val player2Score = roundScores.second

        if (player1Score != -500 && player2Score != -500) {
            val roundNum = GameStateManager.roundCounter.value ?: 1
            addNewRound(roundNum, player1Score, player2Score)

            checkEmptyViewVisibility()
        }
    }

    private val player1NameObserver = Observer<String> { name ->
        binding.player1ColumnHeader.text = name
    }

    private val player2NameObserver = Observer<String> { name ->
        binding.player2ColumnHeader.text = name
    }
}