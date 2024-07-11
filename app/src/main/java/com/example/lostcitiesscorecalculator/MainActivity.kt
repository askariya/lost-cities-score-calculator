package com.example.lostcitiesscorecalculator

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.lostcitiesscorecalculator.databinding.ActivityMainBinding
import com.example.lostcitiesscorecalculator.ui.playerboard.PlayerBoardFragment
import com.example.lostcitiesscorecalculator.ui.playerboard.PlayerBoardPagerAdapter
import com.example.lostcitiesscorecalculator.ui.scoreboard.SharedScoreViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private lateinit var sharedScoreViewModel: SharedScoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.header_toolbar))

        // Initialize the SharedScoreViewModel
        sharedScoreViewModel = ViewModelProvider(this).get(SharedScoreViewModel::class.java)

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        viewPager.adapter = PlayerBoardPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customTabView = layoutInflater.inflate(R.layout.custom_tab_layout, null)
            val tabIcon = customTabView.findViewById<ImageView>(R.id.tab_icon)
            val tabText = customTabView.findViewById<TextView>(R.id.tab_text)

            tabText.text = when (position) {
                0 -> getString(R.string.title_player1)
                1 -> getString(R.string.title_player2)
                else -> getString(R.string.title_score)
            }
            tabIcon.setImageResource(when (position) {
                0 -> {
                    R.drawable.ic_player
                }
                1 -> {
                    R.drawable.ic_player
                }
                else -> {
                    R.drawable.ic_calculator_alternate
                }
            })

            tabIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            tab.customView = customTabView
        }.attach()

        // Set a listener for page change events to update action bar title
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateActionBarTitle(position)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_button -> {
                // Handle button click
                resetBoardIfVisible()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateActionBarTitle(position: Int) {
        supportActionBar?.title = when (position) {
            0 -> getString(R.string.title_player1)
            1 -> getString(R.string.title_player2)
            else -> getString(R.string.title_score)
        }
    }

    private fun resetBoardIfVisible() {
        val currentFragment = supportFragmentManager.findFragmentByTag("f${viewPager.currentItem}")
        if (currentFragment is PlayerBoardFragment) {
            currentFragment.resetBoard()
        }
    }
}