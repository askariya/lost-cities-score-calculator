package com.example.lostcitiesscorecalculator.ui.playerboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lostcitiesscorecalculator.R
import com.example.lostcitiesscorecalculator.databinding.FragmentPlayerboardBinding

class PlayerBoardFragment : Fragment() {

    private var _binding: FragmentPlayerboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val playerBoardViewModel =
            ViewModelProvider(this).get(PlayerBoardViewModel::class.java)

        _binding = FragmentPlayerboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        addButtonsToGridLayout()

//        val textView: TextView = binding.textPlayer1board
//        player1BoardViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private val colours = mutableMapOf<Int, String>().apply {
        this[0] = "yellow"
        this[1] = "white"
        this[2] = "blue"
        this[3] = "green"
        this[4] = "red"
        this[5] = "purple"
    }

    private fun addButtonsToGridLayout() {
        var totalRows = 10;
        var totalColumns = 6;

        val gridLayout = binding.boardGrid

        for (col in 0 until totalColumns)
        {
            for (row in 0 until totalRows)
            {
                val button = Button(ContextThemeWrapper(context, R.style.CardButtonStyle));
                button.tag = "${colours[col]}_$col"
                if (row > 0)
                {
                    button.text = "${row + 1}"
                }

                val packageName = context?.packageName

                val colorResourceId = resources.getIdentifier(colours[col], "color", packageName)
                button.setTextColor(ContextCompat.getColor(requireContext(), colorResourceId))

                val params = GridLayout.LayoutParams()
                params.width = 0
                params.height = 0
                params.rowSpec = GridLayout.spec(row, 1f) // Set row position
                params.columnSpec = GridLayout.spec(col, 1f) // Set column position

                button.layoutParams = params

                gridLayout.addView(button)
            }
        }
    }
}