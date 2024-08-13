package com.example.lostcitiesscorecalculator

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.example.lostcitiesscorecalculator.databinding.ActivityMainBinding
import com.example.lostcitiesscorecalculator.ui.playerboard.PlayerBoardPagerAdapter
import com.example.lostcitiesscorecalculator.ui.scoreboard.EndGameFragment
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
    private var showScoreOnSubmit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.header_toolbar))

        colorPrimary = MaterialColors.getColor(this, androidx.appcompat.R.attr.colorPrimary, Color.BLACK)

        // Observe necessary external properties
        GameStateManager.gameOver.observe(this, endGameObserver)
        GameStateManager.showScoreboardOnSubmit.observe(this, showScoreOnSubmitObserver)
        GameStateManager.roundCounter.observe(this, roundCounterObserver)
        GameStateManager.player1Name.observe(this, player1NameObserver)
        GameStateManager.player2Name.observe(this, player2NameObserver)

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        viewPager.adapter = PlayerBoardPagerAdapter(this)

        viewPager.offscreenPageLimit = 3

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customTabView = layoutInflater.inflate(R.layout.custom_tab_layout, null)
            val tabIcon = customTabView.findViewById<ImageView>(R.id.tab_icon)
            val tabText = customTabView.findViewById<TextView>(R.id.tab_text)

            tabText.text = when (position) {
                0 -> GameStateManager.player1Name.value
                1 -> GameStateManager.player2Name.value
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
                setHeaderToolbarColor(position)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val submitButton = menu.findItem(R.id.submit_button)
        submitButton.isVisible = !(GameStateManager.gameOver.value ?: false)
        return super.onPrepareOptionsMenu(menu)
    }


    // Handle toolbar button clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.submit_button -> {
                onSubmitButtonPressed()
                true
            }
            R.id.restart_game_button -> {
                onRestartGameButtonPressed()
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

    private fun setHeaderToolbarColor(position: Int)
    {
        when (position) {
            0 -> binding.headerToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.player1_colour))
            1 -> binding.headerToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.player2_colour))
            else -> binding.headerToolbar.setBackgroundColor(this.colorPrimary)
        }
    }

    private fun showEndGameFragment() {
        val endGameFragment = EndGameFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.endgame_fragment_container, endGameFragment)
            .commit()
        // Hide ViewPager2 and TabLayout
        viewPager.visibility = View.GONE
        tabLayout.visibility = View.GONE
    }

    private fun hideEndGameFragment() {
        val endGameFragment = supportFragmentManager.findFragmentById(R.id.endgame_fragment_container)
        if (endGameFragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(endGameFragment)
                .commit()
        }

        // Show ViewPager2 and TabLayout
        viewPager.visibility = View.VISIBLE
        tabLayout.visibility = View.VISIBLE
    }

    private fun updateActionBarTitle(title: String) {
            supportActionBar?.title = title
    }

    private fun updateTabText(position: Int, newText: String) {
        val tab = tabLayout.getTabAt(position)
        val customTabView = tab?.customView
        val tabText = customTabView?.findViewById<TextView>(R.id.tab_text)
        tabText?.text = newText
    }

    private fun onSubmitButtonPressed() {
        GameStateManager.submitScore(this)
    }

    private fun onRestartGameButtonPressed() {
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

    private val showScoreOnSubmitObserver = Observer<Boolean> { showScore ->
        showScoreOnSubmit = showScore
    }

    private val endGameObserver = Observer<Boolean> { gameOver ->
        if (gameOver) {
            showEndGameFragment()
            updateActionBarTitle("Game Over")
            setHeaderToolbarColor(2)
            invalidateOptionsMenu()
        }
        else {
            hideEndGameFragment()
            viewPager.currentItem = 0
            invalidateOptionsMenu()
        }
    }

    private val roundCounterObserver = Observer<Int> { round ->
        updateActionBarTitle("${getString(R.string.round)} $round")

        // Jump to the Scoreboard tab if this is a user submitted score and setting is enabled
        if(showScoreOnSubmit && round != 1 && viewPager.currentItem != 2)
            viewPager.currentItem = 2
    }

    private val player1NameObserver = Observer<String> { name ->
        updateTabText(0, name)
    }

    private val player2NameObserver = Observer<String> { name ->
        updateTabText(1, name)
    }

}