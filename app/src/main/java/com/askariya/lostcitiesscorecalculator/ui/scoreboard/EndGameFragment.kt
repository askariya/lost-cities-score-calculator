package com.askariya.lostcitiesscorecalculator.ui.scoreboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.askariya.lostcitiesscorecalculator.R
import com.askariya.lostcitiesscorecalculator.databinding.FragmentEndGameBinding
import com.askariya.lostcitiesscorecalculator.ui.utils.GameStateManager

class EndGameFragment : Fragment() {

    private var _binding: FragmentEndGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEndGameBinding.inflate(inflater, container, false)
        val root: View = binding.root

        handleWinner()

        // Clear the submitted round score to counteract the Scoreboard observer incorrectly
        // being triggered when observer is attached.
        GameStateManager.clearSubmittedRoundScore()
        val fragment = ScoreboardFragment() // Replace with your fragment class
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        // set the restartButton functionality
        val restartGameButton : Button = binding.restartGameButton
        restartGameButton.setOnClickListener{
            onRestartGameButtonPressed()
        }

        // set the reloadButton functionality
        val reloadGameButton : Button = binding.reloadGameButton
        reloadGameButton.setOnClickListener{
            onReloadGameButtonPressed()
        }

        GameStateManager.player1Name.observe(viewLifecycleOwner, playerNameObserver)
        GameStateManager.player2Name.observe(viewLifecycleOwner, playerNameObserver)

        return root
    }

    private val playerNameObserver = Observer<String> { _ ->
        handleWinner()
    }

    private fun handleWinner(){
        val gameResult = getWinnerIndex()
        when (gameResult) {
            0 ->
            {
                setHeaderColor(R.color.player1_colour)
                setFooterColor(R.color.player1_colour)
                setHeaderText("${GameStateManager.player1Name.value} Wins")
            }
            1 ->
            {
                setHeaderColor(R.color.player2_colour)
                setFooterColor(R.color.player2_colour)
                setHeaderText("${GameStateManager.player2Name.value} Wins")
            }
            else -> {
                setHeaderColor(R.color.color_secondary)
                setFooterColor(R.color.color_secondary)
                setHeaderText("Draw")
            }
        }
    }

    private fun getWinnerIndex(): Int
    {
        val player1Pts = GameStateManager.player1TotalPoints.value ?: 0
        val player2Pts = GameStateManager.player2TotalPoints.value ?: 0

        return if (player1Pts > player2Pts ) 0
               else if (player2Pts > player1Pts) 1
               else 2
    }

    private fun  setHeaderColor(color: Int)
    {
        binding.endGameMessage.setBackgroundColor(ContextCompat.getColor(requireContext(), color))
    }
    private fun  setFooterColor(color: Int)
    {
        binding.endGameFooter.setBackgroundColor(ContextCompat.getColor(requireContext(), color))
    }

    private fun setHeaderText(text: String)
    {
        binding.endGameMessage.text = text
    }

    private fun onRestartGameButtonPressed() {
        GameStateManager.restartGame(requireContext())
    }
    private fun onReloadGameButtonPressed() {
        GameStateManager.loadGame(requireContext())
    }
}
