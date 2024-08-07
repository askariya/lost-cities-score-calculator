package com.example.lostcitiesscorecalculator.ui.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.example.lostcitiesscorecalculator.ui.scoreboard.SharedScoreViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GameStateManager {
    private lateinit var gameStateSharedPreferences: SharedPreferences
    private lateinit var settingsSharedPreferences: SharedPreferences
    private lateinit var sharedScoreViewModel: SharedScoreViewModel

    private const val DEFAULT_PLAYER_1_NAME = "Player 1"
    private const val DEFAULT_PLAYER_2_NAME = "Player 2"

    private val _player1Name = MutableLiveData<String>()
    private val _player2Name = MutableLiveData<String>()

    val player1Name: LiveData<String> get() = _player1Name
    val player2Name: LiveData<String> get() = _player2Name

    private val sharedPreferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        // Handle changes based on the key
        onSharedPreferencesChanged(key)
    }

    fun initialize(context: Context, scoreViewModel: SharedScoreViewModel) {
        // initialize custom game state shared preference manager
        gameStateSharedPreferences = context.getSharedPreferences("game_state_preferences", Context.MODE_PRIVATE)
        // initialize default settings shared preference manager
        settingsSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        settingsSharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)

        sharedScoreViewModel = scoreViewModel

        handlePlayerNamePreferences()
        handleRoundLimitPreferences()
    }

    fun cleanup() {
        settingsSharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }

    private fun setPlayer1Name(name : String){
        _player1Name.value = name
    }

    private fun setPlayer2Name(name : String){
        _player2Name.value = name
    }

    private fun onSharedPreferencesChanged(key: String?) {
        when(key){
            "custom-names", "player1name", "player2name" -> {
                handlePlayerNamePreferences()
            }
            "round-limit", "round-limit-number" -> {
                handleRoundLimitPreferences()
            }
        }
    }

    private fun handlePlayerNamePreferences() {
        val useCustomNames: Boolean = settingsSharedPreferences.getBoolean("custom-names", false)
        // Custom names was enabled
        if (useCustomNames) {
            val player1CustomName: String = settingsSharedPreferences
                .getString("player1name", DEFAULT_PLAYER_1_NAME) ?: DEFAULT_PLAYER_1_NAME
            val player2CustomName: String = settingsSharedPreferences
                .getString("player2name", DEFAULT_PLAYER_2_NAME) ?: DEFAULT_PLAYER_2_NAME

            // Set to custom name if necessary
            if (player1Name.value != player1CustomName)
                setPlayer1Name(player1CustomName)

            if (player2Name.value != player2CustomName)
                setPlayer2Name(player2CustomName)
        }
        // Custom names was disabled
        else {
            // Set to default if necessary
            if (player1Name.value != DEFAULT_PLAYER_1_NAME)
                setPlayer1Name(DEFAULT_PLAYER_1_NAME)

            if (player2Name.value != DEFAULT_PLAYER_2_NAME)
                setPlayer2Name(DEFAULT_PLAYER_2_NAME)
        }
    }

    private fun handleRoundLimitPreferences() {

    }

    fun saveGame(context: Context) {
        val editor = gameStateSharedPreferences.edit()
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

        DialogUtils.showGameSavedNotification(context)
    }

    fun loadGame(context: Context) {
        val message = """
            Are you sure you want to load the last saved game?<br><br>
            <i>All player scores and round history will be cleared and replaced 
            with the last saved game data.</i>
            """.trimIndent()
        DialogUtils.showConfirmationDialog(context,
            "Load Game",
            message,
            "Yes",
            "No")
        {
            val rsJsonString = gameStateSharedPreferences.getString("roundScores", null)
            // Convert JSON string to map
            val gson = Gson()
            val type = object : TypeToken<MutableMap<Int, Pair<Int, Int>>>() {}.type

            val scores: MutableMap<Int, Pair<Int, Int>> = gson.fromJson(rsJsonString, type)
                ?: mutableMapOf()
            val player1TotalScore = gameStateSharedPreferences.getInt("player1TotalScore", 0)
            val player2TotalScore = gameStateSharedPreferences.getInt("player2TotalScore", 0)
            val roundCount = gameStateSharedPreferences.getInt("roundCount", 1)

            // Load the saved values back into the game
            sharedScoreViewModel.loadGame(scores, player1TotalScore, player2TotalScore, roundCount)
            DialogUtils.showGameLoadedNotification(context)
        }
    }

    fun restartGame(context: Context) {
        val message = """
            Are you sure you want to restart the game?<br><br>
            <i>All player scores and round history will be cleared.</i>
            """.trimIndent()
        DialogUtils.showConfirmationDialog(context,
            "Restart Game",
            message,
            "Yes",
            "No")
        {
            sharedScoreViewModel.resetGame()
            // We don't save here in case the user wants to reload.
            // The next submission will overwrite the save instead.
        }
    }

    fun submitScore(context: Context)
    {
        val player1Score = sharedScoreViewModel.player1CurrentPoints.value ?: 0
        val player2Score = sharedScoreViewModel.player2CurrentPoints.value ?: 0
        val message = """
            Do you want to submit the following score?<br><br>
            <b>Player 1:</b>&nbsp;&nbsp;&nbsp;&nbsp;$player1Score<br>
            <b>Player 2:</b>&nbsp;&nbsp;&nbsp;&nbsp;$player2Score
            """.trimIndent()
        DialogUtils.showConfirmationDialog(context,
            "Submit Score",
            message,
            "Submit",
            "Cancel")
        {
            sharedScoreViewModel.submitCurrentPointsToTotal()
            saveGame(context) // Save the game automatically when submitting
        }
    }
}