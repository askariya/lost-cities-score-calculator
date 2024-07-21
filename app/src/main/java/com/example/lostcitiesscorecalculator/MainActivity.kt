package com.example.lostcitiesscorecalculator

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.lostcitiesscorecalculator.databinding.ActivityMainBinding
import com.example.lostcitiesscorecalculator.ui.playerboard.PlayerBoardPagerAdapter
import com.example.lostcitiesscorecalculator.ui.scoreboard.SharedScoreViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showGameSavedNotification(){
        Toast.makeText(this, "Game Saved", Toast.LENGTH_SHORT).show()
    }

    private fun showGameLoadedNotification(){
        Toast.makeText(this, "Loaded Save", Toast.LENGTH_SHORT).show()
    }

    private fun setHeaderToolbar(position: Int)
    {
        when (position) {
            0 -> binding.headerToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.player1_colour))
            1 -> binding.headerToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.player2_colour))
            else -> binding.headerToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.color_primary))
        }
    }

    private fun updateActionBarTitle(round: Int) {
        supportActionBar?.title = "${getString(R.string.round)} $round"
    }

    private fun onSubmitButtonPressed() {
        //TODO display a warning here and ask user to confirm (or do this in SharedScoreViewModel)
        sharedScoreViewModel.submitCurrentPointsToTotal()
        onSaveGameButtonPressed() // Save the game automatically when submitting
    }

    private fun onResetGameButtonPressed() {
        //TODO display a warning here and ask user to confirm (or do this in SharedScoreViewModel)
        sharedScoreViewModel.resetGame()
    }

    private fun onSaveGameButtonPressed() {
        //TODO display a warning here and ask user to confirm
        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // Convert map to JSON string
        val gson = Gson()
        val jsonString = gson.toJson(sharedScoreViewModel.roundScores.value)

        // Save JSON string to SharedPreferences
        editor.putString("roundScores", jsonString)
        // Save the total points and the round count as well
        editor.putInt("player1TotalScore", sharedScoreViewModel.player1TotalPoints.value ?: 0)
        editor.putInt("player2TotalScore", sharedScoreViewModel.player2TotalPoints.value ?: 0)
        editor.putInt("roundCount", sharedScoreViewModel.roundCounter.value ?: 0)
        editor.apply()

        showGameSavedNotification()
    }

    private fun onLoadGameButtonPressed() {
        //TODO display a warning here and ask user to confirm
        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val rsJsonString = sharedPreferences.getString("roundScores", null)
        // Convert JSON string to map
        val gson = Gson()
        val type = object : TypeToken<MutableMap<Int, Pair<Int, Int>>>() {}.type

        val scores: MutableMap<Int, Pair<Int, Int>> = gson.fromJson(rsJsonString, type)
            ?: mutableMapOf()
        val player1TotalScore = sharedPreferences.getInt("player1TotalScore", 0)
        val player2TotalScore = sharedPreferences.getInt("player2TotalScore", 0)
        val roundCount = sharedPreferences.getInt("roundCount", 1)

        // Load the saved values back into the game
        sharedScoreViewModel.loadGame(scores, player1TotalScore, player2TotalScore, roundCount)
        showGameLoadedNotification()
    }

    private val roundCounterObserver = Observer<Int> { round ->
        updateActionBarTitle(round)
    }
}