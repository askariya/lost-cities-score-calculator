package com.example.lostcitiesscorecalculator.ui.scoreboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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

        sharedScoreViewModel = ViewModelProvider(requireActivity()).get()

        sharedScoreViewModel.player1TotalPoints.observe(viewLifecycleOwner, player1ScoreObserver)
        sharedScoreViewModel.player2TotalPoints.observe(viewLifecycleOwner, player2ScoreObserver)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val player1ScoreObserver = Observer<Int> { score ->
        val player1Score = binding.player1ScoreTextView
        player1Score.text = "Score: $score"
    }
    private val player2ScoreObserver = Observer<Int> { score ->
        val player2Score = binding.player2ScoreTextView
        player2Score.text = "Score: $score"
    }
}