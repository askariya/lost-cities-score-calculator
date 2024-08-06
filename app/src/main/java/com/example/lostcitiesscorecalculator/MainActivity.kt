package com.example.lostcitiesscorecalculator

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.example.lostcitiesscorecalculator.databinding.ActivityMainBinding
import com.example.lostcitiesscorecalculator.ui.playerboard.PlayerBoardPagerAdapter
import com.example.lostcitiesscorecalculator.ui.scoreboard.SharedScoreViewModel
import com.example.lostcitiesscorecalculator.ui.settings.SettingsDialogFragment
import com.example.lostcitiesscorecalculator.ui.utils.GameStateManager
import com.google.android.material.color.MaterialColors
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var colorPrimary: Int = 0

    private lateinit var sharedScoreViewModel: SharedScoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.header_toolbar))

        colorPrimary = MaterialColors.getColor(this, androidx.appcompat.R.attr.colorPrimary, Color.BLACK)

        // Initialize the SharedScoreViewModel
        sharedScoreViewModel = (application as LostCitiesScoreCalculatorApplication).sharedScoreViewModel

        sharedScoreViewModel.roundCounter.observe(this, roundCounterObserver)

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        viewPager.adapter = PlayerBoardPagerAdapter(this)

        viewPager.offscreenPageLimit = 3

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customTabView = layoutInflater.inflate(R.layout.custom_tab_layout, null)
            val tabIcon = customTabView.findViewById<ImageView>(R.id.tab_icon)
            val tabText = customTabView.findViewById<TextView>(R.id.tab_text)

            tabText.text = when (position) {
                0 -> getString(R.string.title_player1)
                1 -> getString(R.string.title_player2)
                else -> getString(R.string.title_score_short)
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
                setHeaderToolbar(position)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    // Handle toolbar button clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.submit_button -> {
                onSubmitButtonPressed()
                true
            }
            R.id.reset_game_button -> {
                onResetGameButtonPressed()
                true
            }
            R.id.save_game_button -> {
                onSaveGameButtonPressed()
                true
            }
            R.id.load_game_button -> {
                onLoadGameButtonPressed()
                true
            }
            R.id.settings_button -> {
                onSettingsButtonPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setHeaderToolbar(position: Int)
    {
        when (position) {
            0 -> binding.headerToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.player1_colour))
            1 -> binding.headerToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.player2_colour))
            else -> binding.headerToolbar.setBackgroundColor(this.colorPrimary)
        }
    }

    private fun updateActionBarTitle(round: Int) {
        supportActionBar?.title = "${getString(R.string.round)} $round"
    }

    private fun onSubmitButtonPressed() {
        GameStateManager.submitScore(this)
    }

    private fun onResetGameButtonPressed() {
        GameStateManager.restartGame(this)
    }

    private fun onSaveGameButtonPressed() {
        GameStateManager.saveGame(this)
    }

    private fun onLoadGameButtonPressed() {
        GameStateManager.loadGame(this)
    }

    private fun onSettingsButtonPressed() {
        SettingsDialogFragment().show(supportFragmentManager, "SettingsDialog")
    }

    private val roundCounterObserver = Observer<Int> { round ->
        updateActionBarTitle(round)
    }

}