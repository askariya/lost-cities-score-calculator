package com.example.lostcitiesscorecalculator.ui.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.lostcitiesscorecalculator.ui.scoreboard.SharedScoreViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GameStateManager {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedScoreViewModel: SharedScoreViewModel

    fun initialize(context: Context, scoreViewModel: SharedScoreViewModel) {
        sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        sharedScoreViewModel = scoreViewModel
    }

    fun saveGame(context: Context) {
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