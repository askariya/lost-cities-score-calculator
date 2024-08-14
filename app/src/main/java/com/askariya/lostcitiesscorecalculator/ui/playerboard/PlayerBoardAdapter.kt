// PlayerBoardPagerAdapter.kt
package com.askariya.lostcitiesscorecalculator.ui.playerboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.askariya.lostcitiesscorecalculator.ui.scoreboard.ScoreboardFragment

class PlayerBoardPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3 // Number of tabs (2 PlayerBoardFragments and 1 ScoreboardFragment)

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PlayerBoardFragment.newInstance(1)
            1 -> PlayerBoardFragment.newInstance(2)
            else -> ScoreboardFragment()
        }
    }
}