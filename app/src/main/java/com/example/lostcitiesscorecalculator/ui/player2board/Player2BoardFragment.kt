package com.example.lostcitiesscorecalculator.ui.player2board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lostcitiesscorecalculator.databinding.FragmentPlayer2boardBinding

class Player2BoardFragment : Fragment() {

    private var _binding: FragmentPlayer2boardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val player2BoardViewModel =
            ViewModelProvider(this).get(Player2BoardViewModel::class.java)

        _binding = FragmentPlayer2boardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}