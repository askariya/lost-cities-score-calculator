package com.example.lostcitiesscorecalculator.ui.scoreboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.lostcitiesscorecalculator.R
import com.example.lostcitiesscorecalculator.databinding.FragmentEndGameBinding
import com.example.lostcitiesscorecalculator.databinding.FragmentScoreboardBinding
import com.example.lostcitiesscorecalculator.ui.utils.GameStateManager

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

        // set the restartButton functionality
        val restartGameButton : Button = binding.restartGameButton
        restartGameButton.setOnClickListener{
            onRestartGameButtonPressed()
        }

        return root
    }

    private fun onRestartGameButtonPressed() {
        GameStateManager.restartGame(requireContext())
    }
}
