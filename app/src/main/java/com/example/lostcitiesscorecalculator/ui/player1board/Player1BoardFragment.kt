package com.example.lostcitiesscorecalculator.ui.player1board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lostcitiesscorecalculator.databinding.FragmentPlayer1boardBinding

class Player1BoardFragment : Fragment() {

    private var _binding: FragmentPlayer1boardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val player1BoardViewModel =
            ViewModelProvider(this).get(Player1BoardViewModel::class.java)

        _binding = FragmentPlayer1boardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPlayer1board
        player1BoardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}