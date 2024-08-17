package com.askariya.lostcitiesscorecalculator.ui.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.askariya.lostcitiesscorecalculator.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GameStateManager {
    private lateinit var gameStateSharedPreferences: SharedPreferences
    private lateinit var settingsSharedPreferences: SharedPreferences

    private const val DEFAULT_PLAYER_1_NAME = "Player 1"
    private const val DEFAULT_PLAYER_2_NAME = "Player 2"

    // Settings Mutable Data
    private val _showScoreboardOnSubmit = MutableLiveData<Boolean>()
    private val _player1Name = MutableLiveData<String>()
    private val _player2Name = MutableLiveData<String>()
    private val _roundLimit = MutableLiveData<Int>()

    val showScoreboardOnSubmit: LiveData<Boolean> get() = _showScoreboardOnSubmit
    val player1Name: LiveData<String> get() = _player1Name
    val player2Name: LiveData<String> get() = _player2Name
    val roundLimit: LiveData<Int> get() = _roundLimit

    // Scoring Mutable Data
    private val _gameOver = MutableLiveData<Boolean>()
    private val _player1CurrentPoints = MutableLiveData<Int>()
    private val _player2CurrentPoints = MutableLiveData<Int>()
    private val _player1TotalPoints = MutableLiveData<Int>()
    private val _player2TotalPoints = MutableLiveData<Int>()
    private val _roundCounter = MutableLiveData<Int>()
    private val _roundScores: MutableLiveData<MutableMap<Int, Pair<Int, Int>>> = MutableLiveData(mutableMapOf())
    private val _submittedRoundScore: MutableLiveData<Pair<Int, Int>> = MutableLiveData<Pair<Int, Int>>()

    val gameOver: LiveData<Boolean> get() = _gameOver
    val player1CurrentPoints: LiveData<Int> get() = _player1CurrentPoints
    val player2CurrentPoints: LiveData<Int> get() = _player2CurrentPoints
    val player1TotalPoints: LiveData<Int> get() = _player1TotalPoints
    val player2TotalPoints: LiveData<Int> get() = _player2TotalPoints
    val roundCounter: LiveData<Int> get() = _roundCounter
    val roundScores: MutableLiveData<MutableMap<Int, Pair<Int, Int>>> get() = _roundScores
    val submittedRoundScore: MutableLiveData<Pair<Int, Int>> get() = _submittedRoundScore

    private val sharedPreferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        // Handle changes based on the key
        onSharedPreferencesChanged(key)
    }

    private var roundScoresLocal: MutableMap<Int, Pair<Int, Int>> = mutableMapOf()

    fun initialize(context: Context) {
        // initialize custom game state shared preference manager
        gameStateSharedPreferences = context.getSharedPreferences("game_state_preferences", Context.MODE_PRIVATE)

        // Set default values for the settings preferences
        PreferenceManager.setDefaultValues(context, R.xml.root_preferences, true)
        // initialize default settings shared preference manager
        settingsSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        settingsSharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
        handleShowScoreboardOnSubmit()
        handlePlayerNamePreferences()
        handleRoundLimitPreferences()
        resetGameScores()
        setGameOver(false)
    }

    fun cleanup() {
        settingsSharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }

    private fun setGameOver(isGameOver: Boolean) {
        _gameOver.value = isGameOver
    }

    private fun setShowScoreboardOnSubmit(showScoreboard: Boolean) {
        _showScoreboardOnSubmit.value = showScoreboard
    }

    private fun setPlayer1Name(name : String){
        _player1Name.value = name
    }

    private fun setPlayer2Name(name : String){
        _player2Name.value = name
    }

    private fun setRoundLimit(limit : Int){
        _roundLimit.value = limit
    }
    private fun setPlayer1TotalPoints(points: Int) {
        _player1TotalPoints.value = points
    }

    private fun setPlayer2TotalPoints(points: Int) {
        _player2TotalPoints.value = points
    }

    private fun setRoundScores(scores: MutableMap<Int, Pair<Int, Int>>) {
        _roundScores.value = scores
    }

    private fun setRoundScoresLocal(scores: MutableMap<Int, Pair<Int, Int>>) {
        roundScoresLocal = scores
    }

    private fun setSubmittedRoundScore(roundScore: Pair<Int, Int>) {
        _submittedRoundScore.value = roundScore
    }

    fun clearSubmittedRoundScore() {
        _submittedRoundScore.value = Pair(-500, -500)
    }

    private fun incrementRoundCounter() {
        _roundCounter.value = (_roundCounter.value ?: 1) + 1
    }

    private fun setRoundCounter(roundNum: Int) {
        _roundCounter.value = roundNum
    }

    private fun resetRoundCounter() {
        _roundCounter.value = 1
    }

    fun setPlayer1CurrentPoints(points: Int) {
        _player1CurrentPoints.value = points
    }

    fun setPlayer2CurrentPoints(points: Int) {
        _player2CurrentPoints.value = points
    }

    private fun onSharedPreferencesChanged(key: String?) {
        when(key){
            "show-scoreboard-on-submit" -> {
                handleShowScoreboardOnSubmit()
            }
            "custom-names", "player1name", "player2name" -> {
                handlePlayerNamePreferences()
            }
            "round-limit", "round-limit-number" -> {
                handleRoundLimitPreferences()
            }
        }
    }

    private fun handleShowScoreboardOnSubmit() {
        val showScoreboard: Boolean = settingsSharedPreferences.getBoolean("show-scoreboard-on-submit", false)
        setShowScoreboardOnSubmit(showScoreboard)
    }

    private fun handlePlayerNamePreferences() {
        val useCustomNames: Boolean = settingsSharedPreferences.getBoolean("custom-names", false)
        // Custom names was enabled
        if (useCustomNames) {
            val player1CustomName: String = (settingsSharedPreferences
                .getString("player1name", DEFAULT_PLAYER_1_NAME) ?: DEFAULT_PLAYER_1_NAME).trim()
            val player2CustomName: String = (settingsSharedPreferences
                .getString("player2name", DEFAULT_PLAYER_2_NAME) ?: DEFAULT_PLAYER_2_NAME).trim()

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
        val enableRoundLimit: Boolean = settingsSharedPreferences.getBoolean("round-limit", false)
        // Round limit enabled
        if (enableRoundLimit) {
            val limit: Int = settingsSharedPreferences.getString("round-limit-number", "-1")?.toIntOrNull() ?: -1
            setRoundLimit(limit)
        }
        // Round limit disabled
        else {
            setRoundLimit(-1)
        }
    }

    private fun addRoundScore(round: Int, player1RoundScore: Int, player2RoundScore: Int)
    {
        val roundScoreMap = roundScores.value ?: mutableMapOf()
        roundScoreMap[round] = Pair(player1RoundScore, player2RoundScore)
        // We only want to set the local score so that the subscribers don't get an update.
        setRoundScoresLocal(roundScoreMap)
        setSubmittedRoundScore(Pair(player1RoundScore, player2RoundScore))
        setPlayer1TotalPoints((player1TotalPoints.value ?: 0) + player1RoundScore)
        setPlayer2TotalPoints((player2TotalPoints.value ?: 0) + player2RoundScore)
    }

    private fun resetRoundScores()
    {
        setRoundScoresLocal(mutableMapOf())
        setRoundScores(mutableMapOf())
        setPlayer1TotalPoints(0)
        setPlayer2TotalPoints(0)
        resetRoundCounter()
    }

    private fun submitCurrentPointsToTotal() {
        addRoundScore(roundCounter.value ?: 1, player1CurrentPoints.value ?: 0, player2CurrentPoints.value ?: 0)
    }

    fun resetGameScores() {
        setPlayer1CurrentPoints(0)
        setPlayer2CurrentPoints(0)
        resetRoundScores()
    }

    // Should only be used by the MainActivity for loading the scores
    private fun loadGameScores(scores: MutableMap<Int, Pair<Int, Int>>, player1TotalScore: Int, player2TotalScore: Int, roundNum: Int)
    {
        setRoundScoresLocal(scores)
        setRoundScores(scores)
        setPlayer1TotalPoints(player1TotalScore)
        setPlayer2TotalPoints(player2TotalScore)
        setRoundCounter(roundNum)
    }

    fun saveGame(context: Context) {
        val editor = gameStateSharedPreferences.edit()
        // Convert map to JSON string
        val gson = Gson()
        val jsonString = gson.toJson(roundScoresLocal)

        // Save JSON string to SharedPreferences
        editor.putString("roundScores", jsonString)
        // Save the total points and the round count as well
        editor.putInt("player1TotalScore", player1TotalPoints.value ?: 0)
        editor.putInt("player2TotalScore", player2TotalPoints.value ?: 0)
        editor.putInt("roundCount", roundCounter.value ?: 0)
        editor.putBoolean("gameOver", gameOver.value ?: false)
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
            val gameOver = gameStateSharedPreferences.getBoolean("gameOver", false)

            // Load the saved values back into the game
            loadGameScores(scores, player1TotalScore, player2TotalScore, roundCount)
            setGameOver(gameOver)
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
            setGameOver(false)
            resetGameScores()
            // We don't save here in case the user wants to reload.
            // The next submission will overwrite the save instead.
        }
    }

    fun endGame(context: Context)
    {
        val player1 = player1Name.value ?: DEFAULT_PLAYER_1_NAME
        val player2 = player2Name.value ?: DEFAULT_PLAYER_2_NAME
        val player1FinalScore = player1TotalPoints.value ?: 0
        val player2FinalScore = player2TotalPoints.value ?: 0
        // Game ended in a tie
        if (player1FinalScore == player2FinalScore) {
            // Popup a notification notifying the user that the game has ended due to a tie.
            val message = """
            The game has ended in a draw!<br><br>
            Final Scores:<br>
            <b>$player1:</b>&nbsp;&nbsp;&nbsp;&nbsp;$player1FinalScore<br>
            <b>$player2:</b>&nbsp;&nbsp;&nbsp;&nbsp;$player2FinalScore
            """.trimIndent()
            DialogUtils.showNotificationDialog(context,
                "Game Over: Draw",
                message,
                "Disappointing")
            {
            }

        }
        // Game ended with a winner
        else {
            val winner: String = if (player1FinalScore > player2FinalScore) player1 else player2

            // Popup a notification notifying the user that the game has ended with a winner.
            val message = """
            $winner has won the game!<br><br>
            Final Scores:<br>
            <b>$player1:</b>&nbsp;&nbsp;&nbsp;&nbsp;$player1FinalScore<br>
            <b>$player2:</b>&nbsp;&nbsp;&nbsp;&nbsp;$player2FinalScore
            """.trimIndent()
            DialogUtils.showNotificationDialog(context,
                "Game Over: $winner Wins",
                message,
                "Congratulations")
            {
            }
        }

        // Trigger the Game Over screen.
        setGameOver(true)
    }

    fun submitScore(context: Context)
    {
        val player1 = player1Name.value ?: DEFAULT_PLAYER_1_NAME
        val player2 = player2Name.value ?: DEFAULT_PLAYER_2_NAME
        val player1Score = player1CurrentPoints.value ?: 0
        val player2Score = player2CurrentPoints.value ?: 0
        val message = """
            Do you want to submit the following score?<br><br>
            <b>$player1:</b>&nbsp;&nbsp;&nbsp;&nbsp;$player1Score<br>
            <b>$player2:</b>&nbsp;&nbsp;&nbsp;&nbsp;$player2Score
            """.trimIndent()
        DialogUtils.showConfirmationDialog(context,
            "Submit Score",
            message,
            "Submit",
            "Cancel")
        {
            submitCurrentPointsToTotal()

            val submittedRound: Int = roundCounter.value ?: 1
            val limit: Int = roundLimit.value ?: -1
            // When we reach the round limit --> end the game.
            if (submittedRound == limit) {
                endGame(context)
            }
            // If we have not reached the round limit, continue and increment the round counter.
            else {
                incrementRoundCounter()
                saveGame(context) // Save the game automatically when submitting
            }
        }
    }
}